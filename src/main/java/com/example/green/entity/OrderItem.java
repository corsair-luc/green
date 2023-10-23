package com.example.green.entity;


public class OrderItem {


    private long id;

    private String standard;

    private int num;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", standard='" + standard + '\'' +
                ", num=" + num +
                '}';
    }
}
