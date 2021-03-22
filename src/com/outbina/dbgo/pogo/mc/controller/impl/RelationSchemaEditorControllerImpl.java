package com.outbina.dbgo.pogo.mc.controller.impl;

import com.goide.psi.GoFieldDeclaration;
import com.goide.psi.GoFile;
import com.goide.psi.GoStructType;
import com.outbina.dbgo.pogo.mc.controller.RelationSchemaEditorController;
import com.outbina.dbgo.pogo.mc.model.DatabaseFieldInfoBean;
import com.outbina.dbgo.pogo.mc.model.PoTableBean;
import com.outbina.dbgo.pogo.mc.model.RelationSchemaTableRowBean;
import com.outbina.dbgo.pogo.mc.service.DatabaseBinderTagService;
import com.outbina.dbgo.pogo.mc.service.PoStructScanService;
import com.outbina.dbgo.pogo.mc.service.PoStructService;
import com.outbina.dbgo.pogo.mc.service.impl.GormDatabaseBinderTagServiceImpl;
import com.outbina.dbgo.pogo.mc.service.impl.PoStructScanServiceImpl;
import com.outbina.dbgo.pogo.mc.service.impl.PoStructServiceImpl;
import com.intellij.openapi.command.WriteCommandAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RelationSchemaEditorControllerImpl implements RelationSchemaEditorController {

    private DatabaseBinderTagService gormDatabaseBinderTagService = new GormDatabaseBinderTagServiceImpl();
    private PoStructScanService structScanService = new PoStructScanServiceImpl();
    private PoStructService structService = new PoStructServiceImpl();

    /**
     * 获取列表数据
     * @param file go的文件
     */
    @Override
    public List<PoTableBean> getPoTableBean(GoFile file) {

        // 获取Po结构体
        Map<String, GoStructType> goStructTypeMap =  structScanService.scan(file);

        // 检查是否为空
        if (goStructTypeMap == null || goStructTypeMap.isEmpty()) {
            return null;
        }

        // poTableBeans
        ArrayList<PoTableBean> poTableBeans = new ArrayList<>();

        // 获取第一个结构体
        for (String structName : goStructTypeMap.keySet()) {
            GoStructType structType = goStructTypeMap.get(structName);

            // 对结构体字段进行解析
            List<GoFieldDeclaration> goFieldDeclarations =  structType.getFieldDeclarationList();
            List<RelationSchemaTableRowBean> rows = new ArrayList<>();
            for (GoFieldDeclaration goFieldDeclaration : goFieldDeclarations) {

                // 获取go字段名
                String goFieldName = goFieldDeclaration.getFieldDefinitionList().get(0).getName();

                // 获取go字段的类
                String goFieldType = goFieldDeclaration.getType().getText();

                // 对字段tag进行解析
                String tag = goFieldDeclaration.getTagText();
                DatabaseFieldInfoBean databaseFieldInfoBean =  gormDatabaseBinderTagService.getDatabaseFieldInfo(goFieldName,tag);

                // 如果获取得数据为空那么就跳过
                if(databaseFieldInfoBean == null) {
                    continue;
                }

                // 检查comment
                String comment = structService.getGoFieldComment(structType,goFieldName);

                // 组装数据并添加到行
                rows.add(new RelationSchemaTableRowBean(
                        goFieldName,
                        databaseFieldInfoBean.getDbFieldName(),
                        goFieldType,
                        databaseFieldInfoBean.getType(),
                        databaseFieldInfoBean.getLength(),
                        databaseFieldInfoBean.getDot(),
                        databaseFieldInfoBean.getNotNull(),
                        databaseFieldInfoBean.getPrimaryKey(),
                        comment
                ));
            }

            // 将rows转换为数组
            RelationSchemaTableRowBean[] rowBeans = new RelationSchemaTableRowBean[rows.size()];
            rowBeans = rows.toArray(rowBeans);

            // 构建PoTableBean
            PoTableBean tableBean = new PoTableBean();
            tableBean.setTableName(structName);
            tableBean.setFieldBeans(rowBeans);
            poTableBeans.add(tableBean);
        }

        return poTableBeans;
    }

    /**
     * 修改go字段命名
     * @param file
     * @param goStructName
     * @param oldGoFieldName
     * @param newGoFieldName
     */
    @Override
    public int modifyGoFieldName(GoFile file, String goStructName, String oldGoFieldName, String newGoFieldName) {
        GoStructType structType = structScanService.scan(file,goStructName);
        return structService.modifyGoFieldName(file,structType,oldGoFieldName,newGoFieldName);
    }

    /**
     * 是一个新的Go字段
     * @param file
     * @param goStructName
     * @param goFieldName
     * @return
     */
    @Override
    public boolean isNewGoFieldName(GoFile file, String goStructName, String goFieldName) {
        GoStructType structType = structScanService.scan(file,goStructName);
        return structService.isNewGoFieldName(structType,goFieldName);
    }

    /**
     * 添加一个新的字段
     * @param file 修改的文件
     * @param goStructName 需要修改的结构体名称
     * @param index 在第几行的位置
     * @param up 向上还是向下添加,true为向上增加，否则就是向下增加
     * @param newGoFieldName 现得go字段名称
     * @param newDBFieldName 新的数据库字段名称
     */
    @Override
    public void insertNewField(@NotNull GoFile file, @NotNull String goStructName, @NotNull int index, @NotNull boolean up, @NotNull String newGoFieldName, @NotNull String newDBFieldName) {
        GoStructType structType = structScanService.scan(file,goStructName);
        Runnable task = structService.insertNewField(file,structType,index,up,newGoFieldName,newDBFieldName);

        // 执行任务
        if(task != null) {
            WriteCommandAction.runWriteCommandAction(file.getProject(),task);
        }
    }

    /**
     * 删除某个字段
     * @param file 修改的文件
     * @param goStructName 需要修改的结构体名称
     * @param goFieldName 需要删除的go字段
     */
    @Override
    public void deleteField(@NotNull GoFile file, @NotNull String goStructName, @NotNull String goFieldName) {
        GoStructType structType = structScanService.scan(file,goStructName);
        Runnable task = structService.deleteField(file,structType,goFieldName);

        // 执行任务
        if(task != null) {
            WriteCommandAction.runWriteCommandAction(file.getProject(),task);
        }
    }


    /**
     * 修改某一字段的注释
     * @param file 修改的文件
     * @param goStructName 需要修改的结构体名称
     * @param goFieldName 字段名
     * @param comment 修改后的注释内容
     */
    @Override
    public void modifyGoFieldComment(@NotNull GoFile file, @NotNull String goStructName, @NotNull String goFieldName, @NotNull String comment) {
        GoStructType structType = structScanService.scan(file,goStructName);
        Runnable task = structService.modifyGoFieldComment(file,structType,goFieldName,comment);

        // 执行任务
        if(task != null) {
            WriteCommandAction.runWriteCommandAction(file.getProject(),task);
        }
    }

    /**
     * 根据给定的信息修改tag
     * @param file
     * @param goStructName
     * @param goFieldName
     * @param dbFieldInfoBean
     */
    @Override
    public void modifyTag(@NotNull GoFile file, @NotNull String goStructName, @NotNull String goFieldName, @NotNull DatabaseFieldInfoBean dbFieldInfoBean) {

        // 为什么两个任务的获取过程要在新建的 Runnable 方法体内执行：
        // 1， 防止脏读脏写
        // 2， 为了其按照一定的保证顺序执行

        // 执行任务
        WriteCommandAction.runWriteCommandAction(file.getProject(), new Runnable() {
            @Override
            public void run() {

                // 修改gorm的tag
                Runnable task1 = gormDatabaseBinderTagService.modifyTag(file,goStructName,goFieldName,dbFieldInfoBean);
                task1.run();

                // 修改json的tag
                GoStructType structType = structScanService.scan(file,goStructName);
                Runnable task2 = structService.setTagValue(file,structType,goFieldName,"json",dbFieldInfoBean.getDbFieldName());
                task2.run();

            }
        });

    }

}
