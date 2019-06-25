package vyomchandra.com.completeproject.BroadcastReciver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import vyomchandra.com.completeproject.MainActivity;
import vyomchandra.com.completeproject.R;

public class AlarmReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long when=System.currentTimeMillis();
        NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent=new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("MyNotification","MyNotification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"MyNotification")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Daily Hisab")
                .setContentText("Hey Buddy ! how much you spent today ??")
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000,1000,1000,1000});

        notificationManager.notify(0,builder.build());
        Toast.makeText(context, "broadcast", Toast.LENGTH_SHORT).show();

    }
}
