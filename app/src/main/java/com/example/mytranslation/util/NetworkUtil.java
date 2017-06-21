package com.example.mytranslation.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.R.attr.id;

/**
 * Created by Administrator on 2017/4/23 0023.
 */

public class NetworkUtil {

    public  static Boolean isNetworkConnected(Context context){

        if (context!=null){
            ConnectivityManager manager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info=manager.getActiveNetworkInfo();
            if (info!=null){
                return  true;
            }
        }

        return false;
    }

}
