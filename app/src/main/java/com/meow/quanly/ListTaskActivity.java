package com.meow.quanly;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class ListTaskActivity extends AppCompatActivity {

    RecyclerView rv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);

        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar_test);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

    }
}
