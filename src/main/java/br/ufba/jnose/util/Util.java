package br.ufba.jnose.util;

public class Util {


    public static String separator = "/";

    static {

        String OS = System.getProperty("os.name").toLowerCase();

        if(OS.contains("win")){
            separator = "\\";
        }else{
            separator = "/";
        }

    }
}


