package com.outbina.dbgo.goparser.bean;

@Deprecated
public class GoStructField {

    private int     index;
    private String  name;
    private String  type;
    private String  tag;

    public GoStructField(int index ,String name, String type, String tag) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


}
