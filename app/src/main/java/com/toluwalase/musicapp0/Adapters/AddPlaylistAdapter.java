package com.toluwalase.musicapp0.Adapters;

import static com.toluwalase.musicapp0.Fragments.HomeFragment.playLists;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.toluwalase.musicapp0.Customs.PlaylistDiffUtil;
import com.toluwalase.musicapp0.Interfaces.PlaylistClickListener;
import com.toluwalase.musicapp0.Models.PlaylistItem;
import com.toluwalase.musicapp0.R;

public class AddPlaylistAdapter extends ListAdapter<PlaylistItem, AddPlaylistAdapter.MyViewHolder> {
    AlertDialog optionsDialog, renameDialog;
    Context context;
    PlaylistClickListener playlistClickListener;


    public AddPlaylistAdapter (Context context, PlaylistClickListener playlistClickListener){
        super(new PlaylistDiffUtil());
        this.context = context;
        this.playlistClickListener = playlistClickListener;
    }
    @NonNull
    @Override
    public AddPlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_playlist_item, parent, false);
        return new AddPlaylistAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddPlaylistAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(getItem(position).getTitle());
        holder.checkBox.setChecked(getItem(position).isChecked());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    getItem(position).setChecked(false);
                }else{
                    holder.checkBox.setChecked(true);
                    getItem(position).setChecked(true);
                }

                playlistClickListener.onClick(getItem(holder.getAdapterPosition()), position);
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.title);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
