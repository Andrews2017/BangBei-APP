package com.example.dell.demo.Activities;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.demo.AskHelpRelative.AskHelpRecord;
import com.example.dell.demo.OfferHelpRelative.OfferHelpRecord;
import com.example.dell.demo.OfferHelpRelative.OfferHelpRecordAdapter;
import com.example.dell.demo.Order;
import com.example.dell.demo.R;
import com.example.dell.demo.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class OfferHelpRecordActivity extends AppCompatActivity {

    private Context mContext;
    private RecyclerView offerHelpRecordRV;
    private List<OfferHelpRecord> offerHelpRecordList=new ArrayList<>();

    private User currentUser;

    public final int QUERY_HELPED_ORDER_SUCCESS=1;
    public final int GET_CURRENT_USER_SUCCESS=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_help_record);

        initWidgets();

        Intent intent=getIntent();
        String userId=intent.getStringExtra("extra_userId");
        getCurrentUser(userId);

    }

    public void getCurrentUser(String userId){
        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",userId).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {

                User currentUser=list.get(0);
                Message msg=new Message();
                msg.what=GET_CURRENT_USER_SUCCESS;
                msg.obj=currentUser;
                helpedOrderHandler.sendMessage(msg);
            }
        });
    }

    /*获取该用户接的所有订单*/
    public void QueryOrderRecord(User currentUser){

        /*通过userId去Order下单表查询出该用户所有的帮助订单
          并把所有订单组成的数组通过msg发出来 */
        BmobQuery<Order> orderQuery=new BmobQuery<Order>();
        orderQuery.addWhereEqualTo("Helper",currentUser);
        orderQuery.include("Seeker");   //此处一定要获取求助者的信息，不然等会为空
        orderQuery.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        //背景挂什么图？？？“还没帮助过别人哦！”？？
                        Toast.makeText(mContext,"您还没有接过订单哦！",Toast.LENGTH_SHORT).show();
                    }else{
                        /*把遍历到的所有该用户接的单，转化为OfferHelpRecord对象保存在totalList中，并用msg发出*/
                        List<OfferHelpRecord> totalList=new ArrayList<OfferHelpRecord>();
                        for(int i=0;i<list.size();i++){
                            String orderId=list.get(i).getObjectId();
                            String orderNotes=list.get(i).getNotes();
                            String orderStatus=list.get(i).getOrderStatus();
                            User seeker=list.get(i).getSeeker();

                            String ownerName=list.get(i).getOwnerName();
                            String ownerTel=list.get(i).getOwnerTel();
                            String ownerCode=list.get(i).getOwnerCode();

                            OfferHelpRecord record=new OfferHelpRecord(orderId,seeker,orderNotes,orderStatus,ownerName,ownerTel,ownerCode);
                            totalList.add(record);
                        }
                        Message msg=new Message();
                        msg.what=QUERY_HELPED_ORDER_SUCCESS;
                        msg.obj=totalList;
                        helpedOrderHandler.sendMessage(msg);
                    }
                }else {
                    Toast.makeText(mContext,"获取订单失败！错误："+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void initWidgets(){

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.toolbarreturn2);
        }

        mContext=OfferHelpRecordActivity.this;
        offerHelpRecordRV=(RecyclerView)findViewById(R.id.recycler_view_offer_help_record);
    }

    public void RecyclerViewBind(){

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        offerHelpRecordRV.setLayoutManager(layoutManager);
        OfferHelpRecordAdapter adapter=new OfferHelpRecordAdapter(offerHelpRecordList);
        offerHelpRecordRV.setAdapter(adapter);

    }


    private Handler helpedOrderHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                /*先根据UserId获取当前用户的信息，再查询他所有的帮助记录*/
                case GET_CURRENT_USER_SUCCESS:
                    currentUser=(User)(msg.obj);
                    QueryOrderRecord(currentUser);
                    break;

                /*获取帮助订单记录成功后，先复制给总数组
                * 之后还需要对数组中每个记录的seekerName和seekerTel更新*/
                case QUERY_HELPED_ORDER_SUCCESS:
                    offerHelpRecordList.clear();
                    offerHelpRecordList=(List<OfferHelpRecord>)msg.obj;
                    RecyclerViewBind();
                    break;

            }
        }
    };


    /*点击标题栏左边按钮，返回上一个活动*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                OfferHelpRecordActivity.this.finish();
                break;
        }
        return true;
    }

}