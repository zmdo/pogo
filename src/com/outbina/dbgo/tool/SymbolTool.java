package com.outbina.dbgo.tool;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 符号工具
 * 用来对符号进行简单的判断和处理
 */
public class SymbolTool {

    public static final Pattern quotationMarksPattern = Pattern.compile("^\\\"[^\\\"]*\\\"$");
    public static final Pattern parenthesesMarksPattern = Pattern.compile("^\\([^\\)]*\\)$");

    /**
     * 是否是以分号作为结尾
     * @param str
     * @return
     */
    public static boolean isEndsWithSemicolon(@NotNull String str){
        String trimStr = str.trim();
        return trimStr.charAt(trimStr.length()-1) != ';';
    }

    /**
     * 是否被双引号包围
     * @param str
     * @return
     */
    public static boolean isEnclosedInDoubleQuotes(@NotNull String str) {
        return quotationMarksPattern.matcher(str.trim()).find();
    }

    /**
     * 获取双引号中的内容
     * @param str
     * @return
     */
    public static String getContentInDoubleQuotes(@NotNull String str) {
        Matcher matcher = quotationMarksPattern.matcher(str.trim());
        if (matcher.find()) {
            String m = matcher.group();
            return m.substring(1,m.length() - 1);
        }
        return null;
    }

    /**
     * 是否被双引号包围
     * @param str
     * @return
     */
    public static boolean isEnclosedInParentheses(@NotNull String str) {
        return parenthesesMarksPattern.matcher(str.trim()).find();
    }

    /**
     * 获取被括号中的内容
     * @param str
     * @return
     */
    public static String getContentInParentheses(@NotNull String str) {
        Matcher matcher = parenthesesMarksPattern.matcher(str.trim());
        if (matcher.find()) {
            String m = matcher.group();
            return m.substring(1,m.length() - 1);
        }
        return null;
    }

}
