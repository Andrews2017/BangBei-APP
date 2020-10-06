package com.example.dell.demo.OrderRelative;
import com.example.dell.demo.Order;
import com.example.dell.demo.OrderRelative.OrderAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OrderSortHelper {

    /*which为0表示从低到高排序，为1表示从高到低排序*/
    public static void sortByCoin(List<Order> orderList,int which){

        for(int i=0;i<orderList.size();i++){
            for(int j=i+1;j<orderList.size();j++){
                if(orderList.get(i).getCoins()<orderList.get(j).getCoins()){
                    Order tempOrder=orderList.get(i);
                    orderList.set(i,orderList.get(j));
                    orderList.set(j,tempOrder);
                }
            }
        }
        if(which==0){
            Collections.reverse(orderList);
        }
    }

    public static void sortByDistance(List<Order> orderList,int which){
        for(int i=0;i<orderList.size();i++){  //第一趟先把商业街的全部排序到前面
            for(int j=i+1;j<orderList.size();j++){
                if(orderList.get(i).getMainAddr().equals("商业街")==false
                        && orderList.get(j).getMainAddr().equals("商业街")==true){
                    Order tempOrder=orderList.get(i);
                    orderList.set(i,orderList.get(j));
                    orderList.set(j,tempOrder);
                    break;
                }
            }
        }
        for(int i=0;i<orderList.size();i++){  //第二趟再把西门桥头的排序到中间
            for(int j=i+1;j<orderList.size();j++){
                if(orderList.get(i).getMainAddr().equals("西门桥头")==false
                        && orderList.get(i).getMainAddr().equals("商业街")==false
                        && orderList.get(j).getMainAddr().equals("西门桥头")==true){
                    Order tempOrder=orderList.get(i);
                    orderList.set(i,orderList.get(j));
                    orderList.set(j,tempOrder);
                    break;
                }
            }
        }

        /*如果偶数次点按钮，则从远到近排序*/
        if(which==0){
            Collections.reverse(orderList);
        }

    }

    public static List<Order> sortBySpinner(List<Order> orderList,String place){
        List<Order> tempOrderList=new ArrayList<>();
        for(int i=0;i<orderList.size();i++){
            if(orderList.get(i).getMainAddr().equals(place)){
                tempOrderList.add(orderList.get(i));
            }
        }
        return tempOrderList;
    }

}
