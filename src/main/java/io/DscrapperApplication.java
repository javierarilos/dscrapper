package io;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DscrapperApplication extends Application<DscrapperConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DscrapperApplication().run(args);
    }

    @Override
    public String getName() {
        return "Dscrapper";
    }

    @Override
    public void initialize(final Bootstrap<DscrapperConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final DscrapperConfiguration config,
                    final Environment environment) {

        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(config.getTemplate());

        final HelloWorldResource resource = new HelloWorldResource(
                  config.getTemplate(),
                  config.getDefaultName()
        );

        environment.jersey().register(resource);
        environment.healthChecks().register("template", healthCheck);

    }

}
