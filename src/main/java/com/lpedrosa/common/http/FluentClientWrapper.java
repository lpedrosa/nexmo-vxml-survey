package com.lpedrosa.common.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FluentClientWrapper implements HttpOperations {

    public static final Logger log = LoggerFactory.getLogger(FluentClientWrapper.class);

    @Override
    public String doPost(String requestUrl, Map<String, String> requestBody)
            throws IOException {

        Form form = Form.form();

        requestBody.forEach(form::add);

        List<NameValuePair> nameValuePairs = form.build();

        if(log.isDebugEnabled()) {
            nameValuePairs.stream()
                          .forEach(pair -> log.debug("{}:{}", pair.getName(), pair.getValue()));
        }

        String result = Request.Post(requestUrl)
                               .bodyForm(nameValuePairs)
                               .execute()
                               .returnContent()
                               .asString();
        return result;
    }

}
