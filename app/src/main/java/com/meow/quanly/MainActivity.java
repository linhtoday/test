package com.meow.quanly;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meow.quanly.model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    CardView phongban, giaovien, sinhvien;
    MaterialEditText email, pass;

    FirebaseAuth mAuth;
    Button btn_login;

    Dialog dialog;

    Dialog loading;
    HashMap<String, Integer> map = new HashMap<>();

    AlertDialog load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        load =  new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
       // Toast.makeText(this, "meo meo", Toast.LENGTH_SHORT).show();

        load.show();
        FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Common.arrUser.clear();
                Common.hashMapUser.clear();
                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    try
                    {
                        User user = item.getValue(User.class);
                        Common.arrUser.add(user);
                        map.put(user.getUid(), user.getType());
                        Common.hashMapUser.put(user.getUid(), user);
                    }
                    catch (Exception ex)
                    {

                    }
                }



                if(FirebaseAuth.getInstance().getCurrentUser() != null)
                {
                    String tmp = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    final String uid = tmp.substring(0, tmp.length()- 10);

                    User user = null;
                    Common.hashMapUser.clear();
                    for(int i = 0; i < Common.arrUser.size(); i++)
                    {
                        if(Common.arrUser.get(i).getUid().equals(uid))
                        {
                            user = Common.arrUser.get(i);
                        }

                        Common.hashMapUser.put(Common.arrUser.get(i).getUid(), Common.arrUser.get(i));
                    }


                    int type = user.getType();


                    Common.uid = uid;

                    Intent intent = null;

                    switch (type)
                    {
                        case Common.TYPE_PHONGBAN:
                            intent = new Intent(MainActivity.this, PhongBanActivity.class);
                            break;
                        case Common.TYPE_GIAOVIEN:
                            Common.giaovien_cur = Common.uid;
                            intent = new Intent(MainActivity.this, GiaoVienActivity.class);
                            break;
                        case Common.TYPE_SINHVIEN:
                            intent = new Intent(MainActivity.this, SinhVienMangerActivity.class);
                            break;
                    }

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);
                    finish();
                }
                else
                {

                    setContentView(R.layout.activity_main);
                    init();
                    checkAndRequestPermissions();
                }

                load.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








    }

    private void login(final int type) {
        dialog.show();

        btn_login = dialog.findViewById(R.id.btn_login);
        email = dialog.findViewById(R.id.email);
        pass = dialog.findViewById(R.id.password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String txt_email = email.getText().toString();
                String txt_password = pass.getText().toString();

                Log.d("txt_email", email.getText().toString());
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                    Toast.makeText(MainActivity.this, "Yêu cập nhập đầy đủ thông tin!!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else if(map.get(txt_email) == null || map.get(txt_email).compareTo(type) != 0){
                    String s = " phòng ban";
                    if (type == Common.TYPE_GIAOVIEN) s = " giáo viên";
                    else if (type == Common.TYPE_SINHVIEN) s = " sinh viên";

                    Toast.makeText(MainActivity.this, "Tài khoản này không thuộc" + s, Toast.LENGTH_SHORT).show();
                }
                else
                {

                    loading.show();
                    if(txt_email.endsWith("@gmail.com"))
                    {

                    }
                    else
                    {
                        Common.uid = txt_email;
                        txt_email += "@gmail.com";
                    }

                    //Log.d("ccnhe", txt_email + "   " + txt_password);
                    mAuth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Intent intent = null;
                                        switch (type)
                                        {
                                            case Common.TYPE_PHONGBAN:
                                                intent = new Intent(MainActivity.this, PhongBanActivity.class);
                                                break;
                                            case Common.TYPE_GIAOVIEN:
                                                Common.giaovien_cur = Common.uid;
                                                intent = new Intent(MainActivity.this, GiaoVienActivity.class);
                                                break;
                                            case Common.TYPE_SINHVIEN:
                                                intent = new Intent(MainActivity.this, SinhVienMangerActivity.class);
                                                break;
                                        }

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                        startActivity(intent);


                                        //txt_email.de
                                        //Common.uid = mAuth.getUid();

                                        dialog.dismiss();
                                        loading.dismiss();
                                        finish();

                                    } else {
                                        Toast.makeText(MainActivity.this, "Đăng nhập thất bại!!", Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                        dialog.dismiss();
                                    }
                                }
                            });
                }

            }
        });
    }


    private void init() {

        phongban = findViewById(R.id.card_view_phongban);
        giaovien = findViewById(R.id.card_view_giaovien);
        sinhvien = findViewById(R.id.card_view_sinhvien);

        phongban.setAnimation(AnimationUtils.loadAnimation(this, R.anim.transiton_anim));
        giaovien.setAnimation(AnimationUtils.loadAnimation(this, R.anim.transiton_anim));
        sinhvien.setAnimation(AnimationUtils.loadAnimation(this, R.anim.transiton_anim));

        loading = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();



        phongban.setOnClickListener(this);
        giaovien.setOnClickListener(this);
        sinhvien.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_login);





    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.card_view_phongban:
                login(Common.TYPE_PHONGBAN);
                break;
            case R.id.card_view_giaovien:
                login(Common.TYPE_GIAOVIEN);
                break;
            case R.id.card_view_sinhvien:
                login(Common.TYPE_SINHVIEN);
                break;
        }
    }


    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET

        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }
    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


            if(hasAllPermissionsGranted(grantResults))
            {
                Toast.makeText(this, "full quyen", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "chua full quyen", Toast.LENGTH_SHORT).show();
            }




    }
}
