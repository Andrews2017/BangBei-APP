package com.example.dell.demo.MeRelative;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.demo.Activities.AboutActivity;
import com.example.dell.demo.Activities.AskHelpRecordActivity;
import com.example.dell.demo.Activities.HelpActivity;
import com.example.dell.demo.Activities.LoginActivity;
import com.example.dell.demo.Activities.OfferHelpRecordActivity;
import com.example.dell.demo.MyAnimator;
import com.example.dell.demo.R;
import com.example.dell.demo.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class MeAdapter extends RecyclerView.Adapter<MeAdapter.ViewHolder>{

    public final int PROPERTY=0;
    public final int ASK_FOR_HELP = 1;
    public final int PROVIDE_HELP = 2;
    public final int HELP = 3;
    public final int ABOUT = 4;
    public final int EXIT=5;

    private Context mContext;
    private String UserId;  //跳转至求助历史、帮助历史时，以userId去服务器查询
    private List<MeOption> mMeList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View meView;
        ImageView meImage;
        TextView meOption;
        LinearLayout divLayout;  //添加父布局用于设置Gravity以便让“退出”按钮居中显示

        public ViewHolder(View view){
            super(view);
            meView=view;
            meImage=(ImageView)view.findViewById(R.id.me_image);
            meOption=(TextView)view.findViewById(R.id.me_option);
            divLayout=(LinearLayout)view.findViewById(R.id.div_layout);
        }
    }

    public MeAdapter(List<MeOption> meList,String userId){
        mMeList=meList;
        UserId=userId;
    }


    @Override   /*创建ViewHolder实例用于容纳控件，并可以为每个子项设置监听事件*/
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.me_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.meView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MyAnimator.StartPlanAnimation(view);
                int position=holder.getAdapterPosition();
                switch (position){

                    case PROPERTY:
                        BmobQuery<User> propertyQuery=new BmobQuery<>();
                        propertyQuery.addWhereEqualTo("objectId",UserId).findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> list, BmobException e) {
                                if(e==null){
                                    Integer property=list.get(0).getProperty();
                                    Toast.makeText(view.getContext(),"您的金币余额为："+property,Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        break;

                    case ASK_FOR_HELP:{
                        Intent intent=new Intent(v.getContext(),AskHelpRecordActivity.class);
                        intent.putExtra("extra_userId",UserId);
                        v.getContext().startActivity(intent);
                        break;}

                    case PROVIDE_HELP:{
                        Intent intent=new Intent(v.getContext(),OfferHelpRecordActivity.class);
                        intent.putExtra("extra_userId",UserId);
                        v.getContext().startActivity(intent);
                        break;}

                    case HELP:{
                        Intent intent=new Intent(v.getContext(),HelpActivity.class);
                        v.getContext().startActivity(intent);
                        break;
                    }

                    case ABOUT:{
                        Intent intent=new Intent(v.getContext(),AboutActivity.class);
                        v.getContext().startActivity(intent);
                        break;
                    }

                    case EXIT:{
                        Intent intent=new Intent(v.getContext(), LoginActivity.class);
                        //加入该标志，可以让Login界面处于栈顶，同时清除其他所有界面。达到换账号登录的效果
                        //不然由于之前活动声明的SingleTask，会直接切换到栈顶
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);

                        //是否还要通过广播结束MeActivity活动？？？
                        break;
                    }

                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        MeOption option=mMeList.get(position);
        holder.meImage.setImageResource(option.getImgOption());
        holder.meOption.setText(option.getTxtOption());
        if(position==EXIT){
              /*代码中动态设置“退出”按钮的位置和颜色*/
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams)holder.meOption.getLayoutParams();
            params.setMargins(0,0,16,0);
            holder.meOption.setLayoutParams(params);
            holder.meImage.setVisibility(View.GONE);
            holder.divLayout.setGravity(Gravity.CENTER);
            holder.meOption.setTextSize(16);
            holder.meOption.setTextColor(Color.parseColor("#EE0000"));
        }
    }

    @Override
    public int getItemCount(){
        return mMeList.size();
    }

    public void QueryProperty(String userId){

    }

}