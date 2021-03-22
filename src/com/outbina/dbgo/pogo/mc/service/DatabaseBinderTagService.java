package com.outbina.dbgo.pogo.mc.service;

import com.goide.psi.GoFile;
import com.outbina.dbgo.pogo.mc.model.DatabaseFieldInfoBean;
import org.jetbrains.annotations.NotNull;

/**
 * 这个服务是用来对那些绑定数据库关系模式设计的tag进行修改的，一旦你执行了修改方法
 * 结构体小中与之相关的元素也会随之变化（比如类型）
 */
public interface DatabaseBinderTagService {

    /**
     * 根据tag解析出数据库字段信息
     * @param goFieldName go字段名
     * @param tag tag数据
     * @return 数据库字段定义信息
     */
    public DatabaseFieldInfoBean getDatabaseFieldInfo(@NotNull String goFieldName , @NotNull String tag);

    /**
     * 根据给定的信息修改tag
     * @param file
     * @param structName
     * @param goFieldName
     * @param dbFieldInfoBean
     */
    public Runnable modifyTag(@NotNull GoFile file, @NotNull String structName, @NotNull String goFieldName, @NotNull DatabaseFieldInfoBean dbFieldInfoBean);

}
