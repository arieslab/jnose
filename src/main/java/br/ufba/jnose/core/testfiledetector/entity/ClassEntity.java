//package br.ufba.jnose.core.testfiledetector.entity;
//
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class ClassEntity {
//    private Path path;
//    private List<MethodEntity> methods;
//    private ArrayList<String> imports;
//    private String className;
//    private ArrayList<String> technicalDebtComments;
//
//    public ClassEntity(Path path) {
//        this.path = path;
//    }
//
//    public List<MethodEntity> getMethods() {
//        return methods;
//    }
//
//    public void setMethods(List<MethodEntity> methods) {
//        this.methods = methods;
//    }
//
//    public void setImports(ArrayList<String> imports) {
//        this.imports = imports;
//    }
//
//    public long getTotalImports() {
//        return imports.stream().distinct().count();
//    }
//
//    public int getTotalMethods(){return methods.size();}
//
//    public long getTotalMethodStatement() {
//        return methods.stream().mapToLong(i -> i.getTotalStatements()).sum();
//    }
//
//    public long getTestMethodWithoutAnnotationCount() {
//        return methods.parallelStream().filter((p) -> p.isHasAnnotation() == false && p.isHasTestInName() == true).count();
//    }
//
//    public String getFilePath() {
//        return path.toAbsolutePath().toString();
//    }
//
//    public long getTestAnnotationCount() {
//        return methods.parallelStream().filter((p) -> p.isHasAnnotation()).count();
//    }
//
//    public boolean isHasTestInFileName() {
//        String fileName = getFileNameWithoutExtension().toLowerCase();
//        if (fileName.startsWith("test") || fileName.startsWith("tests") || fileName.endsWith("test") || fileName.endsWith("tests"))
//            return true;
//        else
//            return false;
//    }
//
//    public boolean isHasTestInClassName() {
//        String name  = className.toLowerCase();
//        if (name.startsWith("test") || name.startsWith("tests") || name.endsWith("test") || name.endsWith("tests"))
//            return true;
//        else
//            return false;
//    }
//
//    public String getFileName() {
//        return path.getFileName().toString();
//    }
//
//    public String getClassName() {
//        return className;
//    }
//
//    public void setClassName(String className){this.className = className;}
//
//    public String getFileNameWithoutExtension() {
//        String fileName = path.getFileName().toString().substring(0,path.getFileName().toString().toLowerCase().lastIndexOf(".java"));
//        return fileName;
//    }
//
//    public long getTotalTestMethods() {
//        return (getTestMethodWithoutAnnotationCount() + getTestAnnotationCount());
//    }
//
//    public boolean getHas_junitframeworkTest() {
//        return imports.stream().anyMatch(i -> i.equals("junit.framework.Test"));
//    }
//
//    public boolean getHas_junitframeworkTestCase() {
//        return imports.stream().anyMatch(i -> i.equals("junit.framework.TestCase"));
//    }
//
//    public boolean getHas_orgjunitTest() {
//        return imports.stream().anyMatch(i -> i.equals("org.junit.Test"));
//    }
//
//    public boolean getHas_androidtestAndroidTestCase() {
//        return imports.stream().anyMatch(i -> i.equals("android.test.AndroidTestCase"));
//    }
//
//    public boolean getHas_androidtestInstrumentationTestCase() {
//        return imports.stream().anyMatch(i -> i.equals("android.test.InstrumentationTestCase"));
//    }
//
//    public boolean getHas_androidtestActivityInstrumentationTestCase2() {
//        return imports.stream().anyMatch(i -> i.equals("android.test.ActivityInstrumentationTestCase2"));
//    }
//
//    public boolean getHas_orgjunitAssert() {
//        return imports.stream().anyMatch(i -> i.equals("org.junit.Assert"));
//    }
//
//    public String getRelativeFilePath() {
//        String filePath = path.toAbsolutePath().toString();
//        String[] splitString = filePath.split("/");
//        StringBuilder stringBuilder = new StringBuilder();
//        for (int i = 0; i < 5; i++) {
//            stringBuilder.append(splitString[i] + "/");
//        }
//        return filePath.substring(stringBuilder.toString().length()).replace("/", "/");
//    }
//
//    public String getAppName() {
//        String filePath = path.toAbsolutePath().toString();
//        return filePath.split("/")[5];
//    }
//
//    public String getTagName() {
//        String filePath = path.toAbsolutePath().toString();
//        return filePath.split("/")[4];
//    }
//
//    public ArrayList<String> getTechnicalDebtComments() {
//        return technicalDebtComments;
//    }
//
//    public void setTechnicalDebtComments(ArrayList<String> technicalDebtComments) {
//        this.technicalDebtComments = technicalDebtComments;
//    }
//
//    public Boolean getHasTechnicalDebtComments() {
//        return (technicalDebtComments.size() >=1);
//
//    }
//}
