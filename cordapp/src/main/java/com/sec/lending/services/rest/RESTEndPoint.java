package com.sec.lending.services.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sec.lending.coll.query.EligibilityRequest;
import com.sec.lending.coll.query.EligibilityResponse;
import com.sec.lending.flows.close.BorrowerInitiatedClosingFlow;
import com.sec.lending.flows.create.SelfIssueSecuritiesFlow;
import com.sec.lending.flows.issue.LenderIssuingStocksToBorrowerFlow;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.*;

@Path("template")
public class RESTEndPoint {
    private static final Logger logger = LoggerFactory.getLogger(RESTEndPoint.class);

    private final CordaRPCOps rpcOps;

    private final CordaX500Name myLegalName;

    private final List<String> serviceNames = ImmutableList.of("Notary", "Network Map Service");

    public RESTEndPoint(CordaRPCOps services) {
        this.rpcOps = services;
        myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
    }

    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<CordaX500Name>> getPeers() {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        return ImmutableMap.of("peers", nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName())
                .filter(name -> !name.equals(myLegalName) && !serviceNames.contains(name.getOrganisation()))
                .collect(toList()));
    }

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CordaX500Name> whoami() {
        return ImmutableMap.of("me", myLegalName);
    }

    @PUT
    @Path("selfIssueSec")
    public Response test(@QueryParam("noOfStocks") int noOfStocks, @QueryParam("symbol") String symbol) {
        SignedTransaction state = null;
        try {
            state = rpcOps
                    .startTrackedFlowDynamic(SelfIssueSecuritiesFlow.class, symbol, noOfStocks)
                    .getReturnValue()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Response.status(BAD_REQUEST).entity("Exception " + e.getMessage()).build();
        }

        final String msg = "{\"transactionId\":\"" + state.getId() + "\"}";
        return Response.status(CREATED).entity(msg).build();
    }


    @PUT
    @Path("createSecLedger")
    public Response createSecLedger(@QueryParam("partyName") String partyName, @QueryParam("noOfStocks") int noOfStocks, @QueryParam("symbol") String symbol) {


        SignedTransaction state = null;
        try {
            CordaX500Name cordaX500Name = CordaX500Name.parse(java.net.URLDecoder.decode(partyName, "UTF-8"));
            EligibilityRequest eligibilityRequest = new EligibilityRequest();
            eligibilityRequest.setBorrowerName(cordaX500Name.getOrganisation());
            eligibilityRequest.setCustodian(myLegalName.getOrganisation());
            eligibilityRequest.setNoOfStocks(noOfStocks);
            eligibilityRequest.setSymbol(symbol);
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(eligibilityRequest);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<String> request = new HttpEntity<>(jsonRequest, requestHeaders);
            EligibilityResponse response = gson.fromJson(restTemplate.exchange("http://localhost:8090/api/collateral/check-eligibility", HttpMethod.POST, request, String.class).getBody(), EligibilityResponse.class);
            logger.info("Response from collateral api " + response.toString());
            if (response.isEligible()) {
                state = rpcOps
                        .startTrackedFlowDynamic(LenderIssuingStocksToBorrowerFlow.class, rpcOps.wellKnownPartyFromX500Name(cordaX500Name), symbol, noOfStocks)
                        .getReturnValue()
                        .get();
                final String msg = "{\"success\": \"true\",  \"transactionId\":\"" + state.getId() + "\"}";
                return Response.status(OK).
                        entity(msg).
                        build();
            } else {
                logger.warn("In sufficient fund " + response.toString());
                final String msg = "{\"success\": \"false\",  \"message\":\"" + response.getMessage() + "\"}";
                return Response.status(OK)
                        .entity(msg)
                        .build();
            }
        } catch (InterruptedException | ExecutionException |
                UnsupportedEncodingException e) {
            logger.error("Problem with creating sec ledge ", e);
            return Response.status(BAD_REQUEST).entity("Exception " + e.getMessage()).build();
        }
    }


    @PUT
    @Path("closeSecLedger")
    public Response closeSecLedger(@QueryParam("partyName") String partyName, @QueryParam("noOfStocks") int noOfStocks, @QueryParam("symbol") String symbol) {
        SignedTransaction state = null;
        try {
            CordaX500Name cordaX500Name = CordaX500Name.parse(java.net.URLDecoder.decode(partyName, "UTF-8"));

            state = rpcOps
                    .startTrackedFlowDynamic(BorrowerInitiatedClosingFlow.class, rpcOps.wellKnownPartyFromX500Name(cordaX500Name), symbol, noOfStocks)
                    .getReturnValue()
                    .get();

            HttpResponse<JsonNode> jsonResponse = Unirest.put("http://localhost:8090/api/collateral/return-collateral")
                    .header("accept", "application/json")
                    .field("symbol", symbol)
                    .field("noOfStocks", noOfStocks)
                    .field("custodian", myLegalName.getOrganisation())
                    .field("borrowerName", cordaX500Name.getOrganisation())
                    .asJson();
        } catch (InterruptedException | ExecutionException | UnsupportedEncodingException | UnirestException e) {
            return Response.status(BAD_REQUEST).entity("Exception " + e.getMessage()).build();
        }

        final String msg = "{\"transactionId\":\"" + state.getId() + "\"}";
        return Response.status(CREATED).entity(msg).build();
    }

}
