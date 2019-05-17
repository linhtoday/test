package com.meow.quanly;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.meow.quanly.model.Message;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class ClickSinhVienActivity extends AppCompatActivity  implements View.OnClickListener{

    private static final int REQUEST_CHOOSE_FILE = 6969;
    CardView task, thaoluan, guifile;
    String sinhvien, giaovien;
    DatabaseReference countImageExist, updateCountImageExist;
    HashMap<String, Integer> countImage = new HashMap<>();
    DatabaseReference send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_sinh_vien);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        init();
        countIMG();
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
        task = findViewById(R.id.card_view_task);
        thaoluan = findViewById(R.id.card_view_thaoluan);
        guifile = findViewById(R.id.card_view_guifile);


        countImageExist = FirebaseDatabase.getInstance().getReference("image");
        updateCountImageExist = FirebaseDatabase.getInstance().getReference("image");
        task.setOnClickListener(this);
        thaoluan.setOnClickListener(this);
        guifile.setOnClickListener(this);

        sinhvien = getIntent().getStringExtra("sinhvien");
        giaovien = getIntent().getStringExtra("giaovien");

        send = FirebaseDatabase.getInstance().getReference("chat").child(giaovien).child(sinhvien);

        if(sinhvien == null)
        {
            Toast.makeText(this, "something wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.card_view_task:
                Intent intent4 = new Intent(this, QuanLyTask.class);

                intent4.putExtra("sinhvien", sinhvien);
                intent4.putExtra("giaovien", giaovien);
                startActivity(intent4);
                break;
            case R.id.card_view_thaoluan:

                Intent intent = new Intent(this, ChatActivity.class);

                intent.putExtra("sinhvien", sinhvien);
                intent.putExtra("giaovien", giaovien);
                startActivity(intent);

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


                    Toast.makeText(ClickSinhVienActivity.this, "Done", Toast.LENGTH_SHORT).show();




                } else {
                    Toast.makeText(ClickSinhVienActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ClickSinhVienActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
