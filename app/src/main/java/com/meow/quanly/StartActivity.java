package com.meow.quanly;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.quanly.model.User;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null)
        {
            //Intent intent = new Intent()
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else
        {

            String tmp = mAuth.getCurrentUser().getEmail();
            final String uid = tmp.substring(0, tmp.length()- 10);
            Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();


            FirebaseDatabase.getInstance().getReference("user").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    int type = user.getType();


                    Common.uid = uid;

                    Intent intent = null;

                    switch (type)
                    {
                        case Common.TYPE_PHONGBAN:
                            intent = new Intent(StartActivity.this, PhongBanActivity.class);
                            break;
                        case Common.TYPE_GIAOVIEN:
                            Common.giaovien_cur = Common.uid;
                            intent = new Intent(StartActivity.this, GiaoVienActivity.class);
                            break;
                        case Common.TYPE_SINHVIEN:
                            intent = new Intent(StartActivity.this, SinhVienMangerActivity.class);
                            break;
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }
}
