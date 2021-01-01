package com.sec.lending.services.rest;


import java.io.Serializable;

public class CreateSecRequest implements Serializable {
    private String partyName;
    private String noOfStocks;
    private String symbol;

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getNoOfStocks() {
        return noOfStocks;
    }

    public void setNoOfStocks(String noOfStocks) {
        this.noOfStocks = noOfStocks;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
