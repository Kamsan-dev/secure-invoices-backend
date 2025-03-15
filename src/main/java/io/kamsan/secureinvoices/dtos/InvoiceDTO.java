package io.kamsan.secureinvoices.dtos;

import java.time.LocalDateTime;
import java.util.List;

import io.kamsan.secureinvoices.entities.invoices.InvoiceLine;
import io.kamsan.secureinvoices.enums.InvoiceStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceDTO {
	
	private Long invoiceId;
	private String invoiceNumber;
	private String services;
	private LocalDateTime issuedAt;
	private LocalDateTime dueAt;
	private InvoiceStatusEnum status;
	private double total;
	private Double totalVat;
	private Boolean isVatEnabled;
	private Double vatRate;
	private Long customerId;
    private List<InvoiceLine> invoiceLines;
}
