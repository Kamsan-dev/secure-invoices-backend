package io.kamsan.secureinvoices.form.invoice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonthlyInvoiceStatusFilterRequest {

	@NotBlank(message = "Status is required.")
	private String status;

	@NotBlank(message = "Month-Year is required.")
	@Pattern(regexp = "^(January|February|March|April|May|June|July|August|September|October|November|December) \\d{4}$", message = "Invalid Month-Year format. Expected format: 'MMMM yyyy'.")
	private String monthYear;

}
