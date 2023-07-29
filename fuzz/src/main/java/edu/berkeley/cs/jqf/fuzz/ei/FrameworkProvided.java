package edu.berkeley.cs.jqf.fuzz.ei;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER}) // does this mean classes?

// this is supposed to take a boolean? I'm a lil confused

public @interface FrameworkProvided {

}
