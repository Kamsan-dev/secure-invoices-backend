package io.kamsan.secureinvoices.controllers;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import io.kamsan.secureinvoices.dtos.customers.CustomerDTO;
import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.report.CustomerReport;
import io.kamsan.secureinvoices.services.CustomerService;
import io.kamsan.secureinvoices.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(path = "/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
	
	private final CustomerService customerService;
	private final UserService userService;
	
	@GetMapping("/list")
	public ResponseEntity<HttpResponse> getCustomers(@RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), 
						"page", customerService.getCustomers(page.orElse(0), size.orElse(10)), 
						"stats", customerService.getStats()))
				.message("Customers retrieved")
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<HttpResponse> getCustomer(@PathVariable("id") Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), 
						"customer", customerService.getCustomer(id)))
				.message("Custome retrieved")
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
	
	@GetMapping("/search")
	public ResponseEntity<HttpResponse> searchCustomers(@RequestParam Optional<String> name, @RequestParam Optional<String> type, @RequestParam Optional<String> status,
			@RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), 
						"page", customerService.searchCustomers(name.orElse(""), type.orElse("ALL"), status.orElse("ALL"), 
								page.orElse(0), size.orElse(10))))
				.message("Custome retrieved")
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
	
	@PutMapping("/update")
	public ResponseEntity<HttpResponse> updateCustomer(@RequestBody CustomerDTO customerDTO) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.ok()
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), 
						"customer", customerService.updateCustomer(customerDTO)))
				.message("Customer updated")
				.status(OK)
				.statusCode(OK.value())
				.build());
	}
	
	
	@PostMapping("/create")
	public ResponseEntity<HttpResponse> createCustomer(@RequestBody CustomerDTO customer) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDTO userDTO = getAuthenticatedUser(authentication);
		return ResponseEntity
				.created(URI.create(""))
				.body(HttpResponse.builder()
				.timeStamp(now().toString())
				.data(of("user", userService.getUserByEmail(userDTO.getEmail()), 
						"customer", customerService.createCustomer(customer)))
				.message("Customer created")
				.status(CREATED)
				.statusCode(CREATED.value())
				.build());
	}
	
	@GetMapping("/download/report")
	public ResponseEntity<Resource> downloadReport() throws InterruptedException {
		List<Customer> customers = new ArrayList<>();
		customerService.getCustomers().forEach(customer -> customers.add(customer));
		CustomerReport report = new CustomerReport(customers);
		HttpHeaders headers = new HttpHeaders();
		headers.add("File-Name", "customer-report.xlsx");
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; File-Name=customer-report.xlsx");
		return ResponseEntity
				.ok()
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				.headers(headers)
				.body(report.export());
	}
	
	private UserDTO getAuthenticatedUser (Authentication authentication) {
		return ((CustomeUser) authentication.getPrincipal()).getUser();
	}
}
