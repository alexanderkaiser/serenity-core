package net.thucydides.junit.listeners;

import net.thucydides.model.logging.LoggingLevel;
import net.thucydides.model.domain.DataTable;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.domain.TestResult;
import net.thucydides.model.screenshots.ScreenshotAndHtmlSource;
import net.thucydides.model.statistics.TestCount;
import net.thucydides.model.steps.ExecutedStepDescription;
import net.thucydides.model.steps.StepFailure;
import net.thucydides.model.steps.StepListener;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static net.thucydides.model.ThucydidesSystemProperty.SERENITY_DISPLAY_TEST_NUMBERS;

public class TestCountListener implements StepListener {

    private final Logger logger;
    private final EnvironmentVariables environmentVariables;
    private final TestCount testCount;
    private final boolean reportTestCount;

    protected TestCountListener(EnvironmentVariables environmentVariables, Logger logger, TestCount testCount) {
        this.logger = logger;
        this.environmentVariables = environmentVariables;
        this.testCount = testCount;
        this.reportTestCount = SERENITY_DISPLAY_TEST_NUMBERS.booleanFrom(environmentVariables,false);
    }

    public TestCountListener(EnvironmentVariables environmentVariables, TestCount testCount) {
        this(environmentVariables, LoggerFactory.getLogger(""), testCount);
    }

    protected Logger getLogger() {
        return logger;
    }

    public void testSuiteStarted(Class<?> storyClass) {
    }


    public void testSuiteStarted(Story storyOrFeature) {
    }


    public void testSuiteFinished() {
    }


    public void testStarted(String description) {
        int currentTestCount = testCount.getNextTest();
        if (reportTestCount && LoggingLevel.definedIn(environmentVariables).isAtLeast(LoggingLevel.VERBOSE)) {
            getLogger().info("TEST NUMBER: {}", currentTestCount);
        }
    }

    @Override
    public void testStarted(String description, String id) {
        testStarted(description);
    }

    @Override
    public void testStarted(String description, String id, ZonedDateTime startTime) {

    }


    public void testFinished(TestOutcome result) {
    }

    @Override
    public void testFinished(TestOutcome result, boolean isInDataDrivenTest, ZonedDateTime finishTime) {

    }

    public void testRetried() {
    }

    public void stepStarted(ExecutedStepDescription description) {
    }


    public void skippedStepStarted(ExecutedStepDescription description) {
    }


    public void stepFailed(StepFailure failure) {
    }

    @Override
    public void stepFailed(StepFailure failure, List<ScreenshotAndHtmlSource> screenshotList) {

    }


    public void lastStepFailed(StepFailure failure) {
    }


    public void stepIgnored() {
    }

    public void stepPending() {
    }


    public void stepPending(String message) {
    }


    public void stepFinished() {
    }

    @Override
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList) {

    }

    @Override
    public void stepFinished(List<ScreenshotAndHtmlSource> screenshotList, ZonedDateTime time) {

    }


    public void testFailed(TestOutcome testOutcome, Throwable cause) {
    }


    public void testIgnored() {
    }

    @Override
    public void testSkipped() {

    }

    @Override
    public void testAborted() {

    }

    @Override
    public void testPending() {

    }

    @Override
    public void testIsManual() {

    }


    public void notifyScreenChange() {
    }

    public void useExamplesFrom(DataTable table) {
    }

    @Override
    public void addNewExamplesFrom(DataTable table) {

    }

    public void exampleStarted(Map<String, String> data) {
    }

    public void exampleFinished() {
    }

    @Override
    public void assumptionViolated(String message) {
    }

    @Override
    public void testRunFinished() {

    }

    @Override
    public void takeScreenshots(List<ScreenshotAndHtmlSource> screenshots) {

    }

    @Override
    public void takeScreenshots(TestResult testResult, List<ScreenshotAndHtmlSource> screenshots) {

    }
}
