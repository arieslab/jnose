package io.github.arieslab.base;

import io.github.arieslab.dto.TestClass;
import io.github.arieslab.dto.TestSmell;
import org.apache.wicket.behavior.AttributeAppender;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    private static final Logger LOGGER = Logger.getLogger(Util.class.getName());

    /**
     * Checks whether the given string can be parsed as an integer.
     *
     * @param s the input string
     * @return true if the string is a valid integer
     */
    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException er) {
            return false;
        }
    }

    /**
     * Returns the current date and time formatted as "yyyyMMdd-HH:mm:ss - ".
     *
     * @return the formatted date-time string
     */
    public static String dateNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss")) + " - ";
    }

    /**
     * Returns the current date and time formatted as "yyyyMMddHHmmss" for use in folder names.
     *
     * @return the formatted date-time string
     */
    public static String dateNowFolder() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /**
     * Executes an external command in the specified directory and logs its output.
     *
     * @param commandLine the command to execute
     * @param pathExecute the working directory for the command
     */
    public static void execCommand(final String commandLine, String pathExecute) {
        try {
            Process p = Runtime.getRuntime().exec(commandLine, null, new File(pathExecute));
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineOut;
            while ((lineOut = input.readLine()) != null) {
                LOGGER.log(Level.INFO, lineOut);
            }
            input.close();
            p.waitFor();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Command failed: " + commandLine, e);
        }
    }

    /**
     * Extracts the source code lines associated with a test smell from the test class file.
     *
     * @param testClass the test class containing the smell
     * @param testSmell the test smell descriptor
     * @return the source code lines as a string representation of a list
     */
    public static String getCode(TestClass testClass, TestSmell testSmell){
        String nomeClassPath = testClass.getPathFile();
        String range = testSmell.getRange();

        List<Integer> linhasComTestSmells = new ArrayList<>();

        if(range.contains("-")){
            String[] ranger2 = range.split("-");
            int inicio = Integer.parseInt(ranger2[0].trim());
            int fim = Integer.parseInt(ranger2[1].trim());
            for (int i = inicio; i <= fim; i++) {
                linhasComTestSmells.add(i);
            }
        }else if(range.contains(",")){
            String[] ranger2 = range.replace(" ","").split(",");
            for (String linha : ranger2) {
                linhasComTestSmells.add(Integer.parseInt(linha));
            }
        }else if(isInt(range.trim())){
            int range2 = Integer.parseInt(range.trim());
            linhasComTestSmells.add(range2);
        }

        List<String> linhasStringComSmells = new ArrayList();

        try {
            File file = new File(nomeClassPath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int contLinha = 1;
            while ((line = br.readLine()) != null) {
                if(linhasComTestSmells.contains(contLinha)) {
                    linhasStringComSmells.add(line);
                }
                contLinha++;
            }
            fr.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read file: " + nomeClassPath, e);
        }

        return linhasStringComSmells.toString();
    }

    /**
     * Computes a SHA-256 hash of the source code associated with a test smell.
     *
     * @param testClass the test class containing the smell
     * @param testSmell the test smell descriptor
     * @return the SHA-256 hash as a hexadecimal string
     */
    public static String getSHA5Code(TestClass testClass, TestSmell testSmell){
        String code = getCode(testClass,testSmell);
        byte[] encodedhash = null;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            encodedhash = digest.digest(code.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "SHA-256 not available", e);
        }

        return bytesToHex(encodedhash);
    }

    /**
     * Converts a byte array to a hexadecimal string representation.
     *
     * @param hash the byte array
     * @return the hexadecimal string
     */
    private static String bytesToHex(byte[] hash) {
        return HexFormat.of().formatHex(hash);
    }

}
