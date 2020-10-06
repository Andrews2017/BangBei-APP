package com.example.dell.demo;
import android.content.Context;
import android.graphics.Bitmap;

import cn.bmob.v3.BmobObject;


public class Order extends BmobObject {

    private Integer Coins;
    private String MainAddr;
    private String DetailAddr;
    private String OwnerAddr;
    private String OwnerTel;
    private String OwnerName;
    private String OwnerCode;
    private String Notes;
    private String OrderStatus;

    private Bitmap icon;   //简便起见，为每个Order添加头像，方便与适配器对接

    private User Seeker;
    private User Helper;

    public Order(){}

    public String getMainAddr() {
        return MainAddr;
    }

    public void setMainAddr(String mainAddr) {
        MainAddr = mainAddr;
    }

    public String getDetailAddr() {
        return DetailAddr;
    }

    public void setDetailAddr(String detailAddr) {
        DetailAddr = detailAddr;
    }

    public String getOwnerAddr() {
        return OwnerAddr;
    }

    public void setOwnerAddr(String ownerAddr) {
        OwnerAddr = ownerAddr;
    }

    public String getOwnerTel() {
        return OwnerTel;
    }

    public void setOwnerTel(String ownerTel) {
        OwnerTel = ownerTel;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getOwnerCode() {
        return OwnerCode;
    }

    public void setOwnerCode(String ownerCode) {
        OwnerCode = ownerCode;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public Integer getCoins() {
        return Coins;
    }

    public void setCoins(Integer coins) {
        Coins = coins;
    }

    public Order(int coins, String mainAddr, String detailAddr, String ownerAddr, String ownerTel, String ownerName, String ownerCode, String notes, String orderStatus){
        Coins=coins;
        MainAddr=mainAddr;
        DetailAddr=detailAddr;
        OwnerAddr=ownerAddr;
        OwnerTel=ownerTel;
        OwnerName=ownerName;
        OwnerCode=ownerCode;
        Notes=notes;
        OrderStatus=orderStatus;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public User getSeeker() {
        return Seeker;
    }

    public void setSeeker(User seeker) {
        Seeker = seeker;
    }

    public User getHelper() {
        return Helper;
    }

    public void setHelper(User helper) {
        Helper = helper;
    }
}
