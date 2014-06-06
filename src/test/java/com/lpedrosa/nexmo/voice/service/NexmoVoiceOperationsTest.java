package com.lpedrosa.nexmo.voice.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lpedrosa.common.http.HttpOperations;
import com.lpedrosa.nexmo.voice.model.NexmoCallResponse;
import com.lpedrosa.nexmo.voice.model.NexmoCallResponse.CallStatus;
import com.lpedrosa.nexmo.voice.service.NexmoVoiceTemplate.ResponseType;

@RunWith(MockitoJUnitRunner.class)
public class NexmoVoiceOperationsTest {

    private static final String TEST_API_KEY = "key";
    private static final String TEST_API_SECRET = "secret";

    private static final Optional<String> VXML_LOCATION = Optional.of("some-url");
    private static final String VALID_ADDRESS = "111222333";

    private static final ObjectMapper xmlMapper = new XmlMapper();

    private NexmoVoiceOperations voiceOps;

    @Mock
    private HttpOperations mockHttpOperations;

    @Before
    public void setUp() {
        voiceOps = new NexmoVoiceTemplate(TEST_API_KEY,
                                          TEST_API_SECRET,
                                          ResponseType.XML,
                                          mockHttpOperations);
    }

    @Test
    public void simpleVXMLShouldBeAllOk() throws NexmoVoiceException {

        // given
        try {
            String serviceResponse = generateXMLResponse("id", VALID_ADDRESS, CallStatus.SUCCESS, "OK");
            httpOpsShouldReturn(serviceResponse);
        } catch (IOException ex) {
            fail("Shouldn't fail when setting up mockEnvironment");
        }

        // when
        NexmoCallResponse response = this.voiceOps
                                         .callAndForwardToVXML("TEST",
                                                               VALID_ADDRESS,
                                                               VXML_LOCATION);
        // then
        assertEquals(VALID_ADDRESS, response.getRecipient().get());
        assertEquals(CallStatus.SUCCESS, response.getCallStatus());
    }

    @Test(expected = NexmoVoiceException.class)
    public void shouldThrowWhenRequestToApiFails() throws NexmoVoiceException, IOException {
        // given
        httpOpsShouldThrow(new IOException("Request to api failed..."));

        // when
        this.voiceOps.callAndForwardToVXML("TEST",
                                           VALID_ADDRESS,
                                           VXML_LOCATION);
        // then
        fail("Should have thrown a NexmoVoiceException");
    }

    @Test(expected = NexmoVoiceException.class)
    public void shouldThrowWhenUnknownXMLResponse() throws NexmoVoiceException, IOException {
        // given
        httpOpsShouldReturn("FAILED!");

        // when
        this.voiceOps.callAndForwardToVXML("TEST",
                                           VALID_ADDRESS,
                                           VXML_LOCATION);
        // then
        fail("Should have thrown a NexmoVoiceException");
    }

    private void httpOpsShouldThrow(Throwable t) throws IOException {
        when(mockHttpOperations.doPost(anyString(), any())).thenThrow(t);
    }

    private void httpOpsShouldReturn(String value) throws IOException {
        when(mockHttpOperations.doPost(anyString(), any())).thenReturn(value);
    }

    private String generateXMLResponse(String callId, String to, CallStatus status, String errorText) throws IOException {
        Map<String, Object> callSubmissionRequest = new HashMap<>();
        callSubmissionRequest.put("call-id", callId);
        callSubmissionRequest.put("to", to);
        callSubmissionRequest.put("status", status.getStatusCode());
        callSubmissionRequest.put("error-text", errorText);

        return xmlMapper.writeValueAsString(callSubmissionRequest);
    }
}
