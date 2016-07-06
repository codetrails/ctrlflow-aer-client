package com.ctrlflow.aer.client.logback.internal;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrlflow.aer.client.dto.Incident;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IO {
    private static final Logger LOG = LoggerFactory.getLogger(IO.class);
    private static final int CONNECTION_TIMEOUT_MS = 5000;

    /**
     * Sends an incident to the given url, compressed as {@link GzipCompressingEntity}. Returns the server response as
     * string or throws an exception if anything goes wrong.
     */
    public static String sendIncident(Incident incident, String url) throws Exception {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(incident);
        StringEntity stringEntity = new StringEntity(json,
                ContentType.APPLICATION_OCTET_STREAM.withCharset(Charsets.UTF_8));
        HttpEntity entity = new GzipCompressingEntity(stringEntity);

        URI target = null;
        try {
            target = new URI(url);
        } catch (URISyntaxException e) {
            LOG.error("Bad url", e);
        }
        Request request = Request.Post(target)
                .body(entity)
                .connectTimeout(CONNECTION_TIMEOUT_MS)
                .staleConnectionCheck(true)
                .socketTimeout(CONNECTION_TIMEOUT_MS);
        return Executor.newInstance().execute(request).returnContent().asString();
    }
}
