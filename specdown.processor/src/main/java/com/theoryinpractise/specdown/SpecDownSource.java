package com.theoryinpractise.specdown;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import org.concordion.api.Resource;
import org.concordion.api.Source;
import org.pegdown.PegDownProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SpecDownSource implements Source {

    private String srcDirectory;

    public SpecDownSource(String srcDirectory) {
        this.srcDirectory = srcDirectory;
    }

    public InputStream createInputStream(Resource resource) throws IOException {

        String title = resource.getName().replaceAll("\\.specdown", "");
        String specHtmlBody = generateConcordionSpecificationFromMarkdown(resource);

        String html
                = "<html xmlns:concordion=\"http://www.concordion.org/2007/concordion\""
                + "      xmlns:spec=\"http://www.theoryinpractise.com/2011/concordion/spec\">"
                + "<head><title>" + title + "</title></head><body>"
                + specHtmlBody
                + "</body></html>";

        return ByteStreams.newInputStreamSupplier(html.getBytes("UTF-8")).getInput();
    }

    private String generateConcordionSpecificationFromMarkdown(Resource resource) throws IOException {
        String resourcePath = resource.getPath();

        String specSrc = Resources.toString(new File(srcDirectory, resourcePath).toURI().toURL(), Charsets.UTF_8);

        String markdown = specSrc
                .replaceAll("\\s=> (.*) <=", " <span spec:spec=\"\">$1</span>")
                .replaceAll("\\s=> (.*)(\\.)", " * <span spec:spec=\"\">$1</span>$2");

        return new PegDownProcessor().markdownToHtml(markdown);
    }

    public boolean canFind(Resource resource) {
        String resourcePath = resource.getPath();
        return SpecDownSource.class.getResource(resourcePath) != null;
    }

}
