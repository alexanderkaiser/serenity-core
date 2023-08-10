package net.serenitybdd.screenplay;

import net.thucydides.model.domain.TestResult;
import net.thucydides.model.domain.stacktrace.FailureCause;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.core.steps.events.*;
import net.thucydides.core.steps.session.TestSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


public class EventBusInterface {


    private static final Logger LOGGER = LoggerFactory.getLogger(EventBusInterface.class);

    public static void castActor(String name) {
        if (!StepEventBus.getParallelEventBus().isBaseStepListenerRegistered()) {
            return;
        }
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getParallelEventBus().castActor(name);
        }  else {
            TestSession.addEvent(new CastActorEvent(name));
        }

    }

    public void reportStepFailureFor(Performable todo, Throwable e) {
        ExecutedStepDescription taskDescription = ExecutedStepDescription.of(todo.getClass(), "attemptsTo");
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getParallelEventBus().stepFailed(new StepFailure(taskDescription, e));
        }  else {
            List<ScreenshotAndHtmlSource> screenshotList = TestSession.getTestSessionContext().getStepEventBus().takeScreenshots(TestResult.FAILURE);
            TestSession.addEvent(new StepFailedEvent(new StepFailure(taskDescription, e),screenshotList));
        }
    }

    public <T> void reportStepFailureFor(Consequence<T> consequence, Throwable e) {
        ExecutedStepDescription consequenceDescription = ExecutedStepDescription.withTitle(consequence.toString());
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getParallelEventBus().stepFailed(new StepFailure(consequenceDescription, e));
        }  else {
             List<ScreenshotAndHtmlSource> screenshotList = TestSession.getTestSessionContext().getStepEventBus().takeScreenshots(TestResult.FAILURE);
            TestSession.addEvent(new StepFailedEvent(new StepFailure(consequenceDescription, e ), screenshotList));
        }
    }

    public int getRunningStepCount() {
        return StepEventBus.getParallelEventBus().getBaseStepListener().getRunningStepCount();
    }

    public void mergePreviousStep() {
        StepEventBus.getParallelEventBus().mergePreviousStep();
    }

    public void updateOverallResult() {
        if (StepEventBus.getParallelEventBus().isBaseStepListenerRegistered()) {
            if(!TestSession.isSessionStarted()) {
                StepEventBus.getParallelEventBus().updateOverallResults();
            } else {
                TestSession.addEvent(new UpdateOverallResultsEvent());
            }
        }
    }

    public void startQuestion(String title) {
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getParallelEventBus().stepStarted(ExecutedStepDescription.withTitle(title).asAQuestion());
        } else {
            TestSession.addEvent(new StepStartedEvent(ExecutedStepDescription.withTitle(title).asAQuestion()));
        }
    }

    public void finishQuestion() {
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getParallelEventBus().stepFinished();
        } else {
            TestSession.addEvent(new StepFinishedEvent());
        }
    }

    public void reportStepFinished() {
        if (!TestSession.isSessionStarted()) {
            StepEventBus.getParallelEventBus().stepFinished();
        } else {
            TestSession.addEvent(new StepFinishedEvent());
        }

    }

    public void reportStepIgnored() {
        StepEventBus.getParallelEventBus().stepIgnored();
    }

    public void reportStepSkippedFor(Performable todo) {
        ExecutedStepDescription taskDescription = ExecutedStepDescription.of(todo.getClass(), "performAs");
        StepEventBus.getParallelEventBus().stepStarted(taskDescription);
        StepEventBus.getParallelEventBus().stepIgnored();
    }

    public boolean isBaseStepListenerRegistered() {
        return StepEventBus.getParallelEventBus().isBaseStepListenerRegistered();
    }

    public boolean aStepHasFailed() {
        return isBaseStepListenerRegistered() && StepEventBus.getParallelEventBus().getBaseStepListener().aStepHasFailed();
    }

    public boolean aStepHasFailedInTheCurrentExample() {
        return isBaseStepListenerRegistered() && StepEventBus.getParallelEventBus().getBaseStepListener().aStepHasFailedInTheCurrentExample();
    }

    public FailureCause getFailureCause() {
        return StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome().getTestFailureCause();
    }

    public Optional<FailureCause> failureCause() {
        if ((StepEventBus.getParallelEventBus() == null)
                || (!StepEventBus.getParallelEventBus().isBaseStepListenerRegistered())
                || (StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome() == null)) {
            return Optional.empty();
        }
        if (StepEventBus.getParallelEventBus().getBaseStepListener().aStepHasFailed()) {
            if (StepEventBus.getParallelEventBus().getBaseStepListener().latestTestOutcome().isPresent()) {
                return Optional.ofNullable(StepEventBus.getParallelEventBus().getBaseStepListener().getCurrentTestOutcome().getTestFailureCause());
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public boolean shouldIgnoreConsequences() {
        if (StepEventBus.getParallelEventBus().isDryRun()) {
            return true;
        }

        if (StepEventBus.getParallelEventBus().softAssertsActive() && !StepEventBus.getParallelEventBus().currentTestIsSuspended()) {
            return false;
        }
        return (StepEventBus.getParallelEventBus().currentTestIsSuspended() || StepEventBus.getParallelEventBus().aStepInTheCurrentTestHasFailed());
    }

    public void enableSoftAsserts() {
        StepEventBus.getParallelEventBus().enableSoftAsserts();
    }

    public void disableSoftAsserts() {
        StepEventBus.getParallelEventBus().disableSoftAsserts();
    }


    public void assignFactToActor(Actor actor, String fact) {
        if (!StepEventBus.getParallelEventBus().isBaseStepListenerRegistered()) {
            return;
        }
        StepEventBus.getParallelEventBus().getBaseStepListener().latestTestOutcome().ifPresent(
                testOutcome -> testOutcome.assignFact(actor.getName(), fact)
        );
    }

    public void assignAbilityToActor(Actor actor, String ability) {
        if (!StepEventBus.getParallelEventBus().isBaseStepListenerRegistered()) {
            return;
        }
        if (StepEventBus.getParallelEventBus().getBaseStepListener().latestTestOutcome() == null) {
            return;
        }

        StepEventBus.getParallelEventBus().getBaseStepListener().latestTestOutcome().ifPresent(
                testOutcome -> testOutcome.assignAbility(actor.getName(), ability)
        );
    }

}
