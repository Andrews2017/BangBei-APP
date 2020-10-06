package com.example.dell.demo.OrderRelative;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


/*给Orders界面的RecyclerView添加分隔线*/
public class MyItemDecoration extends RecyclerView.ItemDecoration{

    @Override
    public void getItemOffsets(Rect outRect, View view,RecyclerView parent,RecyclerView.State state){

        /*解决分隔线左右不均衡的问题
         根据params.getSpanIndex()来判断左右边确定分割线
         第一列设置左边距为space，右边距为space/2 ；第二列反之*/
        StaggeredGridLayoutManager.LayoutParams params=(StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        int left=0;
        int top=0;
        int right=0;
        int bottom=0;

        if(params.getSpanIndex()%2==0){
          //  outRect.set(8,0,4,0);
           left=8;right=4;
        }else{
       //     outRect.set(4,0,8,0);
            left=4;right=8;
        }
        if(params.getViewAdapterPosition()==0||params.getViewAdapterPosition()==1){
            top=8;
            bottom=4;
       //     outRect.set(0,8,0,4);
        }else{
            top=4;
            bottom=4;
//            outRect.set(0,4,0,4);
        }
        outRect.set(left,top,right,bottom);
    }

}
