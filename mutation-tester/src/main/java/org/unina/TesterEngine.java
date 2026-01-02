package org.unina;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.reflections.Reflections;
import org.unina.classes.BaseTest;
import org.unina.data.*;

import java.io.IOException;
import java.util.*;

public class TesterEngine {

    public static void runTests(MutationDatabase db, Config config) throws IOException {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments(
                "--headless",
                "--disable-gpu",
                "--window-size=1920,1200",
                "--no-sandbox",
                "--ignore-certificate-errors");
        FirefoxDriver driver = new FirefoxDriver(options);

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

            for (Class<? extends BaseTest> classe : classes) {
                TestExecution execution = new TestExecution(classe.getSimpleName(), mut.element);
                try {
                    System.out.println("Starting test for: " + classe.getSimpleName());
                    BaseTest action = classe.getDeclaredConstructor().newInstance();
                    action.init("https://192.168.20.4:4200/", driver);
                    action.runTest();
                    execution.status = TestStatus.PASSED;
                } catch (TimeoutException e) {
                    execution.status = TestStatus.FAILED;
                    execution.errorMessage = e.getMessage();
                } catch (Exception e) {
                    execution.status = TestStatus.BROKEN;
                    execution.errorMessage = e.getMessage();
                }
                batch.addExecution(execution);
                System.out.println("Test ended: " + classe.getSimpleName());
                System.out.println("---------------------------------");
            }
            mut.revertMutations();
        }

        driver.quit();
        saveTestResult(history);
    }


    private static void saveTestResult(List<MutationBatch> batches) {
        Map<String, LocatorStats> statsMap = new HashMap<>();

        for (MutationBatch batch : batches) {
            BatchStatus status = batch.getBatchStatus();

            for (TestExecution exec : batch.getExecutions()) {
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

        System.out.println("Type | Total | Success | Fragile | Obsolete");
        System.out.println("-------------------------------------------");
        for (LocatorStats stats : statsMap.values()) {
            System.out.println(stats.toString());
        }
    }
}
