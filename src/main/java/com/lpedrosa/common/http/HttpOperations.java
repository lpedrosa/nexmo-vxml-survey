package com.lpedrosa.common.http;

import java.io.IOException;
import java.util.Map;

public interface HttpOperations {

    String doPost(String requestUrl, Map<String, String> requestBody) throws IOException;
}
