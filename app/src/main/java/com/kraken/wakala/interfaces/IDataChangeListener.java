package com.kraken.wakala.interfaces;

import java.util.List;

public interface IDataChangeListener {
    void getListData(Boolean success);
    void getData(Object object);
    void deleteData(Object object);
    void updateData(Object object);
    void addData(Object object);
}
