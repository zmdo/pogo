package com.outbina.dbgo.pogo.mc.service.impl;

import com.goide.psi.GoFile;
import com.goide.psi.GoStructType;
import com.outbina.dbgo.finder.GoStructFinder;
import com.outbina.dbgo.pogo.mc.service.PoStructScanService;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Pattern;

public class PoStructScanServiceImpl implements PoStructScanService {

    // 按指定模式在字符串查找
    public static final Pattern DEFAULT_PATTERN =
            Pattern.compile("\\w*"+ DEFAULT_GO_STRUCT_SUFFIX + "$");

    /**
     * 查找后缀为PO的结构体
     * 该方法使用默认后缀进行扫描
     * @param file 需要扫描的文件
     * @return 扫描结果映射
     */
    @Override
    public Map<String,GoStructType> scan(@NotNull GoFile file) {
        return GoStructFinder.find(file,DEFAULT_PATTERN);
    }

    /**
     * 查找指定名称的go结构体
     * @param file 需要扫描的文件
     * @param name 结构体名
     * @return
     */
    @Override
    public GoStructType scan(@NotNull GoFile file, @NotNull String name) {
        Map<String,GoStructType> result =  GoStructFinder.find(file,Pattern.compile(name));
        if (result != null && result.size() > 0) {
            return result.values().iterator().next();
        }
        return null;
    }

    /**
     * 查找以suffix后缀为结尾的结构体
     * @param file 需要扫描的文件
     * @param suffix 后缀名
     * @return
     */
    @Override
    public Map<String,GoStructType> scanBySuffix(@NotNull GoFile file, @NotNull String suffix) {
        // 按指定模式在字符串查找
        Pattern filterPattern =
                Pattern.compile("\\w*"+ suffix + "$");
        return GoStructFinder.find(file,filterPattern);
    }

}
