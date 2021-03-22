package com.outbina.dbgo.goparser.bean;

import java.util.Map;

@Deprecated
public class GoStruct {

    private String name;
    private Map<String,GoStructField>  fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String,GoStructField> getFields() {
        return fields;
    }

    public void setFields(Map<String,GoStructField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "GoStruct{" +
                "name='" + name + '\'' +
                ", fields=" + fields +
                '}';
    }
}
