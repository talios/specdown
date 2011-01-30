package com.theoryinpractise.specdown;

import org.concordion.api.Resource;

public class SpecRunnerUtil {

    public Class<?> classForSpecdown(Resource resource) throws ClassNotFoundException {

        String name = resource.getPath().replaceFirst("/", "").replace("/", ".").replaceAll("\\.specdown$", "");
        Class<?> concordionClass;
        try {
            concordionClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            concordionClass = Class.forName(name + "Spec");
        }

        return concordionClass;

    }


}
