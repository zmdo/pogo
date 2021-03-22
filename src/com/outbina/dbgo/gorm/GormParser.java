package com.outbina.dbgo.gorm;

import java.util.HashMap;
import java.util.Map;

public class GormParser {

    public static final Map<String,String> parse(String gormCode) {

        // 检查是否为null
        if (gormCode == null) return null;
        // 分割及筛选数据
        Map<String,String> content = new HashMap<>();
        String gormConfigs[] = gormCode.split(";");
        for (String config : gormConfigs) {

            // 标准化
            config = config.trim().toLowerCase();

            // 判断是否有参数
            if (config.contains(":")){
                // 将其根据 ":" 分成两部分
                String item[] = config.split(":",2);
                content.put(item[0].trim(),item[1].trim());
            } else {
                content.put(config.replace("\\s+"," "),"");
            }

        }
        return content;
    }

}
