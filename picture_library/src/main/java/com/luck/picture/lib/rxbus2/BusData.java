package com.luck.picture.lib.rxbus2;

/**
 * RxBus data
 * Created by gorden on 2016/7/8.
 */
public class BusData {
    String id;
    String status;
    public BusData() {}
    public BusData(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
