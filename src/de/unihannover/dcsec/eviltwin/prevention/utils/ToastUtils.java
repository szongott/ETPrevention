package de.unihannover.dcsec.eviltwin.prevention.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {

    public static void showToastInUiThread(final Context ctx,
        final String stringRes) {

        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ctx, stringRes, Toast.LENGTH_SHORT).show();
            }
       });
    }
}