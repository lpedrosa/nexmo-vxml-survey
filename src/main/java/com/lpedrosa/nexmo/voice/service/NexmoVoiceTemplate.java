package com.lpedrosa.nexmo.voice.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lpedrosa.common.http.HttpOperations;
import com.lpedrosa.nexmo.voice.model.NexmoCallResponse;
import com.lpedrosa.nexmo.voice.model.NexmoCallResponse.CallStatus;
import com.lpedrosa.util.Try;

public final class NexmoVoiceTemplate implements NexmoVoiceOperations {

    private static final String BASE_URL = "https://rest.nexmo.com/call";

    private final String apiKey;
    private final String apiSecret;
    private final ResponseType responseType;
    private final HttpOperations httpOperations;
    private final Optional<String> errorCallbackUrl;
    private final Optional<String> statusCallbackUrl;

    private ObjectMapper mapper;

    public NexmoVoiceTemplate(String apiKey,
                              String apiSecret,
                              ResponseType responseType,
                              HttpOperations httpOperations) {
        this(apiKey, apiSecret, responseType, httpOperations, Optional.empty(), Optional.empty());
    }

    public NexmoVoiceTemplate(String apiKey,
                              String apiSecret,
                              ResponseType responseType,
                              HttpOperations httpOperations,
                              Optional<String> errorCallbackUrl,
                              Optional<String> statusCallbackUrl) {
        Objects.requireNonNull(apiKey, "You need to provide an api key!");
        Objects.requireNonNull(apiSecret, "You need to provide an api Secret");

        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.responseType = responseType;
        this.httpOperations = httpOperations;
        this.errorCallbackUrl = errorCallbackUrl;
        this.statusCallbackUrl = statusCallbackUrl;

        if (this.responseType == ResponseType.JSON) {
            this.mapper = new ObjectMapper();
        } else if (this.responseType == ResponseType.XML) {
            this.mapper = new XmlMapper();
        }
    }

    @Override
    public NexmoCallResponse callAndForwardToVXML(String from,
                                                  String to,
                                                  Optional<String> VXMLLocation) throws NexmoVoiceException {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        String requestUrl = BASE_URL + responseType.getUrlCommand();

        Map<String, String> requestBody = buildRequestBody(from, to, VXMLLocation);

        String response;
        try {
            response = httpOperations.doPost(requestUrl, requestBody);
        } catch (IOException ex) {
            throw new NexmoVoiceException("Error executing request with nexmo api", ex);
        }
        NexmoCallResponse callResponse = null;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = mapper.readValue(response, Map.class);
            callResponse = unpackCallResponse(responseMap);
        } catch (IOException ex) {
            throw new NexmoVoiceException("Error deserializing response", ex);
        }

        return callResponse;
    }

    private Map<String, String> buildRequestBody(String from, String to, Optional<String> VXMLLocation) {
        final Map<String, String> bodyParams = new HashMap<>();
        bodyParams.put("api_key", this.apiKey);
        bodyParams.put("api_secret", this.apiSecret);
        bodyParams.put("from", from);
        bodyParams.put("to", to);

        registerIfPresent(bodyParams, VXMLLocation, "answer", "GET");
        registerIfPresent(bodyParams, errorCallbackUrl, "error", "POST");
        registerIfPresent(bodyParams, statusCallbackUrl, "status", "POST");

        return bodyParams;
    }

    private void registerIfPresent(Map<String, String> bodyParams, Optional<String> maybeUrl, String paramPrefix, String method) {
        maybeUrl.ifPresent(url -> {
            bodyParams.put(paramPrefix+"_url",url);
            bodyParams.put(paramPrefix+"_method", method);
        });
    }

    private NexmoCallResponse unpackCallResponse(Map<String, Object> responseMap) throws NexmoVoiceException {
        @SuppressWarnings("unchecked")
        Optional<String> callId = (Optional<String>) getAsOptional(responseMap, "call-id");
        @SuppressWarnings("unchecked")
        Optional<Integer> recipient = (Optional<Integer>) getAsOptional(responseMap, "to");
        @SuppressWarnings("unchecked")
        Optional<String> errorText = (Optional<String>) getAsOptional(responseMap, "error-text");
        CallStatus callStatus = getAsOptional(responseMap, "status").flatMap(this::convertToCallStatus)
                                                                    .orElseThrow(() -> new NexmoVoiceException("Failed to unpack response: missing parameter status"));
        return new NexmoCallResponse(callId,
                                     recipient,
                                     callStatus,
                                     errorText);
    }

    private Optional<?> getAsOptional(Map<String, ?> map, String key) {
        return Optional.ofNullable(map.get(key));
    }

    private Optional<CallStatus> convertToCallStatus(Object status) {
        Integer value = Try.success(status)
                           .map(obj -> (String)obj)
                           .map(Integer::parseInt)
                           .orElseGet(() -> (Integer)status)
                           .orElse(-1);
        return CallStatus.getByStatusCode(value);
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public String getApiSecret() {
        return this.apiSecret;
    }

    public ResponseType getResponseType() {
        return this.responseType;
    }

    public Optional<String> getErrorCallbackUrl() {
        return this.errorCallbackUrl;
    }

    public Optional<String> getStatusCallbackUrl() {
        return this.statusCallbackUrl;
    }

    public static enum ResponseType {
        JSON("/json"), XML("/xml");

        private final String urlCommand;

        private ResponseType(String urlCommand) {
            this.urlCommand = urlCommand;
        }

        public String getUrlCommand() {
            return urlCommand;
        }
    }
}
