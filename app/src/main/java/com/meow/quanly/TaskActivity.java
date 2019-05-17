package com.meow.quanly;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.quanly.adapter.TaskAdapter;
import com.meow.quanly.model.Message;
import com.meow.quanly.model.TaskItem;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout ll;
    RecyclerView rv;
    TaskAdapter adapter;
    ArrayList<TaskItem> arr = new ArrayList<>();
    String link, sinhvien,giaovien;
    DatabaseReference updateTask, send;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        init();

    }

    private void init() {
        ll = findViewById(R.id.ll_add_task);
        rv = findViewById(R.id.rv);

        btn = findViewById(R.id.btn_update);


        ll.setOnClickListener(this);
        btn.setOnClickListener(this);

        link = getIntent().getStringExtra("link");
        sinhvien = getIntent().getStringExtra("sinhvien");
        giaovien = getIntent().getStringExtra("giaovien");
        if(link == null) finish();

        updateTask = FirebaseDatabase.getInstance().getReference("tasks").child(sinhvien);

        send = FirebaseDatabase.getInstance().getReference("chat").child(giaovien).child(sinhvien);


        adapter = new TaskAdapter(this, arr);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        updateTask.child(link).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arr.clear();

                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    TaskItem x = item.getValue(TaskItem.class);

                    arr.add(x);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    void addTask()
    {
        final Dialog dialog = new Dialog(TaskActivity.this);
        dialog.setContentView(R.layout.dialog_new_task);
        dialog.show();
        final TextView detail = dialog.findViewById(R.id.dialog_detail);
        Button btn = dialog.findViewById(R.id.dialog_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detail.getText().toString().equals(""))
                {
                    Toast.makeText(TaskActivity.this, "Yêu cầu không được để trống", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    arr.add(new TaskItem(false, detail.getText().toString(), Common.TASK_ITEM_ADD));
                    adapter.notifyDataSetChanged();
                }

                dialog.dismiss();

            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ll_add_task:
                addTask();
                break;
            case R.id.btn_update:
                if(arr.size() != 0)
                {

                    String link = updateTask.push().getKey();
                    send.push().setValue(new Message("Đã sửa task", giaovien, link, Common.TYPE_LIST_TASK, System.currentTimeMillis()));
                    updateTask.child(link).setValue(arr);

                    Toast.makeText(this, "đã cập nhật", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

        }

    }
}
