package com.outbina.dbgo.pogo;

import java.util.HashMap;
import java.util.Map;

public class GoSqlType {

    // 类型的选项
    public static final String[] SQL_TYPES = {
            "smallint",
            "tinyint",
            "int",
            "bigint",
            "float",
            "double",
            "varchar",
            "text",
            "date",
            "datetime",
            "timestamp",
    };

    // 默认Go语言类型映射关系表
    public static final String[] GO_TYPES = {
            "int8",
            "int16",
            "int32",
            "int64",
            "float32",
            "float64",
            "string",
            "string",
            "time.Time",
            "time.Time",
            "time.Time",
    };

    // 映射关系表
    public static final Map<String,String> SQL_TO_GO_TYPE_MAP;
    static  {
        SQL_TO_GO_TYPE_MAP = new HashMap<>();
        for (int i = 0 ; i < SQL_TYPES.length ; i ++) {
                SQL_TO_GO_TYPE_MAP.put(SQL_TYPES[i], GO_TYPES[i]);
        }
    }

}
