package edu.hitsz.Dao;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecordDao implements DaoInterface{
    private ArrayList<Record> records;

    public RecordDao(){
        records = new ArrayList<Record>();

    }


    // 从文件读取序列，并反序列
    public void readFromFile() throws IOException, ClassNotFoundException{
        File file=new File("scoreRecord.txt");
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // 创建一个反序列化的流，指定要读取的文件
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("scoreRecord.txt"));
        // 把文件的内存，解析为一个Object对象
        Object o = ois.readObject();
        // 使用解析后的对象
        if(o instanceof ArrayList<?>){
            for(Object record : (List<?>) o){
                records.add(Record.class.cast(record));
            }
        }
        // 关闭资源
        ois.close();
    }

    // 写回文件中
    public void writeToFile() throws IOException{
        // 创建序列化流 指定文件目的地
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("scoreRecord.txt"));
        // 对对象序列化
            oos.writeObject(records);

        // 刷新流
        oos.flush();

        // 关闭流
        oos.close();
//        System.out.println("w"+records.size());
    }
    @Override
    public List<Record> getAllRecord(){
        return records;
    }

    @Override
    public void add(Record record){
        records.add(record);
    }

    @Override
    public Record findFirst(){
        return records.get(0);
    }

    public void sortAndPrintf(){
        Collections.sort(records,new SortByScore());
        for(int i = 0;i < records.size();i++){
            Record record = records.get(i);
            System.out.println("第" + (i+1) +"名：" + record.getName() + " , " + record.getScore() + " , " + record.getTime());
        }
    }

    public void showAll(){
        for(Record record : records){
            System.out.println(record.getName() + "  " + record.getScore() + "  " + record.getTime());
        }
    }
}



// 为排序创建的类
class SortByScore implements Comparator {
    @Override
    public int compare(Object o1,Object o2){
        Record u1 = (Record) o1;
        Record u2 = (Record) o2;
        if(u1.getScore() < u2.getScore()){
            return 1;
        }else if(u1.getScore() == u2.getScore()){
            return 0;
        }
        return -1;
    }
}