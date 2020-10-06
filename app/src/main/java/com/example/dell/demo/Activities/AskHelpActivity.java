package com.example.dell.demo.Activities;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.dell.demo.MyAnimator;
import com.example.dell.demo.NavigateLayout;
import com.example.dell.demo.Order;
import com.example.dell.demo.R;
import com.example.dell.demo.User;
import java.util.List;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class AskHelpActivity extends AppCompatActivity implements View.OnClickListener,TextWatcher{

    private String userId;
    private String detailAddr;
    private String tel;
    private String name;
    private String code;
    private String notes;
    private Integer payedCoin;
    private Integer property;

    private Context mContext;
    private Spinner mainAddr;
    private Spinner ownerAddr;
    private EditText txtPaidCoins;
    private EditText txtDetailAddr;
    private EditText txtOwnerTel;
    private EditText txtOwnerName;
    private EditText txtOwnerCode;
    private EditText txtNotes;
    private Button btnCommitRequest;
    private Button btnClearRequest;
    private NavigateLayout askHelpNavigation;

    private User currentUser;

    public final int COINS_ENOUGH=1;
    public final int COINS_SHORT=2;
    public final int DELETE_COIN_SUCCESS=3;
    public final int CREATE_ORDER_SUCCESS=4;
    public final int GET_CURRENT_USER_SUCCESS=5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_help);

        Intent intent=getIntent();
        userId=intent.getStringExtra("extra_userId");
        getCurrentUser(userId);

        initWidgets();
        initSpinner();

    }

    public void getCurrentUser(String seekerId){
        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",seekerId).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {

                User currentUser=list.get(0);
                Message msg=new Message();
                msg.what=GET_CURRENT_USER_SUCCESS;
                msg.obj=currentUser;
                coinHandler.sendMessage(msg);
            }
        });
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_commit_request:
                MyAnimator.StartPlanAnimation(view);
                RequestCheck();break;

            case R.id.btn_clear_request:
                MyAnimator.StartPlanAnimation(view);
                ClearRequest();break;
        }
    }

    /*“清除订单”按钮的响应事件*/
    public void ClearRequest(){

        AlertDialog.Builder clearDialog = new AlertDialog.Builder(mContext);
        clearDialog.setTitle("通知");
        clearDialog.setMessage("您确定要清空所填信息吗？");
        clearDialog.setCancelable(true);

        clearDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                ClearOrder();
            }
        });

        clearDialog.setNegativeButton("返回",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
            }
        });

        clearDialog.show();

    }

    /*“提交订单”按钮的响应事件，先文本检测，再金币检测
   * 通过金币检测后会发出msg，通知下一步的扣费操作*/
    public void RequestCheck(){

        /*如果文本输入正确，才会进行金币检测。
        只有当文本和金币检测同时通过时，子线程才会发出消息*/
        if(checkText()==true){
            checkProperty();
        }
    }

    /*二者都检测无误之后，开始扣费操作*/
    public void CommitRequest(){

        AlertDialog.Builder commitDialog = new AlertDialog.Builder(mContext);
        commitDialog.setTitle("通知");
        commitDialog.setMessage("您确定所填信息正确并继续下单吗？");
        commitDialog.setCancelable(true);

        commitDialog.setPositiveButton("下单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                DeleteCoins();
            }
        });

        commitDialog.setNegativeButton("返回",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
            }
        });

        commitDialog.show();

    }

    /*扣除用户金币*/
    public void DeleteCoins(){

        final Integer coin=Integer.parseInt(txtPaidCoins.getText().toString());

        final User user=new User();
        user.setProperty(property-coin);
        user.update(userId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    //当扣费成功则发出消息通知handler进行下一步的生成订单操作
                    Message msg=new Message();
                    msg.what=DELETE_COIN_SUCCESS;
                    coinHandler.sendMessage(msg);
                    Toast.makeText(mContext,"支付成功！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(mContext,"错误："+e.getMessage(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext,"支付失败，请重试！",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*向服务器插入订单*/
    public void InsertOrder(Order order){

        order.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Message msg=new Message();
                    msg.what=CREATE_ORDER_SUCCESS;
                    coinHandler.sendMessage(msg);
                    Toast.makeText(mContext,"下单成功，请耐心等待别人的帮助！",Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(mContext,"错误："+ s+" "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    Toast.makeText(mContext,"生成订单失败，请重试！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*对文本框进行非空检测*/
    public boolean checkText(){

        detailAddr=txtDetailAddr.getText().toString();
        tel=txtOwnerTel.getText().toString();
        name=txtOwnerName.getText().toString();
        code=txtOwnerCode.getText().toString();
        notes=txtNotes.getText().toString();

        if(detailAddr.length()==0){
            Toast.makeText(mContext,"请输入快递公司",Toast.LENGTH_SHORT).show();
            return false;
        }else if(tel.length()!=11){
            Toast.makeText(mContext,"请输入正确的电话号码",Toast.LENGTH_SHORT).show();
            return false;
        }else if(name.length()==0){
            Toast.makeText(mContext,"请输入取件人姓名",Toast.LENGTH_SHORT).show();
            return false;
        }else if(code.length()==0){
            Toast.makeText(mContext,"请输入取货号",Toast.LENGTH_SHORT).show();
            return false;
        }else if(notes.length()==0){
            Toast.makeText(mContext,"请备注您的详细地址或注意事项",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }

    }

    /*对输入金币进行检测*/
    public void checkProperty(){

        /*先判断输入是否为空*/
        String inputCoins=txtPaidCoins.getText().toString();
        if(inputCoins.length()==0){
            Toast.makeText(mContext,"请输入悬赏金币的数量",Toast.LENGTH_LONG).show();
            return ;
        }

        final Integer coins;
        try{ /*先判断数量是纯数字*/
            coins=Integer.parseInt(inputCoins);
        }catch (Exception e){  //如果输入非数字，则报错并结束
            Toast.makeText(mContext,"金币数量必须是整数，请重试！",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return ;
        }

        /*其次判断数量是否为负数*/
        if(coins<0){
            Toast.makeText(mContext,"金币数量必须不能是负数！",Toast.LENGTH_LONG).show();
            return ;
        }

        /*最后判断金币是否足够*/
        BmobQuery<User> coinQuery = new BmobQuery<User>();
        coinQuery.addWhereEqualTo("objectId",userId);
        coinQuery.findObjects(new FindListener<User>()  {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    Message message=new Message();
                    if(list.get(0).getProperty()<coins){
                        message.what=COINS_SHORT;
                    }else{
                        /*如果金币足够，则发出msg
                        当Handler接收到消息之后，进行具体的扣费、生成订单操作*/
                        message.what=COINS_ENOUGH;
                        message.arg1=list.get(0).getProperty();
                        message.arg2=coins;
                    }
                    coinHandler.sendMessage(message);
                }else{
                    Toast.makeText(mContext,"金币数量检测异常，请重试！",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    /*清除所填订单信息*/
    public void ClearOrder(){
        txtPaidCoins.setText("");
        txtDetailAddr.setText("");
        txtOwnerTel.setText("");
        txtOwnerName.setText("");
        txtOwnerCode.setText("");
        txtNotes.setText("");
        txtDetailAddr.requestFocus();
    }


    public void initWidgets(){

        mContext=AskHelpActivity.this;

        askHelpNavigation=(NavigateLayout)findViewById(R.id.ask_help_navigation);
        askHelpNavigation.setUserId(userId);

        mainAddr=(Spinner)findViewById(R.id.spinner_main_addr);
        ownerAddr=(Spinner)findViewById(R.id.spinner_owner_addr);
        txtPaidCoins=(EditText)findViewById(R.id.txt_paid_coins);
        txtDetailAddr=(EditText)findViewById(R.id.txt_detail_addr);
        txtOwnerTel=(EditText)findViewById(R.id.txt_owner_tel);
        txtOwnerName=(EditText)findViewById(R.id.txt_owner_name);
        txtOwnerCode=(EditText)findViewById(R.id.txt_owner_code);
        txtNotes=(EditText)findViewById(R.id.txt_notes);
        btnCommitRequest=(Button)findViewById(R.id.btn_commit_request);
        btnClearRequest=(Button)findViewById(R.id.btn_clear_request);

        btnCommitRequest.setOnClickListener(this);
        btnClearRequest.setOnClickListener(this);
        txtPaidCoins.addTextChangedListener(this);

    }
    public void initSpinner(){

        String[] mainAddrItems ={"商业街","西门桥头","校外"};
        String[] ownerAddrItems ={"宿舍","品学楼","立人楼","主楼"};

        ArrayAdapter<String> mainAddrAdapter=new ArrayAdapter<String>(this,R.layout.spinner_style, mainAddrItems);
        mainAddrAdapter.setDropDownViewResource(R.layout.spinner_expanded_style);
        ArrayAdapter<String> ownerAddrAdapter=new ArrayAdapter<String>(this,R.layout.spinner_style, ownerAddrItems);
        ownerAddrAdapter.setDropDownViewResource(R.layout.spinner_expanded_style);
        mainAddr.setAdapter(mainAddrAdapter);
        mainAddr.setGravity(Gravity.CENTER);
        ownerAddr.setAdapter(ownerAddrAdapter);
        ownerAddr.setGravity(Gravity.CENTER);
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override   /*对Coin输入框进行实时监听*/
    public void afterTextChanged(Editable s) {

        /*先判断输入是否为空*/
        String inputCoins=txtPaidCoins.getText().toString();
        if(inputCoins.length()==0){
            txtPaidCoins.setError("请输入悬赏金币的数量");
            return ;
        }

        final Integer coins;
        try{ /*先判断数量是纯数字*/
            coins=Integer.parseInt(inputCoins);
        }catch (Exception e){  //如果输入非数字，则报错并结束
            txtPaidCoins.setError("金币数量必须是整数，请重试！");
            e.printStackTrace();
            return ;
        }

        /*其次判断数量是否为负数*/
        if(coins<0){
            txtPaidCoins.setError("金币数量必须不能是负数");
            return ;
        }

        /*最后判断金币是否足够*/
        BmobQuery<User> coinQuery = new BmobQuery<User>();
        coinQuery.addWhereEqualTo("objectId",userId);  //查User表要查objectId，查Order表要查UserId
        coinQuery.findObjects(new FindListener<User>()  {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    if(list.get(0).getProperty()>=coins){
                        Drawable dr=getResources().getDrawable(R.drawable.iconright);
                        dr.setBounds(0,0,40,40);
                        txtPaidCoins.setError("金币足够",dr);
                    }else{
                        txtPaidCoins.setError("金币余额不足！");
                    }
                }else{
                    Toast.makeText(mContext,"异常，请重试！",Toast.LENGTH_LONG).show();
                }
            }
        });

    }




    /*根据子线程发出的消息，handler进行不同的操作*/
    private Handler coinHandler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){

                case GET_CURRENT_USER_SUCCESS:
                    currentUser=(User)(msg.obj);
                    break;

                case COINS_SHORT:
                    Toast.makeText(mContext,"金币不足，请重试！",Toast.LENGTH_LONG).show();
                    break;

                case COINS_ENOUGH:  //如果金币足够，则开始扣费
                    property=(Integer) msg.arg1;
                    payedCoin=(Integer) msg.arg2;
                    CommitRequest();
                    break;

                case DELETE_COIN_SUCCESS:  //扣费成功后，向服务器插入订单
                    String itemMainAddr=mainAddr.getSelectedItem().toString();
                    String finalAddr=ownerAddr.getSelectedItem().toString();
                    Order order=new Order(payedCoin,itemMainAddr,detailAddr,finalAddr,tel,name,code,notes,"未被接单");
                    order.setSeeker(currentUser);
                    InsertOrder(order);
                    break;

                case CREATE_ORDER_SUCCESS:  //生成订单成功后，清除用户以填信息
                    ClearOrder();
                    break;

                default:
                    break;
            }
        }
    };

}
