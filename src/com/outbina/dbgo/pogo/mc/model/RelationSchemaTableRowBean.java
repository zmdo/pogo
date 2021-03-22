package com.outbina.dbgo.pogo.mc.model;

/**
 * 存放关系模式的表格的某一行的内容
 */
public class RelationSchemaTableRowBean {

    private String goFieldName;  // Go字段名
    private String dbFieldName;  // 数据库字段名
    private String goFieldType;  // go字段类型
    private String dbFieldType;  // 数据库字段类型
    private Integer length;      // 数据库字段类型的长度
    private Integer dot;         // 数据库字段类型的小数点
    private Boolean notNull;     // 非空
    private Boolean primaryKey;  // 主键
    private String comment;      // 注释

    public RelationSchemaTableRowBean(String goFieldName, String dbFieldName, String goFieldType, String dbFieldType, Integer length, Integer dot, Boolean notNull, Boolean primaryKey, String comment) {
            this.goFieldName = goFieldName;
            this.dbFieldName = dbFieldName;
            this.goFieldType = goFieldType;
            this.dbFieldType = dbFieldType;
            this.length = length;
            this.dot = dot;
            this.notNull = notNull;
            this.primaryKey = primaryKey;
            this.comment = comment;
    }

    public String getGoFieldName() {
        return goFieldName;
    }

    public void setGoFieldName(String goFieldName) {
        this.goFieldName = goFieldName;
    }

    public String getDbFieldName() {
        return dbFieldName;
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }

    public String getGoFieldType() {
        return goFieldType;
    }

    public void setGoFieldType(String goFieldType) {
        this.goFieldType = goFieldType;
    }

    public String getDbFieldType() {
        return dbFieldType;
    }

    public void setDbFieldType(String dbFieldType) {
        this.dbFieldType = dbFieldType;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "RelationSchemaTableBean{" +
                "goFieldName='" + goFieldName + '\'' +
                ", dbFieldName='" + dbFieldName + '\'' +
                ", goFieldType='" + goFieldType + '\'' +
                ", dbFieldType='" + dbFieldType + '\'' +
                ", length=" + length +
                ", dot=" + dot +
                ", notNull=" + notNull +
                ", primaryKey=" + primaryKey +
                ", comment='" + comment + '\'' +
                '}';
    }
}
