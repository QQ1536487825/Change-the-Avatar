package com.example.changeavater;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.CircleImageView;
import com.example.changeavater.Base.BaseActivity;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Uri imageUri;
    @BindView(R.id.tv_change)
    TextView textView;
    @BindView(R.id.civ_head)
    CircleImageView civ_head;
    @BindView(R.id.tv_photo)
    TextView tv_photo;

    public static final int TAKE_CAMERA = 101;
    public static final int PICK_PHOTO = 102;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initListener() {
        textView.setOnClickListener(this);
        tv_photo.setOnClickListener(this);


    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {


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
            case R.id.tv_photo:
                Toast.makeText(this,"kljf",Toast.LENGTH_SHORT).show();
//                创建File对象，用于储存拍照后的照片
//                存放在手机Sd卡的应用关联缓存目录下
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
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_CAMERA:
                if (requestCode == TAKE_CAMERA){
                    //将拍摄的照片显示出来
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
                        civ_head.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_PHOTO:
                if (requestCode == PICK_PHOTO) { //判断手机系统版本号
                    if (Build.VERSION.SDK_INT>=19){
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
        }
                break;
                default:
                    break;
    }
}
    //安卓4.4以前的处理方式
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);


    }

    @RequiresApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                //解析出数字格式id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("conten://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri,则使用普通方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //根据图片路径显示图片
        displayImage(imagePath);
        
    }

    private void displayImage(String imagePath) {
        if (imagePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            civ_head.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri externalContentUri, String selection) {
    String path = null;
    //通过Uri和selection来获取真实的图片路径
        Cursor cursor = this.getContentResolver().query(externalContentUri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;

    }

}
