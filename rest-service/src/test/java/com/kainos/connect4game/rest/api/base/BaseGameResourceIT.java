package com.kainos.connect4game.rest.api.base;

import com.kainos.connect4game.rest.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(DropwizardExtensionsSupport.class)
public abstract class BaseGameResourceIT {

    public static final DropwizardAppExtension<Configuration> RULE = new DropwizardAppExtension<>(Application.class);

}
