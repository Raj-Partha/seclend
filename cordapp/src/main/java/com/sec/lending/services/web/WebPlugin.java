package com.sec.lending.services.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sec.lending.services.rest.RESTEndPoint;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.webserver.services.WebServerPluginRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class WebPlugin implements WebServerPluginRegistry {
    @NotNull
    @Override
    public List<Function<CordaRPCOps, ? extends Object>> getWebApis() {
        return ImmutableList.of(RESTEndPoint::new);
    }

    @NotNull
    @Override
    public Map<String, String> getStaticServeDirs() {
        return ImmutableMap.of(
                // This will serve the templateWeb directory in resources to /web/template
                "template", getClass().getClassLoader().getResource("templateWeb").toExternalForm());
    }

    @Override
    public void customizeJSONSerialization(ObjectMapper om) {

    }
}
