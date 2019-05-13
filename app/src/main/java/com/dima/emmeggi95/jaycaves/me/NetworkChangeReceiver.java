package com.dima.emmeggi95.jaycaves.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.google.android.gms.common.internal.DialogRedirect;

public class NetworkChangeReceiver extends BroadcastReceiver {

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // Get system service object.
        Object systemServiceObj = context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Convert the service object to ConnectivityManager instance.
        connectivityManager = (ConnectivityManager)systemServiceObj;

        // Get network info object.
        networkInfo = connectivityManager.getActiveNetworkInfo();

        // Check whether network is available or not.
        boolean networkIsAvailable = false;

        if(networkInfo!=null)
        {
            if(networkInfo.isAvailable())
            {
                networkIsAvailable = true;
            }
        }

        // Display message based on whether network is available or not.
        String networkMessage = "";
        if(networkIsAvailable)
        {
            networkMessage = "Network is available";
            //Toast.makeText(context, networkMessage, Toast.LENGTH_LONG).show();
        }else
        {
            networkMessage = "Network is not available";
            new AlertDialog.Builder(context)
                    .setTitle("No Internet")
                    .setMessage("This app requires constant internet access. Check internet connection")
                    .setCancelable(false)

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("RETRY", new DialogRedirect() {
                        @Override
                        protected void redirect() {
                            //Intent i = new Intent(context, MainActivity.class);
                            // context.startActivity(i);
                            ((Activity) context).recreate();


                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((Activity) context).finish();
                                }
                            }
                    )
                    .setIcon(android.R.drawable.ic_dialog_alert).show();

        }






        /*   final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        System.out.println("\n HELLO THERE\n");

        if (!wifi.isAvailable() && !mobile.isAvailable()) {


        } */
    }


}
