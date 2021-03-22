package com.outbina.dbgo.pogo.mc.model;

public class DatabaseFieldInfoBean {

    private String dbFieldName; // 字段
    private String type;        // 类型
    private Integer length;     // 长度
    private Integer dot;        // 小数点
    private Boolean notNull;    // 非空
    private Boolean primaryKey; // 主键

    public DatabaseFieldInfoBean(String dbFieldName, String type, Integer length, Integer dot, Boolean notNull, Boolean primaryKey) {
        this.dbFieldName = dbFieldName;
        this.type = type;
        this.length = length;
        this.dot = dot;
        this.notNull = notNull;
        this.primaryKey = primaryKey;
    }

    public String getDbFieldName() {
        return dbFieldName;
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getDot() {
        return dot;
    }

    public void setDot(Integer dot) {
        this.dot = dot;
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public String toString() {
        return "DatabaseFieldInfoBean{" +
                "dbFieldName='" + dbFieldName + '\'' +
                ", type='" + type + '\'' +
                ", length=" + length +
                ", dot=" + dot +
                ", notNull=" + notNull +
                ", primaryKey=" + primaryKey +
                '}';
    }
}
