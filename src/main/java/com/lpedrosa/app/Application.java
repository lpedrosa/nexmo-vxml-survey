package com.lpedrosa.app;

import static spark.Spark.get;
import static spark.Spark.post;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jetty.http.HttpStatus;

import com.lpedrosa.common.http.FluentClientWrapper;
import com.lpedrosa.common.http.HttpOperations;
import com.lpedrosa.common.util.Try;
import com.lpedrosa.nexmo.voice.model.NexmoCallResponse;
import com.lpedrosa.nexmo.voice.service.NexmoVoiceOperations;
import com.lpedrosa.nexmo.voice.service.NexmoVoiceTemplate;
import com.lpedrosa.nexmo.voice.service.NexmoVoiceTemplate.ResponseType;
import com.lpedrosa.survey.model.Survey;
import com.lpedrosa.survey.service.SurveyFetcher;
import com.lpedrosa.survey.service.SurveyFetcherImpl;

public final class Application {

    public static final String API_KEY = "de74db5d";
    public static final String API_SECRET = "d69446f1";

    public static final String BASIC_VXML_FETCH_PATH = "0.0.0.0:4567/survey/fetch";

    public static void main(String[] args) throws Exception {

        // Init stuff
        final URI vxmlLocation = Application.class.getResource("/com/lpedrosa/vxml").toURI();
        final SurveyFetcher surveyService = new SurveyFetcherImpl(vxmlLocation);

        final HttpOperations httpOps = new FluentClientWrapper();
        final NexmoVoiceOperations voiceOps = new NexmoVoiceTemplate(API_KEY,
                                                                     API_SECRET,
                                                                     ResponseType.JSON,
                                                                     httpOps);

        // Handlers
        get("/survey/fetch", (req, resp) -> {
            String header = "<h1>List of Surveys</h1>";
            String listOfSurveys = surveyService.fetchSurveys()
                                                .map(Application::prettyPrintSurvey)
                                                .collect(Collectors.joining("<br />"));
            return header + listOfSurveys;
        });

        get("/survey/fetch/:id", (req, resp) -> {
            Try<Long> id = Try.of(() -> Long.parseLong(req.params(":id")));
            String surveyContent = id.map(surveyService::fetch)
                                     .orElse(Optional.of("The id must be a long number"))
                                     .orElse("There is no survey with the specified id");
            resp.type("application/xml");
            return surveyContent;
        });

        get("/survey/call/from/:from/to/:to/sid/:sid", (req, resp) -> {
            String from = req.params(":from");
            String to = req.params(":to");
            Optional<String> surveyPath = Optional.ofNullable(req.params(":sid"))
                                                  .map(BASIC_VXML_FETCH_PATH::concat);

            String msg = Try.of(() -> voiceOps.callAndForwardToVXML(from, to, surveyPath))
                            .map(NexmoCallResponse::toString)
                            .recover(throwable -> String.format("Failed to perform a call: %s", throwable))
                            .orElse("Something went horribly wrong while handling call errors");

            resp.status(HttpStatus.OK_200);
            return msg;
        });

        // TODO make it handle different surveys
        post("/survey/submit", (req, resp) -> {
            System.out.println(req.body());
            resp.status(HttpStatus.OK_200);
            return "Survey submited";
        });
    }

    private static String prettyPrintSurvey(Survey survey) {
       return String.format("id: %s filename: %s", survey.getId(), survey.getFilename());
    }
}
