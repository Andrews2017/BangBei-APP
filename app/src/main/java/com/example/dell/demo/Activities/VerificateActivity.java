package com.example.dell.demo.Activities;
import com.example.dell.demo.R;
import com.example.dell.demo.User;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.mob.MobSDK;
import org.litepal.crud.DataSupport;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;


public class VerificateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUserTel;
    private EditText etMessageCode;
    private Button btnSendCode;
    private Button btnNextStep;
    private Button btnReturn;

    private String PhoneNumber;
    private String MessageCode;
    private boolean flag;

    private Context mContext;
    private List<User>  selectUserResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificate);

        initWidgets();

        final String AppKey="2197332917358";
        final String AppSecret="0d306bd7c85a6a895a8e1159ae2611eb";
        MobSDK.init(this,AppKey,AppSecret);

        EventHandler eventHandler=new EventHandler(){
            @Override
            public void afterEvent(int event,int result,Object data){
                Message msg=new Message();
                msg.arg1=event;
                msg.arg2=result;
                msg.obj=data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);

    }

    //初始化所有控件
    public void initWidgets(){

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.toolbarreturn2);
        }


        mContext=VerificateActivity.this;
        etUserTel=(EditText)findViewById(R.id.user_tel);
        etMessageCode=(EditText)findViewById(R.id.user_telCode);
        btnSendCode=(Button)findViewById(R.id.btn_getTelCode);
        btnNextStep=(Button)findViewById(R.id.btn_verificateNextStep);
        btnReturn=(Button)findViewById(R.id.btn_verificateReturn);

        btnSendCode.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);
        btnReturn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){

            case R.id.btn_getTelCode:
                PhoneNumber=etUserTel.getText().toString();
                if(PhoneNumber.length()!=11) {  /*先检验输入是否合法*/
                    Toast.makeText(mContext, "电话号码长度非法，请重试！", Toast.LENGTH_SHORT).show();
                    etUserTel.requestFocus();
                    return;
                }

                BmobQuery<User> telBmobQuery = new BmobQuery<User>();
                telBmobQuery.addWhereEqualTo("UserTel",PhoneNumber).findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if(e==null){
                            if(list.size()==0){  //如果该电话还没有注册
                                SMSSDK.getVerificationCode("86",PhoneNumber);
                            }else{  //如果该电话已经注册过
                                Toast.makeText(mContext,"该电话已经注册，请直接登录",Toast.LENGTH_SHORT).show();
                                etUserTel.requestFocus();
                            }
                        }else{
                            Toast.makeText(mContext,"异常，请重试!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;


            /*如果点击“下一步”，则开线程检测验证码是否正确。并同过msg回调*/
            case R.id.btn_verificateNextStep:

                PhoneNumber=etUserTel.getText().toString();
                if(PhoneNumber.length()!=11){  /*先检验输入是否合法*/
                    Toast.makeText(VerificateActivity.this,"电话号码长度非法，请重试！",Toast.LENGTH_SHORT).show();
                    etUserTel.requestFocus();
                    return;
                } else if(etMessageCode.getText().toString().length()!=4){
                    Toast.makeText(this,"验证码长度有误，请重试！",Toast.LENGTH_SHORT).show();
                    etMessageCode.requestFocus();
                    return;
                }else{
                    MessageCode=etMessageCode.getText().toString();
                    SMSSDK.submitVerificationCode("86",PhoneNumber,MessageCode);
                    flag=false;
                }
                break;

            case R.id.btn_verificateReturn:
                VerificateActivity.this.finish();
                break;
        }
    }

    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            int event=msg.arg1;
            int result=msg.arg2;
            Object data=msg.obj;

            if(result==SMSSDK.RESULT_COMPLETE){  /*回调成功*/
                if(event==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){  /*如果通过验证*/
                    Toast.makeText(mContext,"验证成功！",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(mContext,RegisterActivity.class);
                    intent.putExtra("tel_extra",etUserTel.getText().toString());
                    startActivity(intent);
                }else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    Toast.makeText(mContext, "验证码已发送", Toast.LENGTH_SHORT).show();
                }
            }else{   /*回调失败*/
                if(flag){
                    Toast.makeText(VerificateActivity.this,"验证码获取失败，请重新获取",Toast.LENGTH_SHORT).show();
                }else{
                    ((Throwable)data).printStackTrace();
                    Toast.makeText(VerificateActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    @Override
    protected void onDestroy(){
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    /*点击标题栏左边按钮，返回上一个活动*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            case android.R.id.home:
                VerificateActivity.this.finish();
                break;
        }
        return true;
    }

}
