//package br.ufba.jnose.core;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class DBCore {
//
//    private static Statement stmt = null;
//
//    public static void load(){
//
//
//        try {
//            Class.forName("org.hsqldb.jdbc.JDBCDriver" );
//            Connection con = DriverManager.getConnection("jdbc:hsqldb:file:db/jnose.db", "SA", "");
//            stmt = con.createStatement();
//
//            int result = stmt.executeUpdate(
//                    "CREATE TABLE resultados ( " +
//                            "id INT NOT NULL, " +
//                            "projectName VARCHAR(100) NOT NULL, " +
//                            "name VARCHAR(100) NOT NULL, " +
//                            "pathFile VARCHAR(1000) NOT NULL, " +
//                            "productionFile VARCHAR(1000), " +
//                            "junitVersion VARCHAR(10) NOT NULL, " +
//                            "loc INT NOT NULL, " +
//                            "qtdMethods INT NOT NULL, " +
//                            "testSmellName VARCHAR(100) NOT NULL, " +
//                            "testSmellMethod VARCHAR(100) NOT NULL, " +
//                            "testSmellLineBegin INT NOT NULL, " +
//                            "testSmellLineEnd INT NOT NULL, " +
//                            "date DATE, " +
//                            "PRIMARY KEY (id)); ");
//
//            int result2 = stmt.executeUpdate(
//                    "CREATE TABLE projetos ( " +
//                            "id INT NOT NULL, " +
//                            "nome VARCHAR(100) NOT NULL, " +
//                            "path VARCHAR(1000) NOT NULL, " +
//                            "url VARCHAR(1000), " +
//                            "junitVersion VARCHAR(10) NOT NULL, " +
//                            "stars INT NOT NULL, " +
//                            "lastUpdate DATE, " +
//                            "PRIMARY KEY (id)); ");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void save(Project project){
//        try {
//            stmt.executeUpdate("insert into projetos (nome, path, url, junitVersion, stars, lastUpdate) " +
//                    "values ('" + project.getName()
//                    + "','" + project.getPath()
//                    + "','" + project.getUrl()
//                    + "','" + project.getJunitVersion()
//                    + "','" + project.getStars()
//                    + "','" + project.getLastUpdate()
//                    + "')");
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//    }
//
//    public static List<Project> projectList(){
//        return new ArrayList<Project>();
//    }
//}
//
//class Project {
//    private Integer id;
//    private String name;
//    private String path;
//    private String url;
//    private String junitVersion;
//    private Integer stars;
//    private Date lastUpdate;
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public String getJunitVersion() {
//        return junitVersion;
//    }
//
//    public void setJunitVersion(String junitVersion) {
//        this.junitVersion = junitVersion;
//    }
//
//    public Integer getStars() {
//        return stars;
//    }
//
//    public void setStars(Integer stars) {
//        this.stars = stars;
//    }
//
//    public Date getLastUpdate() {
//        return lastUpdate;
//    }
//
//    public void setLastUpdate(Date lastUpdate) {
//        this.lastUpdate = lastUpdate;
//    }
//}
