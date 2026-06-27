package io.github.arieslab.pages;

import io.github.arieslab.WicketApplication;
import io.github.arieslab.core.testsmelldetector.testsmell.AbstractSmell;
import io.github.arieslab.pages.base.ImprimirPage;
import org.apache.wicket.markup.html.basic.Label;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Displays the source code of a test smell detector class for reference.
 */
public class SourcePage extends ImprimirPage {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(SourcePage.class.getName());

    /**
     * Builds a page showing the source code of the given test smell implementation.
     *
     * @param testSmell the test smell detector instance whose source will be displayed
     */
    public SourcePage(AbstractSmell testSmell) {

        add(new Label("title", "Source: " + testSmell.getSmellName()));

        String posURI = testSmell.getClass().getCanonicalName();
        posURI = posURI.replace(".", File.separator);
        posURI = posURI + ".java";

        String sourceString = readLineByLineJava8(WicketApplication.JNOSE_PATH + "/src/main/java/" + posURI);

        add(new Label("source",sourceString).setEscapeModelStrings(false));
    }

    /**
     * Reads a file line by line into a single string with line breaks.
     *
     * @param filePath the path to the file
     * @return the file content as a string
     */
    private static String readLineByLineJava8(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to read source file: " + filePath, e);
        }

        return contentBuilder.toString();
    }
}
