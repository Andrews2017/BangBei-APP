package com.example.dell.demo.OrderRelative;
import android.content.*;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.demo.Activities.OrderDetailActivity;
import com.example.dell.demo.MyAnimator;
import com.example.dell.demo.Order;
import com.example.dell.demo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;


/*自定义类Order的适配器，让Order对象作为RecyclerView的子项*/
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{

    private String HelperId;
    private List<Order> mOrderList; //数组用来存放所有order

    private DisplayImageOptions  options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.defaulthead) // 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.defaulthead) // 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.defaulthead) // 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
            .cacheOnDisk(false) // 设置下载的图片是否缓存在SD卡中
            .build(); // 构建完成



    /*内部类ViewHolder用于容纳UI控件*/
    static class ViewHolder extends RecyclerView.ViewHolder{

       de.hdodenhof.circleimageview.CircleImageView protraitImage;
        TextView userNameText;
        TextView moneyText;
        TextView itemAddressText;
        TextView OwnerAddressText;
        TextView notesText;
        View requestView;

        /*把view作为参数传给ViewHolder的构造函数，从布局中获取到控件的实例*/
        public ViewHolder(View view){
            super(view);
            protraitImage=(de.hdodenhof.circleimageview.CircleImageView)view.findViewById(R.id.protraitImage);
            userNameText=(TextView)view.findViewById(R.id.nameText);
            moneyText=(TextView)view.findViewById(R.id.moneyText);
            itemAddressText=(TextView)view.findViewById(R.id.addr1Text);
            OwnerAddressText=(TextView)view.findViewById(R.id.addr2Text);
            notesText=(TextView)view.findViewById(R.id.notesText);
            requestView=view;
        }
    }


    /*自定义适配器的构造函数，用包含Request对象的数组作为参数*/
    public OrderAdapter(String helperId,List<Order> orderList){
        HelperId=helperId;
        mOrderList=orderList;
    }


    @Override
    /*用于创建viewHolder实例。先加载request_item布局，并作为参数view传给viewHolder*/
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,null,false);
//        view.setBackgroundColor(Color.argb(0xff,0xFF,0xFA,0xF0));
        final ViewHolder holder=new ViewHolder(view);
        holder.requestView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                MyAnimator.StartPlanAnimation(view);
                int position=holder.getAdapterPosition();
                Order order=mOrderList.get(position);

                //点击子项item则跳转到订单详情界面
                String OrderId=order.getObjectId();
                String UserId=order.getSeeker().getObjectId();

                Intent intent=new Intent(view.getContext(), OrderDetailActivity.class);
                intent.putExtra("extra_orderId",OrderId);
                intent.putExtra("extra_userId",UserId);
                intent.putExtra("extra_helperId",HelperId);
                //此处不能直接传bitmap头像，因为会超出文件大小

                view.getContext().startActivity(intent); 吧
        });
        return holder;
    }


    @Override
    /*用于子项的数据赋值。在每个子项滑动进入屏幕的时候执行*/
    public void onBindViewHolder(final ViewHolder holder, int position){

        Order order=mOrderList.get(position);
        if(order==null){
            return;
        }
//
//      //  String imageUrl=order.getImageUrl();
//        if(imageUrl!=null && imageUrl.length()!=0){  //如果上传了图片，则用ImageLoader开源库去加载
//            ImageLoader imageLoader=ImageLoader.getInstance();
//            imageLoader.displayImage(order.getImageUrl(), holder.protraitImage, options);
//        }


        BmobFile icon=order.getSeeker().getIcon();
        if(icon!=null){
            String imageUrl=icon.getFileUrl();
            if(imageUrl.length()!=0){
                ImageLoader imageLoader=ImageLoader.getInstance();
                imageLoader.displayImage(imageUrl, holder.protraitImage, options);
            }
        }

        /*以下信息是可以根据order直接获取并赋值的*/
        //holder.userNameText.setText(order.getUserName());
        Log.d("Name","name="+order.getSeeker().getUserName());
        holder.userNameText.setText(order.getSeeker().getUserName());
        holder.moneyText.setText("×"+order.getCoins());
        holder.itemAddressText.setText(order.getMainAddr()+" "+order.getDetailAddr());
        holder.OwnerAddressText.setText(order.getOwnerAddr());
        holder.notesText.setText(order.getNotes());

    }

    @Override
    public int getItemCount(){
        return mOrderList.size();
    }


}
