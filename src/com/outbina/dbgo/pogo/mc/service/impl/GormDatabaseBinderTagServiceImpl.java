package com.outbina.dbgo.pogo.mc.service.impl;

import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.outbina.dbgo.finder.GoStructFinder;
import com.outbina.dbgo.goparser.GoTagParser;
import com.outbina.dbgo.gorm.GormConst;
import com.outbina.dbgo.gorm.GormHelper;
import com.outbina.dbgo.gorm.GormParser;
import com.outbina.dbgo.pogo.GoSqlType;
import com.outbina.dbgo.pogo.mc.model.DatabaseFieldInfoBean;
import com.outbina.dbgo.pogo.mc.service.DatabaseBinderTagService;
import com.outbina.dbgo.tool.CodeUtil;
import com.outbina.dbgo.tool.GoElementExpandFactory;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GormDatabaseBinderTagServiceImpl implements DatabaseBinderTagService {

    public static final String GORM_TAG = GormConst.GORM_TAG;

    /**
     * 根据tag解析出数据库字段信息
     * @param goFieldName go字段名
     * @param tag tag数据
     * @return 数据库字段定义信息
     */
    @Override
    public DatabaseFieldInfoBean getDatabaseFieldInfo(@NotNull String goFieldName, @NotNull String tag) {

        // 去掉引号 以便解析
        tag = tag.replaceAll("`","").trim();

        // 对tag进行解析
        String gormCode = GoTagParser.parse(tag,"gorm");
        if (gormCode == null) {
            return null;
        }
        Map<String,String> gormConfig = GormParser.parse(gormCode);

        // 判断是否是外键的数据，如果是那么跳过
        if (gormConfig.containsKey(GormConst.FOREIGN_KEY)) {
            return null;
        }

        // 数据库表字段名: 如果存在定义，就使用定义，如果不存在就将驼峰命名转换为下划线命名
        String dbFieldName = gormConfig.containsKey(GormConst.COLUMN)?
                gormConfig.get(GormConst.COLUMN): CodeUtil.underScoreCase(goFieldName);

        // 数据库类型名
        String typeDefine = null ;
        int len = -1;
        int dot = 0;
        if(gormConfig.containsKey(GormConst.TYPE)){
            // 获取类型的定义
            typeDefine = gormConfig.get(GormConst.TYPE);

            // 括号匹配
            Pattern innerPattern = Pattern.compile("\\(.*\\)");
            Matcher innerMatcher = innerPattern.matcher(typeDefine);

            if(innerMatcher.find()) {

                // 获得括号里面的部分
                String inner = innerMatcher.group();
                // 获得括号外部的部分
                typeDefine = typeDefine.replace(inner,"").trim();

                // 去掉括号
                inner = inner.substring(1,inner.length()-1).trim();

                // 获取定义，将数据根据小数点进行分割
                // 小数点前的数据为“长度”小数点后的数
                // 据为“小数点”
                String sizeDefine[] = inner.split("\\.");
                len = Integer.valueOf(sizeDefine[0]);
                if (sizeDefine.length > 1) {
                    dot = Integer.valueOf(sizeDefine[1]);
                }
            }

        }

        // 是否可为空检查
        boolean notnull = gormConfig.containsKey(GormConst.NOT_NULL);

        // 主键检查
        boolean primaryKey = gormConfig.containsKey(GormConst.PRIMARY_KEY);

        // 填充并返回数据
        return new DatabaseFieldInfoBean(dbFieldName,typeDefine,len,dot,notnull,primaryKey);
    }

    @Override
    public Runnable modifyTag(@NotNull GoFile file, @NotNull String poStructName, @NotNull String fieldName, @NotNull DatabaseFieldInfoBean dbFieldInfoBean) {

        // 获取当前所操作的项目
        Project project = file.getProject();

        // 获取结构体对象
        GoStructType structType  = GoStructFinder.find(file,Pattern.compile(poStructName)).get(poStructName);

        // 通过获取声明对象来获取tag的psi对象
        GoFieldDeclaration goFieldDeclaration = GoStructFinder.findGoFieldDeclarationElement(structType,fieldName);
        GoTag tag = goFieldDeclaration.getTag();

        // 获取需要替换的tag文本
        String tagText = tag.getText();

        // 解析旧的tag
        DatabaseFieldInfoBean oldDBFieldInfoBean = getDatabaseFieldInfo(fieldName,tagText);

        // 任务列表，这些是将需要在 Write Action 中运行的任务添加到该任务列表中，但是在执行时
        // 并不是每个任务都会创建一个线程进行运行，而是把他们归到一个线程内一起执行
        List<Runnable> taskList = new ArrayList();

        // 这里需要注意：oldGormText是带有 gorm 的数据，即形如： gorm:"column:id" 的形式
        // 而 newGormText 则是去掉 gorm 和 双引号 的数据，即形如：column:id 的形式
        // 其中newGormText会在结束后添加上 gorm 和 双引号 , 然后用 replace 的形式来替换原
        // tag文本中的 oldGormText

        String oldGormText = GoTagParser.getTag(tagText.replaceAll("`",""),GormConst.GORM_TAG);
        String newGormText = GoTagParser.parse(oldGormText,GormConst.GORM_TAG); // 内部的内容

        // 检查 非空 是否被修改
        if(oldDBFieldInfoBean.getNotNull() != dbFieldInfoBean.getNotNull()) {
            // 检查非空的设置
            if ( dbFieldInfoBean.getNotNull() ) {
                newGormText = GormHelper.add(newGormText,GormConst.NOT_NULL);
            } else {
                newGormText = GormHelper.remove(newGormText,GormConst.NOT_NULL);
            }
        }

        // 检查 主键 是否被修改
        if(oldDBFieldInfoBean.getPrimaryKey() != dbFieldInfoBean.getPrimaryKey()) {
            // 检查主键的配置
            if ( dbFieldInfoBean.getPrimaryKey() ) {
                newGormText = GormHelper.add(newGormText,GormConst.PRIMARY_KEY);
            } else {
                newGormText = GormHelper.remove(newGormText,GormConst.PRIMARY_KEY);
            }
        }

        // 检查 类型、长度、小数点 是否被修改
        if(oldDBFieldInfoBean.getType() != dbFieldInfoBean.getType() ||
        oldDBFieldInfoBean.getLength() != dbFieldInfoBean.getLength() ||
        oldDBFieldInfoBean.getDot() != dbFieldInfoBean.getDot()) {
            // Sql的类型
            String gormType;

            // 获取实际的sql和go类型
            String sqlType = dbFieldInfoBean.getType(); // 括号外面的
            // 检查sqlType是否为空
            if(sqlType != null) {
                String goType = GoSqlType.SQL_TO_GO_TYPE_MAP.get(sqlType);

                Integer length = dbFieldInfoBean.getLength();
                Integer dot = dbFieldInfoBean.getDot();

                if (dot > 0) {
                    gormType = String.format("type:%s(%d.%d)",sqlType,length,dot);
                } else {
                    gormType = String.format("type:%s(%d)",sqlType,length);
                }

                newGormText = GormHelper.remove(newGormText, GormConst.TYPE + "\\s*:\\s*[^;]*" );
                newGormText = GormHelper.add(newGormText,gormType);

                // 替换原来的go类型
                taskList.add(new Runnable() {
                    @Override
                    public void run() {
                        // 需要使用的的go类型元素
                        GoType needTypeElement = GoElementFactory.createType(project,goType,null);
                        GoFieldDeclaration fieldElement = GoStructFinder.findGoFieldDeclarationElement(structType,fieldName);
                        if (needTypeElement != null) {
                            // 现在的go类型元素（即，被替换的go类型元素）
                            GoType nowGoType = fieldElement.getType();
                            nowGoType.replace(needTypeElement);
                        }
                    }
                });
            }
        }

        // 检查字段定义是否被修改
        if(oldDBFieldInfoBean.getDbFieldName() != dbFieldInfoBean.getDbFieldName()) {
            // 获取当前的声明
            final String nowDBFieldName = dbFieldInfoBean.getDbFieldName();
            // 记录的声明
            final String recordDBFieldName = oldDBFieldInfoBean.getDbFieldName();
            // 创建新的column声明
            String newColumn = GormConst.COLUMN + ":" + nowDBFieldName;

            // 不区分大小写
            final Pattern columnPattern = Pattern.compile(GormConst.COLUMN + "\\s*:[^;]*;?",Pattern.CASE_INSENSITIVE);
            Matcher columnMatcher = columnPattern.matcher(newGormText);

            // 如果匹配到字段，那么就将其删除
            if (columnMatcher.find()) {
                newGormText = newGormText.replace(columnMatcher.group(),"");
            }
            // 将数据添加到第一位
            newGormText = GormHelper.add(newGormText,newColumn,false);
        }

        // 添加标注与引号
        newGormText = GormConst.GORM_TAG + ":\"" + newGormText + "\""; // 最后添加上末尾引号

        // 替换新的文本
        String newTagText = tagText.replace(oldGormText,newGormText);

        // 返回任务
        return new Runnable() {
            @Override
            public void run() {
                synchronized (file) {
                    // 执行tag修改任务
                    GoTag newTag = GoElementExpandFactory.createTag(project,newTagText);
                    tag.replace(newTag);

                    // 顺序执行任务
                    for(Runnable task : taskList) {
                        task.run();
                    }

                }
            }
        };
    }

}
