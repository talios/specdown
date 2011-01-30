package com.theoryinpractise.specdown;


import org.concordion.api.*;
import org.concordion.internal.ConcordionBuilder;

/**
 * The BasicConcordionRunner simply runs concordion without any JUnit dependencies, it also adds in our custom
 * commands to the system.
 * <p/>
 * By setting the system property concordion.runner.concordion - concordion's link command will use our runner
 * instead of the default one.
 */
public class BasicConcordionRunner implements Runner {

    private SpecRunnerUtil specRunnerUtil = new SpecRunnerUtil();

    public RunnerResult execute(Resource resource, String href) throws Exception {

        Result result = Result.SUCCESS;
        Resource hrefResource = resource.getParent().getRelativeResource(href);
        Class<?> concordionClass;
        try {
            concordionClass = specRunnerUtil.classForSpecdown(resource);
        } catch (ClassNotFoundException e) {
            return new RunnerResult(Result.FAILURE);
        }

        ResultSummary resultSummary = new ConcordionBuilder()
                .build()
                .process(hrefResource, concordionClass.newInstance());

        if (resultSummary.getFailureCount() > 0) {
            result = Result.FAILURE;
        }
        if (resultSummary.getExceptionCount() > 0) {
            result = Result.EXCEPTION;
        }

        return new RunnerResult(result);

    }
}
