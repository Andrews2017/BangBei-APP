package com.example.dell.demo;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class User extends BmobObject {

    private String UserTel;
    private String UserName;
    private String UserPwd;
    private Integer Property;
    private BmobFile Icon;

    public User(){}

    public User(String userTel,String userName,String passwd,int property,BmobFile icon){
        UserTel=userTel;
        UserName=userName;
        UserPwd=passwd;
        Property=property;
        Icon=icon;
    }

    public String getUserTel() {
        return UserTel;
    }

    public void setUserTel(String userTel) {
        UserTel = userTel;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPasswd() {
        return UserPwd;
    }

    public void setPasswd(String passwd) {
        UserPwd = passwd;
    }

    public Integer getProperty() {
        return Property;
    }

    public void setProperty(Integer balance) {
        Property = balance;
    }

    public BmobFile getIcon() {
        return Icon;
    }

    public void setIcon(BmobFile icon) {
        Icon = icon;
    }
}
