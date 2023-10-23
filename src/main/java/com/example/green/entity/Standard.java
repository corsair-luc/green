package com.example.green.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.math.BigDecimal;

@TableName("good_standard")
public class Standard extends Model<Standard> {


    private Integer goodId;


    private String value;


    private BigDecimal price;


    private Integer store;

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStore() {
        return store;
    }

    public void setStore(Integer store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "Standard{" +
                "goodId=" + goodId +
                ", value='" + value + '\'' +
                ", price=" + price +
                ", store=" + store +
                '}';
    }
}