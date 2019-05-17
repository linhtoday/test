package com.meow.quanly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import com.meow.quanly.adapter.NhacNhoAdapter;
import com.meow.quanly.adapter.TheoDoiTienDoAdapter;
import com.meow.quanly.model.User;

import java.util.ArrayList;

public class TheoDoiTienDoActivity extends AppCompatActivity {

    RecyclerView rv;

    EditText search;

    TheoDoiTienDoAdapter adapter;

    ArrayList<User> arr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theo_doi_tien_do);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        init();


    }

    private void getSinhVien() {
        for(int i = 0; i < Common.arrUser.size(); i++)
        {
            if(Common.arrUser.get(i).getType() == Common.TYPE_SINHVIEN && Common.arrUser.get(i).getBelong() != null && Common.arrUser.get(i).getBelong().equals("") == false)
            {
                arr.add(Common.arrUser.get(i));
            }
        }
    }

    private void init() {
        rv = findViewById(R.id.rv);
        search = findViewById(R.id.search);

        getSinhVien();
        adapter = new TheoDoiTienDoAdapter(this, arr);

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(adapter);

    }
}
