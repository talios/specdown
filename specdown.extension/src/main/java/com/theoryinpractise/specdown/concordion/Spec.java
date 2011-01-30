package com.theoryinpractise.specdown.concordion;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Spec {
    String value();
}
