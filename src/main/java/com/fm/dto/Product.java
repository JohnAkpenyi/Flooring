package com.fm.dto;

import java.math.BigDecimal;

public class Product {
    private final String productType;
    private final BigDecimal costPerSquareFoot;
    private final BigDecimal laborCostPerSquareFoot;

    public Product(String productType, BigDecimal costPerSqFt, BigDecimal laborCostPerSqFt) {
        this.productType = productType;
        this.costPerSquareFoot = costPerSqFt;
        this.laborCostPerSquareFoot = laborCostPerSqFt;
    }

    public String getProductType() {
        return productType;
    }

    public BigDecimal getCostPerSqFt() {
        return costPerSquareFoot;
    }

    public BigDecimal getLaborCostPerSqFt() {
        return laborCostPerSquareFoot;
    }
}
