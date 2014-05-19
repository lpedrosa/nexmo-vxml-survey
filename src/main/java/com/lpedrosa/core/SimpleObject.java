package com.lpedrosa.core;

import com.fasterxml.jackson.annotation.JsonProperty;

// TODO delete me, I exist only for testing
public class SimpleObject {
    final String name;
    final long value;

    public SimpleObject(String name, long value) {
        this.name = name;
        this.value = value;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public long getValue() {
        return value;
    }
}
