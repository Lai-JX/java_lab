package edu.hitsz.Dao;

import java.util.List;
import java.util.Optional;

public interface DaoInterface {

    List<Record> getAllRecord();
    void add(Record record);
    Record findFirst();
    void sortAndPrintf();
    void showAll();

}
