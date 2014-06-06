package com.lpedrosa.nexmo.voice.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class NexmoCallResponse {
    private final Optional<String> callId;
    private final Optional<Integer> recipient;
    private final CallStatus callStatus;
    private final Optional<String> errorText;

    public NexmoCallResponse(Optional<String> callId,
                             Optional<Integer> recipient,
                             CallStatus callStatus,
                             Optional<String> errorText) {
        this.callId = callId;
        this.recipient = recipient;
        this.callStatus = callStatus;
        this.errorText = errorText;
    }

    public Optional<String> getCallId() {
        return this.callId;
    }

    public Optional<Integer> getRecipient() {
        return this.recipient;
    }

    public CallStatus getCallStatus() {
        return this.callStatus;
    }

    public Optional<String> getErrorText() {
        return this.errorText;
    }

    public static enum CallStatus {
        SUCCESS(0, "Call successfully accepted"),
        INVALID_CREDENTIALS(1, "The credentials you supplied are invalid"),
        MISSING_PARAMS(2, "The request is missing mandatory parameters"),
        INVALID_DESTINATION_ADDRESS(4, "The recipient address is invalid"),
        CANNOT_ROUTE(5, "The platform cannot route the message"),
        NUMBER_BARRED(6, "The recipient is blacklisted and may not receive calls"),
        QUOTA_EXCEEDED(7, "You do not have sufficient creadit to proccess this request"),
        INTERNAL_ERROR(99, "An error occurred in the Nexmo platform");

        private static final Map<Integer, CallStatus> statusCodeLookup = new HashMap<>();

        static {
            for(CallStatus s : CallStatus.values()) {
                statusCodeLookup.put(s.statusCode, s);
            }
        }

        private final Integer statusCode;
        private final String description;

        private CallStatus(Integer statusCode, String description) {
            this.statusCode = statusCode;
            this.description = description;
        }

        public static Optional<CallStatus> getByStatusCode(Integer code) {
            return Optional.ofNullable(statusCodeLookup.get(code));
        }

        public Integer getStatusCode() {
            return this.statusCode;
        }

        public String getDescription() {
            return this.description;
        }
    }

    @Override
    public String toString() {
        return new StringBuilder("NexmoCallResponse[").append("callId: ")
                                                      .append(this.callId.orElse("FAILED"))
                                                      .append(" :: recipient: ")
                                                      .append(this.recipient.orElse(-1))
                                                      .append(" :: callStatus: ")
                                                      .append(this.callStatus.getDescription())
                                                      .append(" :: errorText: ")
                                                      .append(this.errorText.orElse("NONE"))
                                                      .append("]")
                                                      .toString();
    }
}
