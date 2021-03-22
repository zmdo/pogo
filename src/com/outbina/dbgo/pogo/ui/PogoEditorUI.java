package com.outbina.dbgo.pogo.ui;

import com.outbina.dbgo.pogo.component.IEditorContainer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBSplitter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class PogoEditorUI implements IEditorContainer {

    /////////////
    // 项目信息 //
    /////////////

    private Project project;
    private VirtualFile virtualFile;

    ////////////////
    // 各种面板信息 //
    ////////////////

    // 次级UI面板
    // private ControllerUI controllerUI ;
    private RelationSchemaEditorUI relationSchemaEditorUI ;

    // 主面板
    private JPanel mainPanel;
    private Splitter splitter ;
    private JPanel statusPanel;
    private JPanel contentPanel;

    private JComponent controllerPanelContainer;
    private JComponent tablePanelContainer;

    // 构造函数
    public PogoEditorUI (@NotNull Project project, @NotNull VirtualFile virtualFile) {
        //设置项目文件参数
        this.project = project;
        this.virtualFile = virtualFile;
    }

    private void createUIComponents() {
        // 获取控制面板UI 和 表编辑器UI
        relationSchemaEditorUI = new RelationSchemaEditorUI(project,virtualFile);

        tablePanelContainer = relationSchemaEditorUI.getComponent();

        // 设置分解
        splitter = new JBSplitter(false);
        splitter.setShowDividerControls(false);
        splitter.setFirstComponent(tablePanelContainer);
        splitter.setShowDividerIcon(false);
        splitter.setDividerWidth(3);

    }

    @Override
    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public JComponent getPreferredFocusableComponent() { return mainPanel; }

    @Override
    public void dispose() { }

    @Override
    public void refresh() {
        relationSchemaEditorUI.refresh();
    }

    @Override
    public boolean isModified() {
        return relationSchemaEditorUI.isModified();
    }
}
