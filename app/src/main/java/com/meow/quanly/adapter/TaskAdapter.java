package com.meow.quanly.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meow.quanly.Common;
import com.meow.quanly.R;
import com.meow.quanly.model.TaskItem;
import com.meow.quanly.model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    ArrayList<TaskItem> arr;

    Context context;
    public TaskAdapter(Context context, ArrayList<TaskItem> arr) {

        this.arr = arr;

        this.context = context;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_new_task, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(arr.get(position));
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        CardView cardView;
        CheckBox checkBox;
        TextView detail;
        Button dialog_btn;

        Dialog dialog;
        MaterialEditText dialog_detail;
        TextView dialog_tv;

        public ViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox_new_task);

            detail = itemView.findViewById(R.id.detail_new_task);

            cardView = itemView.findViewById(R.id.card_view_new_task);

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_new_task);
            dialog_detail = dialog.findViewById(R.id.dialog_detail);
            dialog_btn = dialog.findViewById(R.id.dialog_btn);
            dialog_tv = dialog.findViewById(R.id.txt_loai);


            dialog_tv.setText("Nhập nội dung sửa");

        }

        public void bind(final TaskItem item)
        {
            checkBox.setChecked(item.check);
            detail.setText(item.detail);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) cardView.setBackgroundColor(Color.parseColor("#B6F3ED"));

                    else cardView.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    arr.get(getAdapterPosition()).check = isChecked;
                }
            });
            
//            if(isChecked) cardView.setBackgroundColor(Color.parseColor("#B6F3ED"));
//            else cardView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            if(item.type == Common.TASK_ITEM_EDIT)
            {
                cardView.setBackgroundColor(Color.parseColor("#FFB6C3"));
            }
            else if(item.type == Common.TASK_ITEM_ADD)
            {
                cardView.setBackgroundColor(Color.parseColor("#B6F3ED"));
            }
            else
            {
                //cardView.setBackgroundColor(Color.parseColor("#ffffff"));
            }











            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dialog.show();

                    dialog_detail.setText(item.detail);
                    dialog_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dialog_detail.getText().toString().equals("") == false)
                            {
                                arr.get(getAdapterPosition()).detail = dialog_detail.getText().toString();
                                arr.get(getAdapterPosition()).type = Common.TASK_ITEM_EDIT;
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                            else
                            {
                                Toast.makeText(context, "Yêu cầu nội dung chỉnh sửa không được để trống", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    return true;
                }
            });
        }
    }
}
