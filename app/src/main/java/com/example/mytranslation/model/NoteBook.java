package com.example.mytranslation.model;


import android.support.v7.widget.RecyclerView;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class NoteBook  {

    // 原文
    private String input = null;
    // 译文
    private String output = null;

    public  NoteBook(String input,String output){

       this.input=input;
        this.output=output;

    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
