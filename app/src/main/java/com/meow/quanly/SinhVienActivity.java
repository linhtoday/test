package com.meow.quanly;

import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.meow.quanly.adapter.TaskAdapter;
import com.meow.quanly.model.Message;
import com.meow.quanly.model.Notification;
import com.meow.quanly.model.TaskItem;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class SinhVienActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CHOOSE_FILE = 123;
    LinearLayout ll;
    ArrayList<TaskItem> arr;
    TaskAdapter adapter;
    RecyclerView rv;

    HashMap<String, Integer> countImage = new HashMap<>();
    Button btnAddFile, btnSendFile;
    Uri uri;

    DatabaseReference send;
    DatabaseReference tasks;
    DatabaseReference countImageExist, updateCountImageExist;

    long time = 0;



    TextView username;
    ImageView avt;

    String user_cur = Common.uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinh_vien);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        init();

//        info();

        countIMG();

    //    listNoti();
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
//        Toolbar toolbar = findViewById(R.id.toolbar_test);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
        avt = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        String giaovienhuongdan = Common.hashMapUser.get(Common.uid).getBelong();

        if(giaovienhuongdan == null || giaovienhuongdan.equals(""))
        {
            Toast.makeText(this, "bạn chưa có giáo viên hướng dẫn nào", Toast.LENGTH_SHORT).show();
            finish();
        }

        send = FirebaseDatabase.getInstance().getReference("chat").child(giaovienhuongdan).child(Common.uid);

        countImageExist = FirebaseDatabase.getInstance().getReference("image");

        updateCountImageExist = FirebaseDatabase.getInstance().getReference("image");



        tasks = FirebaseDatabase.getInstance().getReference("tasks").child(Common.uid);


        btnAddFile = findViewById(R.id.btn_add_file);

        btnSendFile = findViewById(R.id.btn_send_file);

        btnSendFile.setOnClickListener(this);

        btnAddFile.setOnClickListener(this);

        arr = new ArrayList<>();
        ll = findViewById(R.id.ll_add_task);
        rv = findViewById(R.id.rv);
        ll.setOnClickListener(this);
        adapter = new TaskAdapter(this, arr);

        rv.setLayoutManager(new LinearLayoutManager(this));

        rv.setAdapter(adapter);


//        for(int i = 0; i < 20; i++)
//        {
//            arr.add(new TaskItem(false, "olala"));
//        }

//        adapter.notifyDataSetChanged();



    }

    void addTask()
    {
        final Dialog dialog = new Dialog(SinhVienActivity.this);
        dialog.setContentView(R.layout.dialog_new_task);
        dialog.show();
        final TextView detail = dialog.findViewById(R.id.dialog_detail);
        Button btn = dialog.findViewById(R.id.dialog_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detail.getText().toString().equals(""))
                {
                    Toast.makeText(SinhVienActivity.this, "Yêu cầu không được để trống", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ll_add_task:
                //Toast.makeText(this, "olala", Toast.LENGTH_SHORT).show();
                addTask();
                break;
            case R.id.btn_send_file:
                if(uri == null && arr.size() == 0)
                {
                    Toast.makeText(this, "Yêu cầu không được để trống task hoặc gửi file", Toast.LENGTH_SHORT).show();
                    return;
                }

                time = System.currentTimeMillis();



                if(arr.size() != 0)
                {

                    String link = tasks.push().getKey();
                    send.push().setValue(new Message("Đã gửi task", Common.uid, link, Common.TYPE_LIST_TASK, time));
                    tasks.child(link).setValue(arr);


                    arr.clear();
                    adapter.notifyDataSetChanged();
                }

                if(uri != null) upfile();



                break;
            case R.id.btn_add_file:

                Intent intentAddFile = new Intent();
                intentAddFile.setType("application/*");
                intentAddFile.setAction(Intent.ACTION_GET_CONTENT);


                startActivityForResult(Intent.createChooser(intentAddFile, "Select choose"),REQUEST_CHOOSE_FILE);

                break;

        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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

                    send.push().setValue(new Message(filename, Common.uid, mUri, Common.TYPE_FILE, time));


                    uri = null;


                    Toast.makeText(SinhVienActivity.this, "Done", Toast.LENGTH_SHORT).show();

                    btnAddFile.setText("Add file");


                } else {
                    Toast.makeText(SinhVienActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SinhVienActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Log.d("chonfile", uri.toString());
                }

                break;

        }

    }


}
