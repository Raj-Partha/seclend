package com.sec.lending.contract.verifiers;

import com.sec.lending.contract.SecuritiesContract;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.transactions.LedgerTransaction;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ContractVerifierFactory {

    public static ContractVerifier getContracts(LedgerTransaction tx) {

        final List<CommandWithParties<CommandData>> commands = tx.getCommands().stream().filter(
                it -> it.getValue() instanceof SecuritiesContract.Commands
        ).collect(Collectors.toList());
        final CommandData command = onlyElementOf(commands).getValue();


        ContractVerifier contractVerifier = new CommonSecuritiesContractVerifier(tx);
        if (command instanceof SecuritiesContract.Commands.Create) {
            contractVerifier.setNext(new SelfIssueContractVerifier(tx));
        } else if (command instanceof SecuritiesContract.Commands.Issue) {
            contractVerifier.setNext(new IssueSecuritiesContractVerifier(tx));
        } else if (command instanceof SecuritiesContract.Commands.Return) {
            contractVerifier.setNext(new ReturnSecuritiesContractVerifier(tx));
        }
        return contractVerifier;
    }

    private static <T> T onlyElementOf(Iterable<T> iterable) {
        Iterator<T> iter = iterable.iterator();
        T item = iter.next();
        if (iter.hasNext()) {
            throw new IllegalArgumentException("Iterable has more than one element!");
        }
        return item;
    }
}
