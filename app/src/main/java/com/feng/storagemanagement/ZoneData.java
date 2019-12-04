package com.feng.storagemanagement;

/**
 * @author Feng Zhaohao
 * Created on 2019/12/4
 */
public class ZoneData {
    private int id;
    private int size;
    private String initialAddress;

    public ZoneData(int id, int size, String initialAddress) {
        this.id = id;
        this.size = size;
        this.initialAddress = initialAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getInitialAddress() {
        return initialAddress;
    }

    public void setInitialAddress(String initialAddress) {
        this.initialAddress = initialAddress;
    }
}
