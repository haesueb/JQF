package edu.berkeley.cs.jqf.fuzz.repro;

import edu.berkeley.cs.jqf.fuzz.junit.TrialRunner;
import edu.berkeley.cs.jqf.fuzz.util.CoverageFactory;
import edu.berkeley.cs.jqf.fuzz.util.ICoverage;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReproConfigGuidance extends ReproGuidance{
    public ReproConfigGuidance(File[] inputFiles, File traceDir) throws IOException {
        super(inputFiles, traceDir);
    }

    public ReproConfigGuidance(File inputFile, File traceDir) throws IOException{
        super(inputFile, traceDir);
    }


    protected ICoverage newCoverage = CoverageFactory.newInstance();
    protected ICoverage runCoverage = CoverageFactory.newInstance();
    protected ICoverage totalCoverage = CoverageFactory.newInstance();

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

        String opp = totalCoverage.totalBranchOpposite(runCoverage,newCoverage);
        // these are not updating correctly, which is why the repro isn't working as intended

        System.out.println("branch counts ->" + opp);
        assertTrue(opp.equals(""));

    }


}
