package io.kamsan.secureinvoices.enums;

public enum InvoiceStatusEnum {
    DRAFT("Draft"),
    PENDING("Pending"),
    PAID("Paid"),
    OVERDUE("Overdue");

    private final String label;

    InvoiceStatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}