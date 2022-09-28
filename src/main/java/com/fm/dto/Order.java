package com.fm.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;


public class Order {
    private int orderNumber;
    private String customerName;
    private String state;
    private String productType;
    private BigDecimal taxRate;
    private BigDecimal costPerSqFt;
    private BigDecimal laborCostPerSqFt;
    private BigDecimal area;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal taxCost;
    private BigDecimal total;
    private LocalDate orderDate;
    
    //Constructor for add order
    public Order(String customerName, String state, String productType, LocalDate orderDate, BigDecimal area, 
            BigDecimal taxRate, BigDecimal costPerSqFt, BigDecimal laborCostPerSqFt) {
        this.customerName = customerName;
        this.state = state;
        this.productType = productType;
        this.orderDate = orderDate;
        this.area = area;
        this.taxRate = taxRate;
        this.costPerSqFt = costPerSqFt;
        this.laborCostPerSqFt = laborCostPerSqFt;
        setCalculableValues();
        setBigDecimalScale();
    }
    
    private void setCalculableValues() {
        this.materialCost = area.multiply(costPerSqFt);
        this.laborCost = area.multiply(laborCostPerSqFt);
        BigDecimal subTotal = laborCost.add(materialCost);
        this.taxCost = subTotal.multiply((taxRate.divide(new BigDecimal("100"))));
        this.total = subTotal.add(taxCost);
    }
    
    //Constructor for file import
    public Order(String orderNumber, String customerName, String state, BigDecimal taxRate, 
            String productType, BigDecimal area, BigDecimal costPerSqFt, BigDecimal laborCostPerSqFt, 
            BigDecimal materialCost, BigDecimal laborCost, BigDecimal taxCost, 
            BigDecimal total) {
        this.orderNumber = Integer.parseInt(orderNumber);
        this.customerName = customerName;
        this.state = state;
        this.productType = productType;
        this.taxRate = taxRate;
        this.costPerSqFt = costPerSqFt;
        this.laborCostPerSqFt = laborCostPerSqFt;
        this.area = area;
        this.materialCost = materialCost;
        this.laborCost = laborCost;
        this.taxCost = taxCost;
        this.total = total;
        setBigDecimalScale();
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getCostPerSqFt() {
        return costPerSqFt;
    }

    public void setCostPerSqFt(BigDecimal costPerSqFt) {
        this.costPerSqFt = costPerSqFt;
    }

    public BigDecimal getLaborCostPerSqFt() {
        return laborCostPerSqFt;
    }

    public void setLaborCostPerSqFt(BigDecimal laborCostPerSqFt) {
        this.laborCostPerSqFt = laborCostPerSqFt;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getMaterialCost() {
        return materialCost;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public BigDecimal getTaxCost() {
        return taxCost;
    }

    public BigDecimal getTotal() {
        return total;
    }

    private void setBigDecimalScale() {
        taxRate = taxRate.setScale(3, RoundingMode.HALF_UP); 
        costPerSqFt = costPerSqFt.setScale(3, RoundingMode.HALF_UP);
        laborCostPerSqFt = laborCostPerSqFt.setScale(3, RoundingMode.HALF_UP); 
        area = area.setScale(2, RoundingMode.HALF_UP); 
        materialCost = materialCost.setScale(2, RoundingMode.HALF_UP); 
        laborCost = laborCost.setScale(2, RoundingMode.HALF_UP); 
        taxCost = taxCost.setScale(2, RoundingMode.HALF_UP); 
        total = total.setScale(2, RoundingMode.HALF_UP);
    }

    public void recalculate() {
        setCalculableValues();
        setBigDecimalScale();
    }


    

}
