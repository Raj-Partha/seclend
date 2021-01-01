package com.sec.lending.contract.verifiers;

import com.sec.lending.state.SecuritiesState;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class CommonSecuritiesContractVerifier extends ContractVerifier {

    public CommonSecuritiesContractVerifier(LedgerTransaction tx) {
        super(tx);
    }

    @Override
    protected void verify() {
        requireThat(require -> {
            final SecuritiesState out = getTx().outputsOfType(SecuritiesState.class).get(0);
            require.using("Only one output state should be created.", getTx().getOutputs().size() == 1);
            require.using("The No of stocks value must be non-negative.", out.getNoOfStocks() > 0);
            require.using("Symbol name should not be empty.", out.getSymbol() != null && !out.getSymbol().isEmpty());
            require.using("Borrower should not be empty ", out.getBorrower() != null);
            require.using("Borrower should not be empty ", out.getLender() != null);
            require.using("Borrower should not be empty ", out.getParticipants().size() == 2);
            return null;
        });
    }
}
