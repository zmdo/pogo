package com.outbina.dbgo.pogo;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class PogoFileHelper {

    public static final String FILE_SUFFIX = ".po.go";

    // 判断是否是.po.go文件
    public static boolean filter(@NotNull VirtualFile virtualFile) {

        // 如果是目录直接返回false
        if (virtualFile.isDirectory()){
            return false;
        }

        // 判断后缀
        String name = virtualFile.getName();
        if(name.length() > 6 && name.lastIndexOf(FILE_SUFFIX) == name.length() -6){
            return true;
        }

        return false;
    }

}
