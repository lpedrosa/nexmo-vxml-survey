package com.lpedrosa.nexmo.voice.service;

public class NexmoVoiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public NexmoVoiceException(String msg) {
        super(msg);
    }

    public NexmoVoiceException(String msg, Throwable t) {
        super(msg, t);
    }
}
