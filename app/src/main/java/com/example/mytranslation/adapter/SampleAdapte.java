package com.example.mytranslation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mytranslation.R;
import com.example.mytranslation.model.BingModel;

import java.util.ArrayList;

import static com.example.mytranslation.R.id.textView;

/**
 * Created by Administrator on 2017/4/20 0020.
 */

public class SampleAdapte extends RecyclerView.Adapter<SampleAdapte.SampleViewHolder> {

    public final Context context;
    public final LayoutInflater inflater;
    public ArrayList<BingModel.Sample> samples;

    public SampleAdapte(Context context, ArrayList<BingModel.Sample> samples) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.samples=samples;
    }

    //定义了一个内部类
    public static class SampleViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
                                  //这个参数通常为子项的最外层布局，然后通过它来获取布局中的textview实例
        public SampleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }



    //创建ViewHolder实例，将sample_item布局加载进来，然后创建ViewHolder,然后返回ViewHolder实例
    //Inflate()作用就是将xml定义的一个布局找出来，但仅仅是找出来而且隐藏的，
    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SampleViewHolder(inflater.inflate(R.layout.sample_item,parent,false));//////////
    }


    //用于对RecycleView子项进行赋值的，会在每个子项滚动到屏幕内的时候执行，这里我们通过position参数得到当前项的BingModel.Sample
    //实例，然后将数据设置到holder的textView中
    @Override
    public void onBindViewHolder(SampleViewHolder holder, int position) {
        String s = samples.get(position).getEng() + "\n" + samples.get(position).getChn();
        holder.textView.setText(s);
    }
    //告诉RecycleView一共有多少子项，直接返回数据的长度
    @Override
    public int getItemCount() {
        return samples.size();
    }
}
