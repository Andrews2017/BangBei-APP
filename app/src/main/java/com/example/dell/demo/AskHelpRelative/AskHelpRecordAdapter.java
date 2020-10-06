package com.example.dell.demo.AskHelpRelative;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.demo.MyAnimator;
import com.example.dell.demo.Order;
import com.example.dell.demo.R;
import com.example.dell.demo.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


/*自定义类AskHelpRecord的适配器，让AskHelpRecord对象作为RecyclerView的子项*/

public class AskHelpRecordAdapter extends RecyclerView.Adapter<AskHelpRecordAdapter.ViewHolder>{

    private Integer payedCoin;
    private Integer helperProperty;
    private String helperId;
    private Context mContext;

    private List<AskHelpRecord> mAskHelpRecordList;
    public final int UPDATE_ORDER_STATUS_SUCCESS=1;
    public final int QUERT_HELPERID_PAYEDCOIN_SUCCESS=2;
    public final int QUERY_HELPER_PROPERTY_SUCCESS=3;
    public final int HELPER_RECEIVE_COIN_SUCCESS=4;


    static class ViewHolder extends RecyclerView.ViewHolder{
        View orderView;
        TextView orderId;
        TextView helperName;
        TextView helperTel;
        TextView orderNotes;
        TextView orderStatus;
        ImageView btnCompleteConfirm;
        ImageView imgOrderComplete;


        public ViewHolder(View view){
            super(view);
            orderView=view;
            orderId=(TextView)view.findViewById(R.id.txt_ask_help_record_id);
            helperName=(TextView)view.findViewById(R.id.txt_helper_name);
            helperTel=(TextView)view.findViewById(R.id.txt_helper_tel);
            orderNotes=(TextView)view.findViewById(R.id.txt_ask_help_record_notes);
            orderStatus=(TextView)view.findViewById(R.id.txt_ask_help_record_status);
            btnCompleteConfirm=(ImageView)view.findViewById(R.id.btn_confirm_complete);
            imgOrderComplete=(ImageView)view.findViewById(R.id.img_order_complete_logo);
        }
    }

