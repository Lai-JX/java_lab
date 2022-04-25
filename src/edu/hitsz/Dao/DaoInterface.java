package edu.hitsz.Dao;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DaoInterface {

    List<Record> getAllRecord();
    void readFromFile()throws IOException, ClassNotFoundException;
    void writeToFile() throws IOException;
    void add(Record record);
    Record findFirst();
    void sortAndPrintf();
    void showAll();
    void sort();
    int getLength();
    String[][] ToString();
    boolean delete(int index);
    Record findLast();

}
