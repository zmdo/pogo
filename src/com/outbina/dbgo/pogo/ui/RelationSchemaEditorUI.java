package com.outbina.dbgo.pogo.ui;

import com.goide.psi.GoFile;
import com.outbina.dbgo.i18n.PogoBundle;
import com.outbina.dbgo.pogo.GoSqlType;
import com.outbina.dbgo.pogo.component.IEditorContainer;
import com.outbina.dbgo.pogo.component.PrimaryKeyEditor;
import com.outbina.dbgo.pogo.component.PrimaryKeyRenderer;
import com.outbina.dbgo.pogo.mc.controller.RelationSchemaEditorController;
import com.outbina.dbgo.pogo.mc.controller.impl.RelationSchemaEditorControllerImpl;
import com.outbina.dbgo.pogo.mc.model.DatabaseFieldInfoBean;
import com.outbina.dbgo.pogo.mc.model.PoTableBean;
import com.outbina.dbgo.pogo.mc.model.RelationSchemaTableRowBean;
import com.outbina.dbgo.tool.CodeUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class RelationSchemaEditorUI implements IEditorContainer {

    // 新行的前缀默认命名
    public static final String NEW_FIELD_NAME_PREFIX = "NewField";
    // 命名检查
    public static final Pattern fieldNameFormatPattern = Pattern.compile("^[A-Z]\\w*$");

    // 表模式
    //public static Object[] columnNames = {"Go变量名","数据库列名", "SQL类型(Go类型)", "长度", "小数点", "不是null","键","注释"};
    public static Object[] columnNames = {
            PogoBundle.message("pogo.table.column.goFieldName"),
            PogoBundle.message("pogo.table.column.sqlFieldName"),
            PogoBundle.message("pogo.table.column.type"),
            PogoBundle.message("pogo.table.column.length"),
            PogoBundle.message("pogo.table.column.dot"),
            PogoBundle.message("pogo.table.column.notNull"),
            PogoBundle.message("pogo.table.column.primaryKey"),
            PogoBundle.message("pogo.table.column.comment"),};

    public static final int COL_GO_FIELD      = 0;
    public static final int COL_DB_FIELD      = 1;
    public static final int COL_TYPE          = 2;
    public static final int COL_LENGTH        = 3;
    public static final int COL_DOT           = 4;
    public static final int COL_NOT_NULL      = 5;
    public static final int COL_KEY           = 6;
    public static final int COL_FIELD_COMMENT = 7;

    // 表类型
    public static String[] SqlAndGoTypes ;
    static {
        SqlAndGoTypes = new String[GoSqlType.SQL_TYPES.length];
        for (int i = 0 ; i < GoSqlType.SQL_TYPES.length ; i ++) {
            SqlAndGoTypes[i] = GoSqlType.SQL_TYPES[i] + "(" + GoSqlType.GO_TYPES[i] + ")";
        }
    }

    // 记录的上次更改的时间戳
    private long modificationStampRecord = 0;

    // 控制器组件
    static RelationSchemaEditorController relationSchemaEditorController = new RelationSchemaEditorControllerImpl();

    // 更新前的数据
    private List<Object[]> tableData;

    // 全局文件控制
    private final Project project;
    private final VirtualFile virtualFile;
    private GoFile goFile;
    private String poStructName;
    private boolean modifiedFlag ;

    // 各种组件的定义
    private JPanel relationSchemaDesignTablePanelContainer;
    private JPanel relationSchemaDesignToolbar;
    private JPanel relationSchemaDesignTablePanel;
    private JTable relationSchemaDesignTable;
    private DefaultTableModel tableModel;
    private JButton saveButton;
    private JButton addFieldButton;
    private JButton insertFieldButton;
    private JButton deleteFieldButton;
    private JButton updateButton;
    private JButton upButton;
    private JButton downButton;
    private JButton refreshButton;

    public RelationSchemaEditorUI(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        this.project = project;
        this.virtualFile = virtualFile;
        this.modifiedFlag = false;
    }

    private void createUIComponents() {

        // 初始化表格
        initTable();

        // 初始化按钮
        initButtons() ;

    }

    // 初始化Table
    private void initTable() {

        // 表模式
        tableModel = new DefaultTableModel(columnNames,0);

        // 初始化表格视图实例
        relationSchemaDesignTable = new JBTable(tableModel);

        // 变量名框
        // TableColumn goFieldCol = relationSchemaDesignTable.getColumn(columnNames[0]);

        // 设置"类型"的下拉框
        TableColumn typeCol = relationSchemaDesignTable.getColumn(columnNames[COL_TYPE]);
        typeCol.setCellEditor(new DefaultCellEditor(new JComboBox<>(SqlAndGoTypes)));

        // 设置表的"不是null"列为选择框
        TableColumn notNullCol = relationSchemaDesignTable.getColumn(columnNames[COL_NOT_NULL]);
        notNullCol.setCellEditor(relationSchemaDesignTable.getDefaultEditor(Boolean.class));
        notNullCol.setCellRenderer(relationSchemaDesignTable.getDefaultRenderer(Boolean.class));

        // 设置表的"主键"定制显示
        TableColumn primaryKeyCol = relationSchemaDesignTable.getColumn(columnNames[COL_KEY]);
        primaryKeyCol.setCellRenderer(new PrimaryKeyRenderer());
        primaryKeyCol.setCellEditor(new PrimaryKeyEditor());

        // 添加监听器
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {

                int start = e.getFirstRow();
                int end = e.getLastRow();
                DefaultTableModel model = (DefaultTableModel) e.getSource();

                // 检查类型
                switch (e.getType()) {
                    case TableModelEvent.INSERT: // 添加数据
                        break;
                    case TableModelEvent.UPDATE: // 更新数据
                        // 检查Go变量名更新
                        modifyGoFieldName(model,start);
                        // 检查注释更新
                        modifyFieldComment(model,start);
                        // 检查与tag有关数据的更新
                        modifyGormTags(model,start);
                        // 更新数据显示
                        update();
                        break;
                    case TableModelEvent.DELETE: // 删除数据
                        deleteRow(start);
                        break;
                }

            }
        });

        // 添加监听器
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                modifiedFlag = true ;
            }
        });

        // 表数据更新
        update();

    }

    /**
     * 删除一行数据
     * @param needDeletedRowIndex 需要被删除行的索引
     */
    private void deleteRow(int needDeletedRowIndex) {
        // 获取字段名
        String fieldName = ((String) tableData.get(needDeletedRowIndex)[COL_GO_FIELD]).trim();
        relationSchemaEditorController.deleteField(goFile,poStructName,fieldName);
        tableData.remove(needDeletedRowIndex);
    }

    /**
     * 修改与tag有关数据的内容
     * @param model 被修改的模型
     * @param modifiedIndex 被修改的行
     */
    private void modifyGormTags(TableModel model, int modifiedIndex) {

        String sqlGoType = (String) tableModel.getValueAt(modifiedIndex, COL_TYPE);
        String sqlType = sqlGoType.replaceAll("\\(.*\\)","").trim(); // 括号外面的
        if(sqlType.isEmpty() || !GoSqlType.SQL_TO_GO_TYPE_MAP.containsKey(sqlType)) {
            sqlType = null;
        }

        Integer len ;
        Object lenObj = model.getValueAt(modifiedIndex, COL_LENGTH);
        if (lenObj != null && !lenObj.toString().trim().isEmpty()) {
            try {
                len = Integer.parseInt(lenObj.toString());
                if(len < 0) {
                    len = 0;
                }
            } catch (NumberFormatException e) {
                len = 0;
            }
        } else {
            len = -1;
        }

        Integer dot = null;
        Object dotObj = model.getValueAt(modifiedIndex, COL_DOT);
        if (dotObj != null && !dotObj.toString().trim().isEmpty()) {
            try {
                dot = Integer.parseInt(dotObj.toString());
                if(dot < 0) {
                    dot = 0;
                }
            } catch (NumberFormatException e) {
                dot = 0;
            }
        } else {
            dot = -1;
        }

        DatabaseFieldInfoBean databaseFieldInfoBean = new DatabaseFieldInfoBean(
                (String) model.getValueAt(modifiedIndex,COL_DB_FIELD),
                sqlType,
                len,
                dot,
                (Boolean) model.getValueAt(modifiedIndex,COL_NOT_NULL),
                (Boolean) model.getValueAt(modifiedIndex,COL_KEY)
        );
        // 获取字段名
        String fieldName = ((String) model.getValueAt(modifiedIndex, COL_GO_FIELD)).trim();
        relationSchemaEditorController.modifyTag(goFile,poStructName,fieldName,databaseFieldInfoBean);

    }

    /**
     * 修改Go变量字段名
     * @param model 被修改的模型
     * @param modifiedIndex 被修改的行
     */
    private void modifyGoFieldName(TableModel model,int modifiedIndex) {

        // 重构变量命名
        String nowGoFieldName = ((String) model.getValueAt(modifiedIndex, COL_GO_FIELD)).trim();
        String recordGoFieldName = ((String) tableData.get(modifiedIndex)[COL_GO_FIELD]).trim();

        // 修改命名
        int status =relationSchemaEditorController.modifyGoFieldName(goFile,poStructName,recordGoFieldName,nowGoFieldName);

        // 根据返回状态进行报错
        switch (status) {
            case RelationSchemaEditorController.GO_FIELD_NAME_IS_EMPTY:
                JOptionPane.showMessageDialog(null,"重命名不能为空","重命名字段错误",JOptionPane.ERROR_MESSAGE);
                if(!recordGoFieldName.isEmpty()) {
                    tableModel.setValueAt(recordGoFieldName,modifiedIndex, COL_GO_FIELD);
                }
                break;
            case RelationSchemaEditorController.GO_FIELD_NAMING_CONVENTIONS_ERROR:
                JOptionPane.showMessageDialog(null,"重命名格式有误，请重新输入!\n注:首字母必须大写","重命名字段错误",JOptionPane.ERROR_MESSAGE);
                if (fieldNameFormatPattern.matcher(recordGoFieldName).find()) {
                    tableModel.setValueAt(recordGoFieldName,modifiedIndex, COL_GO_FIELD);
                }
                break;
        }
    }

    /**
     * 修改注释内容
     * @param model 被修改的模型
     * @param modifiedIndex 被修改的行
     */
    private void modifyFieldComment(TableModel model,int modifiedIndex) {
        String nowGoFieldName = ((String) model.getValueAt(modifiedIndex, COL_GO_FIELD)).trim();
        String nowComment = ((String) model.getValueAt(modifiedIndex, COL_FIELD_COMMENT));
        relationSchemaEditorController.modifyGoFieldComment(goFile,poStructName,nowGoFieldName,nowComment);
    }

    /**
     * 这力将会对所有的按钮进行初始化并添加监听器
     */
    private void initButtons() {

        // 刷新按钮
        this.refreshButton = new JButton();
        refreshButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });

        // 添加按钮
        this.addFieldButton = new JButton();
        addFieldButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(tableData == null) {
                    return;
                }

                Object[] NewRowData = createNewRowData();
                tableData.add(NewRowData);
                tableModel.addRow(NewRowData);

                insertNewRow(tableData.size() - 2 , false ,(String)(NewRowData[COL_GO_FIELD]),(String) (NewRowData[COL_DB_FIELD]));
            }
        });

        // 插入按钮
        this.insertFieldButton = new JButton();
        insertFieldButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(tableData == null) {
                    return;
                }

                int rowIndex = relationSchemaDesignTable.getSelectedRow();
                Object[] NewRowData = createNewRowData();
                if(rowIndex < 0) {
                    tableData.add(NewRowData);
                    tableModel.addRow(NewRowData);
                    insertNewRow(tableData.size() - 2 ,false ,(String)(NewRowData[COL_GO_FIELD]),(String) (NewRowData[COL_DB_FIELD]));
                } else {
                    tableData.add(rowIndex,NewRowData);
                    tableModel.insertRow(rowIndex,NewRowData);
                    insertNewRow( rowIndex  ,true ,(String)(NewRowData[COL_GO_FIELD]),(String) (NewRowData[COL_DB_FIELD]));
                }
            }
        });

        // 删除按钮
        this.deleteFieldButton = new JButton();
        deleteFieldButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(tableData == null) {
                    return;
                }

                int[] rowIndexes = relationSchemaDesignTable.getSelectedRows();
                if (rowIndexes.length <= 0) return; // 如果没有选中那么不做处理

                int fieldColIndex = relationSchemaDesignTable.getColumn(columnNames[COL_DB_FIELD]).getModelIndex();
                StringBuilder fieldNames = new StringBuilder();

                // 获取所有要删除的列名
                fieldNames.append("(");
                for (int i = 0 ; i < rowIndexes.length ; i ++) {
                    fieldNames
                            .append(" ")
                            .append((String) tableModel.getValueAt(rowIndexes[i],fieldColIndex));
                    if (i < rowIndexes.length - 1) {
                        fieldNames.append(",");
                    }
                }
                fieldNames.append(")");

                // 弹出删除提示
                int ans = JOptionPane.showConfirmDialog(null,
                        "您确定要删除字段"  + fieldNames.toString() + "吗?",
                        "确认删除",JOptionPane.YES_NO_OPTION);
                if (ans == 0) {
                    // 必须先从小到大排序，然后才能执行删除
                    Arrays.sort(rowIndexes);
                    for (int i = 0 ; i < rowIndexes.length ; i ++) {
                        tableModel.removeRow(rowIndexes[i]-i);
                    }
                }
            }
        });

        // 上移按钮
        this.upButton = new JButton();
        upButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(tableData == null) {
                    return;
                }

                int[] rowIndexes = relationSchemaDesignTable.getSelectedRows();
                if (rowIndexes.length <= 0) return; // 如果没有选中那么不做处理

                // 检查连续性
                Arrays.sort(rowIndexes);
                int start = rowIndexes[0];
                int end = rowIndexes[rowIndexes.length - 1];
                if (end - start + 1 != rowIndexes.length ) {
                    JOptionPane.showMessageDialog(null,"不是连续的字段，不能进行移动","不能移动",JOptionPane.WARNING_MESSAGE);
                }


                // 判断是否可以移动
                if (start - 1 >= 0) {
                    // 交换数据位置
                    for(int i = start ; i < end + 1 ; i ++) {
                        Collections.swap(tableData , i , i - 1);
                    }

                    // 移动
                    moveRow(start,end,true);
                    // update();

                    // 移动
                    tableModel.moveRow(start,end,start - 1);
                    // 清除选择
                    relationSchemaDesignTable.getSelectionModel().clearSelection();
                    // 重新选择目标
                    relationSchemaDesignTable.addRowSelectionInterval(start - 1,end - 1);
                }

            }
        });

        // 下移按钮
        this.downButton = new JButton();
        downButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(tableData == null) {
                    return;
                }

                int[] rowIndexes = relationSchemaDesignTable.getSelectedRows();
                if (rowIndexes.length <= 0) return; // 如果没有选中那么不做处理

                // 检查连续性
                Arrays.sort(rowIndexes);
                int start = rowIndexes[0];
                int end = rowIndexes[rowIndexes.length - 1];
                if (end - start + 1 != rowIndexes.length ) {
                    JOptionPane.showMessageDialog(null,"不是连续的字段，不能进行移动","不能移动",JOptionPane.WARNING_MESSAGE);
                }

                // 判断是否可以移动
                if (end + 1 < tableModel.getRowCount()) {
                    // 交换数据位置
                    for(int i = end ; i >= start ; i --) {
                        Collections.swap(tableData , i , i + 1);
                    }

                    // 移动
                    moveRow(start,end,false);
                    // update();

                    // 移动
                    tableModel.moveRow(start,end,start + 1);
                    // 清除选择
                    relationSchemaDesignTable.getSelectionModel().clearSelection();
                    // 重新选择目标
                    relationSchemaDesignTable.addRowSelectionInterval(start + 1,end + 1);
                }
            }
        });

        // 保存按钮
        saveButton = new JButton();
        saveButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO
                update();
                // 将标签置为false
                modifiedFlag = false;
            }
        });
    }

    // TODO 移动
    private void moveRow(int start,int end,boolean up) {
        relationSchemaEditorController.moveRow(goFile,poStructName,start,end,up);
    }

    /**
     * 获取一个行新的数据
     * @return
     */
    private Object[] createNewRowData() {
        // 获取当前的行数
        int rowCount = tableModel.getRowCount();
        // 然后创建命名
        String newGoFieldName ;
        // 进行字段审核
        for (int i = rowCount;; i ++) {
            newGoFieldName = NEW_FIELD_NAME_PREFIX + i;
            if ( relationSchemaEditorController.isNewGoFieldName(goFile,poStructName,newGoFieldName) ) {
                break;
            }
        }
        // 构造数据库字段命名
        String newDBFieldName = CodeUtil.underScoreCase(newGoFieldName);
        Object[] newRowData = new Object[columnNames.length];
        newRowData[COL_GO_FIELD] = newGoFieldName;
        newRowData[COL_DB_FIELD] = newDBFieldName;
        newRowData[COL_TYPE] = SqlAndGoTypes[6]; // varchar(string)
        newRowData[COL_LENGTH] = "";
        newRowData[COL_DOT] = 0;
        newRowData[COL_NOT_NULL] = false;
        newRowData[COL_KEY] = false;
        newRowData[COL_FIELD_COMMENT] = "";
        return newRowData;
    }


    private void insertNewRow(int index , boolean up ,String newGoFieldName,String newDBFieldName) {
        relationSchemaEditorController.insertNewField(goFile,poStructName,index,up,newGoFieldName,newDBFieldName);
    }

    // 获取页面数据
    public String getText () {

        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile != null) {
            return psiFile.getText();
        }

        // 获取字符集
        Charset charset = virtualFile.getCharset();

        // 获取charset对象对应的解码器
        CharsetDecoder charsetDecoder = charset.newDecoder();

        // 获取具体数据
        String fileContent = null;
        try {
            ByteBuffer buffer = ByteBuffer.wrap(virtualFile.contentsToByteArray());
            CharBuffer charBuffer = charsetDecoder.decode(buffer);
            fileContent = new String(charBuffer.array());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileContent;
    }

    // 检查更改时间戳
    public void checkModificationStampAndUpdate() {

        // 获取最后更改的时间戳
        long modificationStamp ;
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if(psiFile != null) {
            modificationStamp = psiFile.getModificationStamp();
        } else {
            modificationStamp = virtualFile.getModificationStamp();
        }

        // 比较时间戳,如果不相等就去更新
        if (modificationStamp != this.modificationStampRecord) {
            this.modificationStampRecord = modificationStamp;
            update();
        }

    }

    /**
     * 数据更新 : 这里将会去除整个模型的数据，然后重新添加一次。
     */
    public void update() {

        // 获取go文件本身
        goFile = (GoFile)PsiManager.getInstance(project).findFile(virtualFile);

        List<PoTableBean> poTableBeans = relationSchemaEditorController.getPoTableBean(goFile);
        PoTableBean poTableBean ;

        if(poTableBeans != null && !poTableBeans.isEmpty()) {
            poTableBean = poTableBeans.get(0);
        } else {
            return;
        }

        this.poStructName = poTableBean.getTableName();
        RelationSchemaTableRowBean[] fieldBeans = poTableBean.getFieldBeans();

        // 初始化表内容
        Object[][] rows = new Object[fieldBeans.length][];
        int index = 0;
        for ( RelationSchemaTableRowBean fieldBean : fieldBeans ) {
            // 增加行
            Object[] rowData = new Object[columnNames.length];
            rowData[COL_GO_FIELD] = fieldBean.getGoFieldName();
            rowData[COL_DB_FIELD] = fieldBean.getDbFieldName();
            rowData[COL_TYPE] = fieldBean.getDbFieldType() + "(" + fieldBean.getGoFieldType() + ")";
            rowData[COL_LENGTH] = fieldBean.getLength() < 0 ? "" : fieldBean.getLength();
            rowData[COL_DOT] = fieldBean.getDot() < 0 ? "" : fieldBean.getDot();
            rowData[COL_NOT_NULL] = fieldBean.getNotNull();
            rowData[COL_KEY] = fieldBean.getPrimaryKey();
            rowData[COL_FIELD_COMMENT] = fieldBean.getComment();
            rows[index] = rowData;
            index ++ ;
        }

        // 清空数据
        this.tableModel.getDataVector().clear();
        for (Object[] row : rows) {
            if (rows != null) {
                this.tableModel.addRow(row);
            }
        }

        // 设置表模式
        relationSchemaDesignTable.setModel(tableModel);
        tableData = new LinkedList<>();
        tableData.addAll(Arrays.asList(rows));

        // 清除选择
        relationSchemaDesignTable.getSelectionModel().clearSelection();

    }

    @Override
    public JComponent getComponent() {
        return relationSchemaDesignTablePanelContainer;
    }

    @Override
    public JComponent getPreferredFocusableComponent() {
        return relationSchemaDesignTablePanelContainer;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void refresh() {
        checkModificationStampAndUpdate();
    }

    public boolean isModified() {
        return modifiedFlag;
    }
}
