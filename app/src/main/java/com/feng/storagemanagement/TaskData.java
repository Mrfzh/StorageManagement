package com.feng.storagemanagement;

/**
 * @author Feng Zhaohao
 * Created on 2019/12/4
 */
public class TaskData {
    private int id;
    private int size;
    private String zoneId;
    private String initialAddress;
    private String state;

    public TaskData(int id, int size, String zoneId,
                    String initialAddress, String state) {
        this.id = id;
        this.size = size;
        this.zoneId = zoneId;
        this.initialAddress = initialAddress;
        this.state = state;
    }

    public String getInitialAddress() {
        return initialAddress;
    }

    public void setInitialAddress(String initialAddress) {
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

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
