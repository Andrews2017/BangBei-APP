package com.example.dell.demo.Activities;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.demo.MeRelative.MeAdapter;
import com.example.dell.demo.MeRelative.MeOption;
import com.example.dell.demo.MeRelative.OptionItemDecoration;
import com.example.dell.demo.NavigateLayout;
import com.example.dell.demo.R;
import com.example.dell.demo.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MeActivity extends AppCompatActivity implements View.OnClickListener{

    private String userId;
    private Context mContext;
    private ImageView protrait;
    private TextView nameText;
    private RecyclerView optionRecyclerView;
    private NavigateLayout meNavigation;
    private List<MeOption> meList=new ArrayList<>();

    private Uri imageUri;
    private Dialog mCameraDialog;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        Intent intent=getIntent();
        userId=intent.getStringExtra("extra_userId");

        initOptions();
        initWidgets();
        QueryProtrait(userId);
        QueryName(userId);

    }

    public void initWidgets(){

        mContext=MeActivity.this;

        meNavigation=(NavigateLayout)findViewById(R.id.me_navigation);
        meNavigation.setUserId(userId);

        protrait=(ImageView)findViewById(R.id.img_protrait);
        protrait.setOnClickListener(this);
        nameText=(TextView)findViewById(R.id.txt_user_name_me);


        optionRecyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        optionRecyclerView.addItemDecoration(new OptionItemDecoration());
        optionRecyclerView.setBackgroundColor(Color.argb(0xff,0xcc,0xcc,0xcc));


        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        optionRecyclerView.setLayoutManager(layoutManager);
        MeAdapter adapter=new MeAdapter(meList,userId);
        optionRecyclerView.setAdapter(adapter);

    }

    public void initOptions(){

        MeOption property=new MeOption("我的余额",R.drawable.wodeyue2);
        meList.add(property);
        MeOption psh=new MeOption("求助记录",R.drawable.qiuzhulishi);
        meList.add(psh);
        MeOption ph=new MeOption("帮助记录",R.drawable.bangzhulishi3);
        meList.add(ph);
        MeOption help=new MeOption("帮助",R.drawable.bangzhu2);
        meList.add(help);
        MeOption about=new MeOption("关于",R.drawable.guanyu2);
        meList.add(about);
        MeOption exit=new MeOption("退出账号");
        meList.add(exit);

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.img_protrait:
                ChangeProtrait();
                break;

            case R.id.btn_open_camera:
                TakePhoto();
                break;

            case R.id.btn_open_album:
                ChoosePhoto();
                break;

            case R.id.btn_cancel_dialog:
                mCameraDialog.cancel();
                break;
        }
    }


    public void QueryName(final String userId){

        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",userId).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
//                    String userName=list.get(0).getUserName();
//                    Message msg=new Message();
//                    msg.what=QUERY_USER_NAME_SUCCESS;
//                    msg.obj=userName;
//                    userName=list.get(0).getUserName();
                    nameText.setText(list.get(0).getUserName());

                }else{
                    Toast.makeText(mContext,"查询用户失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void QueryProtrait(String userId){

        BmobQuery<User> query=new BmobQuery<>();
        query.addWhereEqualTo("objectId",userId).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    BmobFile icon=list.get(0).getIcon();
                    if(icon!=null){   //如果没上传头像，则使用默认头像

                        String imagePath=icon.getFileUrl();  //获取已上传头像的URL地址

                        if(imagePath.length()!=0){  //如果上传了图片，则用ImageLoader开源库去加载
                            ImageLoader imageLoader=ImageLoader.getInstance();
                            imageLoader.displayImage(imagePath,protrait);
                        }else{
                            protrait.setImageResource(R.drawable.defaulthead);
                        }
//
//                        String fileName= list.get(0).getObjectId()+".PNG";
//                        File image = new File(getExternalCacheDir(), list.get(0).getObjectId()+".PNG");
//                        String path = getExternalCacheDir()+"/"+list.get(0).getObjectId()+".PNG";  //将要存在本地的URL路径
//
//                        if(image.exists()){  //如果已下载，则直接显示头像
//                            Bitmap exitsBitmap = BitmapFactory.decodeFile(path);
//                            protrait.setImageBitmap(exitsBitmap);
//                        }else{  //如果还未下载，则根据服务器端的URL地址开始下载图片到缓存
//                            try{
//                                image.createNewFile();
//                                URL url = new URL(imagePath);
//                            }catch (IOException e2){
//                                e2.printStackTrace();
//                            }
//                            /*第一个参数是下载后的文件名，第三个参数是下载地址URL*/
//                            BmobFile bmobfile =new BmobFile(fileName,"",imagePath);
//                            downloadFile(bmobfile);
//                        }
                    }
                }else{
                    Toast.makeText(mContext,"查询用户失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    /*将文件下载到当前应用的默认缓存目录中，以file.getFilename()得到的值为文件名*/
    public void downloadFile(BmobFile file){

        File saveFile = new File(getExternalCacheDir(), file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void done(String savePath,BmobException e) {
                if(e==null){  //如果下载完成，就将它显示在ImageView上
                    Bitmap exitsBitmap = BitmapFactory.decodeFile(savePath);
                    protrait.setImageBitmap(exitsBitmap);
                }else{
                    Toast.makeText(mContext,"下载头像失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
            }

        });

    }


    /*点击头像的响应事件。可以拍照或从相册更换照片*/
    public void ChangeProtrait(){
        mCameraDialog=new Dialog(this,R.style.CameraDialog);

        /*先获取三个按钮的父布局，再对内部的三个按钮设置监听事件*/
        LinearLayout root=(LinearLayout) LayoutInflater.from(this).inflate(R.layout.camera_dialog_layout,null);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(this);
        root.findViewById(R.id.btn_open_album).setOnClickListener(this);
        root.findViewById(R.id.btn_cancel_dialog).setOnClickListener(this);

        mCameraDialog.setContentView(root);

        Window dialogWindow=mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.DialogAnimation);

        WindowManager.LayoutParams layoutParams=dialogWindow.getAttributes();
        layoutParams.x=0;
        layoutParams.y=0;
        layoutParams.width=(int)getResources().getDisplayMetrics().widthPixels;
        root.measure(0,0);
        layoutParams.height=root.getMeasuredHeight();
        layoutParams.alpha=9f;

        dialogWindow.setAttributes(layoutParams);
        mCameraDialog.show();
    }


    public void TakePhoto(){
        Toast.makeText(mContext,"敬请期待",Toast.LENGTH_SHORT).show();
//        File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
//        try{  /*如果缓存有数据，则先清空缓存*/
//            if(outputImage.exists()){
//                outputImage.delete();
//            }
//            outputImage.createNewFile();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//
//        /*针对不同的安卓版本，把File对象转化为Uri对象*/
//        if(Build.VERSION.SDK_INT>=24){
//            imageUri= FileProvider.getUriForFile(mContext,"com.example.dell.demo.fileprovider",outputImage);
//            Log.d("FilePath==",imageUri.getPath());
//        }else{
//            imageUri=Uri.fromFile(outputImage);
//        }
//
//        Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
//        startActivityForResult(intent,TAKE_PHOTO);
    }

    public void ChoosePhoto(){
         /*动态申请相册的访问权限*/
        if(ContextCompat.checkSelfPermission(MeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
        }else{
            openAlbum();
        }
    }

    @Override   /*拍照或选择照片返回后，进行相应的操作*/
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        mCameraDialog.cancel();
        switch (requestCode){

            /*如果是从拍照返回*/
            case TAKE_PHOTO:
                if(resultCode==RESULT_OK ){
                    /*如果拍照成功，就将照片解析为Bitmap对象，并在ImageView控件中显示*/
                    try{
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        protrait.setImageBitmap(bitmap);

//                        String picPath=imageUri.getPath();


                        String []imgs={MediaStore.Images.Media.DATA};//将图片URI转换成存储路径
                        Cursor cursor1=this.managedQuery(imageUri, imgs, null, null, null);
                        int index1=cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor1.moveToFirst();
                        String picPath=cursor1.getString(index1);
                        UploadPicture(picPath);

                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;

            /*如果是从相册选择照片返回*/
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    /*如果获取相册照片成功，再根据系统版本对照片进行不同地解析*/
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
        }

    }

    //将头像上传至服务器
    public void UploadPicture(String picPath){

        final BmobFile icon=new BmobFile(new File(picPath));
        icon.uploadblock(new UploadFileListener(){
            @Override
            public void done(BmobException e){
                if(e==null){

                    /*原理同修改数据一样，都是新建User对象，改变icon，然后调用update*/
                    User user=new User();
                    user.setIcon(icon);
                    user.update(userId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Toast.makeText(mContext,"上传成功！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(mContext,"上传头像失败，请重试！"+e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onProgress(Integer value) {
                super.onProgress(value);
            }
        });
    }


    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @TargetApi(19)  /*解析封装过的Uri对象*/
    private void handleImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();

        //以下将根据Uri的不同类型分情况进行解析

        if(DocumentsContract.isDocumentUri(this,uri)){  /*如果是Document类型的Uri*/
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID +"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){  /*如果是content类型的Uri，则用普通方式处理*/
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){  /*如果是file类型的Uri，直接获取图片路径即可*/
            imagePath=uri.getPath();
        }

        displayImage(imagePath);
    }


    /*解析未封装过的Uri对象*/
    private void handleImageBeforeKitKat(Intent data){
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }


    /*通过Uri和selection来获取图片真实的路径*/
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    /*根据图像的路径，把它显示在ImageView控件上*/
    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            protrait.setImageBitmap(bitmap);
            UploadPicture(imagePath);

        }else {
            Toast.makeText(this,"获取图片失败!",Toast.LENGTH_SHORT).show();
        }
    }


    @Override  /*动态获取系统权限*/
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(mContext,"您拒绝了权限申请",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
