package com.outbina.dbgo.pogo.mc.service.impl;

import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.outbina.dbgo.finder.GoCommentFinder;
import com.outbina.dbgo.finder.GoStructFinder;
import com.outbina.dbgo.goparser.GoTagParser;
import com.outbina.dbgo.pogo.mc.service.PoStructService;
import com.outbina.dbgo.tool.GoElementExpandFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.refactoring.RenameRefactoring;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PoStructServiceImpl implements PoStructService {

    // 命名检查
    public static final Pattern fieldNameFormatPattern = Pattern.compile("^[A-Z]\\w*$");

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
    @Override
    public Runnable insertNewField(@NotNull GoFile goFile, @NotNull GoStructType goStructType , @NotNull int index, @NotNull boolean up, @NotNull String newGoFieldName, @NotNull String newDBFieldName) {

        // 获取当前所操作的项目
        Project project = goFile.getProject();

        // 进行位置添加
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (goFile) {

                    // 创建一个声明
                    String structFieldDefine = newGoFieldName + " string `gorm:\"column:" + newDBFieldName + ";type:varchar(0)\" json:\"" + newDBFieldName + "\"`";
                    GoFieldDeclaration goFieldDeclaration = GoElementExpandFactory.createGoFieldDeclaration(project,structFieldDefine);

                    // 查找所需要添加的位置的声明
                    PsiElement anchorFieldDeclaration = goStructType.getFieldDeclarationList().get(index);

                    // 如果直接添加会出现声明重叠到同一行的情况，所以需要在声明语句（前）后方添加一个换行符
                    // 在这里换行符需要进行的是一个 createNewLine 操作（实际上就是加上一个'\n'符号）
                    PsiElement newLine = GoElementFactory.createNewLine(project);

                    // 判断是在上方增加行，还是在下方增加行
                    if (up) {
                        // 上方时在 goFieldDeclaration 前方添加换行符
                        goFieldDeclaration.addBefore(newLine,null);
                        // 往前添加换行
                        anchorFieldDeclaration.addAfter(goFieldDeclaration,null);
                    } else {

                        PsiComment comment = null;
                        // 寻找注释的定义
                        PsiElement next = anchorFieldDeclaration.getNextSibling();
                        while (true) {
                            if(next == null) {
                                break;
                            } else if (next instanceof PsiComment) {
                                // 获取注释
                                comment = GoElementFactory.createComment(project,next.getText());
                                // 注释删掉
                                next.delete();
                                break;
                            } else {
                                next = next.getNextSibling();
                            }
                        }

                        // 这里需要重新实现一次这个东西，特别麻烦
                        // 因为他的API有一些问题，所以只能这么做
                        PsiElement anchor = GoElementExpandFactory.createGoFieldDeclaration(project,anchorFieldDeclaration.getText());
                        if (comment != null) {
                            anchor.addBefore(comment,null);
                        }
                        anchor.addBefore(newLine,null);
                        anchorFieldDeclaration.replace(goFieldDeclaration).addAfter(anchor,null);


                    }
                }
            }

        };
        return runnable;
    }

    /**
     * 删除某个字段
     * @param goFile 修改的文件
     * @param goStructType 需要修改的结构体
     * @param goFieldName 需要删除的go字段
     * @return
     */
    @Override
    public Runnable deleteField(@NotNull GoFile goFile, @NotNull GoStructType goStructType, @NotNull String goFieldName) {

        // 执行删除
        return new Runnable() {
            @Override
            public void run() {
                synchronized (goFile) {
                    // 删除当前的定义
                    GoFieldDeclaration element = GoStructFinder.findGoFieldDeclarationElement(goStructType,goFieldName);
                    // 注释
                    PsiComment comment = GoCommentFinder.find(element);
                    // 先删除注释
                    if (comment != null) {
                        comment.delete();
                    }
                    // 删除整个字段的定义的元素
                    element.delete();

                    // TODO 还要删除FIELD字段内容

                }
            }
        };
    }

    /**
     * 修改某一字段的字段名
     * @param goFile 修改的文件
     * @param goStructType 需要修改的结构体
     * @param oldGoFieldName 老的字段名
     * @param newGoFieldName 新的字段名
     * @return 状态信息
     */
    @Override
    public synchronized int modifyGoFieldName(@NotNull GoFile goFile, @NotNull GoStructType goStructType, @NotNull String oldGoFieldName, @NotNull String newGoFieldName) {

        // 获取当前所操作的项目
        Project project = goFile.getProject();

        // 如果当前列表中的命名数据不等于记录的数据
        if (!oldGoFieldName.equals(newGoFieldName)){

            // 空检查
            if (newGoFieldName.isEmpty()) {
                return GO_FIELD_NAME_IS_EMPTY;
            }

            // 命名格式检查
            Matcher fieldNameFormatMatcher = fieldNameFormatPattern.matcher(newGoFieldName);
            if (!fieldNameFormatMatcher.find()) {
                return GO_FIELD_NAMING_CONVENTIONS_ERROR;
            }

            // 修改命名
            GoNamedElement goNamedElement = GoStructFinder.findGoFieldNamedElement(goStructType, oldGoFieldName);

            RenameRefactoring newName = RefactoringFactory.getInstance(project).createRename(goNamedElement, newGoFieldName);
            newName.run();
        }

        return MODIFY_GO_FIELD_NAME_SUCCESS;

    }

    /**
     * 获取某一字段的注释
     * @param goStructType 结构体
     * @param goFieldName 字段名
     * @return
     */
    @Override
    public String getGoFieldComment(@NotNull GoStructType goStructType, @NotNull String goFieldName) {
        // 寻找当前字段的定义
        GoFieldDeclaration goFieldDeclaration = GoStructFinder.findGoFieldDeclarationElement(goStructType,goFieldName);
        String comment = GoCommentFinder.findCommentString(goFieldDeclaration);
        return comment;
    }

    /**
     * 修改某一字段的注释
     * @param goFile 修改的文件
     * @param goStructType 需要修改的结构体
     * @param goFieldName 字段名
     * @param comment 修改后的注释内容
     * @return
     */
    @Override
    public Runnable modifyGoFieldComment(@NotNull GoFile goFile, @NotNull GoStructType goStructType, @NotNull String goFieldName, @NotNull String comment) {

        // 获取当前所操作的项目
        Project project = goFile.getProject();

        // 获取当前的注释与记录的注释
        String nowComment = comment;

        // 寻找当前字段的定义
        GoFieldDeclaration goFieldDeclaration = GoStructFinder.findGoFieldDeclarationElement(goStructType,goFieldName);
        String recordComment = GoCommentFinder.findCommentString(goFieldDeclaration);

        // 如果当前的注释数据不等于记录的数据
        if (!nowComment.equals(recordComment)) {
            // 修改注释
            return new Runnable() {
                @Override
                public void run() {
                    synchronized (goFile) {
                        // 找到注释
                        PsiComment comment = GoCommentFinder.find(goFieldDeclaration);
                        if (comment != null) {
                            // 删除原来的注释
                            comment.delete();
                        }
                        // 如果修改的注释为"",那么直接返回，否则修改注释
                        if (nowComment == null || nowComment.trim().isEmpty()) {
                            return;
                        } else {
                            // 修改注释
                            PsiComment newComment = GoElementFactory.createComment(project,"// " + nowComment);
                            goFieldDeclaration.addBefore(newComment,null);
                        }
                    }
                }
            };
        }
        return null;

    }

    /**
     * 是否是一个新的go字段
     * @param goStructType
     * @param goFieldName
     * @return
     */
    @Override
    public boolean isNewGoFieldName(@NotNull GoStructType goStructType, @NotNull String goFieldName) {
        return GoStructFinder.findGoFieldNamedElement(goStructType,goFieldName) == null;
    }

    /**
     * 获取Go某一字段的Tag
     * @param goStructType 结构体
     * @param goFieldName 字段名
     * @return
     */
    @Override
    public String getGoFieldTag(@NotNull GoStructType goStructType, @NotNull String goFieldName) {
        GoFieldDeclaration goFieldDeclaration = GoStructFinder.findGoFieldDeclarationElement(goStructType,goFieldName);
        return goFieldDeclaration.getTag().getText();
    }

    /**
     * 修改某一字段的tag
     * @param goFile
     * @param goStructType
     * @param goFieldName
     * @param tagText
     * @return
     */
    @Override
    public Runnable modifyGoFieldTag(@NotNull GoFile goFile, @NotNull GoStructType goStructType, @NotNull String goFieldName, @NotNull String tagText) {

        // 获取当前所操作的项目
        Project project = goFile.getProject();

        // 执行构建
        return new Runnable() {
            @Override
            public void run() {
                synchronized (goFile) {
                    // 获取字段定义
                    GoFieldDeclaration goFieldDeclaration = GoStructFinder.findGoFieldDeclarationElement(goStructType,goFieldName);
                    GoTag tag = goFieldDeclaration.getTag();

                    // 创建tag
                    GoTag newTag = GoElementExpandFactory.createTag(project,tagText);

                    if (tag != null) {
                        // 执行tag修改任务
                        tag.replace(newTag);
                    } else {
                        goFieldDeclaration.addBefore( newTag ,null);
                    }
                }
            }
        };
    }

    /**
     * 获取Go某一字段的Tag某一标签的值
     * @param goStructType 结构体
     * @param goFieldName 字段名
     * @param key
     * @return
     */
    @Override
    public String getTagValue(@NotNull GoStructType goStructType, @NotNull String goFieldName, @NotNull String key) {
        return GoTagParser.parse(getGoFieldTag(goStructType,goFieldName),key);
    }

    /**
     * 修改某一字段的tag某一标签的值
     * @param goFile
     * @param goStructType
     * @param goFieldName
     * @param key
     * @param value
     * @return
     */
    @Override
    public Runnable setTagValue(@NotNull GoFile goFile, @NotNull GoStructType goStructType, @NotNull String goFieldName, @NotNull String key, @Nullable String value) {

        String keyTagContent = GoTagParser.getTag(getGoFieldTag(goStructType,goFieldName),key);
        String newKeyTagContent = key+":\""+ value + "\"";

        String newTag = null;
        String oldTag = getGoFieldTag(goStructType,goFieldName);

        if (keyTagContent != null) {
            if (value == null || value =="" ) {
                newTag = oldTag.replace(keyTagContent,"");
            } else {
                newTag = oldTag.replace(keyTagContent,newKeyTagContent);
            }
        } else {
            // 如果不存在该标签就直接添加一个
            newTag = newTag.trim();
            newTag = newTag.substring(0,newTag.length() - 1);
            newTag = newTag + " " + newKeyTagContent + "`";
        }
        return modifyGoFieldTag(goFile,goStructType,goFieldName,newTag);

    }
}
