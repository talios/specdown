package com.theoryinpractise.specdown;

import org.concordion.api.Resource;
import org.concordion.internal.FileTarget;

import java.io.File;

public class SpecDownTarget extends FileTarget {
    public SpecDownTarget(File basedir) {
        super(basedir);
    }

    @Override
    public File getFile(Resource resource) {
        return resource.getPath().endsWith(".specdown")
                ? super.getFile(new Resource(resource.getPath().replaceAll(".specdown", ".html")))
                : super.getFile(resource);
    }
}
