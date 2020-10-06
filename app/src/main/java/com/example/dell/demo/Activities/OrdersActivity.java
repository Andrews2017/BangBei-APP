package com.example.dell.demo.Activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dell.demo.MyAnimator;
import com.example.dell.demo.NavigateLayout;
import com.example.dell.demo.OrderRelative.MyItemDecoration;
import com.example.dell.demo.Order;
import com.example.dell.demo.OrderRelative.OrderAdapter;
import com.example.dell.demo.OrderRelative.OrderSortHelper;
import com.example.dell.demo.R;
import com.example.dell.demo.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;

public class OrdersActivity extends AppCompatActivity implements View.OnClickListener{

    private int coinArrowTag=0;
    private int distanceArrowTag=0;

    private Context mContext;
    private ArrayList<Order> orderList=new ArrayList<>();
    private RecyclerView orderRecyclerView;

    private String UserId;

    private Spinner spinner;
    private TextView btnSortByCoin;
    private TextView btnSortByDistance;
    private TextView btnSortALL;
    private ImageView imgCoinArrow;
    private ImageView imgDistanceArrow;
    private NavigateLayout ordersNavigation;

    public ImageLoader imageLoader = ImageLoader.getInstance();
    public final int INIT_ORDER_SUCCESS=1;
    public final int QUERY_USERID_SUCCESS=2;
    public final int NO_ORDERS_AT_ALL=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        Bmob.initialize(this,"652bcff3f5aeb5d29c1a99b18603f8c1");
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        Intent intent=getIntent();
        String userTel=intent.getStringExtra("tel_extra");
        QueryUserId(userTel);

    }

    /*向服务器查询所有订单信息，并通过msg发消息和传数据*/
    public void QueryOrders(){
        BmobQuery<Order> ordersQuery = new BmobQuery<Order>();
        ordersQuery.addWhereEqualTo("OrderStatus","未被接单");
        ordersQuery.include("Seeker");   //一定记得此处要把帮助者的信息一起查询出来，方便后面一气呵成
        ordersQuery.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        Toast.makeText(mContext,"当前暂无订单，请刷新！",Toast.LENGTH_SHORT).show();
                        Message msg=new Message();
                        msg.what=NO_ORDERS_AT_ALL;
                        orderHandler.sendMessage(msg);
                    }else{
                        Message msg=new Message();
                        msg.what=INIT_ORDER_SUCCESS;
                        msg.obj=list;
                        orderHandler.sendMessage(msg);
                    }
                }else{
                    Toast.makeText(mContext,"获取订单失败，请重试！",Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext,"错误信息："+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    /*该类是Spinner监听器的接口类*/
    private class SpinnerOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        private boolean isFirst=true;

        @Override  //Spinner点击事件的逻辑
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            if(!isFirst){
                 /*还原“金币按钮”*/
                coinArrowTag=0;
                btnSortByCoin.setTextColor(Color.argb(0xff,0x00,0x00,0x00));
                imgCoinArrow.setImageResource(R.drawable.heiheijiantou);

                /*还原“距离”按钮*/
                distanceArrowTag=0;
                btnSortByDistance.setTextColor(Color.argb(0xff,0x00,0x00,0x00));
                imgDistanceArrow.setImageResource(R.drawable.heiheijiantou);

                /*还原“综合”按钮*/
                btnSortALL.setTextColor(Color.argb(0xff,0x00,0x00,0x00));

                String selected=parent.getItemAtPosition(pos).toString();
                Toast.makeText(view.getContext(), "你选择的是:"+selected, Toast.LENGTH_SHORT).show();
                switch (selected){
                    case "全部":
                        RecyclerViewBind(orderList,orderRecyclerView);
                        break;
                    default:
                        RecyclerViewBind(OrderSortHelper.sortBySpinner(orderList,selected),orderRecyclerView);
                        break;
                }
            }

            isFirst=false;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    /*根据登录界面传来的Tel去获取UserId*/
    public void QueryUserId(String userTel){
        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("UserTel",userTel).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    UserId=list.get(0).getObjectId();
                    Message msg=new Message();
                    msg.what=QUERY_USERID_SUCCESS;
                    orderHandler.sendMessage(msg);
                }else{
                    Toast.makeText(mContext,"获取UserId失败！"+e.getMessage()+"code="+e.getErrorCode(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){

            case R.id.btnSortByCoin:
                SortByCoin();
                break;

            case R.id.btnSortByDistance:
                SortByDistance();
                break;

            case R.id.btnSortAll:
                SortAll();
                break;
        }
    }

    public void SortByCoin(){

        MyAnimator.StartPlanAnimation(btnSortByCoin);

        /*还原“距离”按钮*/
        distanceArrowTag=0;
        btnSortByDistance.setTextColor(Color.argb(0xff,0x00,0x00,0x00));
        imgDistanceArrow.setImageResource(R.drawable.heiheijiantou);

        /*还原“综合”按钮*/
        btnSortALL.setTextColor(Color.argb(0xff,0x00,0x00,0x00));

        if(coinArrowTag %2 ==1){   //将金币从高到低排序
            imgCoinArrow.setImageResource(R.drawable.heihongjiantou);
            btnSortByCoin.setTextColor(Color.argb(0xff,0xFF,0x00,0x00));
            OrderSortHelper.sortByCoin(orderList,1);
        }else {   //如果第一次点或连续偶数次点该按钮，将金币从低到高排序
            imgCoinArrow.setImageResource(R.drawable.hongheijiantou);
            btnSortByCoin.setTextColor(Color.argb(0xff,0xFF,0x00,0x00));
            OrderSortHelper.sortByCoin(orderList,0);
        }
        coinArrowTag+=1;
        RecyclerViewBind(orderList,orderRecyclerView);
        Toast.makeText(mContext,"排序成功！",Toast.LENGTH_SHORT).show();
    }

    public void SortByDistance(){
        MyAnimator.StartPlanAnimation(btnSortByDistance);

        /*还原“金币按钮”*/
        coinArrowTag=0;
        btnSortByCoin.setTextColor(Color.argb(0xff,0x00,0x00,0x00));
        imgCoinArrow.setImageResource(R.drawable.heiheijiantou);

        /*还原“综合”按钮*/
        btnSortALL.setTextColor(Color.argb(0xff,0x00,0x00,0x00));

        if(distanceArrowTag %2 ==1){  //如果偶数次点按钮，则从远到近排序
            imgDistanceArrow.setImageResource(R.drawable.heihongjiantou);
            btnSortByDistance.setTextColor(Color.argb(0xff,0xFF,0x00,0x00));
            OrderSortHelper.sortByDistance(orderList,0);
        }else {  //如果第一次点或奇数次点该按钮，将按距离从近到远排序
            imgDistanceArrow.setImageResource(R.drawable.hongheijiantou);
            btnSortByDistance.setTextColor(Color.argb(0xff,0xFF,0x00,0x00));
            OrderSortHelper.sortByDistance(orderList,1);
        }
        distanceArrowTag+=1;

        RecyclerViewBind(orderList,orderRecyclerView);
        Toast.makeText(mContext,"排序成功！",Toast.LENGTH_SHORT).show();
    }

    public void SortAll(){
        MyAnimator.StartPlanAnimation(btnSortALL);

        /*还原“金币”按钮*/
        coinArrowTag=0;
        btnSortByCoin.setTextColor(Color.argb(0xff,0x00,0x00,0x00));
        imgCoinArrow.setImageResource(R.drawable.heiheijiantou);

         /*还原“距离”按钮*/
        distanceArrowTag=0;
        btnSortByDistance.setTextColor(Color.argb(0xff,0x00,0x00,0x00));
        imgDistanceArrow.setImageResource(R.drawable.heiheijiantou);

        /*将本按钮设置为红色*/
        btnSortALL.setTextColor(Color.argb(0xff,0xff,0x00,0x00));

        Toast.makeText(mContext,"请稍等...",Toast.LENGTH_SHORT).show();
        //点“综合”按钮，则重新向服务器获取订单信息，并按混乱顺序展示
        QueryOrders();

    }

    public void initWidgets(){

        mContext=OrdersActivity.this;

        ordersNavigation=(NavigateLayout)findViewById(R.id.orders_navigation);
        ordersNavigation.setUserId(UserId);

        /*初始化RecyclerView*/
        orderRecyclerView=(RecyclerView)findViewById(R.id.requestRecyclerView);
        orderRecyclerView.addItemDecoration(new MyItemDecoration());
        orderRecyclerView.setBackgroundColor(Color.argb(0xff,0xCC,0xCC,0xCC));

        /*初始化按钮*/
        btnSortByCoin=(TextView) findViewById(R.id.btnSortByCoin);
        btnSortByDistance=(TextView) findViewById(R.id.btnSortByDistance);
        btnSortALL=(TextView)findViewById(R.id.btnSortAll);

        btnSortByCoin.setOnClickListener(this);
        btnSortByDistance.setOnClickListener(this);
        btnSortALL.setOnClickListener(this);
        imgCoinArrow=(ImageView)findViewById(R.id.coin_arrow);
        imgDistanceArrow=(ImageView)findViewById(R.id.distance_arrow);

        /*初始化Spinner*/
        spinner = (Spinner) findViewById(R.id.spinner);
        String[] mainAddrItems ={"全部","商业街","西门桥头","校外"};
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, R.layout.spinner_style, mainAddrItems);
        adapter.setDropDownViewResource(R.layout.spinner_expanded_style);
        spinner.setAdapter(adapter);
        spinner.setGravity(Gravity.CENTER);

        spinner.setOnItemSelectedListener(new SpinnerOnItemSelectedListener());

    }


    /*用order数组填充RecyclerView控件*/
    public void RecyclerViewBind(List<Order> orderList,RecyclerView recyclerView){

        OrderAdapter orderAdapter=new OrderAdapter(UserId,orderList);
        recyclerView.setAdapter(orderAdapter);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

    }


    /*根据子线程发出的消息，handler进行不同的操作*/
    private Handler orderHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                /*先通过Tel查询到UserId后再初始化控件，因为NavigationLayout需要UserId*/
                case QUERY_USERID_SUCCESS:
                    initWidgets();
                    QueryOrders();
                    break;

                case NO_ORDERS_AT_ALL:
                    orderList.clear();
                    RecyclerViewBind(orderList,orderRecyclerView);
                    break;


                /*一旦第一次从服务器请求到订单数据，就把它赋值给orderList
                * 此后，该orderList就作为各种排序和筛选的数据源*/
                case INIT_ORDER_SUCCESS:

                    orderList.clear();
                    orderList=(ArrayList<Order>)(msg.obj);
                    RecyclerViewBind(orderList,orderRecyclerView);
                    break;

            }
        }
    };

    /*此函数只能执行一次。否则会产生重复数据*/
