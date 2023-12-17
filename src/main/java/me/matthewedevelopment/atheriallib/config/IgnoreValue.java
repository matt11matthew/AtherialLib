package me.matthewedevelopment.atheriallib.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Matthew Eisenberg on 5/20/2018 at 5:25 PM for the project atherialapi
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IgnoreValue {
}
