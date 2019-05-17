package com.meow.quanly.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.meow.quanly.Common;
import com.meow.quanly.R;
import com.meow.quanly.model.Notification;
import com.meow.quanly.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    ArrayList<Notification> arr;
    Context context;
    public NotificationAdapter(Context context, ArrayList<Notification> arr) {
        this.arr = arr;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(arr.get(arr.size() - position-1));
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        ImageView avt;
        TextView detail, time;
        public ViewHolder(View itemView) {
            super(itemView);

            avt = itemView.findViewById(R.id.avt);
            detail = itemView.findViewById(R.id.detail);
            time = itemView.findViewById(R.id.time);


        }

        public void bind(final Notification item) {


            if(Common.hashMapUser.get(item.getUid()) == null) return;

            String url_avt = Common.hashMapUser.get(item.getUid()).getImageURL();

            detail.setText(item.getMess());

            if (url_avt.equals("df")) {
                avt.setImageResource(R.drawable.ic_account_circle_24dp);
            } else {
                Picasso.with(context).load(url_avt).placeholder(R.drawable.ic_account_circle_24dp).into(avt);
            }

            time.setText(Common.getDate(item.getTime()));

        }
    }

}
