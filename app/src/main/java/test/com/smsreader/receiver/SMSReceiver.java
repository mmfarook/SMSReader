package test.com.smsreader.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import test.com.smsreader.MainActivity;
import test.com.smsreader.R;

/**
 * Created by mmdfarook on 21/02/19.
 */

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";
    private static int notificationId = 1001;
    private static String groupId;
    private static String channelId;
    private static int SUMMARY_ID = 2000;
    public static String NOTIFY_SCHEME = "smsreader";
    public static String NOTIFY_SCHEME_URL = NOTIFY_SCHEME + "://";

    public static String HOST = "sms.com";

    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.getOriginatingAddress();
                }
            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    smsSender = messages[0].getOriginatingAddress();
                }
            }
            showNotification(context, smsSender);
        }
    }

    private void showNotification(Context context, String sender) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (groupId == null) {
            groupId = context.getApplicationInfo().packageName;
        }

        String message = context.getString(R.string.received) + sender;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setVibrate(new long[]{300, 100, 300, 100, 500})
                        .setGroup(groupId)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        Intent intent = new Intent(context, MainActivity.class);
        intent.setData(Uri.parse(NOTIFY_SCHEME_URL + HOST + "/message?sender=" + sender));
        if (intent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (channelId == null) {
                channelId = context.getApplicationInfo().packageName + ".channel";
            }
            builder.setChannelId(channelId);
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(notificationId, builder.build());
        notificationId++;
        if (notificationId > Integer.MAX_VALUE - 100) {
            notificationId = 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationCompat.Builder summaryBuilder =
                    new NotificationCompat.Builder(context)
                            .setContentTitle(context.getApplicationInfo().name)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setGroup(groupId)
                            .setGroupSummary(true);
            if (channelId != null) {
                summaryBuilder.setChannelId(channelId);
            }
            summaryBuilder.setAutoCancel(true);
            notificationManager.notify(SUMMARY_ID, summaryBuilder.build());
        }
    }
}
