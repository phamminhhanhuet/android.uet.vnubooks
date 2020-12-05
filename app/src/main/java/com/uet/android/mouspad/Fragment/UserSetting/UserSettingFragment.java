package com.uet.android.mouspad.Fragment.UserSetting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uet.android.mouspad.Activity.Authorize.LoginActivity;
import com.uet.android.mouspad.Activity.IntroActivity;
import com.uet.android.mouspad.Activity.UserSetting.AboutUsActivity;
import com.uet.android.mouspad.Activity.UserSetting.AccountSettingActivity;
import com.uet.android.mouspad.Activity.UserSetting.CustomizeThemeActivity;
import com.uet.android.mouspad.Activity.UserSetting.DarkModeActivity;
import com.uet.android.mouspad.Activity.UserSetting.HelpSupportActivity;
import com.uet.android.mouspad.Activity.UserSetting.NotificationSettingActivity;
import com.uet.android.mouspad.Adapter.ViewHolder.RecyclerViewHolder;
import com.uet.android.mouspad.EventInterface.ItemClickListener;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class UserSettingFragment extends Fragment {

    private Toolbar mToolbar;
    private ArrayList<RecyclerView>recyclerViews;

    private String mUserId;
    private User mUser;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFirebaseAuth;

    public UserSettingFragment() {

    }

    public static UserSettingFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_ID, userId);
        UserSettingFragment fragment = new UserSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mUserId = getArguments().getString(Constants.USER_ID);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mUserId = mFirebaseAuth.getCurrentUser().getUid();
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_user_setting, container, false);
        MappingWidgets(view);
        initData();
        ActionToolbar();
        initView(view);
        return view;
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarUserSetting);
        recyclerViews = new ArrayList<>();
    }

    private void initData() {
//        for(User user : UserModel.getInstance(getContext()).getUser()){
//            if(user.getId().equals(mUserId)){
//                mUser = user;
//                break;
//            }
//        }
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.text_setting);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimaryDark);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorThemeWhite));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initView(View view) {
        recyclerViews.add((RecyclerView) view.findViewById(R.id.recyclerViewUserSetting_1));
        recyclerViews.add((RecyclerView) view.findViewById(R.id.recyclerViewUserSetting_2));
        recyclerViews.add((RecyclerView) view.findViewById(R.id.recyclerViewUserSetting_3));
        ArrayList<String> listItems = null ;

        for(int i = 0 ;i < recyclerViews.size(); i ++){
            if(i ==0){
                listItems = new ArrayList<>();
                listItems.add(getString(R.string.text_account_settings));
                listItems.add(getString(R.string.text_notification));
                listItems.add(getString(R.string.text_customize_theme));
                listItems.add(getString(R.string.text_dark_mode));
            } else if(i == 1){
                listItems = new ArrayList<>();
                listItems.add(getString(R.string.text_language));
            } else if(i ==2){
                listItems = new ArrayList<>();
                listItems.add(getString(R.string.text_about_us));
                listItems.add(getString(R.string.text_help_support));
                listItems.add(getString(R.string.text_log_out));
            }
            recyclerViews.get(i).setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),linearLayoutManager.getOrientation());
            recyclerViews.get(i).setLayoutManager(linearLayoutManager);
            recyclerViews.get(i).addItemDecoration(dividerItemDecoration);
            UserSettingAdapter userSettingAdapter = new UserSettingAdapter(listItems, getContext());
            recyclerViews.get(i).setAdapter(userSettingAdapter);
        }
    }

    class UserSettingAdapter extends RecyclerView.Adapter<ViewHolder>{
        ArrayList<String> arrayList;
        Context context;

        public UserSettingAdapter(ArrayList<String> arrayList, Context context) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = layoutInflater.inflate(R.layout.item_user_setting, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.textView.setText(arrayList.get(position));
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    int numberOfItem = arrayList.size();
                    if(numberOfItem == 4){
                        switch (position){
                            case 0:
                                Intent intent = new Intent(getContext(), AccountSettingActivity.class);
                                intent.putExtra(Constants.USER_ID, mUserId);
                                startActivity(intent);
                                break;
                            case 1:
                                startActivity(new Intent(getContext(), NotificationSettingActivity.class));
                                break;
                            case 2:
                                startActivity(new Intent(getContext(), CustomizeThemeActivity.class));
                                break;
                            case 3:
                                startActivity(new Intent(getContext(), DarkModeActivity.class));
                                break;
                        }
                    } else if (numberOfItem ==1){
                            //LayoutUtils.inflateListViewDialogIntoLayout(getContext(), StoryModel.getInstance(getContext()).getStories().get(0).getChapters(), R.string.text_language);
                        Toast.makeText(getContext(), "Language", Toast.LENGTH_SHORT).show();


                    } else if(numberOfItem == 3){
                        switch (position){
                            case 0:
                                startActivity(new Intent(getContext(), AboutUsActivity.class));
                                break;
                            case 1:
                                startActivity(new Intent(getContext(), HelpSupportActivity.class));
                                break;
                            case 2:
                                mFirebaseAuth.signOut();

                                SharedPreferences pref = getContext().getSharedPreferences("introPrefs",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("isIntroOpened",false);
                                editor.apply();

                                SharedPreferences loginPres = getContext().getSharedPreferences(Constants.LOGIN_STATE, MODE_PRIVATE);
                                SharedPreferences.Editor loginEditor = loginPres.edit();
                                editor.putBoolean("isLogin", false);
                                loginEditor.apply();

                                Intent intent = new Intent(getActivity(), IntroActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                        }
                    }
                }
            });
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int numberOfItem = arrayList.size();
                    if(numberOfItem == 4){
                        switch (position){
                            case 0:
                                Intent intent = new Intent(getContext(), AccountSettingActivity.class);
                                intent.putExtra(Constants.USER_ID, mUserId);
                                startActivity(intent);
                                break;
                            case 1:
                                startActivity(new Intent(getContext(), NotificationSettingActivity.class));
                                break;
                            case 2:
                                startActivity(new Intent(getContext(), CustomizeThemeActivity.class));
                                break;
                            case 3:
                                startActivity(new Intent(getContext(), DarkModeActivity.class));
                                break;
                        }
                    } else if (numberOfItem ==1){
                        Toast.makeText(getContext(), "Language", Toast.LENGTH_SHORT).show();
                    } else if(numberOfItem == 3){
                        switch (position){
                            case 0:
                                startActivity(new Intent(getContext(), AboutUsActivity.class));
                                break;
                            case 1:
                                startActivity(new Intent(getContext(), HelpSupportActivity.class));
                                break;
                            case 2:
                                SharedPreferences pref = getContext().getSharedPreferences("introPrefs",MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("isIntroOpened",false);
                                editor.apply();

                                SharedPreferences loginPres = getContext().getSharedPreferences(Constants.LOGIN_STATE, MODE_PRIVATE);
                                SharedPreferences.Editor loginEditor = loginPres.edit();
                                editor.putBoolean("isLogin", false);
                                loginEditor.apply();

                                mFirebaseAuth.signOut();
                                Intent intent = new Intent(getActivity(), IntroActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    class ViewHolder extends RecyclerViewHolder{

        TextView textView;
        CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewItemUserSetting);
            textView = itemView.findViewById(R.id.txtItemUserSetting);
        }
    }
}