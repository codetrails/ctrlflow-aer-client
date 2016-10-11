package com.ctrlflow.aer.client.simple;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.ctrlflow.aer.client.dto.Incident;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SimpleAerClient {

    private static final int CONNECTION_TIMEOUT_MS = 5000;

    /**
     * Sends an incident to the given URI, compressed as {@link GzipCompressingEntity}. Returns the server response as
     * string or throws an exception if anything goes wrong.
     */
    public static String send(Incident incident, String uri) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(incident);
        StringEntity stringEntity = new StringEntity(json,
                ContentType.APPLICATION_OCTET_STREAM.withCharset(StandardCharsets.UTF_8));
        HttpEntity entity = new GzipCompressingEntity(stringEntity);

        URI target = null;
        try {
            target = new URI(uri);
        } catch (URISyntaxException e) {
            throw new IOException("Bad URI", e);
        }
        Request request = Request.Post(target)
                .body(entity)
                .connectTimeout(CONNECTION_TIMEOUT_MS)
                .staleConnectionCheck(true)
                .socketTimeout(CONNECTION_TIMEOUT_MS);
        return Executor.newInstance().execute(request).returnContent().asString();
    }
}
