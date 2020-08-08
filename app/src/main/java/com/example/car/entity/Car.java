package com.example.car.entity;

public class Car {

    private int cid;
    private String cnumber;
    private String cname;
    private String cphone;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCnumber() {
        return cnumber;
    }

    public void setCnumber(String cnumber) {
        this.cnumber = cnumber;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCphone() {
        return cphone;
    }

    public void setCphone(String cphone) {
        this.cphone = cphone;
    }

    @Override
    public String toString() {
        return "Car{" +
                "cid=" + cid +
                ", cnumber='" + cnumber + '\'' +
                ", cname='" + cname + '\'' +
                ", cphone='" + cphone + '\'' +
                '}';
    }
}
