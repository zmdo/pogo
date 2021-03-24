package com.outbina.dbgo.pogo.mc.service;

import com.goide.psi.GoFile;
import com.goide.psi.GoStructType;
import org.jetbrains.annotations.NotNull;

public interface PoStructService {

    /**
     * 对于结构体内修改的Go字段命名的规范的描述 :
     * MODIFY_GO_FIELD_NAME_SUCCESS : go字段命名修改成功
     * GO_FIELD_NAME_IS_EMPTY : 新的go字段命名为空错误
     * GO_FIELD_NAMING_CONVENTIONS_ERROR : 新的字段命名规范错误
     */

    public static final int MODIFY_GO_FIELD_NAME_SUCCESS = 0;      // go字段命名修改成功
    public static final int GO_FIELD_NAME_IS_EMPTY = 1;            // 新的go字段命名为空错误
    public static final int GO_FIELD_NAMING_CONVENTIONS_ERROR = 2; // 新的字段命名规范错误

    /**
     * 添加一个新的字段
     * @param goFile 修改的文件
     * @param goStructType 需要修改的结构体
     * @param index 在第几行的位置
     * @param up 向上还是向下添加,true为向上增加，否则就是向下增加
     * @param newGoFieldName 现得go字段名称
     * @param newDBFieldName 新的数据库字段名称
     * @return
     */
    public Runnable insertNewField(@NotNull GoFile goFile , @NotNull GoStructType goStructType , @NotNull int index , @NotNull boolean up , @NotNull String newGoFieldName, @NotNull String newDBFieldName);

    /**
     * 删除某个字段
     * @param goFile 修改的文件
     * @param goStructType 需要修改的结构体
     * @param goFieldName 需要删除的go字段
     * @return
     */
    public Runnable deleteField(@NotNull GoFile goFile , @NotNull GoStructType goStructType , @NotNull String goFieldName);

    /**
     * 修改某一字段的字段名
     * @param goFile 修改的文件
     * @param goStructType 需要修改的结构体
     * @param oldGoFieldName 老的字段名
     * @param newGoFieldName 新的字段名
     * @return 状态信息
     */
    public int modifyGoFieldName(@NotNull GoFile goFile ,@NotNull GoStructType goStructType ,@NotNull String oldGoFieldName,@NotNull String newGoFieldName);

    /**
     * 获取某一字段的注释,由于按照 Intellij IDEA 插件开发规定，重命名重构的行为不能加入到
     * WriteCommandAction.runWriteCommandAction(Runnable)中去执行，所以这可以直接
     * 执行并看到重命名后的结果
     *
     * @param goStructType 结构体
     * @param goFieldName 字段名
     * @return
     */
    public String getGoFieldComment(@NotNull GoStructType goStructType,@NotNull String goFieldName);

    /**
     * 修改某一字段的注释
     *
     * @param goFile 修改的文件
     * @param goStructType 需要修改的结构体
     * @param goFieldName 字段名
     * @param comment 修改后的注释内容
     * @return
     */
    public Runnable modifyGoFieldComment(@NotNull GoFile goFile , @NotNull GoStructType goStructType, @NotNull String goFieldName, @NotNull String comment);

    /**
     * 是否是一个新的go字段
     * @param goStructType
     * @param goFieldName
     * @return
     */
    public boolean isNewGoFieldName(@NotNull GoStructType goStructType,@NotNull String goFieldName);

    /**
     * 获取Go某一字段的Tag
     * @param goStructType 结构体
     * @param goFieldName 字段名
     * @return
     */
    public String getGoFieldTag(@NotNull GoStructType goStructType,@NotNull String goFieldName);

    /**
     * 修改某一字段的tag
     * @param goFile
     * @param goStructType
     * @param goFieldName
     * @param tag
     * @return
     */
    public Runnable modifyGoFieldTag(@NotNull GoFile goFile ,@NotNull GoStructType goStructType,@NotNull String goFieldName,@NotNull String tag);

    /**
     * 获取Go某一字段的Tag某一标签的值
     * @param goStructType 结构体
     * @param goFieldName 字段名
     * @param key
     * @return
     */
    public String getTagValue(@NotNull GoStructType goStructType,@NotNull String goFieldName,@NotNull String key);

    /**
     * 修改某一字段的tag某一标签的值
     * @param goFile
     * @param goStructType
     * @param goFieldName
     * @param key
     * @param value
     * @return
     */
    public Runnable setTagValue(@NotNull GoFile goFile ,@NotNull GoStructType goStructType,@NotNull String goFieldName,@NotNull String key,@NotNull String value);

    /**
     *
     * @param file
     * @param goStructType
     * @param start
     * @param end
     * @param up
     * @return
     */
    public Runnable moveRow(@NotNull GoFile file,@NotNull GoStructType goStructType,@NotNull int start,@NotNull int end,@NotNull boolean up);
}
