package com.sec.lending.flows.create;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.sec.lending.contract.SecuritiesContract;
import com.sec.lending.state.SecuritiesState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndContract;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import static com.sec.lending.contract.SecuritiesContract.SEC_LENDING_CONTRACT_ID;

@InitiatingFlow
@StartableByRPC
public class SelfIssueSecuritiesFlow extends FlowLogic<SignedTransaction> {

    private final String symbol;
    private final int noOfStocks;


    private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction based on new Stocks.");
    private final ProgressTracker.Step VERIFYING_TRANSACTION = new ProgressTracker.Step("Verifying contract constraints.");
    private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing transaction with our private key.");
    private final ProgressTracker.Step GATHERING_SIGNS = new ProgressTracker.Step("Gathering the counterparty's signature.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.Companion.tracker();
        }
    };
    private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.Companion.tracker();
        }
    };

    // The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
    // checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call()
    // function.
    private final ProgressTracker progressTracker = new ProgressTracker(
            GENERATING_TRANSACTION,
            VERIFYING_TRANSACTION,
            SIGNING_TRANSACTION,
            GATHERING_SIGNS,
            FINALISING_TRANSACTION
    );

    public SelfIssueSecuritiesFlow(String symbol, int noOfStocks) {
        this.symbol = symbol;
        this.noOfStocks = noOfStocks;
    }


    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        final Party me = getServiceHub().getMyInfo().getLegalIdentities().get(0);
        // Stage 1.
//        progressTracker.setCurrentStep(GENERATING_TRANSACTION);
        SecuritiesState securitiesLendingState = new SecuritiesState(me, me, noOfStocks, symbol, new UniqueIdentifier());
        final Command<SecuritiesContract.Commands.Create> txCommand = new Command<>(
                new SecuritiesContract.Commands.Create(),
                ImmutableList.of(me.getOwningKey()));

        StateAndContract stateAndContract = new StateAndContract(securitiesLendingState, SEC_LENDING_CONTRACT_ID);

        final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .withItems(stateAndContract, txCommand);
        //.addOutputState(securitiesLendingState, SEC_LENDING_CONTRACT_ID)
        //.addCommand(txCommand);

//        progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
        // Verify that the transaction is valid.
//        txBuilder.verify(getServiceHub());

//        progressTracker.setCurrentStep(SIGNING_TRANSACTION);
        // Sign the transaction.
        final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);
        SignedTransaction signedTransaction = subFlow(new FinalityFlow(partSignedTx));

        // Notarise and record the transaction in both parties' vaults.
        return signedTransaction;
    }
}
