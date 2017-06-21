package com.example.mytranslation.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mytranslation.R;
import com.example.mytranslation.adapter.SampleAdapte;
import com.example.mytranslation.constants.Constants;
import com.example.mytranslation.db.DButil;
import com.example.mytranslation.db.NotebookDatabaseHelper;
import com.example.mytranslation.model.BingModel;
import com.example.mytranslation.util.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.N;
import static com.example.mytranslation.R.id.textView;

/**
 * Created by Administrator on 2017/4/19 0019.
 */

public class TranslateFragment extends Fragment {

    private EditText editText;
    private TextView textViewClear;
    private ProgressBar progressBar;
    private TextView textViewResult;//搜索到的单词的解释
    private ImageView imageViewMark;
    private View viewResult;
    private AppCompatButton button;

    private ArrayList<BingModel.Sample> samples;
    private RecyclerView recyclerView;
    private SampleAdapte sampleAdapte;

    private NotebookDatabaseHelper dbHelper;
    private  BingModel bingModel;

    private RequestQueue queue;

    private String result=null;
    private Boolean isMarked=false;


    // empty constructor required
    public TranslateFragment(){

    }

////？？？/////////////////////////////////////////////////
    public static TranslateFragment newInstance() {
        return new TranslateFragment();
    }


    //onCreate是指创建该fragment，类似于Activity.onCreate，你可以在其中初始化除了view之外的东西；
    //onCreateView是创建该fragment对应的视图，你必须在这里创建自己的视图并返回给调用者。
///////////////////////////////////////////////////
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,container,false);
        initView(view);

        //在这里进行网络连接的判断，如果没有连接，则进行snackbar的提示
        //如果有网络连接，则不会有任何的操作
        if(!NetworkUtil.isNetworkConnected(getActivity())){
            showNoNetwork();
        }

        //翻译按钮点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!NetworkUtil.isNetworkConnected(getActivity())){
                    showNoNetwork();
                }else if(editText.getText()==null||editText.getText().length()==0){
                    Snackbar.make(button,"输入为空",Snackbar.LENGTH_SHORT);
                }else{
                    sendRequest(editText.getText().toString());
                }
            }
        });

        //清除文本按钮
        textViewClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });


        //编辑文本的监听事件
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(editText.getEditableText().toString().length()!=0){
                        textViewClear.setVisibility(View.VISIBLE);
                }else {
                    textViewClear.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

       //收藏按钮点击事件
        imageViewMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //还没有收藏时
                if(!isMarked){

                    ContentValues values=new ContentValues();
                    values.put("input",bingModel.getWord());
                    values.put("output",result);
                    DButil.insertValue(dbHelper,values);
                    values.clear();

                    imageViewMark.setImageResource(R.drawable.ic_star_black_24dp);
                    Snackbar.make(imageViewMark,"已收藏",Snackbar.LENGTH_SHORT).show();
                    isMarked=true;
                }else {

                    DButil.deleteValue(dbHelper,bingModel.getWord());
                    imageViewMark.setImageResource(R.drawable.ic_star_border_black_24dp);
                    Snackbar.make(imageViewMark,"取消收藏",Snackbar.LENGTH_SHORT).show();
                    isMarked=false;
                }

            }
        });

        //分享按钮点击事件
        viewResult.findViewById(R.id.image_view_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,result);
                startActivity(Intent.createChooser(intent,"请选择要分享的应用"));
            }
        });

       //复制按钮事件
        viewResult.findViewById(R.id.image_view_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager manager= (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipdata=ClipData.newPlainText("text",result);
                manager.setPrimaryClip(clipdata);
                Snackbar.make(viewResult,"复制成功",Snackbar.LENGTH_SHORT).show();

            }
        });

        return view;
    }


