package com.example.changeavater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.allen.library.CircleImageView;
import com.example.changeavater.Base.BaseActivity;

import java.io.File;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

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

                //从6.0开始，读写sd卡被列为危险权限，如果将图片存放在sd卡的任何其他目录

        }
    }

}
