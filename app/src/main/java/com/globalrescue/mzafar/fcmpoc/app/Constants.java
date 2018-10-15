package com.globalrescue.mzafar.fcmpoc.app;

public class Constants {

    //Necessary for Android Oreo and Above
    public static final String CHANNEL_ID="";
    public static final String CHANNEL_NAME="";
    public static final String CHANNEL_DESCRIPTION="";

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "fcm_pref";

    // Keys for FCM Data Messages
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String IMAGE_URL = "image-url";
    public static final String TEST_KEY_2 = "testKey2";
}