//    public void initOrders(){
//        Order order1 =new Order("nND91112",12,"商业街","韵达快递","本科二组团","17713577412","邵慧","6-4-2012","请中午12点之前送到","未被接单");
//        order1.save();
//        Order order2=new Order("nvRqXXXv",8,"西门桥头","百世快递","本科11栋519","18483680996","杨双吉","3011","易碎品，请轻拿轻放。我正在外面忙时间很紧，麻烦你到时候把东西交给我的室友，他们在寝室","未被接单");
//        order2.save();
//        Order order3=new Order("d9af3d3df1",10,"商业街","中通快递","本科11栋519","18482285298","张孟华","5193","总共有两个小东西","未被接单");
//        order3.save();
//        Order order4=new Order("65a9c654ce",9,"商业街","天天快递","留学生宿舍","13264361782","倪永","8429-2","我在寝室楼下等你送过来，多谢！","未被接单");
//        order4.save();
//        Order order5=new Order("e503718090",9,"商业街","圆通快递","本科二组团","18298304472","余晓玥","7-2-4891","希望你能送到我们寝室","未被接单");
//        order5.save();
//        Order order6=new Order("f1c1ae17ad",8,"校外","零时快递点","本科11栋520","17281790824","吴飞","1051","是一个大件，可能有点重","未被接单");
//        order6.save();
//        Order order7=new Order("304580b391",11,"西门桥头","百世快递","本科11栋519","13784200924","koc","3-9-3151","请下午吃饭的时候送给我，我在楼底下等着","未被接单");
//        order7.save();
//        Order order8=new Order("123c081c9e",7,"商业街","中通快递","本科11栋519","18492818980","陈业","4-3-3616","无","未被接单");
//        order8.save();
//    }


}
