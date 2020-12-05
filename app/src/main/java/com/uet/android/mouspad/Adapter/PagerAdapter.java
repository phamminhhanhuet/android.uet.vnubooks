package com.uet.android.mouspad.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.uet.android.mouspad.Fragment.Home.HomeInterfaceFragment;
import com.uet.android.mouspad.Fragment.Home.LibraryFragment;
import com.uet.android.mouspad.Fragment.Home.NoInternetFragment;
import com.uet.android.mouspad.Fragment.Home.SearchFragment;
import com.uet.android.mouspad.Fragment.Home.Updates.NotificationFragment;
import com.uet.android.mouspad.Fragment.Home.UpdatesFragment;
import com.uet.android.mouspad.Fragment.Inbox.InboxListFragment;
import com.uet.android.mouspad.Fragment.UserProfile.FollowingFragment;
import com.uet.android.mouspad.Fragment.UserProfile.FollwersFragment;
import com.uet.android.mouspad.Fragment.UserProfile.UserArchiveFragment;
import com.uet.android.mouspad.Fragment.UserProfile.UserConversationFragment;
import com.uet.android.mouspad.Fragment.UserProfile.UserWorksFragment;
import com.uet.android.mouspad.Fragment.Write.DraftStoryFragment;
import com.uet.android.mouspad.Fragment.Write.PublishedStoryFragment;
import com.uet.android.mouspad.Fragment.WriteStudioFragment;
import com.uet.android.mouspad.Model.FollowItem;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> mTabTitiles;
    private Context mContext;
    private int mActionRequest;
    private String mUserId ;

    private ArrayList<FollowItem> mFollowings , mFollowers;

    public void setFollowings(ArrayList<FollowItem> mFollowings) {
        this.mFollowings = mFollowings;
    }

    public void setFollowers(ArrayList<FollowItem> mFollowers) {
        this.mFollowers = mFollowers;
    }

    public void setUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public PagerAdapter(Context context, @NonNull FragmentManager fm, int actionRequest) {
        super(fm);
        this.mContext = context;
        this.mActionRequest = actionRequest;
        mTabTitiles = new ArrayList<>();
        switch (actionRequest){
            case Constants.PAGER_ADAPTER_HOME_REQUEST:
                mTabTitiles.add("Home");
                mTabTitiles.add("Search");
                mTabTitiles.add("Library");
                mTabTitiles.add("Write");
                mTabTitiles.add("Updates");
                break;
            case Constants.PAGER_ADAPTER_UPDATES_REQUEST:
                mTabTitiles.add("Notifications");
                mTabTitiles.add("Messages");
                break;
            case Constants.PAGER_ADAPTER_WRITE_STUDIO_REQUEST:
                mTabTitiles.add("Published");
                mTabTitiles.add("Drafts");
                break;
            case Constants.PAGER_ADAPTER_USER_REQUEST:
                mTabTitiles.add("Works");
                mTabTitiles.add("Archives");
                mTabTitiles.add("Boards");
                break;
            case Constants.PAGER_ADAPTER_FOLLOW_REQUEST:
                mTabTitiles.add("Following");
                mTabTitiles.add("Followers");
                break;
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (mActionRequest){
            case Constants.PAGER_ADAPTER_HOME_REQUEST:
                if(ConnectionUtils.isConnectingInternet){
                    switch (position){
                        case 0:
                            return HomeInterfaceFragment.newInstance();
                        case 1:
                            return SearchFragment.newInstance();
                        case 2:
                            return LibraryFragment.newInstance();
                        case 3:
                            return WriteStudioFragment.newInstance();
                        case 4:
                            return UpdatesFragment.newInstance();
                    }
                } else {
                    switch (position){
                        case 0: case 1: case 3: case 4:
                            return NoInternetFragment.newInstance();
                        case 2:
                            return LibraryFragment.newInstance();
                    }
                }
                break;
            case Constants.PAGER_ADAPTER_UPDATES_REQUEST:
                switch (position){
                    case 0:
                        return NotificationFragment.newInstance();
                    case 1:
                        return InboxListFragment.newInstance();
                }
                break;
            case Constants.PAGER_ADAPTER_WRITE_STUDIO_REQUEST:
                switch (position){
                    case 0:
                        return PublishedStoryFragment.newInstance();
                    case 1:
                        return DraftStoryFragment.newInstance();
                }
                break;
            case Constants.PAGER_ADAPTER_USER_REQUEST:
                switch (position){
                    case 0:
                        return UserWorksFragment.newInstance(mUserId);
                    case 1:
                        return UserArchiveFragment.newInstance(mUserId);
                    case 2:
                        return UserConversationFragment.newInstance(mUserId);
                }
                break;
            case Constants.PAGER_ADAPTER_FOLLOW_REQUEST:
                switch (position){
                    case 0:
                        return FollowingFragment.newInstance(mFollowings);
                    case 1:
                        return FollwersFragment.newInstance(mFollowers);
                }
                break;
        }
        return null;
    }
    public View getTabView(int position){
        switch (mActionRequest){
            case Constants.PAGER_ADAPTER_HOME_REQUEST:
            case Constants.PAGER_ADAPTER_UPDATES_REQUEST:
            case Constants.PAGER_ADAPTER_WRITE_STUDIO_REQUEST:
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View tab =layoutInflater.inflate(R.layout.tab_custom_home,null);
                TextView textView = tab.findViewById(R.id.txtCustomTab);
                textView.setText(mTabTitiles.get(position));
                return tab;
        }
        return null;
    }


    @Override
    public int getCount() {
        return mTabTitiles.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitiles.get(position);
    }
}
