package com.example.seat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText InputNum,InputCode;
    private Button but_login,but_register;
    private String Id,Code,input_Num,input_Code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InputNum=findViewById(R.id.stu_id);
        InputCode=findViewById(R.id.stu_code);
        but_login=findViewById(R.id.login);
        but_register=findViewById(R.id.register);

        but_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_Num=InputNum.getText().toString();  //输入的学号
                input_Code=InputCode.getText().toString();//输入的密码

                SharedPreferences getpref=getSharedPreferences("Personal_Info"+input_Num, MODE_PRIVATE); //查看该用户对应的用户表是否存在，并比较账号密码是否匹配
                Id=getpref.getString("stu_id","");
                Code=getpref.getString("stu_code","");

                if (input_Num.equals(Id)&&input_Code.equals(Code)){    //密码和账号匹配
                    Intent toHome=new Intent(LoginActivity.this,FrameActivity.class);  //登陆成功，跳转到主界面（教室列表）
                    toHome.putExtra("id",input_Num);        //将id 传到下一活动中，以让下面的活动知道该在哪个用户表里面找
                    startActivity(toHome);
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);   //登陆失败
                    builder.setTitle("登陆失败").setMessage("输入账号密码不匹配！");
                    builder.create().show();
                }
            }
        });

        but_register.setOnClickListener(new View.OnClickListener() {   //点击注册，跳转到注册界面
            @Override
            public void onClick(View v) {
                Intent toRegister=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(toRegister);
            }
        });
    }
}
