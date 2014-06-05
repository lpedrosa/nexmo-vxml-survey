package com.lpedrosa.common.http;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

public class FluentClientWrapper implements HttpOperations {

    @Override
    public String doPost(String requestUrl, Map<String, String> requestBody)
            throws IOException {

        Form form = Form.form();

        requestBody.forEach(form::add);

        String result = Request.Post(requestUrl)
                               .bodyForm(form.build())
                               .execute()
                               .returnContent()
                               .asString();
        return result;
    }

}
