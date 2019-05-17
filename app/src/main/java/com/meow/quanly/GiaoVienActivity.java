package com.meow.quanly;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.quanly.adapter.NotificationAdapter;
import com.meow.quanly.adapter.SinhVienAdapter;
import com.meow.quanly.model.Notification;
import com.meow.quanly.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GiaoVienActivity extends AppCompatActivity {
    TextView smsCountTxt;
    int pendingSMSCount = 0;
    DatabaseReference getNoti;
    String user_cur = Common.uid;
    TextView username;
    ImageView avt;
    RecyclerView rv;


    ArrayList<User> arr;

    SinhVienAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giao_vien);

        init();

        listNoti();

        info();

        getListUserManager();




    }

    private void getListUserManager() {

        arr.clear();
        for(int i = 0; i < Common.arrUser.size(); i++)
        {
            User user = Common.arrUser.get(i);
            if(user.getBelong() == null || user.getBelong().equals(Common.uid) == false) continue;

            arr.add(user);
        }


    }

    private void info() {
        if(Common.hashMapUser.get(Common.uid) == null) return;

        String url_avt = Common.hashMapUser.get(Common.uid).getImageURL();

        username.setText(Common.hashMapUser.get(Common.uid).getUsername());

        if (url_avt.equals("df")) {
            avt.setImageResource(R.drawable.ic_account_circle_24dp);
        } else {
            Picasso.with(this).load(url_avt).placeholder(R.drawable.ic_account_circle_24dp).into(avt);
        }
    }

    private void listNoti() {
        getNoti.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pendingSMSCount = 0;

                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    Notification x = item.getValue(Notification.class);
                    if(x.isCheck() == false) pendingSMSCount++;
                }

                setupBadge();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar_test);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        arr = new ArrayList<>();

        rv = findViewById(R.id.rv);

        adapter = new SinhVienAdapter(this, arr);

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(adapter);

        avt = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        getNoti = FirebaseDatabase.getInstance().getReference("notification").child(user_cur);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View actionView = MenuItemCompat.getActionView(menuItem);
        smsCountTxt = actionView.findViewById(R.id.notification_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_notifications: {
                startActivity(new Intent(GiaoVienActivity.this, NotificationActivity.class));
                finish();
                return true;
            }
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {

        if (smsCountTxt != null) {
            if (pendingSMSCount == 0) {
                if (smsCountTxt.getVisibility() != View.GONE) {
                    smsCountTxt.setVisibility(View.GONE);
                }
            } else {
                smsCountTxt.setText(String.valueOf(Math.min(pendingSMSCount, 99)));
                if (smsCountTxt.getVisibility() != View.VISIBLE) {
                    smsCountTxt.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
