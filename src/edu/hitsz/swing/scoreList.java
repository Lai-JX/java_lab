package edu.hitsz.swing;

import edu.hitsz.Dao.Record;
import edu.hitsz.Dao.RecordDao;
import edu.hitsz.application.AbstractGame;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class scoreList {
    private JPanel MainPanel;
    private JPanel topPanel;
    private JPanel buttomPanel;
    private JLabel headerLabel;
    private JLabel listLabel;
    private JTable scoreTable;
    private JScrollPane tableScorllPanel;
    private JButton deleteButton;
    private JOptionPane optionPane;
    private RecordDao dao;
    private DefaultTableModel model;

    public scoreList() {
        if(chooseDifficulty.getMode()==1){
            headerLabel.setText("难度Easy");
            dao = new RecordDao("easyModelScore.txt");
        }else if(chooseDifficulty.getMode()==2){
            headerLabel.setText("难度Common");
            dao = new RecordDao("commonModelScore.txt");
        }else {
            headerLabel.setText("难度Difficult");
            dao = new RecordDao("difficultModelScore.txt");
        }
        // 输入姓名
        String name = JOptionPane.showInputDialog("游戏结束。你的得分是："+ AbstractGame.score+"\n请输入名字");
        // 增加记录
        dao.add(new Record(name,AbstractGame.score));
        // 写回文件
        try {
            dao.writeToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TableData();


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,"是否确定删除选中的玩家","选择一个选项",JOptionPane.YES_NO_CANCEL_OPTION);
//                System.out.println(n);
//                TableData();
                int row = scoreTable.getSelectedRow();
//                System.out.println(row);
                if(row!=-1 && n==JOptionPane.YES_OPTION){
//                    model.removeRow(row);

                    Record deleteRecord = dao.getAllRecord().get(row);
                    System.out.println("删除的数据：\n 玩家名："+deleteRecord.getName() + "\t得分："+deleteRecord.getScore() + "\t记录时间：" + deleteRecord.getTime());
                    dao.delete(row);
                    TableData();
                }
            }
        });
    }

    public void TableData(){
        String[] columnName={"名次","玩家名","得分","记录时间"};
//        String[][] tableData={{"001","Lily","78"},{"002","Jane","89"},{"003","Tom","98"},{"004","Andy","97"},{"005","John","68"},{"006","Nancy","77"}};
        // 排序
        dao.sort();
        // 将数据转化为字符串
        String[][] tableData=dao.ToString();

//        String[][] = dao.getAllRecord();
        // 表格模型
        model = new DefaultTableModel(tableData,columnName){
            @Override
            public boolean isCellEditable(int row,int col){
                return false;
            }
        };
        // 从表格模型那里获取数据
        scoreTable.setModel(model);
        tableScorllPanel.setViewportView(scoreTable);
    }

    public JPanel getMainPanel() {
        return MainPanel;
    }
}
