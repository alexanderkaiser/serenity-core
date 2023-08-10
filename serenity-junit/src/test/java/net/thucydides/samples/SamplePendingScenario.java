package net.thucydides.samples;

import net.serenitybdd.annotations.Pending;
import net.serenitybdd.annotations.Steps;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SerenityRunner.class)
public class SamplePendingScenario {
    
    @Steps
    public SampleNonWebSteps steps;

    @Test
    @Pending
    public void happy_day_scenario() throws Throwable {
        steps.stepThatSucceeds();
        steps.anotherStepThatSucceeds();
    }
}
