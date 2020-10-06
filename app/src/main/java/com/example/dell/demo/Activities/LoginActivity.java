package com.example.dell.demo.Activities;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.dell.demo.R;
import com.example.dell.demo.User;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText txtTel;
    private EditText txtPwd;
    private Button btnLogin;
    private Button btnRegister;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bmob.initialize(this,"652bcff3f5aeb5d29c1a99b18603f8c1");
        boolean a=cn.bmob.v3.statistics.AppStat.i("652bcff3f5aeb5d29c1a99b18603f8c1","");
        Log.d("TEST","result="+a);
        setTitle("登录");
        initWidgets();
        //initUsers();

         }

    //初始化所有的控件
    public void initWidgets(){
        mContext=LoginActivity.this;
        txtTel=(EditText)findViewById(R.id.user_tel);
        txtPwd=(EditText)findViewById(R.id.user_passwd);
        btnLogin=(Button)findViewById(R.id.imgBtn_login);
        btnRegister=(Button)findViewById(R.id.imgBtn_register);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        setTitle("登录");
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){

            case R.id.imgBtn_login:
                Login();
                break;

            case R.id.imgBtn_register:
                Intent intent=new Intent(LoginActivity.this,VerificateActivity.class);
                view.getContext().startActivity(intent);
                break;

        }
    }


    /*登录按钮的响应事件*/
    public void Login(){
        final String userTel=txtTel.getText().toString();
        String userPwd=txtPwd.getText().toString();
                /*先检查输入是否为空*/
        if(userTel.length()==0||userPwd.length()==0){
            Toast.makeText(mContext,"账号或密码不能为空,请重试！",Toast.LENGTH_SHORT).show();
            return;
        }

        BmobQuery<User> userBmobQuery = new BmobQuery<User>();
        userBmobQuery.addWhereEqualTo("UserTel",userTel);
        userBmobQuery.addWhereEqualTo("UserPwd",userPwd);
        userBmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        Toast.makeText(mContext,"账号不存在或密码错误，请重试！",Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        Toast.makeText(mContext,"登录成功！",Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(mContext,OrdersActivity.class);
                        intent.putExtra("tel_extra",userTel);
                        startActivity(intent);
                    }
                }else{  //如果查询结果为空或出现异常
                    Toast.makeText(mContext,"异常，请重试！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    public void initUsers(){
//        User user1=new User("18483680996","Kobeyond","1997422",R.drawable.protraitkobeyond,999);
//        user1.save();
//        User user2=new User("17713577412","邵慧","123456",R.drawable.protraitshaohui,23);
//        user2.save();
//        User user3=new User("18482285298","灰斜","123456",R.drawable.protraitzhangmenghua,25);
//        user3.save();
//        User user4=new User("13264361782","andrew 倪永","123456",R.drawable.protraitniyong,18);
//        user4.save();
//        User user5=new User("17281790824","吴飞","123456",R.drawable.protraitwufei,15);
//        user5.save();
//        User user6=new User("13784200924","koc","123456",R.drawable.protraitkoc,12);
//        user6.save();
//        User user7=new User("18492818980","陈业","123456",R.drawable.protraitchenye,10);
//        user7.save();
//        User user8=new User("18298304472","余晓玥","123456",R.drawable.protraityuxiaoyue,17);
//        user8.save();
//
//    }

}
