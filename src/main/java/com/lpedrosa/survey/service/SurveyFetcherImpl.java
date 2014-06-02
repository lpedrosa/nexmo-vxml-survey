package com.lpedrosa.survey.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SurveyFetcherImpl implements SurveyFetcher {

    private static final Logger log = LoggerFactory.getLogger(SurveyFetcherImpl.class);

    private final Map<String, ByteArray> filenameIndex;

    public SurveyFetcherImpl(URI uri) throws IOException {
        this.filenameIndex = new HashMap<>();
        try {
            loadSurveys(uri);
        } catch (Exception e) {
            throw new IOException("Could not load surveys", e);
        }
    }

    public void loadSurveys(URI uri) throws Exception {
        Path path = Paths.get(uri);
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(path, "*.xml")) {
                dir.forEach(this::loadFileIntoCache);
        }
    }

    @Override
    public Optional<String> fetch(String filename) {
        if(filenameIndex.containsKey(filename)) {
            String fileAsString = null;
            try {
                fileAsString = new String(filenameIndex.get(filename).contents, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("Unable to fetch file.", e);
            }
            return Optional.ofNullable(fileAsString);
        }
        return Optional.empty();
    }

    @Override
    public Stream<String> fetchSurveyFilenames() {
        return filenameIndex.keySet().stream();
    }

    private boolean loadFileIntoCache(Path p) {
        boolean success = false;
        try {
            String filename = p.getFileName().toString();
            ByteArray bArr = new ByteArray(Files.readAllBytes(p));
            filenameIndex.put(filename, bArr);
            success = true;
        } catch(IOException e) {
            log.warn("Failed to load vxml file", e);
        }
        return success;
    }

    private static class ByteArray {
        private final byte[] contents;

        public ByteArray(byte[] contents) {
            this.contents = contents;
        }

        public byte[] getContents() {
            return contents;
        }
    }
}
