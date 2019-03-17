package com.example.alerter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.alerter.Commom.Common;
import com.example.alerter.Interface.ItemClickListener;
import com.example.alerter.R;

public class EmergenciesViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener
{

    public TextView emergency_title, emergency_location,  date, postedtime, emergency_description;

    private ItemClickListener itemClickListener;

    public EmergenciesViewHolder(View itemView) {
        super(itemView);

        emergency_title = itemView.findViewById(R.id.EmergencyTitle);
        emergency_location = itemView.findViewById(R.id.EmergencyLocation);
        emergency_description = itemView.findViewById(R.id.EmergencyDescription);
        postedtime = itemView.findViewById(R.id.posted_time);
        date = itemView.findViewById(R.id.posted_date);
        itemView.setOnCreateContextMenuListener(this);
        //itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        //contextMenu.setHeaderTitle("Pilih Aksi");

        //contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        //contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
