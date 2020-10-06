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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.dell.demo.AskHelpRelative.AskHelpRecord;
import com.example.dell.demo.AskHelpRelative.AskHelpRecordAdapter;
import com.example.dell.demo.Order;
import com.example.dell.demo.R;
import com.example.dell.demo.User;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AskHelpRecordActivity extends AppCompatActivity {

    private User currentUser;
    private Context mContext;
    private RecyclerView askHelpRecordRV;
    private List<AskHelpRecord> askHelpRecordList=new ArrayList<>();

    public final int QUERY_ORDER_RECORD_SUCCESS=1;
    public final int GET_CURRENT_USER_SUCCESS=4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_help_record);

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
                orderHandler.sendMessage(msg);
            }
        });
    }

    /*获取该用户下所有的订单*/
    public void QueryOrderRecord(User seeker){

        BmobQuery<Order> orderQuery=new BmobQuery<Order>();
        orderQuery.addWhereEqualTo("Seeker",seeker);  //找出当用户订单
        orderQuery.include("Helper");  //一定记得加Helper的信息，这样赋值可以一气呵成
        orderQuery.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        //背景挂什么图？？？
                        Toast.makeText(mContext,"您还没有下过订单哦！",Toast.LENGTH_SHORT).show();
                    }else{
                        /*把遍历到的所有该用户下的单，转化为AskHelpRecord对象保存在totalList中，并用msg发出*/
                        List<AskHelpRecord> totalList=new ArrayList<AskHelpRecord>();
                        for(int i=0;i<list.size();i++){

                            String orderId=list.get(i).getObjectId();
                            String orderNotes=list.get(i).getNotes();
                            String orderStatus=list.get(i).getOrderStatus();
//                            String helperId=list.get(i).getHelperId();
                            User helper=list.get(i).getHelper();
                            /*为了节省属性列的数量，先把helperId暂时存在Name里面*/
                            AskHelpRecord record=new AskHelpRecord(orderId,helper,orderNotes,orderStatus);
                            totalList.add(record);
                        }
                        Message msg=new Message();
                        msg.what=QUERY_ORDER_RECORD_SUCCESS;
                        msg.obj=totalList;
                        orderHandler.sendMessage(msg);
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

        mContext= AskHelpRecordActivity.this;
        askHelpRecordRV=(RecyclerView)findViewById(R.id.recycler_view_ask_help_record);

    }

    public void RecyclerViewBind(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        askHelpRecordRV.setLayoutManager(layoutManager);
        AskHelpRecordAdapter adapter=new AskHelpRecordAdapter(askHelpRecordList);
        askHelpRecordRV.setAdapter(adapter);
    }


    private Handler orderHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                /*先通过UserId获取当前用户信息，再去查询他下的求助单*/
                case GET_CURRENT_USER_SUCCESS:
                    currentUser=(User)(msg.obj);
                    QueryOrderRecord(currentUser);
                    break;


                /*如果查询该用户所有下单成功，则把返回的数据保存在全局变量中*/
                case QUERY_ORDER_RECORD_SUCCESS:
                    askHelpRecordList.clear();
                    askHelpRecordList=(List<AskHelpRecord>)msg.obj;
                    RecyclerViewBind();

            }
        }
    };

    /*点击标题栏左边按钮，返回上一个活动*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                AskHelpRecordActivity.this.finish();
                break;
        }
        return true;
    }


}

