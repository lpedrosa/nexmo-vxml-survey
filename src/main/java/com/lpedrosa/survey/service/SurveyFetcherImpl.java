package com.lpedrosa.survey.service;

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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lpedrosa.survey.model.Survey;

public class SurveyFetcherImpl implements SurveyFetcher {

    private static final Logger log = LoggerFactory.getLogger(SurveyFetcherImpl.class);

    private final Map<Long, Survey> filenameIndex;
    private final AtomicLong idGenerator;

    public SurveyFetcherImpl(URI uri) throws IOException {
        this.filenameIndex = new HashMap<>();
        this.idGenerator = new AtomicLong();
        try {
            loadSurveys(uri);
        } catch (Exception e) {
            throw new IOException("Could not load surveys", e);
        }
    }

    @Override
    public Optional<String> fetch(Long id) {
        if(filenameIndex.containsKey(id)) {
            String fileAsString = null;
            try {
                fileAsString = new String(filenameIndex.get(id).getContents(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("Unable to fetch file.", e);
            }
            return Optional.ofNullable(fileAsString);
        }
        return Optional.empty();
    }

    @Override
    public Stream<Survey> fetchSurveys() {
        return filenameIndex.values().stream();
    }

    private void loadSurveys(URI uri) throws Exception {
        Path path = Paths.get(uri);
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(path, "*.xml")) {
            dir.forEach(this::loadFileIntoCache);
        }
    }

    private boolean loadFileIntoCache(Path p) {
        boolean success = false;
        try {
            String filename = p.getFileName().toString();
            byte[] bArr = Files.readAllBytes(p);
            long index = idGenerator.incrementAndGet();
            filenameIndex.put(index, new Survey(index, filename, bArr));
            success = true;
        } catch (IOException e) {
            log.warn("Failed to load vxml file", e);
        }
        return success;
    }
}
