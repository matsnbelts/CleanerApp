package com.example.matsnbeltsassociate.services;

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
    private static String TAG = "FirebaseInstanceService";


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

    /***
     *
     * @param mainActivity
     * @param customerId
     */
    public static void sendToTopic(MainActivity mainActivity, String customerId, String imgPath,
                                   String msg){
        customerId = customerId.substring(3);
        FirebaseMessaging.getInstance().subscribeToTopic(customerId + "_cleaning_done");
        JSONObject parent = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            data.put("title", "Cleaning Done!!!");
            data.put("body", msg);
            data.put("image", imgPath);

            parent.put("notification", data);
            parent.put("to", "/topics/" + customerId + "_cleaning_done");
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
                if (response.isSuccessful()) {
                    final String responseStr = response.body().string();
                    Log.i("Response Body", responseStr);
                    Snackbar snackbar = Snackbar
                            .make(mainActivity.getCoordinatorLayout(), responseStr + "Push Notification sent to customer", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
         //[END send_to_topic]
    }
}
