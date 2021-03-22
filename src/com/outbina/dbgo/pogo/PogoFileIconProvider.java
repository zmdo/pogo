package com.outbina.dbgo.pogo;

import com.outbina.dbgo.DbgoIcons;
import com.intellij.ide.FileIconProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PogoFileIconProvider implements DumbAware, FileIconProvider {

    public PogoFileIconProvider(){}

    @Override
    public @Nullable Icon getIcon(@NotNull VirtualFile virtualFile, int i, @Nullable Project project) {

        // 判断是否为 null
        if (virtualFile == null) {
            throw new NullPointerException();
        }

        // 判断后缀
        if(PogoFileHelper.filter(virtualFile)){
            return DbgoIcons.POGO;
        }

        return null;
    }
}
