package com.uet.android.mouspad.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uet.android.mouspad.Activity.BlankTextActivity;
import com.uet.android.mouspad.Activity.BookPerfrom.WriteStudioActivity;
import com.uet.android.mouspad.Adapter.PagerAdapter;
import com.uet.android.mouspad.Fragment.Home.HomeInterfaceFragment;
import com.uet.android.mouspad.Fragment.Home.LibraryFragment;
import com.uet.android.mouspad.Fragment.Home.NoInternetFragment;
import com.uet.android.mouspad.Fragment.Home.SearchFragment;
import com.uet.android.mouspad.Fragment.Home.UpdatesFragment;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ActivityUtils;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.LayoutUtils;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;
import static com.uet.android.mouspad.Utils.ConnectionUtils.isLoginValid;

public class HomeFragment extends Fragment {

    private ViewPager mViewPager;
    private PagerAdapter mPaperAdapter;
    private BottomNavigationView mNavigationHome;
    public static TabLayout mTabLayout;

    //initial all fragments
    private ArrayList<Fragment> mFragments;

    private HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        MappingWidgets(view);
        initData();
        ActionToolbar();
        initView(view);

        if(isLoginValid){
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w("TOkenGet", "getInstanceId failed", task.getException());
                                return;
                            }
                            String token = task.getResult().getToken();
                            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
                        }
                    });
            FirebaseMessaging.getInstance().subscribeToTopic("allDevices");
        }
        return view;
    }


    private void MappingWidgets(View view){
        mViewPager = view.findViewById(R.id.viewPagerHome);
        mPaperAdapter = new PagerAdapter(getContext(), getActivity().getSupportFragmentManager(), Constants.PAGER_ADAPTER_HOME_REQUEST);
        mViewPager.setAdapter(mPaperAdapter);
        mTabLayout = view.findViewById(R.id.tabLayoutHome);

        mNavigationHome = view.findViewById(R.id.navigationHome);
        mNavigationHome.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void initData() {
        HomeInterfaceFragment homeInterfaceFragment = HomeInterfaceFragment.newInstance();
        SearchFragment searchFragment = SearchFragment.newInstance();
        UpdatesFragment updatesFragment = UpdatesFragment.newInstance();
        LibraryFragment libraryFragment = LibraryFragment.newInstance();
        NoInternetFragment noInternetFragment = NoInternetFragment.newInstance();

        mFragments = new ArrayList<>();
        mFragments.add(homeInterfaceFragment);
        mFragments.add(searchFragment);
        mFragments.add(libraryFragment);
        mFragments.add(updatesFragment);
        mFragments.add(noInternetFragment);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.linearLayoutContainFragment, homeInterfaceFragment);
        fragmentTransaction.add(R.id.linearLayoutContainFragment, searchFragment);
        fragmentTransaction.add(R.id.linearLayoutContainFragment, libraryFragment);
        fragmentTransaction.add(R.id.linearLayoutContainFragment, updatesFragment);
        fragmentTransaction.add(R.id.linearLayoutContainFragment, noInternetFragment);

        fragmentTransaction.hide(searchFragment);
        fragmentTransaction.hide(libraryFragment);
        fragmentTransaction.hide(updatesFragment);
        if(ConnectionUtils.isConnectingInternet){
            fragmentTransaction.hide(noInternetFragment);
        }else {
            fragmentTransaction.hide(homeInterfaceFragment);
        }
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if(ConnectionUtils.isConnectingInternet){
            if(fragment == mFragments.get(0)){
                fragmentTransaction.hide(mFragments.get(1));
                fragmentTransaction.hide(mFragments.get(2));
                fragmentTransaction.hide(mFragments.get(3));
            }
            if(fragment == mFragments.get(1)){
                fragmentTransaction.hide(mFragments.get(0));
                fragmentTransaction.hide(mFragments.get(2));
                fragmentTransaction.hide(mFragments.get(3));
            }
            if(fragment == mFragments.get(2)){
                fragmentTransaction.hide(mFragments.get(0));
                fragmentTransaction.hide(mFragments.get(1));
                fragmentTransaction.hide(mFragments.get(3));
            }
            if(fragment == mFragments.get(3)){
                fragmentTransaction.hide(mFragments.get(0));
                fragmentTransaction.hide(mFragments.get(1));
                fragmentTransaction.hide(mFragments.get(2));
            }
            fragmentTransaction.hide(mFragments.get(4));
            fragmentTransaction.show(fragment);
        } else {
            if(fragment == mFragments.get(2)){
                fragmentTransaction.hide(mFragments.get(0));
                fragmentTransaction.hide(mFragments.get(1));
                fragmentTransaction.show(mFragments.get(2));
                fragmentTransaction.hide(mFragments.get(3));
                fragmentTransaction.hide(mFragments.get(4));
            } else {
                fragmentTransaction.hide(mFragments.get(0));
                fragmentTransaction.hide(mFragments.get(1));
                fragmentTransaction.hide(mFragments.get(2));
                fragmentTransaction.hide(mFragments.get(3));
                fragmentTransaction.show(mFragments.get(4));
            }
        }
        fragmentTransaction.commit();
    }

    private void ActionToolbar(){
    }

    private void initView(View view){
        HomeInterfaceFragment homeInterfaceFragment = HomeInterfaceFragment.newInstance();
        ActivityUtils.addFragmentToContext(getContext(),homeInterfaceFragment, R.id.linearLayoutContainFragment);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_home:
                    replaceFragment(mFragments.get(0));
                    return true;
                case R.id.action_search_home:
                    replaceFragment(mFragments.get(1));
                    return true;
                case R.id.action_libary_home:
                    replaceFragment(mFragments.get(2));
                    return true;
                case R.id.action_write_home:
                    if(isLoginValid){
                        if(ConnectionUtils.isConnectingInternet){
                            startActivity(new Intent(getContext(), WriteStudioActivity.class));
                        }
                    }
                     else {
                        //replaceFragment(mFragments.get(3));
                    }
                    return true;
                case R.id.action_updates_home:
                    if(isLoginValid){
                        replaceFragment(mFragments.get(3));
                    } else {
                        Intent intent = new Intent(getActivity(), BlankTextActivity.class);
                        intent.putExtra(Constants.READING_MODE, getString(R.string.text_notification));
                        startActivity(intent);
                    }
                    return true;
            }
            return false;
        }
    };
}
