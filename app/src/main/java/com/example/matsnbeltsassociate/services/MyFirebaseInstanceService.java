package com.example.matsnbeltsassociate.services;

import android.app.Notification;
import android.util.Log;

import com.example.matsnbeltsassociate.activity.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyFirebaseInstanceService extends FirebaseMessagingService {
    private static String TAG = "FirebaseMessaging";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public static void sendToTopic(MainActivity mainActivity, String customerId, String serviceType){
        customerId = customerId.substring(3);
        FirebaseMessaging.getInstance().subscribeToTopic(customerId);
        JSONObject parent = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            data.put("title", "Cleaning Done!!!");
            data.put("content", "Associate name just finished the " + serviceType + " cleaning");

            parent.put("data", data);
            parent.put("to", "/topics/" + customerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//
//        String jsonBody = "{\n" +
//                "  \"data\": {\n" +
//                "    \"title\": \"Finish\",\n" +
//                "    \"content\": \"cleaning\"\n" +
//                "  },\n" +
//                "  \"to\": \"/topics/srinis\"\n" +
//                "}";
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), parent.toString());
        Request request = new Request.Builder()
                .header("Authorization", "key=AAAAj_Lpq38:APA91bF8jDd9nUXpDNn8n2SQcXvSQwZV2ibGlxlbN_huuC1X3Pti3OfZvWAPf9Zr2jR9iMqrOnPbGZIqsGNz6GVflZoTa73x6jqxq6D8nBRBGJ9cgB7-d6llEXzj7CJN0-xjC7sX14SM")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .build();
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override public void onResponse(Response response) throws IOException {
                Log.i(TAG, ":::::" + response.body().string());
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.i("FirebaseHelperLog", responseStr);
                    Snackbar snackbar = Snackbar
                            .make(mainActivity.getCoordinatorLayout(), responseStr + "Push Notification sent to customer", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
//        Response response = null;
//        try {
//            response = client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // [START send_to_topic]
        // The topic name can be optionally prefixed with "/topics/".

        // Send a message to the devices subscribed to the provided topic.
//        String response = null;
//        Message message = Message.builder()
//                .putData("score", "850")
//                .putData("time", "2:45")
//                .setTopic("srinis")
//                .build();
//        FirebaseMessaging fm = FirebaseMessaging.getInstance();
//        fm.send(message);
//        // Response is a message ID string.
//        System.out.println("Successfully sent message: " + response);
         //[END send_to_topic]
    }
}
