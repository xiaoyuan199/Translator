package com.example.mytranslation.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mytranslation.R;
import com.example.mytranslation.constants.Constants;
import com.example.mytranslation.db.DButil;
import com.example.mytranslation.db.NotebookDatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class DailyOneFragment extends Fragment {

    private RequestQueue queue;

    private TextView textEnglish;
    private  TextView textChinese;
    private ImageView imageDay;
    private ImageView imageStar;
    private ImageView imageCopy;
    private ImageView imageShare;

    private Boolean isMark=false;

    private NotebookDatabaseHelper dbHelper;

    private String imageUri=null;

   public DailyOneFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue= Volley.newRequestQueue(getActivity().getApplicationContext());
        dbHelper=new NotebookDatabaseHelper(getActivity(),"MyStore.db",null,1);
                                                           //数据库名
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.day_one_imge,container,false);

        initView(view);

        requestData();

        imageStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在没有被收藏的情况下
                if(!isMark){
                    imageStar.setImageResource(R.drawable.ic_star_black_24dp);
                    Snackbar.make(imageStar,"收藏成功",Snackbar.LENGTH_SHORT).show();
                    isMark=true;

                    ContentValues values=new ContentValues();
                    values.put("input",textEnglish.getText().toString());
                    values.put("output",textChinese.getText().toString());
                    DButil.insertValue(dbHelper,values);
                    values.clear();

                }else {
                    imageStar.setImageResource(R.drawable.ic_star_border_black_24dp);
                    Snackbar.make(imageStar,"取消收藏",Snackbar.LENGTH_SHORT).show();
                    isMark=false;
                    DButil.deleteValue(dbHelper,textEnglish.getText().toString());
                }
            }
        });

        imageCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager= (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipdata=ClipData.newPlainText("text",textEnglish.getText().toString()
                        +"\n"
                        +textChinese.getText().toString());
                manager.setPrimaryClip(clipdata);
                Snackbar.make(imageCopy,"复制成功",Snackbar.LENGTH_SHORT).show();
            }
        });

        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,textEnglish.getText().toString()
                        +"\n"
                        +textChinese.getText().toString());
                startActivity(Intent.createChooser(intent,"请选择要分享的应用"));
            }
        });



        return view;
    }


    private void initView(View view) {

        imageDay= (ImageView) view.findViewById(R.id.image_view_daily);
        textEnglish= (TextView) view.findViewById(R.id.text_view_English);
        textChinese= (TextView) view.findViewById(R.id.text_view_Chinese);
        imageStar= (ImageView) view.findViewById(R.id.image_view_mark_star);
        imageCopy= (ImageView) view.findViewById(R.id.image_view_copy);
        imageShare= (ImageView) view.findViewById(R.id.image_view_share);



    }

    private void requestData() {

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, Constants.DAILY_SENTENCE,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try{

                            imageUri=jsonObject.getString("picture2");

                            Glide.with(getActivity())
                                    .load(imageUri)
                                    .asBitmap()//这个方法的意思就是说这里只允许加载静态图片
                                    .placeholder(R.drawable.head_img)//占位图
                                    .error(R.drawable.head_img)//加载失败时显示的图
                                    .into(imageDay);

                            textEnglish.setText(jsonObject.getString("content"));
                            textChinese.setText(jsonObject.getString("note"));

                            if(DButil.quearyIfItemExit(dbHelper,textEnglish.getText().toString())){

                                imageStar.setImageResource(R.drawable.ic_star_black_24dp);
                                isMark=true;

                            }else{
                                imageStar.setImageResource(R.drawable.ic_star_border_black_24dp);
                                isMark=false;
                            }


                        }catch (JSONException e){
                           e.printStackTrace();
                        }

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            Snackbar.make(imageDay,"网络异常",Snackbar.LENGTH_SHORT).show();

            }
        });

        queue.add(request);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(imageUri!=null){

            Glide.with(getActivity())
                    .load(imageUri)
                    .asBitmap()
                    .error(R.drawable.head_img)
                    .into(imageDay);

        }



    }
}
