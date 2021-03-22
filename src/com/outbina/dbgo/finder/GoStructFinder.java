package com.outbina.dbgo.finder;

import com.goide.GoFileType;
import com.goide.psi.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class GoStructFinder {

    /**
     * 寻找go结构体
     * @param goFile Go类型的PsiFile
     * @param filter 过滤器
     * @return 查找到的Go结构体PsiElement对象的（结构体名-->结构体对象）映射
     */
    public static Map<String,GoStructType> find(@NotNull GoFile goFile, @NotNull Pattern filter)  {

        Map<String,GoStructType> structs = null;

        // 获取所有类型定义
        Collection<? extends GoTypeSpec> types = goFile.getTypes();
        structs = new HashMap<String,GoStructType>();

        // 遍历所需的类型
        for(GoTypeSpec type : types) {
            GoType goType = type.getSpecType().getType();
            if (filter == null || filter.matcher(type.getName()).find()) {
                // 对类型进行判断，是否是go结构体的类型
                if (goType instanceof GoStructType) {
                    GoStructType goStructType = (GoStructType)goType;
                    structs.put(type.getName(),goStructType);
                }
            }
        }

        return structs ;
    }

    /**
     * 寻找go结构体
     * @param project 项目
     * @param virtualFile 虚拟文件
     * @param filter 过滤器（正则表达式）
     * @return 查找到的Go结构体PsiElement对象的（结构体名-->结构体对象）映射
     */
    public static Map<String,GoStructType> find(@NotNull Project project, @NotNull VirtualFile virtualFile, @NotNull Pattern filter) {
        PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
        // PsiFile file = PsiManagerEx.getInstance(project).findFile(virtualFile);
        // final FileType GO_FILE_TYPE = FileTypeManager.getInstance().getFileTypeByExtension("go");

        // 如果是go文件
        if (file.getFileType().equals(GoFileType.INSTANCE)) {

            // 使用go插件提供go文件的定义
            GoFile goFile = (GoFile) file;
            return find(goFile,filter);

        }
        return  null;
    }

    /**
     * 获取Go结构体内的字段声明
     * @param structType Go结构定义
     * @param fieldName 想要寻找的字段名
     * @return 对于寻找到的$fieldName字段的声明
     */
    public static GoFieldDeclaration findGoFieldDeclarationElement(GoStructType structType,String fieldName){
        List<GoFieldDeclaration> fieldDeclarations = structType.getFieldDeclarationList();
        for ( GoFieldDeclaration fieldDeclaration : fieldDeclarations ) {
            List<GoFieldDefinition> fieldDefinitions = fieldDeclaration.getFieldDefinitionList();
            if(!fieldDefinitions.isEmpty() && fieldDefinitions.get(0).getName().equals(fieldName)) {
                return fieldDeclaration;
            }
        }
        return null;
    }

    /**
     * 查找Go结构体字段的NamedElement，这个对象可以调用重构的重命名方法来进行重命名
     * @param structType Go结构体PsiElement对象，这里是 GoStructType
     * @param fieldName 想要查询的字段名
     * @return 返回可命名元素
     */
    public static GoNamedElement findGoFieldNamedElement(GoStructType structType,String fieldName){
        // 遍历结果
        List<GoNamedElement> fields = structType.getFieldDefinitions();
        for (GoNamedElement field : fields) {
            if(fieldName.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

}
