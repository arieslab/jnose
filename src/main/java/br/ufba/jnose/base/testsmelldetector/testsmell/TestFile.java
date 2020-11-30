package br.ufba.jnose.base.testsmelldetector.testsmell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestFile {
    private String app, testFilePath, productionFilePath, commitId, commitName, commitDate, commitMsg, tag;
    private List<AbstractSmell> testSmells;
    private Integer loc;
    private Integer qtdMethods;

    public TestFile(String commitId, String commitName,String commitDate,String commitMsg, String tag,String app, String testFilePath, String productionFilePath, Integer loc, Integer qtdMethods) {
        this.commitId = commitId;
        this.commitName = commitName;
        this.commitDate = commitDate;
        this.commitMsg = commitMsg;
        this.tag = tag;
        this.app = app;
        this.testFilePath = testFilePath;
        this.productionFilePath = productionFilePath;
        this.testSmells = new ArrayList<>();
        this.loc = loc;
        this.qtdMethods = qtdMethods;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setTestFilePath(String testFilePath) {
        this.testFilePath = testFilePath;
    }

    public void setProductionFilePath(String productionFilePath) {
        this.productionFilePath = productionFilePath;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitName() {
        return commitName;
    }

    public void setCommitName(String commitName) {
        this.commitName = commitName;
    }

    public String getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(String commitDate) {
        this.commitDate = commitDate;
    }

    public String getCommitMsg() {
        return commitMsg;
    }

    public void setCommitMsg(String commitMsg) {
        this.commitMsg = commitMsg;
    }

    public void setTestSmells(List<AbstractSmell> testSmells) {
        this.testSmells = testSmells;
    }

    public void setQtdMethods(Integer qtdMethods) {
        this.qtdMethods = qtdMethods;
    }

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
        return testFilePath.split(File.separator)[4];
    }

    public String getTestFileName() {
        int lastIndex = testFilePath.lastIndexOf(File.separator);
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
        int lastIndex = productionFilePath.lastIndexOf(File.separatorChar);
        if (lastIndex == -1)
            return "";
        return productionFilePath.substring(lastIndex + 1, productionFilePath.length());
    }

    public String getRelativeTestFilePath() {
        String[] splitString = testFilePath.split(File.separator);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(splitString[i] + File.separator);
        }
        return testFilePath.substring(stringBuilder.toString().length()).replace(File.separator, File.separator);
    }

    public String getRelativeProductionFilePath() {
        if (!productionFilePath.isEmpty()) {
            String[] splitString = productionFilePath.split(File.separator);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stringBuilder.append(splitString[i] + File.separator);
            }
            return productionFilePath.substring(stringBuilder.toString().length()).replace(File.separator, File.separator);
        } else {
            return "";

        }
    }
}