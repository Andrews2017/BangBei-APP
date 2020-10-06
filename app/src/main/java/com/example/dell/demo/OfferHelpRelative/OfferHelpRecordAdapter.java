package com.example.dell.demo.OfferHelpRelative;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.dell.demo.MyAnimator;
import com.example.dell.demo.R;
import java.util.List;

/*自定义类OfferHelpRecord的适配器，让OfferHelpRecord对象作为RecyclerView的子项*/


public class OfferHelpRecordAdapter extends RecyclerView.Adapter<OfferHelpRecordAdapter.ViewHolder>{

    private List<OfferHelpRecord> mOfferHelpRecordList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View orderView;
        TextView orderId;
        TextView seekerName;
        TextView seekerTel;
        TextView orderNotes;
        TextView orderStatus;
        ImageView btnExpand;
        ImageView imgOrderComplete;

        //以下为折叠属性
        TextView ownerName;
        TextView ownerTel;
        TextView ownerCode;
        LinearLayout expandedLayout;

        public ViewHolder(View view){
            super(view);
            orderView=view;
            orderId=(TextView)view.findViewById(R.id.txt_offer_help_order_id);
            seekerName=(TextView)view.findViewById(R.id.txt_offer_help_order_seeker_name);
            seekerTel=(TextView)view.findViewById(R.id.txt_offer_help_order_seeker_tel);
            orderNotes=(TextView)view.findViewById(R.id.txt_offer_help_order_notes);
            orderStatus=(TextView)view.findViewById(R.id.txt_offer_help_order_status);
            btnExpand=(ImageView)view.findViewById(R.id.btn_expand_order_info);
            imgOrderComplete=(ImageView)view.findViewById(R.id.img_order_complete_logo);

            ownerName=(TextView)view.findViewById(R.id.txt_offer_help_owner_name);
            ownerTel=(TextView)view.findViewById(R.id.txt_offer_help_owner_tel);
            ownerCode=(TextView)view.findViewById(R.id.txt_offer_help_owner_code);
            expandedLayout=(LinearLayout)view.findViewById(R.id.expanded_order_info);
        }
    }

    public OfferHelpRecordAdapter(List<OfferHelpRecord> list){
        mOfferHelpRecordList=list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_help_record_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        holder.orderNotes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MyAnimator.StartPlanAnimation(holder.orderView);
            }
        });
        holder.seekerName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MyAnimator.StartPlanAnimation(holder.orderView);
            }
        });
        holder.seekerTel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MyAnimator.StartPlanAnimation(holder.orderView);
            }
        });

        holder.btnExpand.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(holder.expandedLayout.getVisibility()==View.GONE){
                    holder.expandedLayout.setVisibility(View.VISIBLE);
                    holder.btnExpand.setRotation(180);
                }else{
                    holder.expandedLayout.setVisibility(View.GONE);
                    holder.btnExpand.setRotation(0);
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(OfferHelpRecordAdapter.ViewHolder holder, int position){

        OfferHelpRecord record=mOfferHelpRecordList.get(position);
        holder.orderId.setText("订单号："+record.getOrderId());
        holder.seekerName.setText("下单人："+record.getSeeker().getUserName());
        holder.seekerTel.setText("电话    ："+record.getSeeker().getUserTel());
        holder.orderNotes.setText("备注    ："+record.getOrderNotes());

        if(record.getOrderStatus().equals("已被接单")){  //如果还未完成帮助
            holder.orderStatus.setText("进行中");
            holder.orderStatus.setTextColor(Color.parseColor("#FFA500"));

        }else{  //如果已完成帮助
            holder.orderStatus.setText("已完成");
            holder.orderStatus.setTextColor(Color.parseColor("#32CD32"));
            holder.imgOrderComplete.setVisibility(View.VISIBLE);
        }

        holder.ownerName.setText(record.getOwnerName());
        holder.ownerTel.setText(record.getOwnerTel());
        holder.ownerCode.setText(record.getOwnerCode());

    }

    @Override
    public int getItemCount(){
        return mOfferHelpRecordList.size();
    }

}
