package com.uet.android.mouspad.Utils;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uet.android.mouspad.R;

import java.util.Collections;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.uet.android.mouspad.Utils.Constants.*;

public final class ActivityUtils {
    public static void addFragmentToContext(Context mContext, Fragment mFragment, int fragmentId){
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(fragmentId);
        if(fragment ==null){
            fragment = mFragment;
            fragmentManager.beginTransaction().add(fragmentId, fragment).commit();
        }
    }

    public static void replaceFragmentInContext(Context mContext, Fragment fragment, int fragmentId){
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(fragmentId, fragment).commit();
    }

    public static void removeFragmentFromContext(Context mContext, Fragment mFragement){
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        if(mFragement !=null){
            fragmentManager.beginTransaction().remove(mFragement).commit();
        }
    }

    public static void removeAllFragmentFromContext(Context mContext){
        FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        if(fragmentManager.getFragments().size() >0) {
            fragmentManager.getFragments().removeAll(Collections.singleton(null));
        }
    }
    public static boolean isExistFragmentInContext(Context mContext){
        FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
        int size = fragmentManager.getFragments().size();
        return  (size > 0? true: false);
    }

    public static void startActivityToPickImage(Fragment fragment, int requestCode){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(Intent.createChooser(intent, "Pick an image"), requestCode);
    }

    public static void startActivityToPickAndCropImage (Activity activity){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(activity);
    }

    public static void initRetrofit (Retrofit retrofit, String baseUrl){
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            String description = context.getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
