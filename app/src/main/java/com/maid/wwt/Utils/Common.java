package com.maid.wwt.Utils;

import com.maid.wwt.Model.User;
import com.maid.wwt.Remote.IFCMService;
import com.maid.wwt.Remote.RetrofitClient;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static final String USER_INFORMATION ="UserInformation" ;
    public static final String USER_UID_SAVE_KEY = "SaveUid";
    public static final String TOKENS = "Tokens" ;
    public static final String FROM_NAME ="From Name" ;
    public static final String ACCEPT_LIST ="acceptList" ;
    public static final String FROM_UID ="FromUid";
    public static final String TO_UID ="ToUid" ;
    public static final String TO_NAME ="ToName" ;
    public static final String FRIEND_REQUEST ="FriendRequests" ;
    public static final String PUBLIC_LOCATION ="PublicLocation" ;
    public static User loggedUser;
    public static User trackingUser;

    public static IFCMService getFCMService(){
        return RetrofitClient.getClient("https://fcm.googleapis.com/wp/AAAAZi2lBeM:APA91bFUw7HyTeAi0oM3Kf51-hk5Kc23UIWG-gN3-d9VICjqdZTJs14dp6anqb7Lqj_7eb1WSGg8vu5uEHW3_ASQ3nOwrDPsqr6tYgp_P2RnpTdGcEmJJmaJiS8IkStuWUuCCbWO11ue/").create(IFCMService.class);
    }

    public static Date convertTimeStampToDate(long time) {
    return new Date(new Timestamp(time).getTime());
    }

    public static String getDateFormatted(Date date) {

        return  new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString();
    }
}

