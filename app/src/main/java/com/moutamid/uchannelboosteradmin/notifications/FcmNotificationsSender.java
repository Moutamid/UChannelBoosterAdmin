package com.moutamid.uchannelboosteradmin.notifications;

import android.app.Activity;
import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {// TODO: GET NEW SERVER KEY

    String body;
    private final String fcmServerKey = "AAAA-hLNaI8:APA91bHqqnoCINa8f_S3qPeZtL6Ud5za8D3YapW4khnF8weFa8gWZkUSofMMsxdixRe4vwy8bGnPrGE-NJvtwg94JmyPIAtUW8TaD-Nq3ulIHVtqyYE1hUbTYUfG1U-Izw_I7nSazrLs";
    Activity mActivity;
    Context mContext;
    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private RequestQueue requestQueue;
    String title;
    String userFcmToken;


    public FcmNotificationsSender(String userFcmToken2, String title2, String body2, Context mContext2, Activity mActivity2) {
        this.userFcmToken = userFcmToken2;
        this.title = title2;
        this.body = body2;
        this.mContext = mContext2;
        this.mActivity = mActivity2;
    }

    public void SendNotifications() {
        this.requestQueue = Volley.newRequestQueue(this.mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", this.userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", this.title);
            notiObject.put("body", this.body);
            notiObject.put("icon", "icon");
//            notiObject.put("icon", "icon");
            mainObj.put("notification", notiObject);
            this.requestQueue.add(new JsonObjectRequest(1, "https://fcm.googleapis.com/fcm/send", mainObj, new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

