package com.uet.android.mouspad.Service;


import com.uet.android.mouspad.Service.Map.Direction.DirectionRoot;
import com.uet.android.mouspad.Service.Map.Geocode.GeocodingRoot;
import com.uet.android.mouspad.Service.Notifications.MyResponse;
import com.uet.android.mouspad.Service.Notifications.Sender;
import com.uet.android.mouspad.Service.YoutubeApi.SearchRoot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    @Headers({
                    "Content-Type:application/json",
                    "Authorization:key=AAAAVGKiPBM:APA91bH62NfsmRsc1TgzmHGBSilYb2AQgWe7u3FTuvBtGUpFbZvhCyWdE0m5nzcOeR3sRj1V7Cg4pooTfbKPwT5CmnwlUh7jpoxsrOgb0Ew5UxllE-lHV2j_i7mRBAICGoh-JDKcc3nT"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
    @GET("geocode/json?&key=AIzaSyB_Nd1VXFFZMoyPBSXRbe_PrS6YwTX7_LI")
    Call<GeocodingRoot> getLocation(@Query("address") String address);
    @GET("directions/json?&key=AIzaSyB_Nd1VXFFZMoyPBSXRbe_PrS6YwTX7_LI")
    Call<DirectionRoot> getDirection(@Query("origin") String origin,
                                     @Query("destination") String destination);
    @GET("search?part=snippet&eventType=completed&maxResults=25&order=rating&type=video&key=AIzaSyB_Nd1VXFFZMoyPBSXRbe_PrS6YwTX7_LI")
    Call<SearchRoot> getSearch(@Query("q") String keywork);
}
