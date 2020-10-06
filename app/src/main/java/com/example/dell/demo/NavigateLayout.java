package com.example.dell.demo;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dell.demo.Activities.AskHelpActivity;
import com.example.dell.demo.Activities.MeActivity;
//import com.example.dell.demo.Activities.OrderDetailActivity;
import com.example.dell.demo.Activities.OrdersActivity;

/*自定义控件navigate_item的类，是界面底部的导航栏*/

public class NavigateLayout extends LinearLayout implements View.OnClickListener{

    TextView txtHome;
    TextView txtAsk;
    TextView txtMe;
    ImageView btnHome;
    ImageView btnAskHelp;
    ImageView btnMe;

    private String userId; //用于在三个页面跳转时，保存id

    public NavigateLayout(final Context context, AttributeSet attrs){
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.navigate_item,this);

        txtHome=(TextView)findViewById(R.id.txt_home_bottom);
        txtAsk=(TextView)findViewById(R.id.txt_ask_bottom);
        txtMe=(TextView)findViewById(R.id.txt_me_bottom);
        btnHome=(ImageView)findViewById(R.id.btnHome);
        btnAskHelp=(ImageView)findViewById(R.id.btnAskHelp);
        btnMe=(ImageView)findViewById(R.id.btnMe);

        txtHome.setOnClickListener(this);
        txtAsk.setOnClickListener(this);
        txtMe.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnAskHelp.setOnClickListener(this);
        btnMe.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){

        MyAnimator.StartPlanAnimation(view);
        switch (view.getId()){

            case R.id.txt_home_bottom:
            case R.id.btnHome:
                /*不管点图标还是字，二者都会同时有动画，且都会跳转页面*/
                MyAnimator.StartPlanAnimation(txtHome);
                MyAnimator.StartPlanAnimation(btnHome);
                Intent intent=new Intent(getContext(),OrdersActivity.class);
                intent.putExtra("extra_userId",userId);
                getContext().startActivity(intent);
                break;

            case R.id.txt_ask_bottom:
            case R.id.btnAskHelp:
                MyAnimator.StartPlanAnimation(txtAsk);
                MyAnimator.StartPlanAnimation(btnAskHelp);
                Intent intent2=new Intent(view.getContext(), AskHelpActivity.class);
                intent2.putExtra("extra_userId",userId);
                view.getContext().startActivity(intent2);
                break;

            case R.id.txt_me_bottom:
            case R.id.btnMe:
                MyAnimator.StartPlanAnimation(txtMe);
                MyAnimator.StartPlanAnimation(btnMe);
                Intent intent3=new Intent(view.getContext(), MeActivity.class);
                intent3.putExtra("extra_userId",userId);
                view.getContext().startActivity(intent3);
                break;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
