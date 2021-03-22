package com.outbina.dbgo.tool;

import com.goide.psi.GoFieldDeclaration;
import com.goide.psi.GoSpecType;
import com.goide.psi.GoStructType;
import com.goide.psi.GoTag;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class GoElementExpandFactory {

    // 创建一个Tag
    public static final GoTag createTag(Project project,String text) {
        return createTag(project,text,null);
    }

    // 创建一个Tag
    public static final GoTag createTag(Project project,String text,@Nullable PsiElement context) {
        String structText =
                "package main\n" +
                        "type TagCreatorStruct struct { \n" +
                        "inner String " + text + "\n}\n";
        GoSpecType goType = (GoSpecType)(GoElementFactory.createType(project,structText,context));
        GoStructType goStructType = (GoStructType)(goType.getType());
        return goStructType.getFieldDeclarationList().get(0).getTag();
    }

    // 创建一个Go结构体里面的字段声明
    public static final GoFieldDeclaration createGoFieldDeclaration(Project project, String text) {
        return createGoFieldDeclaration(project,text,null);
    }

    // 创建一个Go结构体里面的字段声明
    public static final GoFieldDeclaration createGoFieldDeclaration(Project project, String text,@Nullable PsiElement context) {
        String structText =
                "package main\n" +
                        "type TagCreatorStruct struct { \n" + text + "\n}\n";
        GoSpecType goType = (GoSpecType)(GoElementFactory.createType(project,structText,context));
        GoStructType goStructType = (GoStructType)(goType.getType());
        return goStructType.getFieldDeclarationList().get(0);
    }
}
