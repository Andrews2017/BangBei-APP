package com.example.dell.demo.Activities;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dell.demo.Order;
import com.example.dell.demo.R;
import com.example.dell.demo.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

//   在点击确认接单之后，一定要记得再次查询次订单的状态。防止在这段时间已经被别人接单了
public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    private TextView userNameText;
    private de.hdodenhof.circleimageview.CircleImageView protraitImage;
    private TextView coinsText;
    private TextView itemAddrText;
    private TextView ownerAddrText;
    private TextView notesText;
    private Button btnConfirm;
    private Button btnCancel;
    private TextView ownerNameText;
    private TextView ownerTelText;
    private TextView staffCodeText;
    private TextView txtSecretHint;
    private TableLayout secretTable;
    private ImageView secretLine;
    private TextView txtHelpCount;

    private String orderId;
    private String userId;
    private String helperId;
    private int helpNumber;
    private User currentUser;

    public final int CHECK_ORDER_STATUS_SUCCESS=1;
    public final int CHECK_ORDER_STATUS_FAILURE=2;
    public final int GET_HELP_COUNT_SUCCESS=3;
    public final int GET_CURRENT_USER_SUCCESS=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);


        /*先获取该订单的orderId和userId*/
        Intent intent=getIntent();
        orderId=intent.getStringExtra("extra_orderId");
        userId=intent.getStringExtra("extra_userId");
        helperId=intent.getStringExtra("extra_helperId");

        getCurrentUser(helperId);
        QueryHelpCount(userId);

        initWidgets();

        QueryOrder();
        QueryProtrait(userId);

    }

    public void QueryProtrait(String userId){

        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",userId).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    BmobFile icon=list.get(0).getIcon();
                    if(icon!=null){   //如果没上传头像，则使用默认头像

                        String imagePath=icon.getFileUrl();  //获取已上传头像的URL地址

                        if(imagePath.length()!=0){  //如果上传了图片，则用ImageLoader开源库去加载
                            ImageLoader imageLoader=ImageLoader.getInstance();
                            imageLoader.displayImage(imagePath,protraitImage);
                        }else{
                            protraitImage.setImageResource(R.drawable.defaulthead);
                        }



//                        String fileName= list.get(0).getObjectId()+".PNG";
//                        File image = new File(getExternalCacheDir(), list.get(0).getObjectId()+".PNG");
//                        String path = getExternalCacheDir()+"/"+list.get(0).getObjectId()+".PNG";  //将要存在本地的URL路径
//
//                        if(image.exists()){  //如果已下载，则直接显示头像
////                            Bitmap exitsBitmap = BitmapFactory.decodeFile(path);
////                            protraitImage.setImageBitmap(exitsBitmap);
//                        }else{  //如果还未下载，则根据服务器端的URL地址开始下载图片到缓存
//                            try{
//                                image.createNewFile();
//                            }catch (IOException e2){
//                                e2.printStackTrace();
//                            }
//                            /*第一个参数是下载后的文件名，第三个参数是下载地址URL*/
//                            BmobFile bmobfile =new BmobFile(fileName,"",imagePath);
//                            downloadFile(bmobfile);
//                        }
                    }
                }else{
                    Toast.makeText(mContext,"查询用户失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*将文件下载到当前应用的默认缓存目录中，以file.getFilename()得到的值为文件名*/
    public void downloadFile(BmobFile file){

        File saveFile = new File(getExternalCacheDir(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){  //如果下载完成，就将它显示在ImageView上
                    Bitmap exitsBitmap = BitmapFactory.decodeFile(savePath);
                    protraitImage.setImageBitmap(exitsBitmap);
                }else{
                    Toast.makeText(mContext,"下载头像失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
            }

        });

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
                orderStatusHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnConfirmDetail:
                CheckOrderStatus();
                break;

            case R.id.btnCancelDetail:
                OrderDetailActivity.this.finish();
                break;

        }
    }


    /*“确认接单”按钮的响应事件，先检测此订单目前是否已经被接受*/
    public void CheckOrderStatus(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(mContext);
        dialog.setTitle("提示");
        dialog.setMessage("请问你确定要接收此单吗？\n接单后需要认真履行自己的职责哦");
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                BmobQuery<Order> orderQuery=new BmobQuery<Order>();
                orderQuery.addWhereEqualTo("objectId",orderId);
                orderQuery.findObjects(new FindListener<Order>() {
                    @Override
                    public void done(List<Order> list, BmobException e) {
                        if(e==null){
                            Message msg=new Message();
                            if(list.get(0).getOrderStatus().equals("未被接单")){
                                msg.what=CHECK_ORDER_STATUS_SUCCESS;
                            }else{
                                msg.what=CHECK_ORDER_STATUS_FAILURE;
                            }
                            orderStatusHandler.sendMessage(msg);
                        }
                        else{
                            Toast.makeText(mContext,"查询订单状态失败，请重试！错误："+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    /*接受订单，即修改Status字段*/
    public void ReceiveOrder(){
        Order receiveOrder = new Order();
        receiveOrder.setValue("Helper",currentUser);
        receiveOrder.setValue("OrderStatus","已被接单");
        receiveOrder.setHelper(currentUser);
        receiveOrder.update(orderId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(mContext,"接单成功！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"接单失败！错误："+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*通过orderId和userId查询订单详情*/
    public void QueryOrder(){
        /*通过orderId向服务器查询该订单信息*/
        BmobQuery<Order> orderQuery = new BmobQuery<Order>();
        orderQuery.addWhereEqualTo("objectId",orderId);
        orderQuery.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        Toast.makeText(mContext,"未找到该订单，请重试！",Toast.LENGTH_SHORT).show();
                    }else{
                        coinsText.setText("提供金币："+list.get(0).getCoins());
                        itemAddrText.setText(list.get(0).getMainAddr()+" "+list.get(0).getDetailAddr());
                        ownerAddrText.setText(list.get(0).getOwnerAddr());
                        notesText.setText(list.get(0).getNotes());

                        /*加载秘密信息*/
                        ownerNameText.setText(list.get(0).getOwnerName());
                        ownerTelText.setText(list.get(0).getOwnerTel());
                        staffCodeText.setText(list.get(0).getOwnerCode());
                    }
                }else{
                    Toast.makeText(mContext,"获取订单失败，请重试！",Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext,"错误信息："+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*通过userId向服务器查询该订单信息*/
        BmobQuery<User> userQuery = new BmobQuery<User>();
        userQuery.addWhereEqualTo("objectId",userId);
        userQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        Toast.makeText(mContext,"未找到该用户，请重试！",Toast.LENGTH_SHORT).show();
                    }else{
                        userNameText.setText(list.get(0).getUserName());
                        //头像加载？？？？？
                    }
                }else{
                    Toast.makeText(mContext,"获取订单失败，请重试！",Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext,"错误信息："+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initWidgets(){

        mContext=OrderDetailActivity.this;

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.toolbarreturn2);
        }


        txtHelpCount=(TextView)findViewById(R.id.txt_help_count);
        txtSecretHint=(TextView)findViewById(R.id.txt_secret_hint);
        userNameText=(TextView)findViewById(R.id.nameTextDetail);
        protraitImage=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.protraitImageDetail);
        coinsText=(TextView)findViewById(R.id.moneyTextDetail);
        itemAddrText=(TextView)findViewById(R.id.addr1TextDetail);
        ownerAddrText=(TextView)findViewById(R.id.addr2TextDetail);
        notesText=(TextView)findViewById(R.id.notesTextDetail);
        btnConfirm=(Button)findViewById(R.id.btnConfirmDetail);
        btnCancel=(Button)findViewById(R.id.btnCancelDetail);
        ownerNameText=(TextView)findViewById(R.id.txtOwnerNameDetail);
        staffCodeText=(TextView)findViewById(R.id.txtStaffCodeDetail);
        ownerTelText=(TextView)findViewById(R.id.txtOwnerTelDetail);
        secretTable=(TableLayout)findViewById(R.id.table_secret_information);
        secretLine=(ImageView)findViewById(R.id.img_secret_line);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    public void QueryHelpCount(String seekerId){
        BmobQuery<Order> query=new BmobQuery<>();
        query.addWhereEqualTo("HelperId",seekerId);
        query.addWhereEqualTo("OrderStatus","已收货");
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if (e==null){
                    Message msg=new Message();
                    msg.what=GET_HELP_COUNT_SUCCESS;
                    msg.arg1=list.size();
                    orderStatusHandler.sendMessage(msg);

                }else{
                    Toast.makeText(mContext,"请求Number失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*根据子线程发出的消息，handler进行不同的操作*/
    private Handler orderStatusHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                case GET_HELP_COUNT_SUCCESS:
                    helpNumber=msg.arg1;
                    txtHelpCount.setText("帮助别人："+helpNumber+"次");
                    break;

                case GET_CURRENT_USER_SUCCESS:
                    currentUser=(User)(msg.obj);
                    break;

                /*如果订单状态检测成功，则接受该订单*/
                case CHECK_ORDER_STATUS_SUCCESS:
                    ReceiveOrder();
                    secretTable.setVisibility(View.VISIBLE);
                    secretLine.setVisibility(View.VISIBLE);
                    btnConfirm.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    txtSecretHint.setVisibility(View.GONE);
                    secretLine.setVisibility(View.GONE);
                    break;

                /*如果订单被别人抢先接受，则报错*/
                case CHECK_ORDER_STATUS_FAILURE:
                    Toast.makeText(mContext,"该订单刚刚已被接受了\n请选择帮助其他人吧！",Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    /*点击标题栏左边按钮，返回上一个活动*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                OrderDetailActivity.this.finish();
                break;
        }
        return true;
    }

}
