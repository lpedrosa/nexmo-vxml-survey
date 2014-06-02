package com.lpedrosa.survey.model;

/**
 * Created by lpedrosa on 02/06/14.
 */
public class Survey {
    private final long id;
    private final String filename;
    private final byte[] contents;

    public Survey(final long id, final String filename, final byte[] contents) {
        this.id = id;
        this.filename = filename;
        this.contents = contents;
    }

    public long getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getContents() {
        return contents;
    }
}
