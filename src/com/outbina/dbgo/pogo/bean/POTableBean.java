package com.outbina.dbgo.pogo.bean;

public class POTableBean {
    private String name ;
    private String type;
    private int length;
    private int dot;
    private boolean nullable;
    private int primary;
    private String comment;

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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getDot() {
        return dot;
    }

    public void setDot(int dot) {
        this.dot = dot;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public int getPrimary() {
        return primary;
    }

    public void setPrimary(int primary) {
        this.primary = primary;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public POTableBean getData(){
        return this;
    }

    public void setData(POTableBean data) {

    }
}
