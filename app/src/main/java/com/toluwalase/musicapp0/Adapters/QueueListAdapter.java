package com.toluwalase.musicapp0.Adapters;

import static com.toluwalase.musicapp0.Fragments.QueueFragment.myQueues;
import static com.toluwalase.musicapp0.Fragments.QueueFragment.queueListAdapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.toluwalase.musicapp0.Customs.QueueDiffUtil;
import com.toluwalase.musicapp0.Interfaces.QueueClickListener;
import com.toluwalase.musicapp0.Models.QueueListItems;
import com.toluwalase.musicapp0.R;

public class QueueListAdapter extends ListAdapter<QueueListItems, QueueListAdapter.MyViewHolder1> {
    public static final String SELECTED_STATE = "SelectedState";
    int selectedItem = -1;
    Context context;
    QueueClickListener mlistener;
    AlertDialog confirmDialog, renameDialog;

    public QueueListAdapter (Context context, QueueClickListener mListener){
        super(new QueueDiffUtil());
        this.context = context;
        this.mlistener = mListener;
    }

    public class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView queue_position, queue_title;
        ImageView edit_button, close_button, drop_down;
        RadioButton bullet;
        CardView itemHolder;

        public MyViewHolder1(@NonNull View itemView) {
            super(itemView);
            queue_position = itemView.findViewById(R.id.position_queue);
            queue_title = itemView.findViewById(R.id.title_queue);
            edit_button = itemView.findViewById(R.id.queue_edit_btn);
            close_button = itemView.findViewById(R.id.queue_cancel_btn);
            itemHolder = itemView.findViewById(R.id.queue_list_item);
            bullet = itemView.findViewById(R.id.item_bullet);
            drop_down = itemView.findViewById(R.id.item_drop_down);
        }
    }

    @NonNull
    @Override
    public MyViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_list_items, parent, false);
        return new MyViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder1 holder, @SuppressLint("RecyclerView") int position) {
        String pos = Integer.toString(position + 1);
        holder.queue_title.setText(getItem(position).getTitle());
        holder.queue_title.setSelected(true);
        holder.queue_position.setText(pos);
        holder.bullet.setChecked(getItem(position).isSelected());

        holder.itemHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(getItem(holder.getAdapterPosition()), position);

                getItem(position).setSelected(true);
                for(QueueListItems item : myQueues){
                    if(item != getItem(position)){
                        item.setSelected(false);
                    }
                }
                notifyDataSetChanged();
            }
        });

        holder.close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadConfirmPopup(position);
            }
        });

        holder.edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRenamePopup(position);
            }
        });

        if(getItem(position).isCurrent()){
            holder.drop_down.setImageResource(R.drawable.play_green);
        }else{
            holder.drop_down.setImageResource(R.drawable.ic_arrow_drop_down);
        }
    }

    private void showRenamePopup(int pos) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.rename_popup, null);
        dialogBuilder.setView(dialogView);
        renameDialog = dialogBuilder.create();

        EditText rename = dialogView.findViewById(R.id.queue_name);
        TextView yes_btn, no_button;
        rename.setHint(getItem(pos).getTitle());
        rename.requestFocus();

        yes_btn = dialogView.findViewById(R.id.yes_button);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myQueues.get(pos).setTitle(rename.getText().toString());
                notifyItemChanged(pos);
                renameDialog.dismiss();
            }
        });

        no_button = dialogView.findViewById(R.id.no_button);
        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog.dismiss();
            }
        });

        renameDialog.show();
    }

    private void loadConfirmPopup(int pos) {
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.confirm_popup, null);
        dialogBuilder.setView(dialogView);
        confirmDialog = dialogBuilder.create();

        TextView queue_title, yes_btn, no_button;
        queue_title = dialogView.findViewById(R.id.queue_name);
        queue_title.setText(getItem(pos).getTitle());

        yes_btn = dialogView.findViewById(R.id.yes_button);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getItem(pos).isCurrent()){
                    Toast.makeText(context, "Can't remove current queue", Toast.LENGTH_SHORT).show();
                }else {
                    myQueues.remove(getItem(pos));
                    queueListAdapter.notifyDataSetChanged();
                }
                confirmDialog.dismiss();
            }
        });

        no_button = dialogView.findViewById(R.id.no_button);
        no_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });

        confirmDialog.show();
    }
}
