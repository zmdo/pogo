package com.outbina.dbgo.pogo.component;

import com.intellij.openapi.ui.ComponentContainer;

public interface IEditorContainer extends ComponentContainer {

    public void refresh();

    public boolean isModified();

}
