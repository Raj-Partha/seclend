package com.sec.lending.contract.verifiers;

import com.sec.lending.state.SecuritiesState;
import net.corda.core.transactions.LedgerTransaction;

import static net.corda.core.contracts.ContractsDSL.requireThat;

public class ReturnSecuritiesContractVerifier extends ContractVerifier {
    public ReturnSecuritiesContractVerifier(LedgerTransaction tx) {
        super(tx);
    }

    @Override
    protected void verify() {
        requireThat(require -> {
            // Generic constraints around the IOU transaction.
            final SecuritiesState out = getTx().outputsOfType(SecuritiesState.class).get(0);
            final SecuritiesState in = getTx().inputsOfType(SecuritiesState.class).get(0);

            require.using("input quantiy and output quantity should be the same ", out.getNoOfStocks() == in.getNoOfStocks());
            require.using("Input symbol and output symbol should be the same", out.getSymbol().equals(in.getSymbol()));
            return null;
        });
    }
}
