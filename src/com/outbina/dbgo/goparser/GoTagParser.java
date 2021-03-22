package com.outbina.dbgo.goparser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoTagParser {

    public static final Pattern TagContentPattern = Pattern.compile("\\\"[^\\\"]*\\\"");

    public static final String parse(String tag,String key) {
        // 取tag 对应key的内容
        String content = null;
        String keyTag = getTag(tag,key);
        // 判断key对应的tag是否存在
        if (keyTag != null) {
            Matcher tagContentMatcher = TagContentPattern.matcher(keyTag);
            if (tagContentMatcher.find()) {
                content = tagContentMatcher.group();
                content = content.substring(1,content.length() - 1 ).trim();
            }
        }
        return content;
    }

    public static final String getTag(String tag,String key) {

        if(tag == null || key == null) {
            return null;
        }
        Pattern tagPattern =
                Pattern.compile( key + "\\s*:\\s*\\\"[^\\\"]*\\\"");

        Matcher tagMatcher = tagPattern.matcher(tag.trim());

        //System.out.println(tagMatcher.find());

        if (tagMatcher.find()) {
            return tagMatcher.group();
        }
        return null ;
    }

}
