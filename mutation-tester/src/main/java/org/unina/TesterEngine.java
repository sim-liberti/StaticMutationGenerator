package org.unina;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.reflections.Reflections;
import org.unina.classes.BaseTest;
import org.unina.data.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        System.out.printf("Found %d to apply", (long) mutationsToApply.size());
        int completed = 0;
        for (Mutation mut : mutationsToApply) {
            System.out.printf("\n%d out of %d remaining.", completed, (long) mutationsToApply.size());
            MutationBatch batch = new MutationBatch();
            batch.batchName = mut.name;
            batch.batchId = String.format("mutation_%s_%s_%s", mut.mutation_id, mut.mutation_type, mut.element);
            mut.mutatedFiles = db.getMutatedFiles(mut.uuid);
            mut.applyMutationToRepository();
            nx.resetForNextBuild();

            System.out.println("Mutation " + batch.batchId + " applied.");
            System.out.println("Waiting for the target application to recompile (max 20s)...");

            boolean recompiledOk = nx.waitForRebuild(20);
            if (recompiledOk)
                System.out.println("Application recompiled successfully.");

            for (Class<? extends BaseTest> testClass : classes) {
                TestExecution execution = new TestExecution(mut.name, testClass.getSimpleName(), mut.element);
                if (!recompiledOk){
                    execution.status = TestStatus.NOT_APPLICABLE;
                    execution.errorMessage = "Application did not recompile. Marking test as not applicable.";
                    batch.addExecution(execution);
                    System.out.println("---------------------------------------------");
                    continue;
                }

                System.out.println("Starting test for: " + testClass.getSimpleName());

                Logger.getLogger("org.junit").setLevel(Level.OFF);
                LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                        .selectors(DiscoverySelectors.selectClass(testClass))
                        .build();
                Launcher launcher = LauncherFactory.create();
                SummaryGeneratingListener listener = new SummaryGeneratingListener();
                launcher.registerTestExecutionListeners(listener);
                launcher.execute(request);
                var summary = listener.getSummary();

                if (summary.getTestsFailedCount() == 0) {
                    execution.status = TestStatus.PASSED;
                    batch.addExecution(execution);
                    System.out.println("Test ended: " + testClass.getSimpleName());
                    System.out.println("---------------------------------------------");
                    continue;
                }

                var failures = summary.getFailures();
                if (!failures.isEmpty()) {
                    var failure = failures.get(0);
                    Throwable exception = failure.getException();
                    execution.errorMessage = exception.getMessage()
                            .replace(",", " ");
                    execution.status = (exception instanceof java.lang.AssertionError ||
                            exception instanceof org.openqa.selenium.WebDriverException)
                            ? TestStatus.FAILED
                            : TestStatus.BROKEN;
                }
                batch.addExecution(execution);
                System.out.println("Test ended: " + testClass.getSimpleName());
                System.out.println("---------------------------------------------");
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
        StringBuilder batchCsv = new StringBuilder();
        batchCsv.append("Nome,Identificativo,Locatore,Tag,Stato,Errore");

        for (MutationBatch batch : batches) {
            BatchStatus status = batch.getBatchStatus();

            for (TestExecution exec : batch.getExecutions()) {
                batchCsv.append(String.format("%s,%s,%s,%s,%s,%s\n",
                        batch.batchName, batch.batchId, exec.locatorName, exec.mutatedTag, exec.status.toString(), exec.errorMessage));

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
                    case NOT_TESTED:
                        stats.notTestedCount++;
                        break;
                }
            }
        }

        StringBuilder statsCsv = new StringBuilder();
        statsCsv.append("Locatore,Test totali,Successi,Fragilità,Obsolescenza,Non testati\n");
        for (LocatorStats stats : statsMap.values()) {
            statsCsv.append(stats.toString()).append("\n");
        }
        System.out.println("Executed batches:");
        System.out.println(batchCsv);
        System.out.println("\nTest results:");
        System.out.println(statsCsv);

        saveCsv(batchCsv, Paths.get("output/tests/batches.csv").toString());
        saveCsv(statsCsv, Paths.get("output/tests/stats.csv").toString());
    }

    private static void saveCsv(StringBuilder content, String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {

            writer.write(content.toString());
            System.out.println("File saved: " + path);

        } catch (IOException e) {
            System.err.println("Error while saving " + path);
            e.printStackTrace();
        }
    }

}
