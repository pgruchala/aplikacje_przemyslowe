package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImportSummary {
    int imported;
    List<String> errorList;
    public ImportSummary(){
        this.imported = 0;
        this.errorList = new ArrayList<>();
    }
    public void error(String e){
        this.errorList.add(e);
    }
    public void success() {
        this.imported +=1;
    }

    public int getImported() {
        return imported;
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public String toString(){
      return String.format("\nPomyślnie zaimportowano : %s pracowników\n" +
              "Lista błędów: %s",this.imported, this.errorList);
    }
}
