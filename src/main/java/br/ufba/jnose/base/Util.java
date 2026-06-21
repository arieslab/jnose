package br.ufba.jnose.base;

import br.ufba.jnose.dto.TestClass;
import br.ufba.jnose.dto.TestSmell;
import org.apache.wicket.behavior.AttributeAppender;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    private static final Logger LOGGER = Logger.getLogger(Util.class.getName());

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException er) {
            return false;
        }
    }

    public static String dateNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss")) + " - ";
    }

    public static String dateNowFolder() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

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

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
