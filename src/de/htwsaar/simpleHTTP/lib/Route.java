package de.htwsaar.simpleHTTP.lib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    public String method() default "GET";
    public String path();
}
