package com.example.seat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

public class MineFragment extends Fragment {


    private TextView show_id,show_name,show_ctime,show_cinfo,show_tomorrow;
    private Button scan;
    private String studentid,showid,showname,showtime,showinfo,showtomorrow;

    private Handler handler=new Handler();
    private Runnable runnable=new Runnable() {      //页面定时刷新
        @Override
        public void run() {
            this.update();
            handler.postDelayed(this,1000);   //每秒刷新一次
        }
        void update(){      //更新数据的方法
            SharedPreferences getPersonal = getActivity().getSharedPreferences("Personal_Info" + studentid, Context.MODE_MULTI_PROCESS);
            showid = getPersonal.getString("stu_id", "");
            showname = getPersonal.getString("stu_name", "");
            showtime = getPersonal.getString("Reserve_time", "");
            showinfo = getPersonal.getString("Reserve_room", "");
            showtomorrow = getPersonal.getString("Reservefor_time", "");

            show_id.setText(showid);
            show_name.setText(showname);
            show_ctime.setText(showtime);
            show_cinfo.setText(showinfo);
            show_tomorrow.setText(showtomorrow);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mine, container);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        show_id = (TextView) getView().findViewById(R.id.showid);
        show_name = (TextView) getView().findViewById(R.id.showname);
        show_ctime = (TextView) getView().findViewById(R.id.showtime);
        show_cinfo = (TextView) getView().findViewById(R.id.showinfo);
        show_tomorrow = (TextView) getView().findViewById(R.id.tomorrow);
        scan = (Button) getView().findViewById(R.id.scan);
        studentid = ((FrameActivity) getActivity()).getStudent_id();     //从这个fragment所在activity中获取对应的studnentid，用于读取该用户的数据

        handler.postDelayed(runnable,1000);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }
    //用于实现扫描二维码功能，源自网上代码，https://blog.csdn.net/weixin_43117800/article/details/83830664
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*** 处理二维码的扫描结果*/
        if (requestCode == 1){
            //处理扫描结果(在界面上显示)
            if (data != null){
                Bundle bundle = data.getExtras();
                if (bundle == null){
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS){
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(getActivity(),"解析结果"+result,Toast.LENGTH_LONG).show();
                }else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED){
                    Toast.makeText(getActivity(),"解析二维码失败",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    public void onDestroy() {
        handler.removeCallbacks(runnable);      //停止刷新
        super.onDestroy();
    }
}
