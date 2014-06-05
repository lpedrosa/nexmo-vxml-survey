package com.lpedrosa.nexmo.voice.service;

import java.util.Optional;

import com.lpedrosa.nexmo.voice.model.NexmoCallResponse;

public interface NexmoVoiceOperations {
    NexmoCallResponse callAndForwardToVXML(String from,
                                           String to,
                                           Optional<String> VXMLLocation) throws NexmoVoiceException;
}
