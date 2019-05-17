package com.meow.quanly;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.quanly.adapter.NotificationAdapter;
import com.meow.quanly.model.Notification;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView rv;
    String user_cur = Common.uid;
    ArrayList<Notification> arrNoti = new ArrayList<>();
    DatabaseReference getNoti;
    NotificationAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        init();

        listNoti();
    }

    private void listNoti() {

        getNoti = FirebaseDatabase.getInstance().getReference("notification").child(user_cur);

        getNoti.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrNoti.clear();
                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    Notification x = item.getValue(Notification.class);
                    if(x.isCheck() == false)
                    {
                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("check", true);

                        item.getRef().updateChildren(hashMap);

                    }

                    arrNoti.add(x);
                }

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {

        rv = findViewById(R.id.rv);

        adapter = new NotificationAdapter(this, arrNoti);

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(adapter);

    }
}
