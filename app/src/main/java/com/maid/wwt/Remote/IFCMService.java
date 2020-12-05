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
            "Authorisation:key=AAAAZi2lBeM:APA91bHLTGA-Rl8sxlgKHnZeoLU5J6JgxinRUL-zu1Vt_HJr5EYuKTcNDh7z4JEzCwhDyyYgbR3usT5N6D5M-EE9NnQf356860ucmHaITijeWr-g6KFG6J9KV5eQkll5--ap9lcJO5W_"
    })
    @POST("form/send")
    Observable<MyResponse> sendFriendRequestToUser(@Body Request body);
}