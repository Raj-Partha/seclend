package com.sec.lending.flows.issue;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.sec.lending.contract.SecuritiesContract;
import com.sec.lending.state.SecuritiesState;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.identity.PartyAndCertificate;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.Nullable;

import static com.sec.lending.contract.SecuritiesContract.SEC_LENDING_CONTRACT_ID;
import static net.corda.core.contracts.ContractsDSL.requireThat;

@InitiatingFlow
@StartableByRPC
public class LenderIssuingStocksToBorrowerFlow extends FlowLogic<SignedTransaction> {

    private static final ProgressTracker.Step STARTING_SECURITY_ISSUE = new ProgressTracker.Step("Lender connected, Issuing security started");
    private static final ProgressTracker.Step BUILDING = new ProgressTracker.Step("Building transaction");
    private static final ProgressTracker.Step COLLECTION = new ProgressTracker.Step("Borrower connected, signature capture in-progress") {
        @Nullable
        @Override
        public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.tracker();
        }
    };

    private final ProgressTracker progressTracker = new ProgressTracker(STARTING_SECURITY_ISSUE, BUILDING, COLLECTION);


    private final Party otherParty;
    private final int noOfStocks;
    private final String symbol;

    public LenderIssuingStocksToBorrowerFlow(Party otherParty, String symbol, int noOfStocks) {
        this.otherParty = otherParty;
        this.noOfStocks = noOfStocks;
        this.symbol = symbol;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        progressTracker.setCurrentStep(STARTING_SECURITY_ISSUE);


        FlowSession initiateFlow = initiateFlow(otherParty);

        final PartyAndCertificate me = getServiceHub().getMyInfo().getLegalIdentitiesAndCerts().get(0);
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);


        requireThat(require -> {
            require.using("No notary nodes registered", !getServiceHub().getNetworkMapCache().getNotaryIdentities().isEmpty());
            return null;
        });

        StateAndRef<SecuritiesState> securitiesLendingStateStateAndRef = getServiceHub().getVaultService().queryBy(SecuritiesState.class)
                .getStates().get(0);

        final Command<SecuritiesContract.Commands.Issue> txCommand = new Command<>(
                new SecuritiesContract.Commands.Issue(),
                ImmutableList.of(me.getOwningKey(), initiateFlow.getCounterparty().getOwningKey()));

//        StateAndContract stateAndContract = new StateAndContract(securitiesLendingStateStateAndRef, SEC_LENDING_CONTRACT_ID);

        final TransactionBuilder txBuilder = new TransactionBuilder(notary);
        //.withItems(securitiesLendingStateStateAndRef, txCommand);
        txBuilder.addInputState(securitiesLendingStateStateAndRef);
        txBuilder.addOutputState(new SecuritiesState(me.getParty(), otherParty, getNoOfStocks(), getSymbol(),
                new UniqueIdentifier()), SEC_LENDING_CONTRACT_ID);
        txBuilder.addCommand(txCommand);


        final SignedTransaction onceSignedTx = getServiceHub().signInitialTransaction(txBuilder, me.getOwningKey());
        // SignedTransaction twiceSignedTx = getServiceHub().addSignature(onceSignedTx);

        SignedTransaction signedTransaction = subFlow(new CollectSignaturesFlow(
                onceSignedTx,
                ImmutableList.of(initiateFlow),
                ImmutableList.of(me.getOwningKey()),
                COLLECTION.childProgressTracker())
        );

        return subFlow(new FinalityFlow(signedTransaction));
    }

    public Party getOtherParty() {
        return otherParty;
    }

    public int getNoOfStocks() {
        return noOfStocks;
    }

    public String getSymbol() {
        return symbol;
    }
}

