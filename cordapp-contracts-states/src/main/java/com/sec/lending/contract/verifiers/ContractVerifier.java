package com.sec.lending.contract.verifiers;

import net.corda.core.transactions.LedgerTransaction;

public abstract class ContractVerifier {
    private ContractVerifier next;
    private LedgerTransaction tx;

    public ContractVerifier(LedgerTransaction tx) {
        this.tx = tx;
    }

    public void executeRules() {
        this.verify();
        if (next != null) {
            next.executeRules();
        }
    }

    protected abstract void verify();


    void setNext(ContractVerifier next) {
        this.next = next;
    }

    protected LedgerTransaction getTx() {
        return tx;
    }
}
