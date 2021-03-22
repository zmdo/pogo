package com.outbina.dbgo.goparser;

import com.outbina.dbgo.goparser.bean.GoStruct;
import com.outbina.dbgo.goparser.bean.GoStructField;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Go结构体语法解析器
 * 这个解析器被弃用，原因是在官方的Go插件包中找到了合适的API代替其作用
 */
// 结构体解析器
@Deprecated
public class GoStructParser {

    public static final Pattern goStructDefinePattern =
            Pattern.compile("type\\s+\\w*\\s+struct\\s*\\{\\s*\\n[^\\}]*\\n\\}");
    public static final Pattern goStructNamePattern = Pattern.compile("type\\s+(\\w*)\\s+struct");
    public static final Pattern generalLinePattern = Pattern.compile("^\\w*(\\s+[\\w.]*)?(\\s+`.*`)?$");
    public static final Pattern tagPattern = Pattern.compile("`.*`");

    // 进行解析
    // 在测试中发现一个Bug，即结构体的tag不可换行，否则将无法解析
    public static GoStruct parse(String code) {

        GoStruct goStruct = new GoStruct();

        String structName = null;
        Map<String,GoStructField> fields = new HashedMap();

        // 检查格式
        Matcher goStructDefineMatcher = goStructDefinePattern.matcher(code);
        if (goStructDefineMatcher.find()) {
            // 获取一般化结构体定义数据
            String goStructDefineCode = goStructDefineMatcher.group();
            // 获取结构体名字
            Matcher goStructNameMatcher = goStructNamePattern.matcher(goStructDefineCode);
            if(goStructNameMatcher.find()) {
                structName = goStructNameMatcher.group().split("\\s+")[1];
            }
            // 去掉第一行和最后一行的数据
            String[] lines = goStructDefineCode.trim().replaceFirst(".*\\{\\s*\\n","").replace("}", "").trim().split("\n");

            int index = 0;
            for (String line : lines ) {
                line = line.trim() ;
                // 如果是空字符串那么直接跳过
                if(line.isEmpty()) {
                    continue;
                }

                // 匹配字段行的定义
                Matcher generalLineMatcher = generalLinePattern.matcher(line);
                if(generalLineMatcher.find()) {
                    // 匹配
                    String generalLine = generalLineMatcher.group();

                    // 获取tag
                    String tag  = null;
                    Matcher tagMatcher = tagPattern.matcher(generalLine);
                    if (tagMatcher.find()) {
                        tag = tagMatcher.group().replaceAll("`","").trim();
                    }

                    String name = null;
                    String type = null;

                    String[] blocks = generalLine.replaceAll("`.*`", "").split("\\s+");

                    // 如果大小为1，那么说明只有一个
                    switch(blocks.length) {
                        case 1:name=type=blocks[0];break;
                        case 2:name=blocks[0];type=blocks[1];break;
                    }

                    fields.put(name,new GoStructField(index ,name, type, tag));
                    index ++ ;
                    continue;
                } else {
                    // 认为格式有问题直接返回null
                    return null;
                }

            }
        } else {
            return null;
        }

        goStruct.setName(structName);
        goStruct.setFields(fields);

        return goStruct;
    }

}
