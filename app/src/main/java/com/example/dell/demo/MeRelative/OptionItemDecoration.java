package com.example.dell.demo.MeRelative;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


/*用于给Me界面的RecyclerView添加分隔线*/

public class OptionItemDecoration extends RecyclerView.ItemDecoration{

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){

        /*根据params.getViewAdapterPosition()来获取序号，从而控制分割线的粗细，最终实现item分组的效果*/
        StaggeredGridLayoutManager.LayoutParams params=(StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();

        if(params.getViewAdapterPosition()== 3){
            outRect.set(0,20,0,0);
        }else if(params.getViewAdapterPosition()==5){
            outRect.set(0,20,0,5);
        }else{
            outRect.set(0,5,0,0);
        }
    }

}