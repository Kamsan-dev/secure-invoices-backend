package io.kamsan.secureinvoices.dtos.stats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.kamsan.secureinvoices.enums.InvoiceStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceStatusCountDTO {
	
	@JsonIgnore
	private InvoiceStatusEnum status;
	private Long count;
	
	@JsonProperty("status")
	public String getStatusLabel() {
		if (status == null) return null;
		return this.status.getLabel();
	}
}
