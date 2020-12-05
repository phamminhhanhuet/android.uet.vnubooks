package com.uet.android.mouspad.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.uet.android.mouspad.Activity.Authorize.LoginActivity;
import com.uet.android.mouspad.Adapter.IntroViewPagerAdapter;
import com.uet.android.mouspad.Model.Intro.ScreenItem;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.uet.android.mouspad.Utils.ConnectionUtils.isLoginValid;

public class IntroActivity extends AppCompatActivity {

    private ViewPager mScreenPager;
    IntroViewPagerAdapter mIntroViewPagerAdapter ;
    TabLayout mTabIndicator;
    Button mBtnNext;
    int position = 0 ;
    Button mBtnFreeAcc, mBtnCreateAcc;
    LinearLayout mLayoutContain;
    Animation mBtnAnim ;
    TextView mTxtSkip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // when this activity is about to be launch we need to check if its openened before or not
        if (restorePrefData()) {
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                Intent mainActivity = new Intent(getApplicationContext(), HomeActivity.class );
                mainActivity.putExtra(Constants.LOGIN_STATE, true);
                startActivity(mainActivity);
                finish();
            }else {
                Intent mainActivity = new Intent(getApplicationContext(), HomeActivity.class );
                mainActivity.putExtra(Constants.LOGIN_STATE, false);
                startActivity(mainActivity);
                finish();
            }
        }

        setContentView(R.layout.activity_intro);

        // ini views
        mBtnNext = findViewById(R.id.btn_next);
        mBtnFreeAcc = findViewById(R.id.btn_free_account);
        mBtnCreateAcc = findViewById(R.id.btn_create_account);
        mLayoutContain = findViewById(R.id.containAnimation);
        mTabIndicator = findViewById(R.id.tab_indicator);
        mBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.intro_animation);
        mTxtSkip = findViewById(R.id.tv_skip);

        // fill list screen

        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("A huge amount of books","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.intro_1));
        mList.add(new ScreenItem("Unleash your creativity","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.intro_2));
        mList.add(new ScreenItem("Join community and free share","Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua, consectetur  consectetur adipiscing elit",R.drawable.intro_3));

        mScreenPager =findViewById(R.id.screen_viewpager);
        mIntroViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        mScreenPager.setAdapter(mIntroViewPagerAdapter);

        mTabIndicator.setupWithViewPager(mScreenPager);

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = mScreenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    mScreenPager.setCurrentItem(position);
                }

                if (position == mList.size()-1) { // when we rech to the last screen
                    loadLastScreen();
                }
            }
        });

        mTabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        mBtnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrefsData(true);
                Intent mainActivity = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });

        mBtnFreeAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePrefsData(false);
                Intent mainActivity = new Intent(getApplicationContext(),HomeActivity.class);
                mainActivity.putExtra(Constants.LOGIN_STATE, false);
                startActivity(mainActivity);
                finish();
            }
        });

        mTxtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScreenPager.setCurrentItem(mList.size());
            }
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("introPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpened",false);
        return  isIntroActivityOpnendBefore;
    }

    private void savePrefsData(boolean isValid) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("introPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.apply();

        SharedPreferences loginPres = getApplicationContext().getSharedPreferences(Constants.LOGIN_STATE, MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginPres.edit();
        editor.putBoolean("isLogin", isValid);
        loginEditor.apply();
    }

    private void loadLastScreen() {
        mBtnNext.setVisibility(View.INVISIBLE);
        mBtnFreeAcc.setVisibility(View.VISIBLE);
        mTxtSkip.setVisibility(View.INVISIBLE);
        mTabIndicator.setVisibility(View.INVISIBLE);
       // mBtnCreateAcc.setAnimation(mBtnAnim);
        mLayoutContain.setVisibility(View.VISIBLE);
         mLayoutContain.setAnimation(mBtnAnim);
    }
}
