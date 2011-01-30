package com.theoryinpractise.specdown;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.theoryinpractise.specdown.concordion.SpecExtension;
import org.concordion.api.Resource;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;
import org.pegdown.PegDownProcessor;

import java.io.*;
import java.util.Set;

public class SpecdownProcessor {

    private String srcDirectory;

    public SpecdownProcessor(String srcDirectory) {
        this.srcDirectory = srcDirectory;
    }

    public void process(final String spec) throws IOException {
        new SpecdownResourceProcessor(new Resource(spec)).process();
    }

    public void processAll() throws IOException {

        Set<Resource> failedResources = Sets.newHashSet();

        for (SpecdownResourceProcessor specdownResourceProcessor : findAll()) {
            try {
                specdownResourceProcessor.process();
            } catch (AssertionError e) {
                failedResources.add(specdownResourceProcessor.getResource());
            }
        }

        if (!failedResources.isEmpty()) {

            StringBuilder sb = new StringBuilder();
            sb.append("### Failed Specifications\n\n");

            String baseDir = System.getProperty("concordion.output.dir", "/tmp");

            SpecDownTarget specDownTarget = new SpecDownTarget(new File(baseDir));

            for (Resource failedResource : failedResources) {
                sb
                        .append(" * [")
                        .append(failedResource.getName().substring(0, failedResource.getName().indexOf(".")))
                        .append("](")
                        .append(specDownTarget.getFile(failedResource).toURI().toURL().toString())
                        .append(")\n");
            }

            String html
                    = "<html><body>"
                    + new PegDownProcessor().markdownToHtml(sb.toString())
                    + "</body></html>";

            Files.write(html, new File(baseDir, "failedspecs.html"), Charsets.UTF_8);


        }


    }


    public Set<SpecdownResourceProcessor> findAll() throws FileNotFoundException {
        return findAll(new File(srcDirectory));
    }

    private Set<SpecdownResourceProcessor> findAll(File directory) throws FileNotFoundException {

        Set<SpecdownResourceProcessor> processors = Sets.newHashSet();

        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    processors.addAll(findAll(file));
                } else if (file.getName().endsWith(".specdown")) {
                    String path = file.getPath().substring(srcDirectory.length());
                    processors.add(new SpecdownResourceProcessor(new Resource(path)));
                }
            }
        } else {
            throw new FileNotFoundException("Path " + directory.getPath() + " not found.");
        }

        return processors;
    }


    public class SpecdownResourceProcessor {

        private Resource resource;

        public SpecdownResourceProcessor(Resource resource) {
            this.resource = resource;
        }

        public Resource getResource() {
            return resource;
        }

        public ResultSummary process() throws IOException {

            Object fixture;
            try {
                fixture = new SpecRunnerUtil().classForSpecdown(resource).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Can't instantiate fixture class for resource: " + resource.getName(), e);
            }

            System.setProperty("concordion.runner.concordion", BasicConcordionRunner.class.getName());
            System.setProperty("concordion.extensions", SpecExtension.class.getName());

            ResultSummary resultSummary = new ConcordionBuilder()
                    .withSource(new SpecDownSource(srcDirectory))
                    .withTarget(new SpecDownTarget(new File(System.getProperty("concordion.output.dir", "/tmp"))))
                    .build()
                    .process(resource, fixture);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resultSummary.print(new PrintStream(baos), fixture);
            resultSummary.assertIsSatisfied(fixture);

            return resultSummary;
        }

    }


}
