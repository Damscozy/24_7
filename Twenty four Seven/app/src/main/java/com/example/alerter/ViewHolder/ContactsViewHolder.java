package com.example.alerter.ViewHolder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alerter.FragmentContacts;
import com.example.alerter.R;

import com.example.alerter.Interface.ItemClickListener;
import com.example.alerter.textchat.MsgAdapter;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;

import java.util.HashMap;

/**
 * Created by Hamsoft technologies
 */

public class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String USER_ID = "current-user-id";
    static private HashMap<String, MsgAdapter> contacts = new HashMap<String, MsgAdapter>();

    private static final String APP_KEY = "d4df55d5-15ee-40ce-9ce6-081bce13d04c";
    private static final String APP_SECRET = "OlNnghwnjE2pKX+f0YRJDQ==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private static final String FIRE_NO = "+2348163671085";
    private static final String POLICE_NO = "+46000000000";
    private static final String MEDICAL_NO= "+46000000000";

    public TextView contact_name,contact_phone,contact_id;

    public ImageView contact_image;
    public ImageView police_call;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;

    }

    public ContactsViewHolder(View itemView) {
        super(itemView);

      contact_name = (TextView)itemView.findViewById(R.id.contact_name);
      contact_phone = (TextView)itemView.findViewById(R.id.contact_no);
      //contact_id = (TextView)itemView.findViewById(R.id.contact_)

      contact_image = (ImageView)itemView.findViewById(R.id.contact_image);

        police_call = (ImageView) itemView.findViewById(R.id.callpolice);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
