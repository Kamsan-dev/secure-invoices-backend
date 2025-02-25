package io.kamsan.secureinvoices.controllers;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.kamsan.secureinvoices.domain.CustomeUser;
import io.kamsan.secureinvoices.domain.HttpResponse;
import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.invoices.Invoice;
import io.kamsan.secureinvoices.entities.invoices.InvoiceLine;
import io.kamsan.secureinvoices.form.invoice.MonthlyInvoiceStatusFilterRequest;
import io.kamsan.secureinvoices.repositories.InvoiceRepository;
import io.kamsan.secureinvoices.services.CustomerService;
import io.kamsan.secureinvoices.services.InvoiceService;
import io.kamsan.secureinvoices.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/invoice")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {
	
	private final CustomerService customerService;
	private final InvoiceService invoiceService;
	private final UserService userService;
	
	@GetMapping("/new")
	public ResponseEntity<HttpResponse> newInvoice() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), 
						"customers", customerService.getCustomers()))
				.message("Invoice created")
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
	
	@PostMapping("/create")
	public ResponseEntity<HttpResponse> createInvoice(@RequestBody Invoice invoice) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.created(URI.create(""))
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), 
						"invoice", invoiceService.createInvoice(invoice)))
				.message("Invoice created")
				.status(CREATED)
				.statusCode(CREATED.value())
				.build());
	}
	
	
	@GetMapping("/list")
	public ResponseEntity<HttpResponse> getInvoices(@RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), 
						"page", invoiceService.getInvoices(page.orElse(0), size.orElse(10))))
				.message("Customers retrieved")
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<HttpResponse> getInvoice(@PathVariable("id") Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Invoice invoice = invoiceService.getInvoice(id);
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), 
						"invoice", invoice, "customer", invoice.getCustomer()))
				.message("Invoice retrieved")
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<HttpResponse> updateInvoiceById(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), "invoice", invoiceService.update(id, invoice)))
				.message("Invoice has been updated and affected to " + customerService.getCustomer(id).getName())
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
	
	@PostMapping("/add-to-customer/{id}")
	public ResponseEntity<HttpResponse> addInvoiceToCustomer(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		invoiceService.update(id, invoice);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail())))
				.message("Invoice "+ invoiceService.getInvoice(id).getInvoiceNumber() + " has been updated.")
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
	
	private UserDTO getAuthenticatedUser (Authentication authentication) {
		return ((CustomeUser) authentication.getPrincipal()).getUser();
	}
	
	// Chart endpoint
	
	@PostMapping("/list/monthly-status")
	public ResponseEntity<HttpResponse> getDataFiltered(@Valid @RequestBody MonthlyInvoiceStatusFilterRequest request, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), "page", 
						invoiceService.getMonthlyStatusInvoices(
								request.getStatus(), request.getMonthYear(), page.orElse(0), size.orElse(5))))
				.message("Invoice retrieved")
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
}
