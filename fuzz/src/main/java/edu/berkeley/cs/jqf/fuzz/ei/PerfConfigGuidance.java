package edu.berkeley.cs.jqf.fuzz.ei;

import edu.berkeley.cs.jqf.fuzz.guidance.Result;
import edu.berkeley.cs.jqf.fuzz.junit.TrialRunner;
import edu.berkeley.cs.jqf.fuzz.util.CoverageFactory;
import edu.berkeley.cs.jqf.fuzz.util.ICoverage;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertFalse;

public class PerfConfigGuidance extends PerfZestGuidance {

    protected ICoverage newCoverage = CoverageFactory.newInstance();

    protected ICoverage diffCoverage = CoverageFactory.newInstance();

    public PerfConfigGuidance(String testName, Duration duration, Long trials, File outputDirectory, Random sourceOfRandomness) throws IOException {
        super(testName, duration, trials, outputDirectory,sourceOfRandomness);
    }

    public PerfConfigGuidance(String testName, Duration duration, Long trials, File outputDirectory, File seedInputDir, Random sourceOfRandomness) throws IOException {
        super(testName, duration, trials, outputDirectory, seedInputDir, sourceOfRandomness);
    }

    @Override
    public void run(TestClass testClass, FrameworkMethod method, Object[] args) throws Throwable {

        args[args.length - 1] = true;
        new TrialRunner(testClass.getJavaClass(), method, args).run();
        newCoverage = runCoverage.copy(); // is it total or run here?

        // should this be an interface or a coverage?
        // try to print things

        runCoverage.clear();

        args[args.length - 1] = false;
        new TrialRunner(testClass.getJavaClass(), method, args).run();


        assertFalse(totalCoverage.totalBranchOpposite(runCoverage,newCoverage));
        // do i need to make it so this throws something?

    }

    @Override
    protected List<String> checkSavingCriteriaSatisfied(Result result) {
        List<String> reasonsToSave = new ArrayList<>();

        // updating the total and valid coverage "arrays" with new values of maximum edge count differences
        // the number of non-zeros will likely still deal with the differences, unless they have the same edge counts ...
        // oh this is a case i need to fix, maybe instead of using total and valid, i create new variables ...?


        int nonZeroBefore = totalCoverage.getNonZeroCount();
        int validNonZeroBefore = validCoverage.getNonZeroCount();

        // Update total coverage
        totalCoverage.updateBits(runCoverage);
        if (result == Result.SUCCESS) {
            validCoverage.updateBits(runCoverage);
        }

        // Coverage after
        int nonZeroAfter = totalCoverage.getNonZeroCount();
        if (nonZeroAfter > maxCoverage) {
            maxCoverage = nonZeroAfter;
        }
        int validNonZeroAfter = validCoverage.getNonZeroCount();

        // Save if new total coverage found
//        if (nonZeroAfter > nonZeroBefore) {
//            reasonsToSave.add("+cov");
//        }
//
//        // Save if new valid coverage is found (done the same as coverage, just with those inputs which result in successes)
//        if (this.validityFuzzing && validNonZeroAfter > validNonZeroBefore) {
//            reasonsToSave.add("+valid");
//        }

        // want to check if there's a new total of differences
        if(totalCoverage.totalBranchDiff(runCoverage, newCoverage)){
            reasonsToSave.add("+total");
        }

        // slow then fast
        boolean newDiff = totalCoverage.newMaxEdgeDiff(runCoverage, newCoverage);

        // check if there's new difference between the two types
        if (newDiff){
            reasonsToSave.add("+diff");
        }

        boolean negDiff = totalCoverage.totalBranchOppositeMax(runCoverage,newCoverage);

        if (negDiff){
            reasonsToSave.add("+negDiff");
        }


        return reasonsToSave;
    }

    @Override
    protected void completeCycle() {
        // Increment cycle count
        cyclesCompleted++;
        infoLog("\n# Cycle " + cyclesCompleted + " completed.");

        // Go over all inputs and do a sanity check (plus log)
        infoLog("Here is a list of favored inputs:");
        int sumResponsibilities = 0;
        numFavoredLastCycle = 0;
        for (Input input : savedInputs) {
            if (input.isFavored()) {
                int responsibleFor = input.responsibilities.size();
                infoLog("Input %d is responsible for %d branches", input.id, responsibleFor);
                sumResponsibilities += responsibleFor;
                numFavoredLastCycle++;
            }
        }
        int totalCoverageCount = totalCoverage.getNonZeroCount();
        infoLog("Total %d branches covered", totalCoverageCount);
        if (sumResponsibilities != totalCoverageCount) {
            if (multiThreaded) {
                infoLog("Warning: other threads are adding coverage between test executions");
            }
        }

        // Break log after cycle
        infoLog("\n\n\n");
    }
}

// do i need to override the computeResponsibilities as well? what are responsibilities
// another thing, i didn't consider valid coverage here at all, should i do that?
