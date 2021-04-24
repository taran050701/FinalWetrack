package com.maid.wwt.Remote;

import com.maid.wwt.Model.MyResponse;
import com.maid.wwt.Model.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorisation:key=AAAAZi2lBeM:APA91bFUw7HyTeAi0oM3Kf51-hk5Kc23UIWG-gN3-d9VICjqdZTJs14dp6anqb7Lqj_7eb1WSGg8vu5uEHW3_ASQ3nOwrDPsqr6tYgp_P2RnpTdGcEmJJmaJiS8IkStuWUuCCbWO11ue"
    })
    @POST("form/send")
    Observable<MyResponse> sendFriendRequestToUser(@Body Request body);
}