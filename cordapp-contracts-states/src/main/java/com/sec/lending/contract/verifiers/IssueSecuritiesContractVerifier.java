package com.sec.lending.contract.verifiers;

import com.sec.lending.state.SecuritiesState;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class IssueSecuritiesContractVerifier extends ContractVerifier {
    public IssueSecuritiesContractVerifier(LedgerTransaction tx) {
        super(tx);
    }

    @Override
    protected void verify() {
        requireThat(require -> {
            // Generic constraints around the IOU transaction.
            final SecuritiesState out = getTx().outputsOfType(SecuritiesState.class).get(0);
            require.using("No input states should be there.", getTx().getInputs().size() == 1);
            require.using("Borrower and Lender should be same, for self issue. ", out.getBorrower() != out.getLender());
            return null;
        });
    }
}
