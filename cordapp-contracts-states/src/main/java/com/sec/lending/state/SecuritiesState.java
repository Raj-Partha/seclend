package com.sec.lending.state;

import com.google.common.collect.ImmutableList;
import com.sec.lending.schema.SecuritiesSchemaV1;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Define your state object here.
 */
public class SecuritiesState implements LinearState, QueryableState {
    private final AbstractParty borrower;
    private final AbstractParty lender;
    private final int noOfStocks;
    private final String symbol;
    private final UniqueIdentifier linearId;

    public SecuritiesState(AbstractParty lender, AbstractParty borrower, int noOfStocks, String symbol, UniqueIdentifier linearId) {
        this.borrower = borrower;
        this.lender = lender;

        this.noOfStocks = noOfStocks;
        this.symbol = symbol;
        this.linearId = linearId;
    }

    /**
     * The public keys of the involved parties.
     */

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(borrower, lender);
    }

    public AbstractParty getBorrower() {
        return borrower;
    }

    public AbstractParty getLender() {
        return lender;
    }

    public int getNoOfStocks() {
        return noOfStocks;
    }

    public String getSymbol() {
        return symbol;
    }

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new SecuritiesSchemaV1());
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof SecuritiesSchemaV1) {
            return new SecuritiesSchemaV1.SecLendingPersistence(
                    this.lender.nameOrNull().toString(),
                    this.borrower.nameOrNull().toString(),
                    this.getNoOfStocks(),
                    this.getSymbol(),
                    this.linearId.getId());
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }
}