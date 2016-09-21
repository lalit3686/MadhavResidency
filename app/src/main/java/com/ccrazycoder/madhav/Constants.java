package com.ccrazycoder.madhav;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Vishal.Khakhkhar on 21/09/2016.
 */

public class Constants {
    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
