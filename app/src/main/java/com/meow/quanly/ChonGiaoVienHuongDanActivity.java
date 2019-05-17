package com.meow.quanly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;

import com.meow.quanly.adapter.GiangVienHuongDanAdapter;
import com.meow.quanly.model.User;

import java.util.ArrayList;

public class ChonGiaoVienHuongDanActivity extends AppCompatActivity {

    RecyclerView rv;
    EditText search;

    GiangVienHuongDanAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_giao_vien_huong_dan);
        overridePendingTransition(R.anim.enter, R.anim.exit);
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

        //arr = ;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        adapter = new GiangVienHuongDanAdapter(this, Common.getArrUserType(Common.TYPE_GIAOVIEN));

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(adapter);
    }
}
