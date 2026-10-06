package com.kainos.connect4game.rest;

import com.kainos.connect4game.domain.Game;
import com.kainos.connect4game.rest.api.GameResource;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Application extends io.dropwizard.core.Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(Configuration configuration) {
                SwaggerBundleConfiguration swaggerConfiguration = new SwaggerBundleConfiguration();
                swaggerConfiguration.setResourcePackage(getClass().getPackage().getName());
                return swaggerConfiguration;
            }
        });
    }

    public void run(Configuration configuration, Environment environment) {
        List<Game> games = new CopyOnWriteArrayList<>();

        environment.jersey().register(new GameResource(games));
    }
}
