package com.fm.dto;

import java.math.BigDecimal;

public class Tax {
    private final String stateAbbreviation;
    private final String stateName;
    private final BigDecimal taxRate;

    public Tax(String stateAbbrev, String stateName, BigDecimal taxRate) {
        this.stateAbbreviation = stateAbbrev;
        this.stateName = stateName;
        this.taxRate = taxRate;
    }
    
    public String getStateAbbrev() {
        return stateAbbreviation;
    }
    public BigDecimal getTaxRate() {
        return taxRate;
    }

}
