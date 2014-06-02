package com.lpedrosa.survey.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class SurveyFetcherTest {

    public URI testPath;

    @Before
    public void setUp() throws URISyntaxException {
        this.testPath = this.getClass().getResource("/com/lpedrosa/vxml").toURI();
    }

    @Test
    public void shouldLoadResourcesWhenBasepathSpecified() throws IOException {
        // given
        SurveyFetcher surveyFetcher = new SurveyFetcherImpl(testPath);

        // when
        long count = surveyFetcher.fetchSurveyFilenames().count();

        // then
        assertEquals(1, count);
    }

    @Test
    public void shouldGetMeTheCorrectSurvey() throws IOException {
        // given
        SurveyFetcher surveyFetcher = new SurveyFetcherImpl(testPath);

        // when
        long count = surveyFetcher.fetchSurveyFilenames()
                                  .map(surveyFetcher::fetch)
                                  .filter(Optional::isPresent)
                                  .count();

        assertEquals(1, count);
    }
}
