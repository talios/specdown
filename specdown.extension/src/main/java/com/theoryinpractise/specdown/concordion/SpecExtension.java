package com.theoryinpractise.specdown.concordion;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

public class SpecExtension implements ConcordionExtension {

    public static final java.lang.String NAMESPACE_SPEC = "http://www.theoryinpractise.com/2011/concordion/spec";

    public void addTo(ConcordionExtender concordionExtender) {
        concordionExtender.withCommand(NAMESPACE_SPEC, "spec", new SpecCommand());
    }

}
