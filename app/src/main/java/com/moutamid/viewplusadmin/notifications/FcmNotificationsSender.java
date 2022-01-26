package com.moutamid.viewplusadmin.notifications;

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

public class FcmNotificationsSender {
    String body;
    private final String fcmServerKey = "AAAAhtBbk3Q:APA91bH1Rky7KosNHPGZXxSRSx9P_Mi4qNKc1p5yNFH0y6vqoFvyphOyITMaaWfBbw2-_O4uZLb0VjTLuEaJpp2VbfaGtyVj_mr9wMwc1Vb23LYpliZj3R7CCP0-wVnZcbVlwCyr0305";
    //    private final String fcmServerKey = "AAAApf24zxI:APA91bGR1OY2AYOcCJ9Nt156xLrOXrkzJKbwM6hj4d03d4YenZWBxgFTI4fnQnOMmZzFlXOlvr_VsGo39waxcVH4oyJYLZWK-YeMQgP5KDiOkdimuTFa93PoJY-1fRh5NeOhP0IMlGeZ";
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
//                    header.put("authorization", "key=AAAAhtBbk3Q:APA91bH1Rky7KosNHPGZXxSRSx9P_Mi4qNKc1p5yNFH0y6vqoFvyphOyITMaaWfBbw2-_O4uZLb0VjTLuEaJpp2VbfaGtyVj_mr9wMwc1Vb23LYpliZj3R7CCP0-wVnZcbVlwCyr0305");
                    return header;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

