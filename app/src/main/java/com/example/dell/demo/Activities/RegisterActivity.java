package com.example.dell.demo.Activities;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.dell.demo.R;
import com.example.dell.demo.User;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher{

    private EditText txtUserName;
    private EditText txtUserPwd;
    private EditText txtUserPwd2;
    private Button btnRegisterComplete;

    private int nameTag=0;
    private Context mContext;
    private String UserTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent=this.getIntent();
        UserTel=intent.getStringExtra("tel_extra");

        initWidgets();

    }

    public void initWidgets(){

        mContext=RegisterActivity.this;
        txtUserName=(EditText)findViewById(R.id.user_setName);
        txtUserPwd=(EditText)findViewById(R.id.user_setPwd);
        txtUserPwd2=(EditText)findViewById(R.id.user_setPwdAgain);
        btnRegisterComplete=(Button)findViewById(R.id.btn_registerComplete);
        btnRegisterComplete.setOnClickListener(this);
        txtUserName.addTextChangedListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){

            case R.id.btn_registerComplete:
                RegisterComplete();
                break;

        }
    }

    /*“确认注册”按钮的响应事件*/
    public void RegisterComplete(){

        String name=txtUserName.getText().toString();
        String pwd1=txtUserPwd.getText().toString();
        String pwd2=txtUserPwd2.getText().toString();

        if(pwd1.length()==0 || pwd2.length()==0 || name.length()==0){
            Toast.makeText(mContext,"输入不能为空，请重试！",Toast.LENGTH_SHORT).show();
        } else if(pwd1.equals(pwd2)==false){
            Toast.makeText(mContext,"两次输入的密码不一致，请重试！",Toast.LENGTH_SHORT).show();
            txtUserPwd2.requestFocus();
        }else if(nameTag==2){
            txtUserName.setError("该用户名已被使用");
        }else if(nameTag==0){
            txtUserName.setError("用户名不能为空");
        }else{   /*如果两次密码一致并且昵称合法就插入数据库中*/
            User newUser=new User(UserTel,name,pwd1,20,null);
            newUser.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        Toast.makeText(mContext,"注册成功！",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(mContext,LoginActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(mContext,"错误："+ s+" "+e.getMessage(),Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext,"注册失败，请重试！",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override   /*对输入的姓名实时查重*/
    public void afterTextChanged(Editable s) {

        String userName=txtUserName.getText().toString();
        BmobQuery<User> nameQuery = new BmobQuery<User>();
        nameQuery.addWhereEqualTo("UserName",userName).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(txtUserName.getText().length()!=0){
                    if(list.size()==0){
                        Drawable dr=getResources().getDrawable(R.drawable.iconright);
                        dr.setBounds(0,0,40,40);
                        txtUserName.setError("该昵称可用",dr);
                        nameTag=1;
                    }else{
                        txtUserName.setError("该用户名已被使用");
                        nameTag=2;
                    }
                }
            }
        });

    }

}
