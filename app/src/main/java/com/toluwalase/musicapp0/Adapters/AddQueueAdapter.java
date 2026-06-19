package com.toluwalase.musicapp0.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.toluwalase.musicapp0.Customs.QueueDiffUtil;
import com.toluwalase.musicapp0.Interfaces.QueueClickListener;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

public class AddQueueAdapter extends ListAdapter<QueueListItems, AddQueueAdapter.MyViewHolder> {
    Context context;
    QueueClickListener mlistener;

    public AddQueueAdapter (Context context, QueueClickListener mListener){
        super(new QueueDiffUtil());
        this.context = context;
        this.mlistener = mListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView queue_title;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            queue_title = itemView.findViewById(R.id.title_queue);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_queue, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddQueueAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.queue_title.setText(getItem(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(getItem(holder.getAdapterPosition()), position);
            }
        });
    }
}
