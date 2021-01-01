package com.sec.lending.services.rpc;

import com.sec.lending.state.SecuritiesState;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.DataFeed;
import net.corda.core.node.services.Vault;
import net.corda.core.utilities.NetworkHostAndPort;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.concurrent.ExecutionException;

/**
 * Demonstration of how to use the CordaRPCClient to connect to a Corda Node and
 * stream the contents of the node's vault.
 */
public class SecuritiesStateClient {
    private static final Logger logger = LoggerFactory.getLogger(SecuritiesStateClient.class);

    private static void logState(StateAndRef<SecuritiesState> state) {
        logger.info("{}", state.getState().getData());
    }

    public static void main(String[] args) throws ActiveMQException, InterruptedException, ExecutionException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: TemplateClient <node address>");
        }

        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse(args[0]);
        final CordaRPCClient client = new CordaRPCClient(nodeAddress, CordaRPCClientConfiguration.DEFAULT);

        // Can be amended in the Main file.
        final CordaRPCOps proxy = client.start("user1", "test").getProxy();

        // Grab all existing TemplateStates and all future TemplateStates.
        final DataFeed<Vault.Page<SecuritiesState>, Vault.Update<SecuritiesState>> dataFeed = proxy.vaultTrack(SecuritiesState.class);

        final Vault.Page<SecuritiesState> snapshot = dataFeed.getSnapshot();
        final Observable<Vault.Update<SecuritiesState>> updates = dataFeed.getUpdates();

        // Log the existing TemplateStates and listen for new ones.
        snapshot.getStates().forEach(SecuritiesStateClient::logState);
        updates.toBlocking().subscribe(update -> update.getProduced().forEach(SecuritiesStateClient::logState));
    }
}