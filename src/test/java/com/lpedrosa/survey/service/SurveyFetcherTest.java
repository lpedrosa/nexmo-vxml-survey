package com.lpedrosa.survey.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.lpedrosa.survey.model.Survey;

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
        long count = surveyFetcher.fetchSurveys().count();

        // then
        assertEquals(1, count);
    }

    @Test
    public void shouldGetMeTheCorrectSurvey() throws IOException {
        // given
        SurveyFetcher surveyFetcher = new SurveyFetcherImpl(testPath);

        // when
        long count = surveyFetcher.fetchSurveys()
                                  .map(Survey::getId)
                                  .map(surveyFetcher::fetch)
                                  .filter(Optional::isPresent)
                                  .count();

        assertEquals(1, count);
    }

    @Test
    public void shouldGetMeAnEmptyOptionalIfSurveyDoesNotExist() throws IOException {
        // given
        SurveyFetcher surveyFetcher = new SurveyFetcherImpl(testPath);

        // when
        boolean exists = surveyFetcher.fetch(-1L).isPresent();

        assertFalse(exists);
    }
}
