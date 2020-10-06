package com.example.dell.demo.AskHelpRelative;
import com.example.dell.demo.User;


/*该类是历史求助订单记录，其对象作为数据传给RecyclerView*/

public class AskHelpRecord {
    private String orderId;
    private User helper;
    private String orderNotes;
    private String orderStatus;


    public AskHelpRecord(String orderId,User helper,String orderNotes,String orderStatus){
        this.orderId=orderId;
        this.helper=helper;
        this.orderNotes=orderNotes;
        this.orderStatus=orderStatus;
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

    public User getHelper() {
        return helper;
    }

    public void setHelper(User helper) {
        this.helper = helper;
    }
}