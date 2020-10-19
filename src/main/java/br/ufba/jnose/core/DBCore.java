package br.ufba.jnose.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBCore {

    public static void load(){
        Statement stmt = null;
        int result = 0;

        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver" );
            Connection con = DriverManager.getConnection("jdbc:hsqldb:file:db/jnose.db", "SA", "");
            stmt = con.createStatement();

            result = stmt.executeUpdate(
                    "CREATE TABLE resultados ( " +
                            "id INT NOT NULL, " +
                            "projectName VARCHAR(100) NOT NULL, " +
                            "name VARCHAR(100) NOT NULL, " +
                            "pathFile VARCHAR(1000) NOT NULL, " +
                            "productionFile VARCHAR(1000), " +
                            "junitVersion VARCHAR(10) NOT NULL, " +
                            "loc INT NOT NULL, " +
                            "qtdMethods INT NOT NULL, " +
                            "testSmellName VARCHAR(100) NOT NULL, " +
                            "testSmellMethod VARCHAR(100) NOT NULL, " +
                            "testSmellLineBegin INT NOT NULL, " +
                            "testSmellLineEnd INT NOT NULL, " +
                            "date DATE, " +
                            "PRIMARY KEY (id)); ");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
