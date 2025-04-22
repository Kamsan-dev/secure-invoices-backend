package io.kamsan.secureinvoices.enums.customers;

public enum CustomerStatusEnum {
	ACTIVE("Active"),
    INACTIVE("Inactive"),
    BANNED("Banned"),
    PENDING("Pending");
    
    private final String label;

    CustomerStatusEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
