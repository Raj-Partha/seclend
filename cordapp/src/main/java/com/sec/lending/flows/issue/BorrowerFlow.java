package com.sec.lending.flows.issue;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;
import org.jetbrains.annotations.Nullable;

@InitiatedBy(LenderIssuingStocksToBorrowerFlow.class)
public class BorrowerFlow extends FlowLogic<SignedTransaction> {

    private final FlowSession otherPartySeesion;

    private static final ProgressTracker.Step STARTING_SECURITY_ISSUE = new ProgressTracker.Step("Lender connected, Issuing security started");
    private static final ProgressTracker.Step BUILDING = new ProgressTracker.Step("Building transaction");
    private static final ProgressTracker.Step SIGNING = new ProgressTracker.Step("Borrower connected, signature capture in-progress") {
        @Nullable
        @Override
        public ProgressTracker childProgressTracker() {
            return SignTransactionFlow.tracker();
        }
    };

    private final ProgressTracker progressTracker = new ProgressTracker(STARTING_SECURITY_ISSUE, BUILDING, SIGNING);

    public BorrowerFlow(FlowSession otherPartySeesion) {
        this.otherPartySeesion = otherPartySeesion;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        SignedTransaction stx = subFlow(new SignTxFlowNoChecking(otherPartySeesion, progressTracker));
        return waitForLedgerCommit(stx.getId());
    }

    static class SignTxFlowNoChecking extends SignTransactionFlow {
        SignTxFlowNoChecking(FlowSession otherFlow,ProgressTracker progressTracker) {
            super(otherFlow, SIGNING.childProgressTracker());
        }

        @Override
        protected void checkTransaction(SignedTransaction tx) {

        }
    }
}
