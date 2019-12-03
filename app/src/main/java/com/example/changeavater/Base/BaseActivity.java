package com.example.changeavater.Base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    public Context mContext; //上下文
    Unbinder unbinder;
    /**是否显示标题栏*/
    private boolean isshowtitle = true;

    /**封装toast对象*/
    private static Toast toast;

    /**获取TAG的activity名称**/
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (!isshowtitle){
            //需要软件全屏显示、自定义标题（使用按钮等控件）和其他的需求,
            //.DEFAULT_FEATURES：系统默认状态，一般不需要指定
            //
            //2.FEATURE_CONTEXT_MENU：启用ContextMenu，默认该项已启用，一般无需指定
            //
            //3.FEATURE_CUSTOM_TITLE：自定义标题。当需要自定义标题时必须指定。如：标题是一个按钮时
            //
            //4.FEATURE_INDETERMINATE_PROGRESS：不确定的进度
            //
            //5.FEATURE_LEFT_ICON：标题栏左侧的图标
            //
            //6.FEATURE_NO_TITLE：没有标题
            //
            //7.FEATURE_OPTIONS_PANEL：启用“选项面板”功能，默认已启用。
            //
            //8.FEATURE_PROGRESS：进度指示器功能
            //
            //9.FEATURE_RIGHT_ICON:标题栏右侧的图标
            requestWindowFeature(Window.FEATURE_NO_TITLE); //没有标题
        }
        //设置布局
        setContentView(initLayout());
        unbinder = ButterKnife.bind(this); //绑定控件

        //初始化控件
        initView();
        //设置数据
        initData();

        //设置监听
        initListener();
    }

    //设置监听
    protected abstract void initListener();

    //设置数据
    protected abstract void initData();

    //初始化布局
    protected abstract void initView();

    //设置布局
    public abstract int initLayout();

    //是否设置标题栏
    public void setTitle(boolean ishow){
        isshowtitle = ishow;
    }

    //设置是否显示状态栏

    public void setState(boolean isshow){
    }

    //显示长toast
    public void toastLong(String msg){
        if (null == toast){
            toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_LONG);//设置toast显示时长
            toast.setText(msg);
            toast.show();
        }else {
            toast.setText(msg);
            toast.show();
        }
    }

    //显示短toast
    public void toastShort(String msg){
        if (null == toast){
            toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(msg);
            toast.show();
        }else {
            toast.setText(msg);
            toast.show();
        }
    }
    private void unbinder(){
        if (unbinder != null){
            unbinder.unbind();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder();
    }
}
