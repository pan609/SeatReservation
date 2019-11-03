package com.example.seat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.support.v4.content.ContextCompat.getSystemService;


public class FeedbackFragment extends Fragment {

    private EditText pub_title, pub_content;
    private TextView phone;
    private Button pub_button, pub_photo;
    private Drawable drawable_photo;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pub_title = (EditText) getView().findViewById(R.id.public_title);
        pub_content = (EditText) getView().findViewById(R.id.public_content);
        pub_button = (Button) getView().findViewById(R.id.public_button);
        phone=(TextView)getView().findViewById(R.id.phone);

        pub_photo = (Button) getView().findViewById(R.id.public_photo);
        drawable_photo = getResources().getDrawable(R.drawable.photo);  //设置按钮的形式
        drawable_photo.setBounds(0, 0, 250, 250);
        pub_photo.setCompoundDrawables(null, drawable_photo, null, null);

        picture = (ImageView) getView().findViewById(R.id.picture);

       pub_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {      //引用的书上的choose_from_album功能，未实现
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                    pub_photo.setVisibility(View.INVISIBLE);  //隐藏按钮
                }
            }
        });

       phone.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               callPhone(phone.getText().toString());
           }
       });

        pub_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = pub_title.getText().toString();  //得到标题
                final String content = pub_content.getText().toString();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());
                String current_time = formatter.format(curDate);
                SharedPreferences.Editor editor= getActivity().getSharedPreferences("Feedback_Info"+((FrameActivity)getActivity()).getStudent_id(), MODE_PRIVATE).edit();  //保存反馈信息到反馈表中
                editor.putString("Fback_title", title);
                editor.putString("Fback_content", content);
                editor.putString("Fback_time", current_time);
                editor.apply();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());    //提交通知
                builder.setTitle("提示").setMessage("反馈提交成功！").setPositiveButton("OK",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("gassd",content);
                        pub_content.setText("");    //提交后清空
                        pub_title.setText("");
                    }
                });
                builder.create().show();
            }
        });
    }

    public void callPhone(String phoneNum){         //textview一个属性设置为autoLink="phone"或者all，转化成超链接实现打电话功能，网上找的功能
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);  //打开相册
    }



    public void onRequestPermissionResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                }
                else {
                    Toast.makeText(getActivity(),"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case CHOOSE_PHOTO:
                if (requestCode==RESULT_OK){
                    if (Build.VERSION.SDK_INT>=19){
                        //4.4以上系统使用这个方法处理图片
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(getActivity(),uri)){//如果是document类型的Uri，则通过document id处理
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数字格式的id
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){ //如果是content类型的Uri，则使用普通方式处理
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){ //如果是file类型的Uri，直接获取图片路径即可
            imagePath=uri.getPath();
        }
        displayImage(imagePath);//根据路径显示图片
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri, String selection) {
        String path=null;       //通过Uri和selection来获取真实的图片路径
        Cursor cursor=getActivity().getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath){
        if (imagePath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
            //picture.setImageBitmap(bitmap);

            Resources r = getResources();
            float screenWidth = r.getDimension(R.dimen.activity_horizontal_margin);
           // float btn_w = r.getDimension(R.dimen.activity_vertical_margin);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            //int screenWidth=dm.widthPixels;
            if(bitmap.getWidth()<=screenWidth){
                picture.setImageBitmap(bitmap);
            }else{
                Bitmap bmp=Bitmap.createScaledBitmap(bitmap,(int)screenWidth, bitmap.getHeight()*(int)screenWidth/bitmap.getWidth(), true);
                picture.setImageBitmap(bmp);
            }
            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();  //保存图片
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);
            String imageBase64 = new String(Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
            editor.putString("Fback_image",imageBase64);*/
        }else{
            Toast.makeText(getActivity(),"faild to get image",Toast.LENGTH_SHORT).show();
        }
    }
}
