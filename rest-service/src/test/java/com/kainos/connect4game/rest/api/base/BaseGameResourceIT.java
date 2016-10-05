package com.kainos.connect4game.rest.api.base;

import com.kainos.connect4game.rest.Application;
import io.dropwizard.Configuration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;

public class BaseGameResourceIT {

    @ClassRule
    public static final DropwizardAppRule<Configuration> RULE = new DropwizardAppRule<>(Application.class);

}
