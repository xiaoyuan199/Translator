package com.example.mytranslation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mytranslation.R;
import com.example.mytranslation.model.NoteBook;
import com.example.mytranslation.myInterface.OnRecyclerViewOnClickListener;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

/**
 * Created by Administrator on 2017/4/24 0024.
 */

public class NoteBookAdapter extends RecyclerView.Adapter<NoteBookAdapter.NBViewHolder>{

    private ArrayList<NoteBook> mnoteBookslist;
    private final Context context;
    private  final LayoutInflater inflater;

    private OnRecyclerViewOnClickListener mListener;

    public NoteBookAdapter(Context context, ArrayList<NoteBook> noteBookslist ) {
        this.context = context;
        this.mnoteBookslist=noteBookslist;
        this.inflater =LayoutInflater.from(context);
    }

    @Override
    public NBViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new NBViewHolder(inflater.inflate(R.layout.notebook_mark_item,parent,false),mListener);
    }

    @Override
    public void onBindViewHolder(final NBViewHolder holder,final int position) {

        NoteBook item=mnoteBookslist.get(position);
        holder.tvOutput.setText(item.getInput()+"\n"+item.getOutput());
    }

    @Override
    public int getItemCount() {
        return mnoteBookslist.size();
    }

///////////不理解///////////////
    public void setItemClickListener(OnRecyclerViewOnClickListener listener){
        this.mListener = listener;
    }
//////////////////////////////

    class NBViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvOutput;
        ImageView ivMarkStar;
        ImageView ivCopy;
        ImageView ivShare;

        OnRecyclerViewOnClickListener listener;////////////

        public NBViewHolder(View itemView,final OnRecyclerViewOnClickListener listener1) {
            super(itemView);

            tvOutput = (TextView) itemView.findViewById(R.id.text_view_output);
            ivMarkStar = (ImageView) itemView.findViewById(R.id.image_view_mark_star);
            ivCopy = (ImageView) itemView.findViewById(R.id.image_view_copy);
            ivShare = (ImageView) itemView.findViewById(R.id.image_view_share);

            ///////////////////////不理解///////////////////
            this.listener=listener1;
            itemView.setOnClickListener(this);

            ivMarkStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnSubViewClick(ivMarkStar,getLayoutPosition());
                }
            });

            ivCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnSubViewClick(ivCopy,getLayoutPosition());
                }
            });

            ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnSubViewClick(ivShare,getLayoutPosition());
                }
            });
              ////////////////////////////////////////
        }


        @Override
        public void onClick(View v) {
            if(listener!=null){
                listener.OnItemClick(v,getLayoutPosition());
            }
        }
    }

}
