package com.meow.quanly;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.meow.quanly.model.Message;
import com.meow.quanly.model.Notification;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class SinhVienMangerActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CHOOSE_FILE = 6969;
    CardView task, thaoluan, guifile;
    String sinhvien, giaovien;
    DatabaseReference countImageExist, updateCountImageExist;
    HashMap<String, Integer> countImage = new HashMap<>();
    DatabaseReference send;
    TextView username;
    ImageView avt;
    TextView smsCountTxt;
    int pendingSMSCount = 0;
    String user_cur = Common.uid;

    DatabaseReference getNoti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinh_vien_manger);


        //overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
        init();

        countIMG();

        info();

        listNoti();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_notifications);

        View actionView = MenuItemCompat.getActionView(menuItem);
        smsCountTxt = actionView.findViewById(R.id.notification_badge);
        
        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    private void listNoti() {
        getNoti.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pendingSMSCount = 0;

                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    Notification x = item.getValue(Notification.class);
                    if(x.isCheck() == false) pendingSMSCount++;
                }

                setupBadge();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_notifications: {
                startActivity(new Intent(this, NotificationActivity.class));
                finish();
                return true;
            }
            case R.id.logout:
                //Toast.makeText(this, "olala", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();

                return true;
                default:
                    Toast.makeText(this, "nothing", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {

        if (smsCountTxt != null) {
            if (pendingSMSCount == 0) {
                if (smsCountTxt.getVisibility() != View.GONE) {
                    smsCountTxt.setVisibility(View.GONE);
                }
            } else {
                smsCountTxt.setText(String.valueOf(Math.min(pendingSMSCount, 99)));
                if (smsCountTxt.getVisibility() != View.VISIBLE) {
                    smsCountTxt.setVisibility(View.VISIBLE);
                }
            }
        }
    }
    private void info() {
        if(Common.hashMapUser.get(Common.uid) == null) return;

        String url_avt = Common.hashMapUser.get(Common.uid).getImageURL();

        username.setText(Common.hashMapUser.get(Common.uid).getUsername());

        if (url_avt.equals("df")) {
            avt.setImageResource(R.drawable.ic_account_circle_24dp);
        } else {
            Picasso.with(this).load(url_avt).placeholder(R.drawable.ic_account_circle_24dp).into(avt);
        }
    }

    private void countIMG() {

        countImageExist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                countImage.clear();

                for(DataSnapshot item: dataSnapshot.getChildren())
                {
                    String filename = item.getKey();
                    int cnt = item.getValue(Integer.class);
                    countImage.put(filename, cnt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void init() {
        Toolbar toolbar = findViewById(R.id.toolbar_test);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getNoti = FirebaseDatabase.getInstance().getReference("notification").child(user_cur);
        task = findViewById(R.id.card_view_task);
        thaoluan = findViewById(R.id.card_view_thaoluan);
        guifile = findViewById(R.id.card_view_guifile);

        avt = findViewById(R.id.avt);
        giaovien = Common.hashMapUser.get(Common.uid).getBelong();

        username = findViewById(R.id.username);
        countImageExist = FirebaseDatabase.getInstance().getReference("image");
        updateCountImageExist = FirebaseDatabase.getInstance().getReference("image");
        task.setOnClickListener(this);
        thaoluan.setOnClickListener(this);
        guifile.setOnClickListener(this);

        sinhvien = Common.uid;

        send = FirebaseDatabase.getInstance().getReference("chat").child(giaovien).child(sinhvien);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.card_view_task:


                Intent intent = new Intent(this, SinhVienActivity.class);

                startActivity(intent);
                break;
            case R.id.card_view_thaoluan:

                Intent intent2 = new Intent(this, ChatActivity.class);

                String giaovienhuongdan = Common.hashMapUser.get(Common.uid).getBelong();

                intent2.putExtra("type", 3);
                intent2.putExtra("sinhvien", Common.uid);
                intent2.putExtra("giaovien", giaovienhuongdan);
                startActivity(intent2);

                break;
            case R.id.card_view_guifile:

                Intent intentAddFile = new Intent();
                intentAddFile.setType("application/*");
                intentAddFile.setAction(Intent.ACTION_GET_CONTENT);


                startActivityForResult(Intent.createChooser(intentAddFile, "Select choose"),REQUEST_CHOOSE_FILE);
                break;
        }
    }
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    String filename = "";
    Uri uri;
    public int getUniqueInteger(String name){
        String plaintext = name;
        int hash = name.hashCode();
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(plaintext.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            String hashtext = bigInt.toString(10);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while(hashtext.length() < 32 ){
                hashtext = "0"+hashtext;
            }
            int temp = 0;
            for(int i =0; i<hashtext.length();i++){
                char c = hashtext.charAt(i);
                temp+=(int)c;
            }
            return hash+temp;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hash;
    }
    void upfile()
    {
//        String ex = getFileExtension(uri);
//
//
//        if(ex == null) ex = "docx";

        String s = getFileName(uri);




        String ex = "";

        if(s.contains("pdf")) ex = "pdf";
        else if(s.contains("docx")) ex = "docx";

        if(ex.equals(""))
        {
            Toast.makeText(this, "Yêu cầu up file docx hoặc pdf", Toast.LENGTH_SHORT).show();
            return;
        }

        final String tmp = getUniqueInteger(s)+"";
        if(countImage.get(tmp) == null)
        {
            filename = s;
            countImage.put(tmp, 1);
            //updateCountImageExist.child(tmp).setValue(1);
        }
        else
        {
            int cnt = countImage.get(tmp) + 1;
            countImage.put(tmp, cnt);
            // updateCountImageExist.child(tmp).setValue(cnt);


            if(s.contains("docx"))  filename = s.substring(0, s.length()-5) + "("+ cnt+")" + ".docx";
            else if(s.contains("pdf")) filename = s.substring(0,s.length()-4)  + "("+ cnt+")" + ".pdf";

        }

        Log.d("kkkaa", filename);


        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        final StorageReference fileReference = storageReference.child(ex).child(filename);

        UploadTask uploadTask = fileReference.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw  task.getException();
                }

                return  fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()){

                    Uri downloadUri = task.getResult();

                    String mUri = downloadUri.toString();



                    Log.d("done", mUri);

                    updateCountImageExist.child(tmp).setValue(countImage.get(tmp));

                    send.push().setValue(new Message(filename, Common.uid, mUri, Common.TYPE_FILE, System.currentTimeMillis()));


                    uri = null;


                    Toast.makeText(SinhVienMangerActivity.this, "Done", Toast.LENGTH_SHORT).show();




                } else {
                    Toast.makeText(SinhVienMangerActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SinhVienMangerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("done", e.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK ) return;

        switch (requestCode)
        {
            case REQUEST_CHOOSE_FILE:
                if(data == null || data.getData() == null )
                {
                    Toast.makeText(this, "Chưa chọn file nào", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    uri = data.getData();
                    Toast.makeText(this, "đã chọn file", Toast.LENGTH_SHORT).show();
                    upfile();
                    Log.d("chonfile", uri.toString());
                }

                break;

        }

    }
}
