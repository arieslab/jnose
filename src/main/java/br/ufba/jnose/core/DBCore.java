package br.ufba.jnose.core;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBCore {

    public static void load(){
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver" );
            Connection c = DriverManager.getConnection("jdbc:hsqldb:file:db/jnose.db", "SA", "");
        } catch (Exception e) {
            System.err.println("ERRO: falha ao carregar o driver JDBC do HSQLDB!");
            e.printStackTrace();
            return;
        }

    }
}
