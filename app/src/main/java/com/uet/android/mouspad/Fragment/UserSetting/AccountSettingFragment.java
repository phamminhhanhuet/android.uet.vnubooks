package com.uet.android.mouspad.Fragment.UserSetting;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uet.android.mouspad.Activity.MapsActivity;
import com.uet.android.mouspad.Adapter.InformationActionAdapter;
import com.uet.android.mouspad.EventInterface.StringValueCallback;
import com.uet.android.mouspad.Model.InformationAction;
import com.uet.android.mouspad.Model.RepoLocation;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;
import com.uet.android.mouspad.Utils.LayoutUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountSettingFragment extends Fragment implements StringValueCallback {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView_Avatar, mRecyclerViewBackground;
    private EditText mEditAbout, mEditUsername, mEditFullName, mEditGender, mEditEmail, mEditPassword, mEditBirthday, mEditLocation;
    private TextView mTextLocation;
    private CardView mCardViewUserName, mCardViewFullName, mCardViewGender, mCardViewBirthday, mCardViewLocation, mCardViewEmail, mCardViewPassword, mCardViewForgotPassword;
    private CheckBox mCheckFacebook, mCheckGoogle;
    private LoginButton mButtonLoginFacebook;
    private SignInButton mButtonLoginGoogle;

    private User mUser;
    private String mUserId;
    private ArrayList<InformationAction> mInformationActions_avatar;
    private ArrayList<InformationAction> mInformationActions_background;

    private InformationActionAdapter mInformationActionAdapter_avatar;
    private InformationActionAdapter mInformationActionAdapter_background;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private AuthCredential mAuthCredentialGoogle = null;

    public  static int RC_SIGN_IN = 123;
    public static boolean isUpdateAdapter = false;

    //firebase
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;
    private String user_id, fullname, account, description, background , avatar, email, gender;
    private Date birthday;
    private Uri avatarUri, backgroundUri;

    public AccountSettingFragment() {
    }

    public static AccountSettingFragment newInstance(String mUserId) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.USER_ID, mUserId);
        AccountSettingFragment fragment = new AccountSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserId = getArguments().getString(Constants.USER_ID);
        setHasOptionsMenu(true);
        //google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        //facebook
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutUtils.changeStatusBarBackgroundColor(getActivity());
        getActivity().setTheme(LayoutUtils.Constant.theme);
        View view = inflater.inflate(R.layout.fragment_account_setting, container, false);
        MappingWidgets(view);
        ActionToolbar();
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onDestroy() {
        storeUserInformation(avatarUri,backgroundUri);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mRepoLocation != null){
            mFirebaseFirestore.collection("locations/").document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mRepoLocation = task.getResult().toObject(RepoLocation.class);
                    mEditLocation.setText(mRepoLocation.getDescription());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.GALLERY_REQUEST_CODE_FOR_COVER && data != null){
            isUpdateAdapter = true;
            avatarUri = data.getData();
            final StorageReference image_path = mStorageReference.child("image_avatars")
                    .child(avatarUri.getLastPathSegment());
            image_path.putFile(avatarUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            storeUserInformation(uri, backgroundUri);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String mes = e.getMessage();
                    Toast.makeText(getActivity(), mes , Toast.LENGTH_SHORT).show();
                }
            });

        } else if( requestCode == Constants.GALLERY_REQUEST_CODE_FOR_BACKGROUND && data != null){
            isUpdateAdapter = true;
            backgroundUri = data.getData();
            final StorageReference image_path = mStorageReference.child("image_backgrounds")
                    .child(backgroundUri.getLastPathSegment());
            image_path.putFile(backgroundUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            storeUserInformation(avatarUri, uri);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String mes = e.getMessage();
                    Toast.makeText(getActivity(), mes , Toast.LENGTH_SHORT).show();
                }
            });
        } else if( requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else if(requestCode == Constants.MAP_REQUEST_USER_CODE){
            mFirebaseFirestore.collection("locations/").document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    mRepoLocation = task.getResult().toObject(RepoLocation.class);
                    mEditLocation.setText(mRepoLocation.getDescription());
                }
            });
        }
        initData();
    }

    private void MappingWidgets(View view) {
        mToolbar = view.findViewById(R.id.toolbarAccountSetting);
        mRecyclerView_Avatar = view.findViewById(R.id.recyclerProfileAvatarAccountSetting);
        mRecyclerViewBackground = view.findViewById(R.id.recyclerProfileBackgroundAccountSetting);
        mEditAbout = view.findViewById(R.id.editAboutAccountSetting);
        mEditUsername = view.findViewById(R.id.editUsernameAccountSetting);
        mEditFullName =view.findViewById(R.id.editFullNameAccountSetting);
        mEditEmail = view.findViewById(R.id.editEmailAccountSetting);
        mEditGender = view.findViewById(R.id.editGenderAccountSetting);
        mEditPassword = view.findViewById(R.id.editPasswordAccountSetting);
        mEditLocation = view.findViewById(R.id.editLocationAccountSetting);
        mEditBirthday = view.findViewById(R.id.editBirthdayAccountSetting);
        mTextLocation = view.findViewById(R.id.txtLocationAccountSetting);

        //card view
        mCardViewUserName = view.findViewById(R.id.cardViewUserName);
        mCardViewFullName = view.findViewById(R.id.cardViewFullName);
        mCardViewGender = view.findViewById(R.id.cardViewGender);
        mCardViewBirthday = view.findViewById(R.id.cardViewBirthday);
        mCardViewLocation = view.findViewById(R.id.cardViewLocation);
        mCardViewEmail = view.findViewById(R.id.cardViewEmail);
        mCardViewPassword = view.findViewById(R.id.cardViewPassword);
        mCardViewForgotPassword = view.findViewById(R.id.cardViewForgotPassword);


        mCheckFacebook = view.findViewById(R.id.checkFacebookAccountSetting);
        mCheckGoogle = view.findViewById(R.id.checkGoogleAccountSetting);
        mButtonLoginFacebook = view.findViewById(R.id.login_buttonAccountSetting);
        mButtonLoginGoogle = view.findViewById(R.id.login_googleAccountSetting);
    }

    private RepoLocation mRepoLocation;

    private void initData() {
        user_id = mFirebaseAuth.getCurrentUser().getUid();

        mFirebaseFirestore.collection("users").document(user_id)
                .get().addOnCompleteListener(getActivity(), new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    mUser = task.getResult().toObject(User.class);
                    avatarUri = Uri.parse(mUser.getAvatar());
                    backgroundUri = Uri.parse(mUser.getBackground());
                    InformationAction informationAction = new InformationAction( mUser.getAvatar(), getString(R.string.text_profile_picture), getString(R.string.text_tap_change) , new Date(System.currentTimeMillis()));
                    InformationAction informationAction2 = new InformationAction(mUser.getBackground(), getString(R.string.text_background_picture), getString(R.string.text_tap_change), new Date(System.currentTimeMillis()));
                    mInformationActions_avatar.clear();
                    mInformationActions_background.clear();
                    mInformationActions_avatar.add(informationAction);
                    mInformationActions_background.add(informationAction2);
                    mInformationActionAdapter_avatar.notifyDataSetChanged();
                    mInformationActionAdapter_background.notifyDataSetChanged();

                    mEditAbout.setText(mUser.getDescription());
                    mEditUsername.setText(mUser.getAccount());
                    mEditFullName.setText(mUser.getFullname());
                    mEditGender.setText(mUser.getGender());
                    mEditEmail.setText(mUser.getEmail());
                    birthday = mUser.getBirthday();
                    mEditBirthday.setText(birthday.toString());


                    mFirebaseFirestore.collection("locations").document(mUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult().exists()){
                                mRepoLocation = task.getResult().toObject(RepoLocation.class);
                                String place = mRepoLocation.getDescription();
                                if(place!= null && !place.equals("")){
                                    mEditLocation.setText(place);
                                } else {
                                    mEditLocation.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void ActionToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.text_account_settings);
        mToolbar.setTitleTextColor(LayoutUtils.Constant.colorPrimary);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            birthday = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
            mEditBirthday.setText(birthday.toString());
        }
    };


    private void initView(View view) {
        mCardViewGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> list = new ArrayList<>();
                list.add(getResources().getString(R.string.gender_female));
                list.add(getResources().getString(R.string.gender_male));
                list.add(getResources().getString(R.string.gender_they));
                list.add(getResources().getString(R.string.gender_no));

                PopupMenu popupMenu = new PopupMenu(getContext(), view.findViewById(R.id.txtGenderAccountSetting));
                for(final String string : list){
                    MenuItem m = popupMenu.getMenu().add(string);
                    m.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            mEditGender.setText(string);
                            return true;
                        }
                    });
                }
                popupMenu.show();
            }
        });

        mCardViewBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(birthday);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mCardViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                intent.putExtra(Constants.MAP_REQUEST, Constants.MAP_REQUEST_USER_CODE);
                intent.putExtra(Constants.USER_ID, mUser);
                startActivityForResult(intent, Constants.MAP_REQUEST_USER_CODE);
            }
        });

        if(getFacebookCredentials() != null){
            mCheckFacebook.setChecked(true);
        } else {
            mCheckFacebook.setChecked(false);
        }

        if(getGoogleCredentials() != null){
            mCheckGoogle.setChecked(true);
        } else {
            mCheckGoogle.setChecked(false);
        }

        if(!mCheckFacebook.isChecked()){
            mButtonLoginFacebook.setReadPermissions(Arrays.asList(Constants.EMAIL));
            mButtonLoginFacebook.setFragment(this);
            mButtonLoginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    mEditAbout.setText(mEditAbout.getText().toString() +
                            " User Id: " + loginResult.getAccessToken().getUserId());
                    String imgUrl = "https://graph.facebook.com/"
                            + loginResult.getAccessToken().getUserId()
                            + "/picture?return_ss1_resources=1";
                    Log.d("Facebook", imgUrl);
                    linkAuthWithCredentials(getFacebookCredentials());
                }

                @Override
                public void onCancel() { }
                @Override
                public void onError(FacebookException error) { }
            });

            mCheckFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    mButtonLoginFacebook.performClick();
                    linkAuthWithCredentials(getFacebookCredentials());
                }
            });

        } else {
            mCheckFacebook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    unlinkAuthWithCredentials("facebook.com");
                }
            });
        }

        if(!mCheckGoogle.isChecked()){
            mButtonLoginGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
            mCheckGoogle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
        } else {
            mCheckGoogle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    unlinkAuthWithCredentials("google.com");
                }
            });
        }

        mInformationActions_avatar = new ArrayList<>();
        mInformationActions_background = new ArrayList<>();
        mInformationActionAdapter_avatar = new InformationActionAdapter(mInformationActions_avatar ,getContext(), this);
        mInformationActionAdapter_avatar.setRequestCode(Constants.GALLERY_REQUEST_CODE_FOR_COVER);
        mInformationActionAdapter_background = new InformationActionAdapter(mInformationActions_background,getContext(), this);
        mInformationActionAdapter_background.setRequestCode(Constants.GALLERY_REQUEST_CODE_FOR_BACKGROUND);

        mRecyclerView_Avatar.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_avatar = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView_Avatar.setLayoutManager(linearLayoutManager_avatar);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), linearLayoutManager_avatar.getOrientation());
        mRecyclerView_Avatar.addItemDecoration(dividerItemDecoration);
        mRecyclerView_Avatar.setAdapter(mInformationActionAdapter_avatar);

        mRecyclerViewBackground.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_background = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewBackground.setLayoutManager(linearLayoutManager_background);
        DividerItemDecoration dividerItemDecoration_background = new DividerItemDecoration(getContext(), linearLayoutManager_background.getOrientation());
        mRecyclerViewBackground.addItemDecoration(dividerItemDecoration_background);
        mRecyclerViewBackground.setAdapter(mInformationActionAdapter_background);
    }

    private String ALGOLIA_APPLICATION_API = "WFKPO6G4ZS";
    private String ALGOLIA_ADMIN_API = "92326d76f5c191740a6741af6aebfc7e";

    private void storeUserInformation(Uri uriAvatar, Uri uriBackground){
        String avatar = "";
        String background = "";

        fullname = mEditFullName.getText().toString();
        account = mEditUsername.getText().toString();
        description = mEditAbout.getText().toString();
        email = mEditEmail.getText().toString();
        gender = mEditGender.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("user_id", user_id);
        user.put("fullname", fullname);
        user.put("account", account);
        user.put("description", description);
        user.put("email", email);
        user.put("gender", gender);
        user.put("birthday", birthday);

        avatar = uriAvatar.toString();
        user.put("avatar", avatar);
        background = uriBackground.toString();
        user.put("background", background);

        mFirebaseFirestore.collection("users")
                .document(user_id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }}
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String mes = e.getMessage();
                        Log.d("Exception Firestore", mes);
                    }
                });

        //save agolia
        Client client = new Client(ALGOLIA_APPLICATION_API, ALGOLIA_ADMIN_API);
        final Index indexUser = client.getIndex("firebase_user");
        try {
            JSONObject jsonObjects = new JSONObject().put("userInfo", fullname + " " + account).
                    put("userAccount", account)
                    .put("userFullname", fullname)
                    .put("instanceId", user_id)
                    .put("userAvatar", avatar)
                    .put("userBackground", background)
                    .put("userDes", description)
                    .put("userMail",email)
                    .put("userGender",gender)
                    .put("userBirthday",birthday);
            indexUser.addObjectAsync(jsonObjects, user_id, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuthCredentialGoogle = credential;
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            linkAuthWithCredentials(mAuthCredentialGoogle);
                        } else {
                        }
                    }
                });
    }


    public AuthCredential getGoogleCredentials() {
        final boolean isGoogle = false;
        mFirebaseAuth.getCurrentUser().getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
            @Override
            public void onComplete(@NonNull Task<GetTokenResult> task) {
                String token = task.getResult().getToken();
                AuthCredential credential = GoogleAuthProvider.getCredential(token, null);
                onCallback(credential);
            }
        });
        return mAuthCredentialGoogle;
    }


    public AuthCredential getFacebookCredentials() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if(token != null){
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            return credential;
        }
        return null;
    }

    public AuthCredential getEmailCredentials() {
        String email = "";
        String password = "";
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        return credential;
    }

    public void linkAuthWithCredentials (AuthCredential credential) {
        mFirebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                        }
                    }
                });
    }

    public void unlinkAuthWithCredentials(String customProvideId){
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        List<? extends UserInfo> providerData = user.getProviderData();
        for (UserInfo userInfo : providerData ) {
            String providerId = userInfo.getProviderId();
            if (providerId.equals(customProvideId)) {
                user.unlink(providerId)
                        .addOnCompleteListener(getActivity(),
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                        }
                                    }
                                });
            }
        }
    }

    @Override
    public AuthCredential onCallback(AuthCredential value) {
        mAuthCredentialGoogle = value;
        return value;
    }
}

