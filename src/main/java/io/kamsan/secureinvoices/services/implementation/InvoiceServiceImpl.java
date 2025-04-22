package io.kamsan.secureinvoices.services.implementation;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.kamsan.secureinvoices.dtomapper.InvoiceDTOMapper;
import io.kamsan.secureinvoices.dtos.InvoiceDTO;
import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.entities.invoices.Invoice;
import io.kamsan.secureinvoices.entities.invoices.InvoiceLine;
import io.kamsan.secureinvoices.enums.InvoiceStatusEnum;
import io.kamsan.secureinvoices.exceptions.ApiException;
import io.kamsan.secureinvoices.repositories.CustomerRepository;
import io.kamsan.secureinvoices.repositories.InvoiceLineRepository;
import io.kamsan.secureinvoices.repositories.InvoiceRepository;
import io.kamsan.secureinvoices.services.InvoiceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepository invoiceRepository;
	private final CustomerRepository customerRepository;
	
	@PersistenceContext
    private EntityManager entityManager;

	@Override
	public Invoice createInvoice(String description) {
		Invoice newInvoice = new Invoice();
		newInvoice.setIssuedAt(LocalDateTime.now());
		newInvoice.setDueAt(LocalDateTime.now());
		newInvoice.setServices(description);
		newInvoice.setInvoiceNumber(randomAlphanumeric(8).toUpperCase());
		newInvoice.setTotal(0.00);
		newInvoice.setStatus(InvoiceStatusEnum.DRAFT);
		newInvoice.setIsVatEnabled(true);
		return invoiceRepository.save(newInvoice);
	}

	@Override
	public Page<Invoice> getInvoices(int page, int size) {
		return invoiceRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.desc("issuedAt"))));
	}

	@Transactional
	@Override
	public void addInvoiceToCustomer(Long customerId, Invoice invoice) {
		invoice.setInvoiceNumber(randomAlphanumeric(8).toUpperCase());
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new ApiException("Customer with id " + customerId + " has not been found"));
		// Set the customer on the invoice (owning side)
		//invoice.setCustomer(customer);
		invoiceRepository.save(invoice);

	}

	@Override
	public InvoiceDTO getInvoice(Long id) {
		log.info("Retrieving invoice data");
		return InvoiceDTOMapper.fromInvoice(invoiceRepository.findById(id)
				.orElseThrow(() -> new ApiException("Invoice with id " + id + " has not been found")));
	}

	@Override
	public Page<Invoice> getMonthlyStatusInvoices(String status, String date_range, int page, int size) {

		// Use a formatter with an explicit locale to convert MMMM yyyy into
		// LocalDateTime
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
		// Parse Month-Year into YearMonth
		YearMonth yearMonth = YearMonth.parse(date_range, formatter);
		// Convert to LocalDateTime
		LocalDateTime startDateTime = yearMonth.atDay(1).atStartOfDay();
		LocalDateTime endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);

		System.out.println("Start DateTime: " + startDateTime);
		System.out.println("End DateTime: " + endDateTime);
		return invoiceRepository.findByStatusAndDateRange(InvoiceStatusEnum.valueOf(status.toUpperCase()), startDateTime, endDateTime,
				PageRequest.of(page, size));
	}

	@Transactional
	@Override
	public InvoiceDTO update(Long invoiceId, InvoiceDTO invoice) {
	    // Fetch the existing invoice with its lines to ensure the invoice lines are attached
	    Invoice existingInvoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new ApiException("Invoice not found"));
	    
	    // Updating fields of existing invoice
	    existingInvoice.setServices(invoice.getServices());
	    existingInvoice.setIssuedAt(invoice.getIssuedAt());
	    existingInvoice.setDueAt(invoice.getDueAt());
	    existingInvoice.setStatus(invoice.getStatus());
	    existingInvoice.setTotal(invoice.getTotal());
	    existingInvoice.setTotalVat(invoice.getTotalVat());
	    existingInvoice.setIsVatEnabled(invoice.getIsVatEnabled());
	    existingInvoice.setVatRate(invoice.getVatRate());
	    
	    List<InvoiceLine> existingLines = existingInvoice.getInvoiceLines();
	    // delete invoice line if necessary.
	    existingLines.removeIf(line -> !invoice.getInvoiceLines().contains(line));
	    
	    
	    // Update the invoice lines or add a new ones.
	    for (InvoiceLine updatedLine : invoice.getInvoiceLines()) {
	          
	        if (!existingLines.contains(updatedLine)) {
	    		updatedLine.setInvoice(existingInvoice);
	    		InvoiceLine mergedLine = entityManager.merge(updatedLine);
	            existingLines.add(mergedLine);
	            log.info("adding new invoice line");
	    	} else {
		        // Update the line attributes
	    		InvoiceLine line = existingLines.stream()
	    		        .filter(existingLine -> existingLine.getInvoiceLineId().equals(updatedLine.getInvoiceLineId()))
	    		        .findFirst()
	    		        .orElseThrow(() -> new ApiException("InvoiceLine not found"));
	    		
		        line.setDescription(updatedLine.getDescription());
		        line.setType(updatedLine.getType());
		        line.setDuration(updatedLine.getDuration());
		        line.setPrice(updatedLine.getPrice());
		        line.setQuantity(updatedLine.getQuantity());
	            log.info("updating an existing invoice line");
		        
	    	}

	    }
	    
	    // Check if the customerId is provided in the request body and update customer
        if (invoice.getCustomerId() != null) {
        	log.info("updating customer of this invoice");
            Customer customer = customerRepository.findById(invoice.getCustomerId())
                .orElseThrow(() -> new ApiException("Customer not found"));

            existingInvoice.setCustomer(customer);
        }

	    log.info("Total VAT before saving: {}", invoice.getTotalVat());
	    // Flush the changes to the database to ensure the latest changes are applied
	    entityManager.flush();
	    // Ensure the total is recalculated
	    existingInvoice.updateTotal();
	    log.info("Total VAT in invoice before saving: {}", existingInvoice.getTotalVat());
	    // Save the updated invoice with the attached invoice lines
	    return InvoiceDTOMapper.fromInvoice(invoiceRepository.save(existingInvoice));
	}

	@Override
	public void deleteInvoiceById(Long id) {
	    if (!invoiceRepository.existsById(id)) {
	        throw new ApiException("Invoice with id " + id + "not found");
	    }
	    invoiceRepository.deleteById(id);
	}

	@Override
	public Page<InvoiceDTO> getInvoicesByCustomerId(Long customerId, int page, int size) {
		return  invoiceRepository.findByCustomer_CustomerId(customerId, PageRequest.of(page, size)).map(InvoiceDTOMapper::fromInvoice);
	}
}
