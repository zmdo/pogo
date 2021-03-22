package com.outbina.dbgo.pogo.mc.service;

import com.goide.psi.GoFile;
import com.goide.psi.GoStructType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * 这里是对PO结构体进行扫描的服务
 */
public interface PoStructScanService {

    // 结构体默认扫描的后缀
    public static final String DEFAULT_GO_STRUCT_SUFFIX = "PO";

    /**
     * 查找后缀为PO的结构体
     * 该方法使用默认后缀进行扫描
     * @param file 需要扫描的文件
     * @return
     */
    public Map<String,GoStructType> scan(@NotNull GoFile file);

    /**
     * 查找指定名称的go结构体
     * @param file 需要扫描的文件
     * @param name 结构体名
     * @return
     */
    public GoStructType scan(@NotNull GoFile file , @NotNull String name);

    /**
     * 查找以suffix后缀为结尾的结构体
     * @param file 需要扫描的文件
     * @param suffix 后缀名
     * @return
     */
    public Map<String,GoStructType> scanBySuffix(@NotNull GoFile file , @NotNull String suffix);

}
