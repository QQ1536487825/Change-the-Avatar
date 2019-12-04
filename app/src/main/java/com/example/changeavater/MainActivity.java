package com.example.changeavater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.allen.library.CircleImageView;
import com.example.changeavater.Base.BaseActivity;

import java.io.File;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Uri imageUri;
    @BindView(R.id.tv_change)
    TextView textView;
    @BindView(R.id.civ_head)
    CircleImageView civ_head;
    @BindView(R.id.tv_phone)
    TextView tv_phone;

    public static final int TAKE_CAMERA = 101;
    public static final int PICK_PHOTO = 102;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initListener() {
        textView.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {


    }

    @Override
    public int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_change:
                //动态申请获取访问，读取磁盘的权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
                }else {
//                    打开相册
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,PICK_PHOTO);
                }
                break;
            case R.id.tv_phone:
                //创建File对象，用于储存拍照后的照片
                //存放在手机Sd卡的应用关联缓存目录下
                File outputImage = new File(this.getExternalCacheDir(),"output_image.jpg");

                /*从6.0开始，读写sd卡被列为危险权限，如果将图片存放在sd卡的任何其他目录，
                都要进行运行时的权限处理才行，而使用应用关联目录则可以跳过这一步
                 */
                try {
                    if (outputImage.exists()){
                        outputImage.delete();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                /*从7.0开始，直接用本地真实路径Uri被认为不安全，会抛出一个FileUriExposedException异常。
                而FileProvider则是一种特殊的内容提供器，他使用了内容提供器类似的机制对数据进行保护
                ，可以选择性地的、将封装过的Uri共享给外部，从而提高了应用的安全性
                 */
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    //大于等于24（7.0）场合
                    imageUri = FileProvider.getUriForFile(this,"com.feige.pickphoto.fileprovider",outputImage);
                }else {
                    //小于android版本7.0（2.4）的场合
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机程序
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //MediaStore.ACTION_IMAGE_CAPTURE =
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_CAMERA);


        }
    }

}
