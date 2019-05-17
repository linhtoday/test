package com.meow.quanly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meow.quanly.adapter.ChooseSinhVienAdapter;
import com.meow.quanly.adapter.GiangVienHuongDanAdapter;

import com.meow.quanly.model.Item;
import com.meow.quanly.model.Notification;

import java.util.HashMap;
import java.util.Map;

public class ChonSinhVienActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView rv;

    EditText search;

    ChooseSinhVienAdapter adapter;

    String giangvien;

    Button btn;

    HashMap<String, Boolean> map;


    int count()
    {
        int cnt = 0;
        for(Map.Entry<String, Boolean> entry : map.entrySet()) {
            //String key = entry.getKey();
            //HashMap value = entry.getValue();

            Boolean c = entry.getValue();
            if(c == true) cnt++;
            // do what you have to do here
            // In your case, another loop.
        }

        return cnt;
    }

    private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          //  Log.d("card_view", "olalalal");
            if (intent.getAction().equals("My Broadcast")) {


                Item item = (Item) intent.getSerializableExtra("data");

                if(item == null) return;

                map.put(item.uid, item.check);



                btn.setText(count()+"");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_sinh_vien);

        init();

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void init() {
        rv = findViewById(R.id.rv);
        search = findViewById(R.id.search);
        btn = findViewById(R.id.btn_xacnhan);
        btn.setOnClickListener(this);
        giangvien = getIntent().getStringExtra("giangvien");

        adapter = new ChooseSinhVienAdapter(this, Common.getArrUserType(Common.TYPE_SINHVIEN));

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(adapter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        map = new HashMap<>();

        IntentFilter filter = new IntentFilter();

        filter.addAction("My Broadcast");

        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, filter);

    }

    void push()
    {
        if(Common.giaovien_cur == null || Common.giaovien_cur.equals("")) return;

        DatabaseReference root = FirebaseDatabase.getInstance().getReference("guided").child(Common.giaovien_cur);
        DatabaseReference noti = FirebaseDatabase.getInstance().getReference("notification").child(Common.giaovien_cur);



        DatabaseReference updateUser = FirebaseDatabase.getInstance().getReference("user");
        for(Map.Entry<String, Boolean> entry : map.entrySet()) {

            String uid = entry.getKey();
            Boolean c = entry.getValue();

            if(c == true)
            {
                root.push().setValue(uid);
                updateUser.child(uid).child("belong").setValue(Common.giaovien_cur);
                //noti.push().setValue(new Notification(Common.uid, , System.currentTimeMillis(), uid, false));
                noti.push().setValue(new Notification(Common.uid, "Phòng ban đã gán một sinh viên cho bạn",uid,System.currentTimeMillis(), false));
            }
        }

        map.clear();

        Common.giaovien_cur = "";
        Intent intent = new Intent();

        intent.setAction("update_user");

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        new CountDownTimer(1000, 1000){
            public void onTick(long millisUntilFinished){
                Toast.makeText(ChonSinhVienActivity.this, "done", Toast.LENGTH_SHORT).show();
            }
            public  void onFinish(){
                finish();
            }
        }.start();



        //FirebaseDatabase.getInstance().getReference("guided ").child(Common.giaovien_cur).updateChildren()
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_xacnhan:
                push();
                break;

        }
    }
}
