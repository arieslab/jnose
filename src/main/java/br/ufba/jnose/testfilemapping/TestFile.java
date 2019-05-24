package br.ufba.jnose.testfilemapping;

import br.ufba.jnose.util.Util;

public class TestFile {

    private String filePath, productionFilePath;
    String[] data;

    private int loc = 0;

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public int getLoc() {
        return loc;
    }

    public String getFileName() {
        return data[data.length - 1];
    }

    public String getFilePath() {
        return filePath;
    }

    public String getProductionFilePath() {
        return productionFilePath;
    }

    public void setProductionFilePath(String productionFilePath) {
        this.productionFilePath = productionFilePath;
    }

    public String getProjectRootFolder() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.length-1; i++) {
            stringBuilder.append(data[i] + Util.separator);
        }
        return stringBuilder.toString();
    }

    public String getAppName() {
        return data[3];
    }

    public String getTagName() {
        return data[4];
    }

    public TestFile(String filePath) {
        this.filePath = filePath;
        data = filePath.split(Util.separator);
    }

    public String getRelativeTestFilePath(){
        String[] splitString = filePath.split(Util.separator);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(splitString[i] + Util.separator);
        }
        return filePath.substring(stringBuilder.toString().length()).replace(Util.separator,Util.separator);
    }

    public String getRelativeProductionFilePath(){
        if (!productionFilePath.isEmpty()){
            String[] splitString = productionFilePath.split(Util.separator);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                stringBuilder.append(splitString[i] + Util.separator);
            }
            return productionFilePath.substring(stringBuilder.toString().length()).replace(Util.separator,Util.separator);
        }
        else{
            return "";
        }

    }
}
