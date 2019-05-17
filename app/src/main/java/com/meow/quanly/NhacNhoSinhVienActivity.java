package com.meow.quanly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.meow.quanly.adapter.NhacNhoAdapter;
import com.meow.quanly.model.User;

import java.util.ArrayList;

public class NhacNhoSinhVienActivity extends AppCompatActivity {

    RecyclerView rv;

    EditText search;

    NhacNhoAdapter adapter;

    ArrayList<User> arr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhac_nho_sinh_vien);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        init();
    }

    private void init() {
        rv = findViewById(R.id.rv);
        search = findViewById(R.id.search);

        arr = Common.getSinhVien();

        adapter = new NhacNhoAdapter(this, arr);

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(adapter);

    }
}
