package io.kamsan.secureinvoices.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
public class MonthlyInvoiceStatistic {
    private String month;       
    private String status;      // Status of the invoices (e.g., "Paid", "Unpaid", "Overdue")
    private int invoiceCount;   // Count of invoices for that status and month
}