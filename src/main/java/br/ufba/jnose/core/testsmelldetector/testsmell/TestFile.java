package br.ufba.jnose.core.testsmelldetector.testsmell;

import java.util.ArrayList;
import java.util.List;

public class TestFile {
    private String app, testFilePath, productionFilePath;
    private List<AbstractSmell> testSmells;
    private Integer loc;
    private Integer qtdMethods;

    public String getApp() {
        return app;
    }

    public String getProductionFilePath() {
        return productionFilePath;
    }

    public String getTestFilePath() {
        return testFilePath;
    }

    public List<AbstractSmell> getTestSmells() {
        return testSmells;
    }

    public boolean getHasProductionFile() {
        return ((productionFilePath != null && !productionFilePath.isEmpty()));
    }

    public TestFile(String app, String testFilePath, String productionFilePath) {
        this(app,testFilePath,productionFilePath,null,null);
    }

    public TestFile(String app, String testFilePath, String productionFilePath, Integer loc, Integer qtdMethods) {
        this.app = app;
        this.testFilePath = testFilePath;
        this.productionFilePath = productionFilePath;
        this.testSmells = new ArrayList<>();
        this.loc = loc;
        this.qtdMethods = qtdMethods;
    }

    public Integer getQtdMethods(){
        return this.qtdMethods;
    }

    public void setLoc(Integer loc) {
        this.loc = loc;
    }

    public Integer getLoc() {
        return loc;
    }

    public void addSmell(AbstractSmell smell) {
        testSmells.add(smell);
    }

    public String getTagName() {
        return testFilePath.split("/")[4];
    }

    public String getTestFileName() {
        int lastIndex = testFilePath.lastIndexOf("/");
        return testFilePath.substring(lastIndex + 1, testFilePath.length());
    }

    public String getTestFileNameWithoutExtension() {
        int lastIndex = getTestFileName().lastIndexOf(".");
        return getTestFileName().substring(0, lastIndex);
    }

    public String getProductionFileNameWithoutExtension() {
        int lastIndex = getProductionFileName().lastIndexOf(".");
        if (lastIndex == -1)
            return "";
        return getProductionFileName().substring(0, lastIndex);
    }

    public String getProductionFileName() {
        int lastIndex = productionFilePath.lastIndexOf("/");
        if (lastIndex == -1)
            return "";
        return productionFilePath.substring(lastIndex + 1, productionFilePath.length());
    }

    public String getRelativeTestFilePath() {
        String[] splitString = testFilePath.split("/");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(splitString[i] + "/");
        }
        return testFilePath.substring(stringBuilder.toString().length()).replace("\\", "/");
    }

    public String getRelativeProductionFilePath() {
        if (!productionFilePath.isEmpty()) {
            String[] splitString = productionFilePath.split("/");
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stringBuilder.append(splitString[i] + "/");
            }
            return productionFilePath.substring(stringBuilder.toString().length()).replace("/", "/");
        } else {
            return "";

        }
    }
}