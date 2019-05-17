package com.meow.quanly;

import android.app.Dialog;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meow.quanly.adapter.TaskAdapter;
import com.meow.quanly.model.Message;
import com.meow.quanly.model.Notification;
import com.meow.quanly.model.TaskItem;

import java.util.ArrayList;

public class AddTaskActivity extends AppCompatActivity {

    LinearLayout ll;
    Button done;
    ArrayList<TaskItem> arr = new ArrayList<>();
    DatabaseReference tasks, chat, noti;
    String giaovien, sinhvien;
    TaskAdapter adapter;
    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        init();
    }

    private void init() {
        ll = findViewById(R.id.ll_add_task);
        done = findViewById(R.id.btn_done);

        adapter = new TaskAdapter(this, arr);



        rv = findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(adapter);
        giaovien = getIntent().getStringExtra("giaovien");
        sinhvien = getIntent().getStringExtra("sinhvien");

        tasks = FirebaseDatabase.getInstance().getReference("tasks").child(sinhvien);
        chat = FirebaseDatabase.getInstance().getReference("chat").child(giaovien).child(sinhvien);

        noti = FirebaseDatabase.getInstance().getReference("notification").child(sinhvien);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arr.size() == 0)
                {
                    Toast.makeText(AddTaskActivity.this, "Yêu cầu không được để trống task hoặc gửi file", Toast.LENGTH_SHORT).show();
                    return;
                }

                long time = System.currentTimeMillis();

                String link = tasks.push().getKey();
                chat.push().setValue(new Message("Đã gửi task", giaovien, link, Common.TYPE_LIST_TASK, time));
                tasks.child(link).setValue(arr);

                noti.push().setValue(new Notification(giaovien, "Giáo viên đã thêm 1 task", "", System.currentTimeMillis(),false));


                new CountDownTimer(1000, 1000){
                    public void onTick(long millisUntilFinished){

                    }
                    public  void onFinish(){
                        Toast.makeText(AddTaskActivity.this, "Đã thêm", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }.start();



            }
        });
    }

    void addTask()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_new_task);
        dialog.show();
        final TextView detail = dialog.findViewById(R.id.dialog_detail);
        Button btn = dialog.findViewById(R.id.dialog_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detail.getText().toString().equals(""))
                {
                    Toast.makeText(AddTaskActivity.this, "Yêu cầu không được để trống", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    arr.add(new TaskItem(false, detail.getText().toString(), Common.TASK_ITEM_NORMAL));
                    adapter.notifyDataSetChanged();
                }

                dialog.dismiss();

            }
        });
    }
}
