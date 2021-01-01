package com.sec.lending.coll.query;

import java.io.Serializable;

public class EligibilityResponse implements Serializable {
    private String borrowerName;
    private boolean eligible;
    private String message;
    private long shortage;

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getShortage() {
        return shortage;
    }

    public void setShortage(long shortage) {
        this.shortage = shortage;
    }
}