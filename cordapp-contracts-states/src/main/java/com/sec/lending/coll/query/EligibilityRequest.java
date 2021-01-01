package com.sec.lending.coll.query;

import java.io.Serializable;

public class EligibilityRequest  implements Serializable {
    private String symbol;
    private long noOfStocks;
    private double currentMarketPrice;
    private String borrowerName;
    private String custodian;
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getNoOfStocks() {
        return noOfStocks;
    }

    public void setNoOfStocks(long noOfStocks) {
        this.noOfStocks = noOfStocks;
    }

    public double getCurrentMarketPrice() {
        return currentMarketPrice;
    }

    public void setCurrentMarketPrice(double currentMarketPrice) {
        this.currentMarketPrice = currentMarketPrice;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getCustodian() {
        return custodian;
    }

    public void setCustodian(String custodian) {
        this.custodian = custodian;
    }

}