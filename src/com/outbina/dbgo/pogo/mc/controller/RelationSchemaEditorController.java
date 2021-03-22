package com.outbina.dbgo.pogo.mc.controller;

import com.goide.psi.GoFile;
import com.outbina.dbgo.pogo.mc.model.DatabaseFieldInfoBean;
import com.outbina.dbgo.pogo.mc.model.PoTableBean;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RelationSchemaEditorController {

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
     * 获取列表数据
     * @param file go的文件
     */
    public List<PoTableBean> getPoTableBean(GoFile file);

    /**
     * 修改go字段命名
     * @param file
     * @param goStructName
     * @param oldGoFieldName
     * @param newGoFieldName
     */
    public int modifyGoFieldName(GoFile file , String goStructName, String oldGoFieldName, String newGoFieldName);

    /**
     * 是一个新的Go字段
     * @param file
     * @param goStructName
     * @param goFieldName
     * @return
     */
    public boolean isNewGoFieldName(GoFile file , String goStructName, String goFieldName);

    /**
     * 添加一个新的字段
     * @param file 修改的文件
     * @param goStructName 需要修改的结构体名称
     * @param index 在第几行的位置
     * @param up 向上还是向下添加,true为向上增加，否则就是向下增加
     * @param newGoFieldName 现得go字段名称
     * @param newDBFieldName 新的数据库字段名称
     */
    public void insertNewField(@NotNull GoFile file , @NotNull String goStructName , @NotNull int index , @NotNull boolean up , @NotNull String newGoFieldName, @NotNull String newDBFieldName);

    /**
     * 删除某个字段
     * @param file 修改的文件
     * @param goStructName 需要修改的结构体名称
     * @param goFieldName 需要删除的go字段
     */
    public void deleteField(@NotNull GoFile file , @NotNull String goStructName ,@NotNull String goFieldName);

    /**
     * 修改某一字段的注释
     * @param file 修改的文件
     * @param goStructName 需要修改的结构体名称
     * @param goFieldName 字段名
     * @param comment 修改后的注释内容
     */
    public void modifyGoFieldComment(@NotNull GoFile file , @NotNull String goStructName , @NotNull String goFieldName,@NotNull String comment);

    /**
     * 根据给定的信息修改tag
     * @param file
     * @param goStructName
     * @param goFieldName
     * @param dbFieldInfoBean
     */
    public void modifyTag(@NotNull GoFile file,@NotNull String goStructName,@NotNull String goFieldName,@NotNull DatabaseFieldInfoBean dbFieldInfoBean);
}
