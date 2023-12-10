package me.matthewedevelopment.atheriallib.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Matthew Eisenberg on 6/27/2018 at 3:51 PM for the project atherialapi
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();
    String[] aliases() default {};
    String description() default "none";
    String usage() default "";
}