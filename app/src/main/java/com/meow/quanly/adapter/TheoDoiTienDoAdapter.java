package com.meow.quanly.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meow.quanly.ChatActivity;
import com.meow.quanly.Common;
import com.meow.quanly.R;
import com.meow.quanly.model.Notification;
import com.meow.quanly.model.User;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TheoDoiTienDoAdapter extends RecyclerView.Adapter<TheoDoiTienDoAdapter.ViewHolder> implements Filterable {

    ArrayList<User> arr, arrFilter;

    Context context;
    public TheoDoiTienDoAdapter(Context context, ArrayList<User> arr) {
        this.arr = arr;
        this.arrFilter = arr;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_giaovienhuongdan, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = arrFilter.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return arrFilter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        ImageView avt;
        TextView username, detail;
        View v;

        public ViewHolder(View itemView) {
            super(itemView);

            v = itemView;

            cardView = itemView.findViewById(R.id.item_card_view_giaovien);
            avt = itemView.findViewById(R.id.avt);
            username = itemView.findViewById(R.id.username);
            detail = itemView.findViewById(R.id.detail);


        }

        public void bind(final User item) {

            username.setText(item.getUsername());
            if (item.getImageURL().equals("df")) {
                avt.setImageResource(R.drawable.ic_account_circle_24dp);
            } else {
                Picasso.with(context).load(item.getImageURL()).placeholder(R.drawable.ic_account_circle_24dp).into(avt);
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent2 = new Intent(context, ChatActivity.class);

                    String giaovienhuongdan = item.getBelong();

                    intent2.putExtra("type", 1);
                    intent2.putExtra("sinhvien", item.getUid());
                    intent2.putExtra("giaovien", giaovienhuongdan);
                    context.startActivity(intent2);


                }
            });

        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    arrFilter = arr;
                } else {
                    ArrayList<User> filteredList = new ArrayList<>();
                    for (User row : arr) {

                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase()) || row.getUid().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    arrFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = arrFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                arrFilter = (ArrayList<User>) filterResults.values;

                notifyDataSetChanged();
            }
        };
    }
}

