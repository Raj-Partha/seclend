package com.sec.lending.contract;

import com.sec.lending.contract.verifiers.ContractVerifier;
import com.sec.lending.contract.verifiers.ContractVerifierFactory;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

/**
 * Define your contract here.
 */
public class SecuritiesContract implements Contract {

    // This is used to identify our contract when building a transaction.
    public static final String SEC_LENDING_CONTRACT_ID = "com.sec.lending.contract.SecuritiesContract";

    /**
     * A transaction is considered valid if the verify() function of the contract of each of the transaction's input
     * and output states does not throw an exception.
     */
    @Override
    public void verify(LedgerTransaction tx) {
        ContractVerifier contracts = ContractVerifierFactory.getContracts(tx);
        contracts.executeRules();
    }

    public interface Commands extends CommandData {
        class Create implements Commands {
        }

        class Issue implements Commands {
        }

        class Return implements Commands {

        }
    }

}