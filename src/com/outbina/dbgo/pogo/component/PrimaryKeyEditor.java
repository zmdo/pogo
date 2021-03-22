package com.outbina.dbgo.pogo.component;

import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PrimaryKeyEditor extends DefaultCellEditor {
    private JBPanel button;
    private Boolean cellValue ;

    public PrimaryKeyEditor() {
        super(new JCheckBox());
        button = new JBPanel();
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            public void mousePressed(MouseEvent e) {
                // 设置记录的值为相反的值
                cellValue = !cellValue;
                // 刷新渲染器
                fireEditingStopped();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        });

    }

    // 获取编辑器的值
    public JComponent getTableCellEditorComponent(JTable table, Object value,
                                                  boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }

        // value 源于单元格数值
        cellValue = (value == null || !(value instanceof Boolean)) ? Boolean.FALSE : (Boolean)value;
        return button;
    }

    // 获取当前的值
    public Object getCellEditorValue() {
        if(cellValue == null) return Boolean.FALSE;
        return cellValue;
    }
}
