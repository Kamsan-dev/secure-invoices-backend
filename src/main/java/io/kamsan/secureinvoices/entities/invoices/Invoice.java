package io.kamsan.secureinvoices.entities.invoices;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.kamsan.secureinvoices.entities.Customer;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
@Entity
@Table(name = "\"Invoice\"")
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invoice_id") 
	private Long invoiceId;
	@Column(name = "invoice_number") 
	private String invoiceNumber;
	private String services;
	@Column(name = "issued_at") 
	private LocalDateTime issuedAt;
	@Column(name = "due_at") 
	private LocalDateTime dueAt;
	private String status;
	private double total;
	
	
	@Column(name = "is_vat_enabled")
	private Boolean isVatEnabled;
	
	@Column(name = "vat_rate") 
	private Double vatRate = 20.00;
	
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "customerId", nullable = true)
	private Customer customer;
	
	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceLine> invoiceLines;
	
	public void updateTotal() {
		log.info("updating total price of the invoice");
	    
	    // Print each individual total price for debugging purposes
	    invoiceLines.forEach(line -> log.info("Invoice Line Total: {}", line.getTotalPrice()));
	    
	    // Calculate and set the total price of the invoice
	    this.total = invoiceLines.stream()
	            .mapToDouble(InvoiceLine::getTotalPrice)
	            .sum();
	    
	    // Log the final total after summing the prices
	    log.info("Total invoice price before VAT : {}", this.total);
	    if (this.isVatEnabled) {
	    	this.total += ((this.total * this.vatRate) / 100);
	    }
	}
}
