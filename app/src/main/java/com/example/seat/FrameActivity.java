package com.example.seat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FrameActivity extends FragmentActivity {

    private Drawable drawable_Home,drawable_Feedback,drawable_Clause,drawable_Mine;;
    private Fragment mFragments[];
    private RadioGroup radioGroup;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private RadioButton rbtHome,rbtFeedback,rbtClause,rbtMine;
    private String student_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        mFragments=new Fragment[4];    //四个fragment的框
        fragmentManager=getSupportFragmentManager();
        mFragments[0]=fragmentManager.findFragmentById(R.id.fragment_feedback);
        mFragments[1]=fragmentManager.findFragmentById(R.id.fragment_home);
        mFragments[2]=fragmentManager.findFragmentById(R.id.fragment_clause);
        mFragments[3]=fragmentManager.findFragmentById(R.id.fragment_mine);
        fragmentTransaction=fragmentManager.beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]);
        fragmentTransaction.show(mFragments[1]).commit();    //打开时显示的是选座界面

        radioGroup=(RadioGroup)findViewById(R.id.bottomGroup);  //页面下方四个按钮
        rbtHome=(RadioButton)findViewById(R.id.radioHome);
        rbtFeedback=(RadioButton)findViewById(R.id.radioFeedback);
        rbtClause=(RadioButton)findViewById(R.id.radioClause);
        rbtMine=(RadioButton)findViewById(R.id.radioMine);

        Background();

        Intent intent=getIntent();
        student_id=intent.getStringExtra("id");    //获取从login传来的用户id，便于其下几个fragment读写对应的用户表

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                fragmentTransaction=
                        fragmentManager.beginTransaction().hide(mFragments[0]).hide(mFragments[1]).hide(mFragments[2]).hide(mFragments[3]);
                switch (checkedId){
                    case R.id.radioHome:    //单选按钮，设置点击对应按钮后的图片变化
                        fragmentTransaction.show(mFragments[1]).commit();
                        Background();
                        drawable_Home=getResources().getDrawable(R.drawable.home2);        //底部按钮设置图片
                        drawable_Home.setBounds(0,0,130,130);
                        rbtHome.setCompoundDrawables(null,drawable_Home,null,null);
                        break;
                    case R.id.radioFeedback:
                        fragmentTransaction.show(mFragments[0]).commit();
                        Background();
                        drawable_Feedback=getResources().getDrawable(R.drawable.closefile);        //底部按钮设置图片
                        drawable_Feedback.setBounds(0,0,140,130);
                        rbtFeedback.setCompoundDrawables(null,drawable_Feedback,null,null);
                        break;
                    case R.id.radioClause:
                        fragmentTransaction.show(mFragments[2]).commit();
                        Background();
                        drawable_Clause=getResources().getDrawable(R.drawable.clausenew);        //底部按钮设置图片
                        drawable_Clause.setBounds(0,0,140,130);
                        rbtClause.setCompoundDrawables(null,drawable_Clause,null,null);
                        break;
                    case R.id.radioMine:
                        fragmentTransaction.show(mFragments[3]).commit();
                        Background();
                        drawable_Mine=getResources().getDrawable(R.drawable.unavailable);        //底部按钮设置图片
                        drawable_Mine.setBounds(0,0,140,130);
                        rbtMine.setCompoundDrawables(null,drawable_Mine,null,null);
                        break;
                     default:
                         break;
                }
            }
        });
    }

    public void Background(){    //初始化底部按钮的属性
        drawable_Home=getResources().getDrawable(R.drawable.home1);        //底部按钮设置图片
        drawable_Home.setBounds(0,0,130,130);
        rbtHome.setCompoundDrawables(null,drawable_Home,null,null);

        drawable_Feedback=getResources().getDrawable(R.drawable.openfile);        //底部按钮设置图片
        drawable_Feedback.setBounds(0,0,140,130);
        rbtFeedback.setCompoundDrawables(null,drawable_Feedback,null,null);

        drawable_Clause=getResources().getDrawable(R.drawable.openclause);        //底部按钮设置图片
        drawable_Clause.setBounds(0,0,130,130);
        rbtClause.setCompoundDrawables(null,drawable_Clause,null,null);

        drawable_Mine=getResources().getDrawable(R.drawable.available);        //底部按钮设置图片
        drawable_Mine.setBounds(0,0,140,130);
        rbtMine.setCompoundDrawables(null,drawable_Mine,null,null);
    }
    public String getStudent_id(){  //该方法可将用户id传给该activity下的framents
        return student_id;
    }
    public void setStudent_id(String student_id){
        this.student_id=student_id;
    }
}
