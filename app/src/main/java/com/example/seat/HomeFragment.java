package com.example.seat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener {

    List<String> rooms=new ArrayList<String>();
    ArrayAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView=(ListView)getView().findViewById(R.id.roomlist);     //定义教室列表
        for(int i=1;i<11;i++){
            rooms.add("Room"+i);
        }
        adapter=new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,rooms);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);   //列表点击事件
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent Inroom=new Intent(getActivity(),ChooseActivity.class);   //通过getActivity()方法跳转，fragment不能直接跳转
        Inroom.putExtra("room_num",String.valueOf(position+1));
        Inroom.putExtra("studentid",((FrameActivity)getActivity()).getStudent_id());//该fragment将从其父activity中获取到的用户id跳转传给另一个Activity choose
        startActivity(Inroom);
        adapter.notifyDataSetChanged();
    }

}
