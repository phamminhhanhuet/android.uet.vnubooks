package com.uet.android.mouspad.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uet.android.mouspad.Activity.Authorize.LoginActivity;
import com.uet.android.mouspad.Activity.IntroActivity;
import com.uet.android.mouspad.Ebook.EpubListActivity;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.LayoutUtils;


public class SplashFragment extends Fragment implements Animation.AnimationListener {

    public SplashFragment() {
    }


    public static SplashFragment newInstance() {
        return new SplashFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        MappingWidgets(view);
        return view;
    }

    private void MappingWidgets(View view){
        RelativeLayout mRelativeLayout = view.findViewById(R.id.layoutSplashAct);
        ImageView mImageView = view.findViewById(R.id.imgSplashAct_Logo);
        TextView mTextView = view.findViewById(R.id.txtAppName);

        Animation transitionAnim = AnimationUtils.loadAnimation(getContext(), R.anim.transition_icon);
        Animation alphaAnim = AnimationUtils.loadAnimation(getContext(), R.anim.background_alpha);
        mTextView.setAnimation(transitionAnim);
        mRelativeLayout.setAnimation(alphaAnim);
        alphaAnim.setAnimationListener(this);
    }
    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(getActivity(), IntroActivity.class));
//                Log.d("Directory external", Environment.getExternalStorageState());
//                Log.d("Directory intenal", Environment.getExternalStorageDirectory().toString());
//                Log.d("Directory document", Environment.DIRECTORY_DOCUMENTS);
//                Log.d("Directory download", Environment.DIRECTORY_DOWNLOADS);
//                Log.d("Diectotory dcim", Environment.DIRECTORY_DCIM);

                getActivity().finish();
            }
        }, 2000);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}