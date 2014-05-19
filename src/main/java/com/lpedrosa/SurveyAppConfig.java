package com.lpedrosa;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class SurveyAppConfig extends Configuration {
    @NotEmpty
    private String apiKey;
    @NotEmpty
    private String apiSecret;

    @JsonProperty
    public String getApiKey() {
        return apiKey;
    }

    @JsonProperty
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @JsonProperty
    public String getApiSecret() {
        return apiSecret;
    }

    @JsonProperty
    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }
}
