package com.meow.quanly.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.meow.quanly.ChatActivity;
import com.meow.quanly.Common;
import com.meow.quanly.R;
import com.meow.quanly.TaskActivity;
import com.meow.quanly.model.Message;
import com.meow.quanly.model.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 0;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;

    String user_cur;

    String url_image_other = "df";
    private Context mContext;


    private ArrayList<Message> arr;

    public ChatAdapter(Context context, ArrayList<Message> messageList, String user_cur) {
        mContext = context;
        arr = messageList;

        this.user_cur = user_cur;


    }

    @Override
    public int getItemCount() {
        return arr.size();
    }


    @Override
    public int getItemViewType(int position) {

        if (user_cur.equals(arr.get(position).getFrom())){

            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_phai, parent, false);
            return new RightHolder(view);
        } else {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_trai, parent, false);
            return new LeftHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Message message = arr.get(position);


        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                //((RightHolder) holder).rl.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_fake));
                ((RightHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                //((LeftHolder) holder).rl.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_scale_anim));
                ((LeftHolder) holder).bind(message);
        }
    }

    private class RightHolder extends RecyclerView.ViewHolder {


       RelativeLayout cardView;
       TextView txt_chat, txt_date;

        View v;
        RightHolder(View itemView) {

            super(itemView);
            cardView = itemView.findViewById(R.id.card_view_chat_right);

            txt_chat =  itemView.findViewById(R.id.txt_chat);
            txt_date =  itemView.findViewById(R.id.txt_date);
            v = itemView;

        }

        void bind(final Message message) {

            txt_date.setText(Common.getDate(message.getTime()));
            txt_chat.setText(message.getMess());

            if(message.getType() == Common.TYPE_LIST_TASK)
            {

                txt_chat.setPaintFlags(txt_chat.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);

                txt_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, TaskActivity.class);

                        intent.putExtra("link", message.getLink());

                        String sinhvien = ChatActivity.sinhvien;
                        String giaovien = "";
                        //giaovien
                        if(Common.hashMapUser.get(message.getFrom()).getBelong() == null || Common.hashMapUser.get(message.getFrom()).getBelong().equals("") == true)
                        {
                            giaovien = message.getFrom();
                        }
                        else giaovien = Common.hashMapUser.get(message.getFrom()).getBelong();


                        intent.putExtra("sinhvien", sinhvien);

                        intent.putExtra("giaovien", giaovien);


                        mContext.startActivity(intent);
                    }
                });
            }
            else if(message.getType() == Common.TYPE_FILE)
            {
                txt_chat.setPaintFlags(txt_chat.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);


                txt_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Toast.makeText(mContext, "yes", Toast.LENGTH_SHORT).show();
                                        download(message.getMess(), message.getLink());
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        Toast.makeText(mContext, "no", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Bạn muốn tải file này về không").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });
            }
        }
    }

    private class LeftHolder extends RecyclerView.ViewHolder {
        ImageView  avt;

        RelativeLayout cardView;
        String url;
        TextView txt_chat, txt_date;

        View v;
        LeftHolder(View itemView) {

            super(itemView);

            cardView = itemView.findViewById(R.id.card_view_chat_left);
            avt = itemView.findViewById(R.id.avt_chat);
            //type_chat = itemView.findViewById(R.id.type_chat);
            txt_chat =  itemView.findViewById(R.id.txt_chat);
            txt_date = itemView.findViewById(R.id.txt_date);

            v = itemView;


        }

        void bind(final Message message) {


            txt_date.setText(Common.getDate(message.getTime()));
            if(url == null) url = Common.hashMapUser.get(message.getFrom()).getImageURL();

            if (url.equals("df")) {
                avt.setImageResource(R.drawable.ic_account_circle_24dp);
            } else {
                Picasso.with(mContext).load(url).placeholder(R.drawable.ic_account_circle_24dp).into(avt);
            }


            txt_chat.setText(message.getMess());



            if(message.getType() == Common.TYPE_LIST_TASK)
            {
             //   type_chat.setVisibility(View.VISIBLE);
                txt_chat.setPaintFlags(txt_chat.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
               // type_chat.setImageResource(R.drawable.ic_eye);

                txt_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, TaskActivity.class);

                        intent.putExtra("link", message.getLink());

                        String sinhvien = ChatActivity.sinhvien;
                        String giaovien = "";
                        //giaovien
                        if(Common.hashMapUser.get(message.getFrom()).getBelong() == null || Common.hashMapUser.get(message.getFrom()).getBelong().equals("") == true)
                        {
                            giaovien = message.getFrom();
                        }
                        else giaovien = Common.hashMapUser.get(message.getFrom()).getBelong();


                        intent.putExtra("sinhvien", sinhvien);

                        intent.putExtra("giaovien", giaovien);


                        mContext.startActivity(intent);
                    }
                });
            }
            else if(message.getType() == Common.TYPE_FILE)
            {
                txt_chat.setPaintFlags(txt_chat.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
                //type_chat.setVisibility(View.VISIBLE);
                //type_chat.setImageResource(R.drawable.ic_down);
                txt_chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Toast.makeText(mContext, "yes", Toast.LENGTH_SHORT).show();
                                        download(message.getMess(), message.getLink());
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        Toast.makeText(mContext, "no", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Bạn muốn tải file này về không").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });
            }

        }
    }

    void download(final String filename, String url)
    {
        final File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), filename);
        //File localFile = File.createTempFile("images", "jpg");

        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);


        httpsReference.getFile(file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {

                    Toast.makeText(mContext, filename + " được tải xuống thu mục Download trong bộ nhớ thiết bị", Toast.LENGTH_LONG).show();
                    Log.d("isOk", file.toString());




                }
                else
                {
                    Log.d("isOk", "wtf");
                    Toast.makeText(mContext, "Tải xuống thất bại", Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ahuhu", e.getMessage());
            }
        });
    }

}
