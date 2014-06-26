package com.lpedrosa.app;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.SparkBase;

import com.lpedrosa.common.http.FluentClientWrapper;
import com.lpedrosa.common.http.HttpOperations;
import com.lpedrosa.nexmo.voice.model.NexmoCallResponse;
import com.lpedrosa.nexmo.voice.service.NexmoVoiceOperations;
import com.lpedrosa.nexmo.voice.service.NexmoVoiceTemplate;
import com.lpedrosa.nexmo.voice.service.NexmoVoiceTemplate.ResponseType;
import com.lpedrosa.survey.model.Survey;
import com.lpedrosa.survey.service.SurveyFetcher;
import com.lpedrosa.survey.service.SurveyFetcherImpl;
import com.lpedrosa.util.Try;

public final class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final String DEFAULT_PORT = "9000";

    public static void main(String[] args) throws Exception {

        // Init stuff
        final String host = System.getProperty("service.host", DEFAULT_HOST);
        final String port = System.getProperty("service.port", DEFAULT_PORT);
        final URI vxmlLocation = Optional.ofNullable(System.getProperty("vxml.loc"))
                                         .map(File::new)
                                         .map(File::toURI)
                                         .map(Try::success)
                                         .orElseGet(Application::getLocationFromClasspath)
                                         .orElse(null); // not pretty

        final String apiKey = System.getProperty("api.key");
        final String apiSecret = System.getProperty("api.secret");

        final String baseUrl = System.getProperty("external.url", "");

        final String fullVxmlFetchPath = baseUrl + "/survey/fetch/";
        final String fullErrorPath = baseUrl + "/report/error";
        final String fullStatusPath = baseUrl + "/report/status";

        SparkBase.setIpAddress(host);
        SparkBase.setPort(Integer.parseInt(port));

        final SurveyFetcher surveyService = new SurveyFetcherImpl(vxmlLocation);
        final HttpOperations httpOps = new FluentClientWrapper();
        final NexmoVoiceOperations voiceOps = new NexmoVoiceTemplate(apiKey,
                                                                     apiSecret,
                                                                     ResponseType.JSON,
                                                                     httpOps,
                                                                     Optional.of(fullErrorPath),
                                                                     Optional.of(fullStatusPath));

        log.info("Service start up done.");
        log.info("Initializing the endpoints.");

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
                                                  .map(fullVxmlFetchPath::concat);

            String msg = Try.of(() -> voiceOps.callAndForwardToVXML(from, to, surveyPath))
                            .map(NexmoCallResponse::toString)
                            .recover(throwable -> String.format("Failed to perform a call: %s", throwable))
                            .orElse("Something went horribly wrong while handling call errors");

            resp.status(HttpStatus.SC_OK);
            return msg;
        });

        // TODO make it handle different surveys
        post("/survey/submit", (req, resp) -> {
            System.out.println(req.body());
            resp.status(HttpStatus.SC_OK);
            return "Survey submited";
        });


        // Report handlers
        post("/report/error", (req, resp) -> {
            log.info("ERROR REPORT POST");
            printAllRequestInfo(req);

            resp.status(HttpStatus.SC_OK);
            return "OK";
        });

        get("/report/error", (req, resp) ->{
            log.info("ERROR REPORT GET");
            printAllRequestInfo(req);

            resp.status(HttpStatus.SC_OK);
            return "OK";
        });
        post("/report/status", (req, resp) -> {
            log.info("STATUS REPORT POST");
            printAllRequestInfo(req);

            resp.status(HttpStatus.SC_OK);
            return "OK";
        });
        get("/report/status", (req, resp) -> {
            log.info("STATUS REPORT GET");
            printAllRequestInfo(req);

            resp.status(HttpStatus.SC_OK);
            return "OK";
        });
    }

    private static void printAllRequestInfo(Request req) {
        System.out.println("path: " + req.pathInfo());
        System.out.println("query params:");
        req.queryMap().toMap()
                      .forEach((name, value) -> System.out.println(name + ":" + value));
        System.out.println("headers:");
        req.headers().stream()
                     .map(headerName -> Arrays.asList(headerName, req.headers(headerName)))
                     .forEach(Application::printHeader);
        System.out.println("body:");
        System.out.println(req.body());
    }

    private static void printHeader(List<String> nameAndValue) {
        System.out.println(nameAndValue.get(0) + " : " + nameAndValue.get(1));
    }

    private static Try<URI> getLocationFromClasspath() {
        return Try.of(() -> Application.class.getResource("/com/lpedrosa/vxml").toURI());
    }

    private static String prettyPrintSurvey(Survey survey) {
       return String.format("id: %s filename: %s", survey.getId(), survey.getFilename());
    }
}
