package com.meow.quanly;

import android.text.format.DateFormat;

import com.meow.quanly.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Common {
    public static String key;
    public static String uid;
    public static ArrayList<User> arrUser = new ArrayList<>();

    public static HashMap<String, User> hashMapUser = new HashMap<>();
    public final static int TYPE_PHONGBAN = 1;
    public final static int TYPE_GIAOVIEN = 2;
    public final static int TYPE_SINHVIEN = 3;

    public final static int TYPE_MESSAGE = 1;
    public final static int TYPE_LIST_TASK = 2;
    public final static int TYPE_FILE = 3;

    public final static int TASK_ITEM_NORMAL = 1;
    public final static int TASK_ITEM_ADD = 2;
    public final static int TASK_ITEM_EDIT = 3;
    public static String giaovien_cur = "";

    public static ArrayList<User> getArrUserType(int type)
    {


        ArrayList<User> tmp = new ArrayList<>();


        if(type == TYPE_SINHVIEN)
        {
            for(int i = 0; i < arrUser.size(); i++)
            {
                if(arrUser.get(i).getType() == type && (arrUser.get(i).getBelong() == null || arrUser.get(i).getBelong().equals("")))
                {
                    tmp.add(arrUser.get(i));
                }
            }

            return tmp;
        }

        for(int i = 0; i < arrUser.size(); i++)
        {
            if(arrUser.get(i).getType() == type)
            {
                tmp.add(arrUser.get(i));
            }
        }

        return tmp;
    }

    public static ArrayList<User> getSinhVien()
    {


        ArrayList<User> tmp = new ArrayList<>();


        for(int i = 0; i < arrUser.size(); i++)
        {
            if(arrUser.get(i).getType() == TYPE_SINHVIEN)
            {
                tmp.add(arrUser.get(i));
            }
        }

        return tmp;
    }

    public static String getDate(long milliSeconds)
    {
        String dateFormat = "dd/MM/yyyy hh:mm";
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String convertDate(long dateInMilliseconds) {

        String dateFormat = "dd/MM/yyyy hh:mm:ss";

        return DateFormat.format(dateFormat, dateInMilliseconds).toString();
    }
}
