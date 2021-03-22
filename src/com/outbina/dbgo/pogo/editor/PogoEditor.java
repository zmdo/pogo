package com.outbina.dbgo.pogo.editor;

import com.outbina.dbgo.pogo.ui.PogoEditorUI;
import com.intellij.diff.util.FileEditorBase;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PogoEditor extends FileEditorBase {

    public static String POGO_EDITOR_ID = "PO Table";

    private Project project;
    private VirtualFile virtualFile;

    private PogoEditorUI pogoEditorUI;

    // 用于初始化编辑器
    public PogoEditor(@NotNull Project project, @NotNull VirtualFile virtualFile){
        // 初始化面板
        pogoEditorUI = new PogoEditorUI(project,virtualFile);
    }

    // 是否发生改变
    @Override
    public boolean isModified() {
        // 判断其他浏览器是否发生了改变
        FileEditor[] editors = FileEditorManager.getInstance(project).getEditors(virtualFile);
        for (FileEditor editor : editors) {
            if(editor == this) {
                continue;
            } else if (editor.isModified()) {
                return true;
            }
        }
        // 判断编辑器界面是否发生了改变
        return false;
    }

    @Override
    public @NotNull JComponent getComponent() {

        // 这个过程会再切换时被调用
        pogoEditorUI.refresh();

        // 返回结果
        return pogoEditorUI.getComponent();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return pogoEditorUI.getPreferredFocusableComponent();
    }

    @Override
    public @NotNull String getName() {
        return POGO_EDITOR_ID;
    }
}
