package com.sec.lending.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

public class SecuritiesSchemaV1 extends MappedSchema {
    public SecuritiesSchemaV1() {
        super(SecuritiesSchema.class, 1, ImmutableList.of(SecLendingPersistence.class));
    }

    @Entity
    @Table(name = "securities_states")
    public static class SecLendingPersistence extends PersistentState {
        @Column(name = "lender")
        private final String lender;
        @Column(name = "borrower")
        private final String borrower;
        @Column(name = "no_of_stocks")
        private final int noOfStocks;
        @Column(name = "symbol")
        private final String symbol;
        @Column(name = "linear_id")
        private final UUID linearId;


        public SecLendingPersistence(String lender, String borrower, int noOfStocks, String symbol, UUID linearId) {
            this.lender = lender;
            this.borrower = borrower;
            this.noOfStocks = noOfStocks;
            this.symbol = symbol;
            this.linearId = linearId;
        }

        // Default constructor required by hibernate.
        public SecLendingPersistence() {
            this.lender = null;
            this.borrower = null;
            this.noOfStocks = 0;
            this.symbol = null;
            this.linearId = null;
        }

        public String getLender() {
            return lender;
        }

        public String getBorrower() {
            return borrower;
        }

        public UUID getId() {
            return linearId;
        }

        public int getNoOfStocks() {
            return noOfStocks;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
