package com.kainos.connect4game.rest;

import com.kainos.connect4game.rest.api.GameResource;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Application extends io.dropwizard.core.Application<Configuration> {
    static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(new GameResource(new ConcurrentHashMap<>()));

        try {
            new JaxrsOpenApiContextBuilder<>()
                    .openApiConfiguration(new SwaggerConfiguration()
                            .openAPI(new OpenAPI())
                            .prettyPrint(true)
                            .resourcePackages(Set.of(Application.class.getPackage().getName())))
                    .buildContext(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Swagger context", e);
        }
        environment.jersey().register(new OpenApiResource());
    }
}
