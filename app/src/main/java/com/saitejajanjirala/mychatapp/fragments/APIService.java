package com.saitejajanjirala.mychatapp.fragments;

import com.saitejajanjirala.mychatapp.notifications.MyResponse;
import com.saitejajanjirala.mychatapp.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA8psOu70:APA91bFnyXpvq1RiuWBazRiYsi5BytFjzGuy6c2jcN-Toe0-MwckxjlBRcqlWH6gnFFm_E32Ehgnyo-JaK4DSA_1DO5ZFl8hfOND1nqIFP5buZIz60-ivt8VwtdIjNEKnlQkXd0YES_Z"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
