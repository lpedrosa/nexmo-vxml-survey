package com.lpedrosa.survey.service;

import java.util.Optional;
import java.util.stream.Stream;

import com.lpedrosa.survey.model.Survey;

/**
 * Created by lpedrosa on 30/05/14.
 */
public interface SurveyFetcher {

    Optional<String> fetch(Long id);

    Stream<Survey> fetchSurveys();
}
