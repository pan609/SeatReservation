package com.example.seat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG ="RegisterActivity";
    private EditText regid,regcode,repregcode,regname;
    private Button but_signup;
    private String Regid,Regcode,Repregcode,Regname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regid=findViewById(R.id.regstu_id);
        regcode=findViewById(R.id.regstu_code);
        repregcode=findViewById(R.id.repstu_code);
        regname=findViewById(R.id.regstu_name);
        but_signup=findViewById(R.id.signup);
        but_signup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);    //错误弹窗提示
        Regid=regid.getText().toString();
        Regcode=regcode.getText().toString();
        Repregcode=repregcode.getText().toString();
        Regname=regname.getText().toString();     //获取用户信息后存入数据库
        if(!Regid.isEmpty()&&!Regcode.isEmpty()&&!Repregcode.isEmpty()&&!Regname.isEmpty()){  //确保各项信息填写完整
            if(Regcode.equals(Repregcode)) {         //验证两次输入密码相等
                //这里建立的用户表加上用户id，确保对于每个注册的用户各有一张表保存个人信息，最开始考虑的一张用户表保存所有用户信息，但发现仍然需要区别各自的个人信息
                //所以干脆各自一张表连同选座位的信息一起记录，再单独维护一张座位选择表可记录所有人的选作情况，确保不会相互影响
                SharedPreferences.Editor editor = getSharedPreferences("Personal_Info"+Regid, MODE_PRIVATE).edit();
                editor.putString("stu_id", Regid);       //保存学号
                editor.putString("stu_code", Regcode);  //保存密码
                editor.putString("stu_name", Regname);  //保存姓名
                editor.apply();

                Intent backtoLogin=new Intent(RegisterActivity.this,LoginActivity.class);   //返回登陆界面
                startActivity(backtoLogin);
            }else{
                builder.setTitle("提示").setMessage("两次密码输入不同，请重新确认密码！");
                builder.create().show();
            }
        }else {
            builder.setTitle("提示").setMessage("请填写完整各项信息！");
            builder.create().show();
        }
    }
}
