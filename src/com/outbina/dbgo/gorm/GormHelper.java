package com.outbina.dbgo.gorm;

import com.outbina.dbgo.tool.SymbolTool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// GormTag 制造器
public class GormHelper {

    public static String add(String gormTag , String key) {
        return add(gormTag,key,true);
    }

    /**
     * 添加一个新的标签
     * @param gormTag
     * @param key
     * @return
     */
    public static String add(String gormTag , String key ,boolean last) {
        String newGormTag;
        if (last) {
            if (SymbolTool.isEndsWithSemicolon(gormTag)) {
                newGormTag = gormTag + ";" + key;
            } else {
                newGormTag = gormTag + key;
            }
        } else {
            newGormTag = key + ";" + gormTag;
        }
        return newGormTag;
    }

    /**
     * 移除一个标签
     * @param gormTag
     * @param key
     * @return
     */
    public static String remove(String gormTag,String key) {
        // 首先将首尾的空格移除
        String keyTrim = key.trim();
        // 将所有的中间空格填充为正则表达式
        String keyRegex = keyTrim.replaceAll("\\s+","\\\\s+");
        // 进行匹配
        Pattern pattern = Pattern.compile(keyRegex + "\\s*;?",Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(gormTag);
        // 如果找到就进行删除
        if (matcher.find()) {
            return gormTag.replace(matcher.group(),"");
        }
        return gormTag;
    }



}
