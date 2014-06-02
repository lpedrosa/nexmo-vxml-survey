package com.lpedrosa.survey.service;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by lpedrosa on 30/05/14.
 */
public interface SurveyFetcher {

    Optional<String> fetch(String filename);

    Stream<String> fetchSurveyFilenames();
}
