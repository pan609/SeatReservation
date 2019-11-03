package com.example.seat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener {

    private Drawable drawable_available, drawable_unavailable;
    private Button btn_seat1, btn_seat2, btn_seat3, btn_seat4, btn_seat5, btn_seat6, btn_cancel, btn_submit,clickedBtn;
    private String RoomNum, SeatNum=null, studentid;
    private TextView show_room;
    private SharedPreferences getRes;
    private Integer[] seatid;
    private Button[] buttons;
    /*private Handler handler=new Handler();
    private Runnable runnable=new Runnable() {      //页面定时刷新
        @Override
        public void run() {
            this.update();
            handler.postDelayed(this,1000);   //每秒刷新一次，不需要
        }
        void update(){      //更新数据的方法
            judgeReserved(getRes);
        }
    };;*/

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        Intent intent = getIntent();
        RoomNum = intent.getStringExtra("room_num");
        studentid = intent.getStringExtra("studentid");

        seatid = new Integer[]{R.id.seat1, R.id.seat2, R.id.seat3, R.id.seat4, R.id.seat5, R.id.seat6};   //座位id
        buttons = new Button[]{btn_seat1, btn_seat2, btn_seat3, btn_seat4, btn_seat5, btn_seat6};  //座位按钮

        for (int x = 0; x < 6; x++) {
            buttons[x] = (Button) findViewById(seatid[x]);
        }
        btn_submit = findViewById(R.id.submit);
        btn_cancel = findViewById(R.id.cancel);
        show_room = findViewById(R.id.showroom);
        show_room.setText("Room" + RoomNum);    //顶部显示房间号

        drawable_available = getResources().getDrawable(R.drawable.available);
        drawable_unavailable = getResources().getDrawable(R.drawable.unavailable);
        drawable_available.setBounds(0, 0, 120, 120);
        drawable_unavailable.setBounds(0, 0, 120, 120);

        getRes = getSharedPreferences("Reservation", MODE_PRIVATE);

        judgeReserved(getRes);//刚打开页面时，用函数探查该表，看看该房间是否有座位是被别人预定了的，有则修改他的背景颜色为红色，并设置为不可点击，其他的则正常

        final SharedPreferences getRes_Info = getSharedPreferences("Personal_Info" + studentid, MODE_PRIVATE);//查找该用户的表
        boolean self_reserved = getRes_Info.getBoolean("Reserved", false);
        if (self_reserved) {   //该用户已预定过座位，则不能再预定，冻结所有按钮
            unclickable();
        }

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(ChooseActivity.this);
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(ChooseActivity.this);
                if (SeatNum != null) {
                    builder.setTitle("提示").setMessage("确认选座？").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder2.setTitle("提示").setMessage("Room" + RoomNum + " " + SeatNum + "座位 " + "预约成功!").setPositiveButton("OK", null);
                            builder2.create().show();

                            clickedBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.btnshape_unclick));

                            Calendar calendar = Calendar.getInstance();   //调用日历获取今明两天时间
                            Date today = calendar.getTime();
                            calendar.add(Calendar.DATE, +1);
                            Date tomorrow = calendar.getTime();
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm E");
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                            String ChooseTime = sdf.format(today);
                            String Tomorrow = sdf2.format(tomorrow);

                            SharedPreferences.Editor editor = getSharedPreferences("Personal_Info" + studentid, MODE_PRIVATE).edit();  //保存预定信息到预定表中
                            editor.putString("Reserve_room", "Room" + RoomNum + " " + SeatNum + "座位");
                            editor.putString("Reserve_time", ChooseTime);
                            editor.putString("Reservefor_time", Tomorrow + " 06:30-23:00");
                            editor.putBoolean("Reserved", true);      //用户表中的这个标签表示该用户  已经预定座位了，则其他页面也不能再预定
                            editor.apply();

                            SharedPreferences.Editor editor1 = getSharedPreferences("Reservation", MODE_PRIVATE).edit();
                            editor1.putBoolean("Room" + RoomNum + " " + SeatNum + "座位", true);   //该座位是否已被预订，包括他人和自己预定都算
                            editor1.apply();
                            unclickable();   //选中后全部座位锁定
                        }
                    }).setNegativeButton("No", null);
                } else {
                    builder.setTitle("提示").setMessage("请先选择座位！");
                }
                builder.create().show();

            }
        });

        for (int k = 0; k < 6; k++) {
            buttons[k].setOnClickListener(this);  //为每一个座位设置监听器
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {        //取消过后则除了被预定了的都能点
                   //取消的前提是该用户已点击选择了座位，若座位号为空，则不处理
                    SharedPreferences.Editor editor = getSharedPreferences("Personal_Info" + studentid,MODE_PRIVATE).edit();  //保存预定信息到该用户的表中
                    String Res_room = getRes_Info.getString("Reserve_room", "");
                    editor.putString("Reserve_room", "暂无");
                    editor.putString("Reserve_time", "暂无");
                    editor.putString("Reservefor_time", "暂无");
                    editor.putBoolean("Reserved", false);//将预定信息删除后，该界面也要设置未预定过，只有预定过的才冻住按钮

                    SharedPreferences.Editor editor2 = getSharedPreferences("Reservation", MODE_PRIVATE).edit();
                    editor2.putBoolean(Res_room, false);
                    editor2.apply();
                    editor.apply();

                    //正则表达匹配得到座位号，从而找到座位按钮进行改变
                    judgeReserved(getRes);
                    for (int k=1;k<=6;k++){
                        String regex=" "+k+"号座位";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher m = pattern.matcher(Res_room);
                        Log.i("gdsgs","g12334445");
                        if(m.find()){
                            buttons[k-1].setBackgroundDrawable(getResources().getDrawable(R.drawable.btnshape)); //将用户预定的座位还原到可选状态
                            buttons[k-1].setCompoundDrawables(null, drawable_available, null, null);
                        }
                    }
                    btn_submit.setEnabled(true);
            }
        });
    }

    private void judgeReserved(SharedPreferences getRes) {
        for (int i = 0; i < 6; i++) {
            boolean self_reserved = getRes.getBoolean("Room" + RoomNum + " " + String.valueOf(i + 1) + "号座位", false);
            if (self_reserved) {  //已经被预定了，则对应按钮置为红色，并设置为不可点击，所以通过按钮数组获取对应按钮呢？？？
                buttons[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.btnshape_unclick));
                buttons[i].setEnabled(false);
                buttons[i].setCompoundDrawables(null, drawable_unavailable, null, null);//选中座位后更改按钮图片
            } else {
                initial(i);  //没有被预定的则初始化为可以点击的图标,当然按钮也可点击
                clickable(i);
            }
        }
    }

    @Override
    public void onClick(View v) { //为每一个按钮添加点击事件
        int id = v.getId();  //获取当前点击的那个按钮的id
        for (int m = 0; m < 6; m++) {
            if (buttons[m].getId() == id) {    //判断点击的是哪个按钮,但是要把刚刚点击了的消除
                judgeReserved(getRes);
                SeatNum = buttons[m].getText().toString();
                clickedBtn=buttons[m];   //记录最后选中的座位（按钮），把这个被点击的按钮传出去，便于对其进行修改
                buttons[m].setCompoundDrawables(null, drawable_unavailable, null, null);

            }
        }
    }

    public void initial(int i) {     //初始化设置按钮图片
        buttons[i].setCompoundDrawables(null, drawable_available, null, null);
    }

    public void unclickable() {    //设置按钮不可点击
        buttons[0].setEnabled(false);
        buttons[1].setEnabled(false);
        buttons[2].setEnabled(false);
        buttons[3].setEnabled(false);
        buttons[4].setEnabled(false);
        buttons[5].setEnabled(false);
        btn_submit.setEnabled(false);
    }

    public void clickable(int i) {    //设置按钮可点击
        buttons[i].setEnabled(true);
    }
}
    /*public void onDestroy() {
        handler.removeCallbacks(runnable);      //停止刷新
        super.onDestroy();
    }*/

   /* public void buttonset(Button button,String seatNum,String roomNum){ //判断该座位是否已被预定过，现已不需要
        boolean Reserved=getRes1.getBoolean("Room"+roomNum+" "+seatNum+"座位",false);
        if(Reserved){  //已被预订，则提示不能预定此座位并置颜色为红？？？？
            builder=new AlertDialog.Builder(this);
            builder.setTitle("提示").setMessage("该座位已被预定，请重新选座！");
            builder.create().show();
            //button.setCompoundDrawables(null,drawable_unavailable,null,null);
        }
        else{  //未被预定，则可以预定
            initial();
            button.setCompoundDrawables(null,drawable_unavailable,null,null);//选中座位后更改按钮图片
            // btn_seat1.setBackgroundColor(Color.parseColor("#D81B60"));
        }
    }
}*/
