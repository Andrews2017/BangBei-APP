package com.example.dell.demo;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;



public class  MyAnimator {

    public static void StartAnimation(View view){
        ObjectAnimator animatorX=ObjectAnimator.ofFloat(view,"ScaleX",1f,1.5f,1f);
        ObjectAnimator animatorY=ObjectAnimator.ofFloat(view,"ScaleY",1f,1.5f,1f);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.play(animatorX).with(animatorY);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

    public static void StartPlanAnimation(View view){
        ObjectAnimator animatorX=ObjectAnimator.ofFloat(view,"ScaleX",1f,1.1f,1f);
        ObjectAnimator animatorY=ObjectAnimator.ofFloat(view,"ScaleY",1f,1.1f,1f);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.play(animatorX).with(animatorY);
        animatorSet.setDuration(200);
        animatorSet.start();
    }

}