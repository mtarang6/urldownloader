package com.tara.toasty;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
    public static void getToaster(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
