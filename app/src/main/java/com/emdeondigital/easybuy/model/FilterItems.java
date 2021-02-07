package com.emdeondigital.easybuy.model;

public class FilterItems {

        String tec;
        String tecid;
        Boolean isSelected;

    public FilterItems(String tec, String tecid, Boolean isSelected) {
        this.tec = tec;
        this.tecid = tecid;
        this.isSelected = isSelected;
    }

    public String getTecid() {
        return tecid;
    }

    public void setTecid(String tecid) {
        this.tecid = tecid;
    }

    public String getTec() {
        return tec;
    }

    public void setTec(String tec) {
        this.tec = tec;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }


}
