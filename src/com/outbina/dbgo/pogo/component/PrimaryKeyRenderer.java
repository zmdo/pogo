package com.outbina.dbgo.pogo.component;

import com.outbina.dbgo.DbgoIcons;
import com.intellij.ui.components.JBLabel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.Serializable;

// 主键按钮的渲染器
public class PrimaryKeyRenderer implements TableCellRenderer,Serializable {

    public static final Icon KEY_ICON = DbgoIcons.KEY;

    private JBLabel viewLabel;
    public PrimaryKeyRenderer() {
        viewLabel = new JBLabel();
        viewLabel.setOpaque(true);
        viewLabel.setHorizontalAlignment(JBLabel.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            viewLabel.setForeground(table.getSelectionForeground());
            viewLabel.setBackground(table.getSelectionBackground());
        } else {
            viewLabel.setForeground(table.getForeground());
            viewLabel.setBackground(table.getBackground());
        }

        if (Boolean.TRUE.equals(value)) {

            // 将图标设置为钥匙的图标
            viewLabel.setIcon(KEY_ICON);

            // 检查是第几个键值
            int index = 1;
            for (int i = 0 ; i < row ; i ++ ) {
                if (table.getModel().getValueAt(i,column).equals(Boolean.TRUE)) {
                    index ++ ;
                }
            }

            // 设置数值
            viewLabel.setText(String.valueOf(index));
        } else {
            viewLabel.setIcon(null);
            viewLabel.setText(null);
        }
        return viewLabel;
    }
}
