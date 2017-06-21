package com.example.mytranslation.ui;

import android.support.v4.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mytranslation.R;
import com.example.mytranslation.adapter.NoteBookAdapter;
import com.example.mytranslation.db.DButil;
import com.example.mytranslation.db.NotebookDatabaseHelper;
import com.example.mytranslation.model.NoteBook;
import com.example.mytranslation.myInterface.OnRecyclerViewOnClickListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.R.id.input;
import static android.R.id.list;
import static android.R.string.no;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by Administrator on 2017/4/25 0025.
 */

public class NoteBookFragment extends Fragment {

    private RecyclerView recyclerViewNotebook;
    private ArrayList<NoteBook> list=new ArrayList<>();
    private NoteBookAdapter noteBookAdapter;
    private TextView tvNoNote;

    private NotebookDatabaseHelper dbHelper;

    public NoteBookFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new NotebookDatabaseHelper(getActivity(), "MyStore.db", null,1);

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.notebook_fragment,container,false);

        initViews(view);

        getDataFromDB();

        if(list.isEmpty()){
            tvNoNote.setVisibility(View.VISIBLE);
        }else{
            tvNoNote.setVisibility(View.GONE);
        }

        Collections.reverse(list);
        noteBookAdapter=new NoteBookAdapter(getActivity(),list);
        recyclerViewNotebook.setAdapter(noteBookAdapter);

        noteBookAdapter.setItemClickListener(new OnRecyclerViewOnClickListener() {
            @Override
            public void OnItemClick(View view, int position) {

            }

            @Override
            public void OnSubViewClick(View view, final int position) {

                switch (view.getId()){

                    case R.id.image_view_mark_star:

                        final NoteBook item1=list.get(position);
                        DButil.deleteValue(dbHelper,item1.getInput());
                        list.remove(position);

                        noteBookAdapter.notifyItemRemoved(position);
                        noteBookAdapter.notifyItemRangeChanged(position,list.size());
                        Snackbar.make(tvNoNote,"删除笔记",Snackbar.LENGTH_SHORT)
                                .setAction("撤销", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        ContentValues values=new ContentValues();
                                        values.put("input",item1.getInput());
                                        values.put("output",item1.getOutput());
                                        DButil.insertValue(dbHelper,values);
                                        values.clear();

                                        list.add(position,item1);
                                        //???????????????????
                                        noteBookAdapter.notifyItemInserted(position);
                                        recyclerViewNotebook.smoothScrollToPosition(position);

                                    }
                                }).show();


                        break;
                    case R.id.image_view_share:

                        NoteBook item2=list.get(position);
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,String.valueOf(item2.getInput() + "\n" + item2.getOutput()));
                        startActivity(Intent.createChooser(intent,"请选择要分享的应用"));
                        break;
                    case R.id.image_view_copy:


                        NoteBook item3=list.get(position);
                        ClipboardManager manager=(ClipboardManager)getActivity()
                                .getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData=ClipData.newPlainText("text",
                                String.valueOf(item3.getInput())+"\n"+item3.getOutput());
                        manager.setPrimaryClip(clipData);

                        Snackbar.make(tvNoNote,"复制成功",Snackbar.LENGTH_SHORT).show();





                }

            }
        });
        return view;
    }

      ///从数据库读取数据
    private void getDataFromDB() {

        if(list!=null){
            list.clear();
        }
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        Cursor cursor=db.query("Notebook",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String in=cursor.getString(cursor.getColumnIndex("input"));
                String out=cursor.getString(cursor.getColumnIndex("output"));
                NoteBook item=new NoteBook(in,out);
                list.add(item);

            }while(cursor.moveToNext());
        }
        cursor.close();

    }


    private void initViews(View view) {

        tvNoNote= (TextView) view.findViewById(R.id.no_notebook);
        recyclerViewNotebook= (RecyclerView) view.findViewById(R.id.recycler_view_notebook);
        recyclerViewNotebook.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        getDataFromDB();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataFromDB();
        if(noteBookAdapter!=null){
            noteBookAdapter.notifyDataSetChanged();
        }
    }
}
