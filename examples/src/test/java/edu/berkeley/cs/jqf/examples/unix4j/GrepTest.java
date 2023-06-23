package edu.berkeley.cs.jqf.examples.unix4j;

import com.pholser.junit.quickcheck.From;
import edu.berkeley.cs.jqf.examples.common.AlphaStringGenerator;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.unix4j.Unix4j;
import org.unix4j.builder.Unix4jCommandBuilder;
import org.unix4j.io.Output;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(JQF.class)
public class GrepTest {

    private Unix4jCommandBuilder unix4j;
    @Fuzz(repro="target/fuzz-results/edu.berkeley.cs.jqf.examples.unix4j.GrepTest/testWithStringGenerator/failures/id_000000")
    public void testWithStringGenerator(@From(AlphaStringGenerator.class) String input1, @From(AlphaStringGenerator.class) String input2){

        // valid regex ? (in an assume?)

        boolean contain = input2.contains(input1);
        // input 2 has input 1 within it

        assumeTrue(contain);
        String out = Unix4j.fromString(input1).grep(input2).toStringResult();
        // pattern = input1

//        System.out.println(out);

        boolean has = (out.equals(""));
        // if it contains, then the output contains smth, which means it's not equal to ""
        // try and make then the same (input 2 contains input 1, check the orderings)

        assertFalse(has); //idk if this logic makes sense, but i will deal with in a moment
    }
}

