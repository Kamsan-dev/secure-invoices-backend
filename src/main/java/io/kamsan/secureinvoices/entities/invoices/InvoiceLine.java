package io.kamsan.secureinvoices.entities.invoices;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.kamsan.secureinvoices.entities.Customer;
import io.kamsan.secureinvoices.enums.InvoiceLineType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_DEFAULT)
@Entity
@Table(name = "\"InvoiceLines\"")
public class InvoiceLine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "invoice_line_id")
	private Long invoiceLineId;

	@ManyToOne
	@JoinColumn(name = "invoice_id", nullable = false)
	@JsonIgnore
	private Invoice invoice;

	@Column(nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InvoiceLineType type;

	private Integer quantity;

	private Integer duration;

	@Column(nullable = false)
	private Double price;

	@Column(nullable = false, name = "total_price")
	private Double totalPrice;

	// Calcul automatique du prix total
	@PrePersist
	@PreUpdate
	public void calculateTotalPrice() {
		if (type == InvoiceLineType.PRODUCT && quantity != null) {
			this.totalPrice = this.price * this.quantity;
		} else if (type == InvoiceLineType.SERVICE && duration != null) {
			this.totalPrice = this.price * this.duration;
		} else {
			throw new IllegalArgumentException("Invalid invoice line configuration.");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		InvoiceLine that = (InvoiceLine) o;
		return Objects.equals(this.invoiceLineId, that.invoiceLineId); // Compare by invoiceLineId
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.invoiceLineId); // Use invoiceLineId for hashCode
	}
}
