package com.shengid.liture.coursetable.Helper;

import android.app.AlertDialog;
import android.content.Context;

import com.shengid.liture.coursetable.Activity.LoginActivity;

public class Alert {
    public static void info(Context context, String info){
        ( (LoginActivity)context).runOnUiThread( () -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(info);
            dialog.setPositiveButton("OK", (_dialog, _which) -> {  }  );
            dialog.show();
        });
    }
}
