package com.luck.picture.lib.rxbus2;

/**
 * @author：luck
 * @date：2016-7-08 16:21
 * @describe：Rx BusData
 */
public class BusData {
    String id;
    String status;

    public BusData() {
    }

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
