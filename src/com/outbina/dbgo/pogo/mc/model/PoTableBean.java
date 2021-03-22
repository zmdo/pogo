package com.outbina.dbgo.pogo.mc.model;

import java.util.Arrays;

/**
 * 存放关系模式的表格内容，即用户看到的视图内容的数据
 */
public class PoTableBean {

    private String tableName;            // 表格名
    private RelationSchemaTableRowBean[] fieldBeans;

    public PoTableBean(){}

    public PoTableBean(String tableName, RelationSchemaTableRowBean[] fieldBeans) {
        this.tableName = tableName;
        this.fieldBeans = fieldBeans;
    }

    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public RelationSchemaTableRowBean[] getFieldBeans() {
        return fieldBeans;
    }
    public void setFieldBeans(RelationSchemaTableRowBean[] fieldBeans) {
        this.fieldBeans = fieldBeans;
    }

    @Override
    public String toString() {
        return "PoTableBean{" +
                "tableName='" + tableName + '\'' +
                ", fieldBeans=" + Arrays.toString(fieldBeans) +
                '}';
    }
}
