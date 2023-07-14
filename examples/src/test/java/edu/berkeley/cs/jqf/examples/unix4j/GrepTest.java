package edu.berkeley.cs.jqf.examples.unix4j;

import com.pholser.junit.quickcheck.From;
import edu.berkeley.cs.jqf.examples.common.AlphaStringGenerator;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.berkeley.cs.jqf.fuzz.ei.FrameworkProvided;
import org.junit.After;
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

//
//        input1 = "a";
//        input2 = "aa";

//        System.out.println("input1: " + input1);
//        System.out.println("input2: " + input2);

        boolean contain = input2.contains(input1);
        // input 2 has input 1 within it

        // need to clear the file, maybe it's easier to create a new file and then delete it every time?

        File input2file = new File("input2.txt");

        byte[] arr = input2.getBytes();
        try {
            Files.write(path, arr);
        } catch (IOException ex) {
            // Print message as exception occurred when
            // invalid path of local machine is passed
            System.out.print("Invalid Path");
        }

        assumeTrue(contain);


//        String out = Unix4j.fromString(input1).grep(input2).toStringResult();
        String out = Unix4j.grep(input1, input2file).toStringResult();
        // out is a string which contains the lines / words of input 2 which contains input 1

        boolean has = (out.equals(""));
        // to prove that grep is working correctly, we want the string out to be non-empty, thus we want bool as to be false

        if(input1.equals("") || input2.equals("")){
            has = false;
        }

//        System.out.println("contain: \"" + contain + "\"");
//        System.out.println("out: \"" + out + "\"");

        assertFalse(has);

        input2file.delete();
    }

    @Fuzz
    public void configTest(@From(AlphaStringGenerator.class) String input1, @From(AlphaStringGenerator.class) String input2, boolean config){

        if (config){
            // out is a string which contains the lines / words of input 2 which contains input 1

            boolean contain = input2.contains(input1);
            // input 2 has input 1 within it

            assumeTrue(contain);
            String out = Unix4j.fromString(input1).grep(Grep.Options.c, input2).toStringResult();
            // out is a string which contains the lines / words of input 2 which contains input 1

            boolean has = (out.equals(""));
            // opposite because of the option

            assertFalse(has);
        } else {

            boolean contain = input2.contains(input1);
            // input 2 has input 1 within it

            assumeTrue(contain);
            String out = Unix4j.fromString(input1).grep(input2).toStringResult();
            // out is a string which contains the lines / words of input 2 which contains input 1
                // if this is words which contain then i understand why there's the error it's throwing, else it doesn't make sense

            boolean has = (out.equals(""));
            // if input 2 contains input 1, then the string out is not empty, which means it's not equal to ""

            assertFalse(has);
            // to prove that grep is working correctly, we want the string out to be non-empty, thus we want bool as to be false
        }
    }
}


