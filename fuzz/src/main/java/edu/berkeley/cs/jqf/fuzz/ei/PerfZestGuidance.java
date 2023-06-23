package edu.berkeley.cs.jqf.fuzz.ei;

import edu.berkeley.cs.jqf.fuzz.afl.AFLGuidance;
import edu.berkeley.cs.jqf.fuzz.guidance.Result;
import edu.berkeley.cs.jqf.fuzz.util.IOUtils;
import edu.berkeley.cs.jqf.instrument.tracing.FastCoverageSnoop;
import janala.instrument.FastCoverageListener;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PerfZestGuidance extends ZestGuidance {
    public PerfZestGuidance(String testName, Duration duration, Long trials, File outputDirectory, Random sourceOfRandomness) throws IOException {
        super(testName, duration, trials, outputDirectory,sourceOfRandomness);
    }

    public PerfZestGuidance(String testName, Duration duration, Long trials, File outputDirectory, File[] seedInputFiles, Random sourceOfRandomness) throws IOException {
        super(testName, duration, trials, outputDirectory, seedInputFiles, sourceOfRandomness);
    }

    /**
     * Creates a new Zest guidance instance with seed input directory and optional
     * duration, optional trial limit, an possibly deterministic PRNG.
     *
     * @param testName the name of test to display on the status screen
     * @param duration the amount of time to run fuzzing for, where
     *                 {@code null} indicates unlimited time.
     * @param trials   the number of trials for which to run fuzzing, where
     *                 {@code null} indicates unlimited trials.
     * @param outputDirectory the directory where fuzzing results will be written
     * @param seedInputDir the directory containing one or more input files to be used as initial inputs
     * @param sourceOfRandomness      a pseudo-random number generator
     * @throws IOException if the output directory could not be prepared
     */
    public PerfZestGuidance(String testName, Duration duration, Long trials, File outputDirectory, File seedInputDir, Random sourceOfRandomness) throws IOException {
        super(testName, duration, trials, outputDirectory, seedInputDir, sourceOfRandomness);
    }

    /**
     * Creates a new Zest guidance instance with seed inputs and
     * optional duration.
     *
     * @param testName the name of test to display on the status screen
     * @param duration the amount of time to run fuzzing for, where
     *                 {@code null} indicates unlimited time.
     * @param outputDirectory the directory where fuzzing results will be written
     * @param seedInputDir the directory containing one or more input files to be used as initial inputs
     * @throws IOException if the output directory could not be prepared
     */
    public PerfZestGuidance(String testName, Duration duration, File outputDirectory, File seedInputDir) throws IOException {
        super(testName, duration, outputDirectory, seedInputDir);
    }

    /**
     * Creates a new Zest guidance instance with seed inputs and
     * optional duration.
     *
     * @param testName the name of test to display on the status screen
     * @param duration the amount of time to run fuzzing for, where
     *                 {@code null} indicates unlimited time.
     * @param outputDirectory the directory where fuzzing results will be written
     * @throws IOException if the output directory could not be prepared
     */
    public PerfZestGuidance(String testName, Duration duration, File outputDirectory) throws IOException {
        super(testName, duration, outputDirectory);
    }

    /**
     * Creates a new Zest guidance instance with seed inputs and
     * optional duration.
     *
     * @param testName the name of test to display on the status screen
     * @param duration the amount of time to run fuzzing for, where
     *                 {@code null} indicates unlimited time.
     * @param outputDirectory the directory where fuzzing results will be written
     * @throws IOException if the output directory could not be prepared
     */
    public PerfZestGuidance(String testName, Duration duration, File outputDirectory, File[] seedFiles) throws IOException {
        super(testName, duration, outputDirectory, seedFiles);
    }

    /* concept of what other functions may need overriding (both big and small)
    getTitle

     */

    @Override
    protected List<String> checkSavingCriteriaSatisfied(Result result){
        int nonZeroBefore = totalCoverage.getNonZeroCount();
        int validNonZeroBefore = validCoverage.getNonZeroCount();

        // save of a branch has higher hit
        String edgeHits = totalCoverage.newMaxEdgeHits(runCoverage);

        if (result == Result.SUCCESS) {
            validCoverage.updateBits(runCoverage);
        }

        // Coverage after
        int nonZeroAfter = totalCoverage.getNonZeroCount();
        if (nonZeroAfter > maxCoverage) {
            maxCoverage = nonZeroAfter;
        }
        int validNonZeroAfter = validCoverage.getNonZeroCount();

        // Possibly save input
        List<String> reasonsToSave = new ArrayList<>();

        int before = totalCoverage.getTotalMax();

        // save if total branch hits is larger
        // change per input instead of overall ? new variable to keep that
        if(totalCoverage.totalBranchHits(runCoverage)){
            int after = totalCoverage.getTotalMax();
            reasonsToSave.add("+total. before: " + before + " after: " + after);
        }

        // Save if new total coverage found
        if (nonZeroAfter > nonZeroBefore) {
            reasonsToSave.add("+cov");
        }

        // Save if new valid coverage is found
        if (this.validityFuzzing && validNonZeroAfter > validNonZeroBefore) {
            reasonsToSave.add("+valid");
        }


        if (edgeHits.length() > 0) {
            reasonsToSave.add("+edge");
        }

        return reasonsToSave;
    }

}
