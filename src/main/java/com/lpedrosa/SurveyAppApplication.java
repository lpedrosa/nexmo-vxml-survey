package com.lpedrosa;

import com.lpedrosa.resources.SimpleResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class SurveyAppApplication extends Application<SurveyAppConfig> {
    public static void main(String[] args) throws Exception {
        new SurveyAppApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<SurveyAppConfig> bootstrap) {
        // Nothing so far...
    }

    @Override
    public void run(SurveyAppConfig configuration, Environment environment) throws Exception {
        // Register resource
        environment.jersey().register(new SimpleResource());
    }
}
