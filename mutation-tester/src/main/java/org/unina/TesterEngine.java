package org.unina;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.openqa.selenium.WebDriverException;
import org.reflections.Reflections;
import org.unina.classes.BaseTest;
import org.unina.data.*;

import java.io.IOException;
import java.util.*;

public class TesterEngine {

    public static void runTests(MutationDatabase db, Config config) throws IOException {
        // Initializing Angular application
        NxConsoleWrapper nx = new NxConsoleWrapper(false);
        nx.start(config.repositoryRootPath);

        System.out.println("Starting TypeScript Application (npm start - nx serve)...");
        if (!nx.waitForRebuild(120)) {
            System.err.println("Cannot start Angular application. Exiting...");
            nx.stop();
            return;
        }
        System.out.println("Angular application started...");

        System.out.println("Looking for test classes to execute...");
        Reflections reflections = new Reflections("org.unina.classes");
        Set<Class<? extends BaseTest>> classes = reflections.getSubTypesOf(BaseTest.class);

        if (classes.isEmpty()) {
            System.out.println("No elegible class found.");
        }

        List<MutationBatch> history = new ArrayList<>();
        Set<Mutation> mutationsToApply = db.getPendingMutations();

        for (Mutation mut : mutationsToApply) {
            MutationBatch batch = new MutationBatch();
            batch.batchId = String.format("mutation_%s_%s_%s", mut.mutation_id, mut.mutation_type, mut.element);
            mut.mutatedFiles = db.getMutatedFiles(mut.uuid);
            mut.applyMutationToRepository();
            nx.resetForNextBuild();

            System.out.println("Mutation applied.");
            System.out.println("Waiting for the target application to recompile (max 20s)...");

            boolean recompiledOk = nx.waitForRebuild(20);
            if (recompiledOk)
                System.out.println("Application recompiled successfully.");

            for (Class<? extends BaseTest> testClass : classes) {
                TestExecution execution = new TestExecution(testClass.getSimpleName(), mut.element);
                if (!recompiledOk){
                    execution.status = TestStatus.BROKEN;
                    execution.errorMessage = "Application did not recompile. Marking test as broken.";
                    batch.addExecution(execution);
                    continue;
                }

                System.out.println("Starting test for: " + testClass.getSimpleName());
                Result testResult = JUnitCore.runClasses(testClass);
                if (testResult.wasSuccessful()) {
                    execution.status = TestStatus.PASSED;
                    batch.addExecution(execution);
                    System.out.println("Test ended: " + testClass.getSimpleName());
                    continue;
                }

                Failure failure = testResult.getFailures().get(0);
                Throwable exception = failure.getException();
                execution.errorMessage = failure.getDescription().toString();
                execution.status = (exception instanceof java.lang.AssertionError ||
                        exception instanceof WebDriverException)
                        ? TestStatus.FAILED
                        : TestStatus.BROKEN;

                batch.addExecution(execution);
                System.out.println("Test ended: " + testClass.getSimpleName());
            }
            mut.revertMutations();
            history.add(batch);
        }

        WebDriverFactory.quitDriver();
        nx.stop();
        saveTestResult(history);
    }

    private static void saveTestResult(List<MutationBatch> batches) {
        Map<String, LocatorStats> statsMap = new HashMap<>();

        for (MutationBatch batch : batches) {
            BatchStatus status = batch.getBatchStatus();

            for (TestExecution exec : batch.getExecutions()) {
                String batchCsvRecord = String.format("%s;%s;%s;%s;%s",
                    batch.batchId, exec.locatorName, exec.mutatedTag, exec.status.toString(), exec.errorMessage);

                LocatorStats stats = statsMap.computeIfAbsent(
                        exec.locatorName,
                        LocatorStats::new
                );

                switch (status) {
                    case OBSOLETE:
                        stats.obsolescenceFailureCount++;
                        break;
                    case FRAGILE:
                        if (exec.isPassed())
                            stats.successCount++;
                        else
                            stats.fragilityFailureCount++;
                        break;
                    case ROBUST:
                        stats.successCount++;
                        break;
                }
            }
        }

        System.out.println("-------------------------------------------");
        System.out.println("Type | Total | Success | Fragile | Obsolete");
        System.out.println("-------------------------------------------");
        for (LocatorStats stats : statsMap.values()) {
            System.out.println(stats.toString());
        }
    }


}
