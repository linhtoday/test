package com.meow.quanly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.quanly.model.Item;
import com.meow.quanly.model.User;

public class PhongBanActivity extends AppCompatActivity implements View.OnClickListener{

    CardView ganquyen,theodoi, nhacnho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phong_ban);
        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar_test);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ganquyen = findViewById(R.id.card_view_ganquyen);
        theodoi = findViewById(R.id.card_view_theodoitiendo);
        nhacnho = findViewById(R.id.card_view_nhacnho);


        ganquyen.setAnimation(AnimationUtils.loadAnimation(this, R.anim.transiton_anim));
        theodoi.setAnimation(AnimationUtils.loadAnimation(this, R.anim.transiton_anim));
        nhacnho.setAnimation(AnimationUtils.loadAnimation(this, R.anim.transiton_anim));

        ganquyen.setOnClickListener(this);
        theodoi.setOnClickListener(this);
        nhacnho.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();

        filter.addAction("update_user");

        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, filter);
    }


    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId())
        {
            case R.id.card_view_ganquyen:
                intent = new Intent(PhongBanActivity.this, ChonGiaoVienHuongDanActivity.class);
                break;
            case R.id.card_view_theodoitiendo:
                intent = new Intent(PhongBanActivity.this, TheoDoiTienDoActivity.class);
                break;
            case R.id.card_view_nhacnho:
                intent = new Intent(PhongBanActivity.this, NhacNhoSinhVienActivity.class);
                break;
                default:
                    return;
        }

        if(intent != null)startActivity(intent);
    }

    private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //  Log.d("card_view", "olalalal");
            if (intent.getAction().equals("update_user")) {

                FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Common.arrUser.clear();

                        for(DataSnapshot item: dataSnapshot.getChildren())
                        {
                            try
                            {
                                User user = item.getValue(User.class);
                                Common.arrUser.add(user);
                            }
                            catch (Exception ex)
                            {

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case R.id.logout:
                //Toast.makeText(this, "olala", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();

                return true;
            default:
                Toast.makeText(this, "nothing", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
