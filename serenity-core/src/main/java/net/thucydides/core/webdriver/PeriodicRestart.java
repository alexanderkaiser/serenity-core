package net.thucydides.core.webdriver;

import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;

public class PeriodicRestart {
    private final int restartFrequency;

    public PeriodicRestart(EnvironmentVariables environmentVariables) {
        this.restartFrequency = ThucydidesSystemProperty.SERENITY_RESTART_BROWSER_FREQUENCY.integerFrom(environmentVariables, 0);

    }

    public boolean forTestNumber(int currentTestNumber) {
        if (restartFrequency == 0) { return false; }
        if (currentTestNumber == 0) { return false; }
        
        return (currentTestNumber % restartFrequency) == 0;
    }
}
