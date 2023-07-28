package edu.berkeley.cs.jqf.examples.unix4j;

import com.pholser.junit.quickcheck.From;
import edu.berkeley.cs.jqf.examples.common.AlphaStringGenerator;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.berkeley.cs.jqf.fuzz.ei.FrameworkProvided;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kohsuke.args4j.Argument;
import org.unix4j.Unix4j;
import org.unix4j.builder.Unix4jCommandBuilder;
import org.unix4j.io.Output;
import org.unix4j.unix.Grep;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(JQF.class)
public class GrepTest {
    private Unix4jCommandBuilder unix4j;
    Path path = Paths.get("/Users/haesueb/Desktop/JQF-GrepTest/examples/input2.txt");
    @Fuzz
    public void testWithStringGenerator(@From(AlphaStringGenerator.class) String input1, @From(AlphaStringGenerator.class) String input2){


        boolean contain = input2.contains(input1);

        File input2file = new File("input2.txt");

        byte[] arr = input2.getBytes();
        try {
            Files.write(path, arr);
        } catch (IOException ex) {
            System.out.print("Invalid Path");
        }

        assumeTrue(contain);


        String out = Unix4j.grep(input1, input2file).toStringResult();
        // out is a string which contains the lines / words of input 2 which contains input 1

        boolean has = (out.equals(""));
        // to prove that grep is working correctly, we want the string out to be non-empty, thus we want bool as to be false

        if(input1.equals("") || input2.equals("")){
            has = false;
        }

        assertFalse(has);
        input2file.delete();
    }

    @Fuzz (repro = "examples/target/fuzz-results/edu.berkeley.cs.jqf.examples.unix4j.GrepTest/configTest/failures/id_000000")
    //ArbitraryLengthStringGenerator
    // a repro will let me see the number of branches (this is b/c the vars you can see with the debugger)
    public void configTest(@From(AlphaStringGenerator.class) String input1, @From(AlphaStringGenerator.class) String input2, boolean config){
        Assume.assumeFalse(input1.isEmpty());
        // try {
        // grep(s1, s2); }
        // catch (RegexException e) {
        // Assume.assumeNoException(e);
        //
//        Assume.assumeNoException();
        if (config){
            boolean contain = input2.contains(input1);

            File input2file = new File("input2.txt");

            byte[] arr = input2.getBytes();
            try {
                Files.write(path, arr);
            } catch (IOException ex) {
                System.out.print("Invalid Path");
            }

            assumeTrue(contain);


            String out = Unix4j.grep(Grep.Options.F, input1, input2file).toStringResult();
            // out is a string which contains the lines / words of input 2 which contains input 1

            boolean has = (out.equals(""));
            // to prove that grep is working correctly, we want the string out to be non-empty, thus we want bool as to be false

            if(input1.equals("") || input2.equals("")){
                has = false;
            }

            assertFalse(has);
            input2file.delete();
        } else {

            boolean contain = input2.contains(input1);

            File input2file = new File("input2.txt");

            byte[] arr = input2.getBytes();
            try {
                Files.write(path, arr);
            } catch (IOException ex) {
                System.out.print("Invalid Path");
            }

            assumeTrue(contain);


            String out = Unix4j.grep(input1, input2file).toStringResult();
            // out is a string which contains the lines / words of input 2 which contains input 1

            boolean has = (out.equals(""));
            // to prove that grep is working correctly, we want the string out to be non-empty, thus we want bool as to be false

            if(input1.equals("") || input2.equals("")){
                has = false;
            }

            assertFalse(has);
            input2file.delete();
        }
    }
}


