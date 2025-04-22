package io.kamsan.secureinvoices.enums.customers;

public enum CustomerTypeEnum {
	REGULAR("Regular"),
    VIP("VIP"),
    INDIVIDUAL("Individual"),
    INSTITUTION("Institution");

    private final String label;

    CustomerTypeEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