    public AskHelpRecordAdapter(List<AskHelpRecord> list){
        mAskHelpRecordList=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ask_help_record_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        holder.orderId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAnimator.StartPlanAnimation(holder.orderView);
            }
        });
        holder.orderNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAnimator.StartPlanAnimation(holder.orderView);
            }
        });
        holder.helperName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAnimator.StartPlanAnimation(holder.orderView);
            }
        });
        holder.helperTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAnimator.StartPlanAnimation(holder.orderView);
            }
        });


        /*“确认收货”按钮点击事件*/
        holder.btnCompleteConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder clearDialog = new AlertDialog.Builder(view.getContext());
                clearDialog.setTitle("提示");
                clearDialog.setMessage("请问您收到的物品完好无误吗？\n确认收货之后不能撤销哦");
                clearDialog.setCancelable(true);

                clearDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //依次更新Status、helper加金币、按钮Gone、贴图Visable
                        UpdateOrderStatus(holder);
                    }
                });

                clearDialog.setNegativeButton("返回",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                    }
                });

                clearDialog.show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){

        mContext=holder.orderView.getContext();

        AskHelpRecord record=mAskHelpRecordList.get(position);

        holder.orderId.setText(record.getOrderId());  //这里分两个TextView展示，为了方便从控件获取id并查询
        if(record.getHelper()==null){
            holder.helperName.setText("接单人：暂无");
            holder.helperTel.setText("电话    ：暂无");
        }else{
            holder.helperName.setText("接单人："+record.getHelper().getUserName());
            holder.helperTel.setText("电话    ："+record.getHelper().getUserTel());
        }

        holder.orderNotes.setText("备注    ："+record.getOrderNotes());

        if(record.getOrderStatus().equals("未被接单")){
            holder.orderStatus.setText(record.getOrderStatus());
            holder.orderStatus.setTextColor(Color.parseColor("#999999"));
            holder.btnCompleteConfirm.setVisibility(View.GONE);

        }else if(record.getOrderStatus().equals("已被接单")){
            holder.orderStatus.setText(record.getOrderStatus());
            holder.orderStatus.setTextColor(Color.parseColor("#FFA500"));
        }else{
            holder.orderStatus.setText("已完成");
            holder.orderStatus.setTextColor(Color.parseColor("#32CD32"));
            holder.btnCompleteConfirm.setVisibility(View.GONE);
            holder.imgOrderComplete.setVisibility(View.VISIBLE);
        }
        holder.orderStatus.setText(record.getOrderStatus());

    }


    /*把OrderStatus更新为“已收货”
    * 先通过ViewHolder获取到orderId，再去服务器Update*/
    public void UpdateOrderStatus(final AskHelpRecordAdapter.ViewHolder holder){

        String orderId=holder.orderId.getText().toString();
        Order order=new Order();
        order.setOrderStatus("已收货");
        order.update(orderId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //服务器Status状态改变成功之后，先更新UI
                    holder.orderStatus.setText("已收货");
                    holder.orderStatus.setTextColor(Color.parseColor("#32CD32"));
                    holder.btnCompleteConfirm.setVisibility(View.GONE);
                    holder.imgOrderComplete.setVisibility(View.VISIBLE);

                    Message msg =new Message();
                    msg.what=UPDATE_ORDER_STATUS_SUCCESS;
                    msg.obj=holder;
                    completeOrderHandler.sendMessage(msg);
                }else{
                    Toast.makeText(holder.orderView.getContext(),"更新OrderStatus失败！错误："+e.getMessage()+e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*通过holder里面的orderId，去服务器查询HelperId和PayedCoin*/
    public void QueryHelperAndCoin(final AskHelpRecordAdapter.ViewHolder holder){

        String orderId=holder.orderId.getText().toString();
        BmobQuery<Order> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",orderId).findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> list, BmobException e) {
                if(e==null){
                    String helperId=list.get(0).getHelper().getObjectId();
                    Integer payedCoin=list.get(0).getCoins();
                    //此时不需要再传入holder，因为UI方面(txtStatus,btnComplete)已经更新完成了
                    Message msg=new Message();
                    msg.what=QUERT_HELPERID_PAYEDCOIN_SUCCESS;
                    msg.arg1=(int)payedCoin;
                    msg.obj=helperId;
                    completeOrderHandler.sendMessage(msg);
                }else{
                    Toast.makeText(holder.orderView.getContext(),"获取HelperId和Coin失败！错误："+e.getMessage(),Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    /*通过helperId去User表查询用户当前余额*/
    public void QueryHelperProperty(String helperId){
        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",helperId).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    Integer helperProperty=list.get(0).getProperty();
                    Message msg=new Message();
                    msg.what=QUERY_HELPER_PROPERTY_SUCCESS;
                    msg.obj=helperProperty;
                    completeOrderHandler.sendMessage(msg);
                }else{
                    Toast.makeText(mContext,"查询用户剩余余额失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*通过HelperId去User表更新帮助者的金币*/
    public void ReceiveCoin(String helperId,Integer propertyNew){

        final User user=new User();
        user.setProperty(propertyNew);
        user.update(helperId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Message msg=new Message();
                    msg.what=HELPER_RECEIVE_COIN_SUCCESS;
                    completeOrderHandler.sendMessage(msg);
                    Toast.makeText(mContext,"订单已结束，感谢您的使用！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"更新用户余额失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount(){
        return mAskHelpRecordList.size();
    }


    /*根据子线程发出的消息，handler进行不同的操作*/
    private Handler completeOrderHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                /*更新OrderStatus完成之后，开始获取HelperId和Coin*/
                case UPDATE_ORDER_STATUS_SUCCESS:
                    ViewHolder holder=(ViewHolder)(msg.obj);
                    QueryHelperAndCoin(holder);
                    break;

                /*获取到HelperId和Coin之后，先去User表获取用户当前金币数量*/
                case QUERT_HELPERID_PAYEDCOIN_SUCCESS:
                    payedCoin=(Integer)(msg.arg1);
                    helperId=(String)(msg.obj);
                    QueryHelperProperty(helperId);
                    break;

                /*获取到帮助者的当前余额之后，开始收获金币*/
                case QUERY_HELPER_PROPERTY_SUCCESS:
                    helperProperty=(Integer)msg.obj;
                    ReceiveCoin(helperId,helperProperty+payedCoin);
                    break;

                case HELPER_RECEIVE_COIN_SUCCESS:
                    //
                    break;


            }
        }
    };

}

