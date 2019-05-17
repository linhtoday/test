package com.meow.quanly;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.quanly.adapter.ChatAdapter;
import com.meow.quanly.model.Message;

import java.util.ArrayList;


public class QuanLyTask extends AppCompatActivity {

    String giaovien;

    RelativeLayout ll;
    ArrayList<Message> arr = new ArrayList<>();
    public static String sinhvien;
    DatabaseReference updateMessage;
    RecyclerView rv;
    ChatAdapter adapter;
    TextView img_newtask;
    TextView text_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly_task);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        init();

        getList();


    }

    private void getList() {

    }

    private void init() {

        text_send = findViewById(R.id.text_send);
        img_newtask = findViewById(R.id.img_newtask);
        //btn_send = findViewById(R.id.btn_send);

        ll = findViewById(R.id.bottom);

        if(Common.uid.equals("1041060273"))
        {
            ll.setVisibility(View.GONE);
        }

        sinhvien = getIntent().getStringExtra("sinhvien");
        giaovien = getIntent().getStringExtra("giaovien");

        int type = getIntent().getIntExtra("type",1);

        String t = "";
        if(type == 3) t = sinhvien;
        else t = giaovien;

//        String test = getIntent().getStringExtra("test");

        adapter = new ChatAdapter(this, arr, t);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(adapter);
        updateMessage = FirebaseDatabase.getInstance().getReference("chat").child(giaovien).child(sinhvien);

        updateMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arr.clear();
                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    try
                    {
                        Message message = item.getValue(Message.class);
                        if(message.getType() == Common.TYPE_LIST_TASK) arr.add(message);
                    }
                    catch (Exception ex)
                    {

                    }
                }

                adapter.notifyDataSetChanged();

                if(arr.size() > 0) rv.smoothScrollToPosition(arr.size()-1);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        img_newtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuanLyTask.this, AddTaskActivity.class);
                intent.putExtra("giaovien", giaovien);
                intent.putExtra("sinhvien", sinhvien);
                startActivity(intent);
            }
        });
//        btn_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(text_send.getText().toString().equals(""))
//                {
//                    Toast.makeText(ChatActivity.this, "Nội dung không được để trống", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                int type = Common.hashMapUser.get(Common.uid).getType();
//
//                if(type == 2)
//
//                    updateMessage.push().setValue(new Message(text_send.getText().toString(),giaovien, "",Common.TYPE_MESSAGE,System.currentTimeMillis() ));
//                else
//                    updateMessage.push().setValue(new Message(text_send.getText().toString(),sinhvien, "",Common.TYPE_MESSAGE,System.currentTimeMillis() ));
//
//                adapter.notifyDataSetChanged();
//                text_send.setText("");
//
//            }
//        });
    }
}