//////////////////////////////////////////////////
    //Volley它非常适合去进行数据量不大，但通信频繁的网络操作，
    // 而对于大数据量的网络操作，比如说 下载文件 等，Volley的表现就会 非常糟糕 。
    // 所以不建议用它去进行下载文件、加载大图的操作。
    //发送网络请求
    private void sendRequest(String in) {

        progressBar.setVisibility(View.VISIBLE);
        viewResult.setVisibility(View.INVISIBLE);

        // 监听输入面板的情况，如果激活则隐藏
        //输入发法管理器
        InputMethodManager manager= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(manager.isActive()){
            manager.hideSoftInputFromWindow(button.getWindowToken(),0);
        }

        String url= Constants.BING_BASE+"?Word=" + in + "&Samples=";
    /*
        if (showSamples) {
            url += "true";
        } else {
            url += "false";
        }*/

        StringRequest request=new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            Gson gson = new Gson();
                            bingModel = gson.fromJson(s, BingModel.class);


                            if (bingModel != null) {

                                result = bingModel.getWord() + "\n";
                                if(DButil.quearyIfItemExit(dbHelper,bingModel.getWord())){
                                    imageViewMark.setImageResource(R.drawable.ic_star_black_24dp);
                                    isMarked=true;
                                }else {
                                    imageViewMark.setImageResource(R.drawable.ic_star_border_black_24dp);
                                    isMarked=false;
                                }

                                if(bingModel.getPronunciation()!=null){
                                    BingModel.Pronunciation pro=bingModel.getPronunciation();
                                    result=result+"\nAmE:"+" /"+pro.getAmE()+"/"
                                            +"\nBrE:"+"  /"+pro.getBrE()+"/"+"\n";
                                }

                                for(BingModel.Definition def:bingModel.getDefs()){
                                    result=result+def.getPos()+"\n"+def.getDef()+"\n";
                                }

                                result.subSequence(0,result.length()-1);

                                if(bingModel.getSams()!=null&&bingModel.getSams().size()!=0){

                                    if(samples==null){
                                        samples=new ArrayList<>();
                                    }

                                    samples.clear();

                                    for(BingModel.Sample samp:bingModel.getSams()){

                                        samples.add(samp);
                                    }

                                    if(sampleAdapte==null){
                                        sampleAdapte=new SampleAdapte(getActivity(),samples);
                                        recyclerView.setAdapter(sampleAdapte);//将拿到的例句写入
                                    }else{
                                        sampleAdapte.notifyDataSetChanged();
                                    }

                                }

                                progressBar.setVisibility(View.INVISIBLE);
                                viewResult.setVisibility(View.VISIBLE);
                                textViewResult.setText(result);//将拿到的单词写入


                            }

                            }catch (JsonSyntaxException ex){
                            showTransError();
                        }

                        progressBar.setVisibility(View.GONE);
                    }



                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar.setVisibility(View.GONE);
                showTransError();
            }
        });

            //发送网络数据请求
            queue.add(request);

    }

    private void showTransError() {
        Snackbar.make(button, "网络错误", Snackbar.LENGTH_SHORT)
                .setAction("重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();

    }


    ///////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue= Volley.newRequestQueue(getActivity().getApplicationContext());
        dbHelper=new NotebookDatabaseHelper(getActivity(),"MyStore.db",null,1);
    }

////////////////////////////////////////////////
    private void initView(View view) {

        editText= (EditText) view.findViewById(R.id.et_main_input);
        textViewClear= (TextView) view.findViewById(R.id.clear);

        progressBar= (ProgressBar) view.findViewById(R.id.progress_bar);
        textViewResult= (TextView) view.findViewById(R.id.text_view_output);
        viewResult=view.findViewById(R.id.include);

        imageViewMark= (ImageView) view.findViewById(R.id.image_view_mark_star);
        imageViewMark.setImageResource(R.drawable.ic_star_border_black_24dp);

        button= (AppCompatButton) view.findViewById(R.id.buttonTranslate);

        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerView);

        //LinearLayoutManager用于指定recyclerView的布局方式为线性布局
        //原型是：
        //LinearLayoutManager manager1=new LinearLayoutManager(getActivity());
        //recyclerView.setLayoutManager(manager1);
       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setNestedScrollingEnabled(false);
                     // 组嵌套滚动启用;
    }
///////////////////////////////////////////////////////////

            private void showNoNetwork() {

                Snackbar.make(button,"网络错误",Snackbar.LENGTH_INDEFINITE)
                        .setAction("设置", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Settings.ACTION_SETTINGS));
                            }
                        }).show();

            }


}