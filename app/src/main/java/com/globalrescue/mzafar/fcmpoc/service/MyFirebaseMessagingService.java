package com.globalrescue.mzafar.fcmpoc.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.globalrescue.mzafar.fcmpoc.activity.MainActivity;
import com.globalrescue.mzafar.fcmpoc.app.Constants;
import com.globalrescue.mzafar.fcmpoc.utils.NotificationsManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService  {

    private static final String TAG = "mFirebaseMessageService";

    private NotificationsManager notificationsManager;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.i(TAG, "onNewToken: "+s);

        // Saving reg id to shared preferences
        storeRegIdInPref(s);

        // sending reg id to the server
        sendRegistrationToServer(s);
    }

    // Callback Handler for Push Messages Received from Server
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Log.i(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            
            //Delegates RemoteMessage Object and handles result accordingly. 
            handleNotification(remoteMessage);
        }
        
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());

            try {

                JSONObject data = new JSONObject(remoteMessage.getData());
                long timestamp = remoteMessage.getSentTime();

                handleDataMessage(data, timestamp);

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
//            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
//            } else {
                // Handle message within 10 seconds
//                handleNow();
//            }

        }

    }

    private void handleNotification(RemoteMessage remoteMessage) {
        if (!NotificationsManager.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", remoteMessage.getNotification().getBody());
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // Generate a local push notification
            notificationsManager = new NotificationsManager(getApplicationContext());
            notificationsManager.showSimpleNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(), remoteMessage.getSentTime(), null);

            notificationsManager = null;
        }
        // If the app is in background, firebase itself handles the notification
    }

    private void handleDataMessage(JSONObject data, long timestamp) {
        Log.e(TAG, "push json: " + data.toString());

        try {

            String title = data.getString(Constants.TITLE);
            String message = data.getString(Constants.MESSAGE);
            String imageURL = data.getString(Constants.IMAGE_URL);

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);


            if (!NotificationsManager.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // Generate a local push notification
                notificationsManager = new NotificationsManager(getApplicationContext());


                //Decide based on Image Attachment
                if (TextUtils.isEmpty(imageURL)){
                    notificationsManager.showSimpleNotification(title, message, timestamp, resultIntent);
                }else{
                    notificationsManager.showNotificationWithImage(title,message,timestamp,resultIntent,imageURL);
                }

                // check for image attachment
//                if (TextUtils.isEmpty(imageUrl)) {
//                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
//                } else {
                    // image is present, show notification with image
//                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
//                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationsManager = new NotificationsManager(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationsManager.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationsManager = new NotificationsManager(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationsManager.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    /*
    *  This method will be responsible to make an API cal and send the
    *  FCM Registration token to the server
    */
    private void sendRegistrationToServer(final String token) {
        Log.i(TAG, "sendRegistrationToServer: " + token);
    }

    // Store FCM Registration Token in Shared Preferences for further usage.
    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }
}
