package com.outbina.dbgo.pogo.editor;

import com.outbina.dbgo.pogo.PogoFileHelper;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

// DumbAware
public class PogoEditorProvider implements FileEditorProvider,DumbAware {

    // 获取编辑器的设置
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return PogoFileHelper.filter(virtualFile);
    }

    // 获取一个新的编辑器
    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        return new PogoEditor(project,virtualFile);
    }

    // 获取编辑器ID
    @Override
    public @NotNull String getEditorTypeId() {
        return PogoEditor.POGO_EDITOR_ID;
    }

    // 设置优先级策略
    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
