package com.example.dell.demo.OfferHelpRelative;

/*该类是历史帮助订单记录，其对象作为数据传给RecyclerView*/

import com.example.dell.demo.User;

public class OfferHelpRecord {

    //以下为显示的属性
    private String orderId;
    private User seeker;
    private String orderNotes;
    private String orderStatus;

    //以下为折叠的属性
    private String ownerName;
    private String ownerTel;
    private String ownerCode;

    public OfferHelpRecord(String orderId,User seeker,String orderNotes,String orderStatus,
                            String ownerName,String ownerTel,String ownerCode){
        this.orderId=orderId;
        this.seeker=seeker;
        this.orderNotes=orderNotes;
        this.orderStatus=orderStatus;
        this.ownerName=ownerName;
        this.ownerTel=ownerTel;
        this.ownerCode=ownerCode;

    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderNotes() {
        return orderNotes;
    }

    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerTel() {
        return ownerTel;
    }

    public void setOwnerTel(String ownerTel) {
        this.ownerTel = ownerTel;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public User getSeeker() {
        return seeker;
    }

    public void setSeeker(User seeker) {
        this.seeker = seeker;
    }
}
