package com.uet.android.mouspad.Activity.Authorize;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.uet.android.mouspad.Activity.HomeActivity;
import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.ConnectionUtils;
import com.uet.android.mouspad.Utils.Constants;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mEditUser, mEditPassword;
    private SignInButton mButtonLoginGoogle;
    private LoginButton mButtonLoginFacebook;
    private ImageView mButtonGoggleCustom, mButtonFacebookCustom;

    private Button mButtonLogin, mButtonRegister;
    private ProgressBar mProgressBar;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference ;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    public  static int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
        MappingWidgets();
        initData();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if(currentUser != null){
            sendToHome();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }

    }

    private void MappingWidgets() {
        mEditUser =findViewById(R.id.editUserLogin);
        mEditPassword = findViewById(R.id.editPasswordLogin);
        mButtonLogin = findViewById(R.id.buttonLogin);
        mButtonRegister = findViewById(R.id.buttonLoginRegister);
        mProgressBar = findViewById(R.id.progressBarLogin);
        mButtonLoginGoogle = findViewById(R.id.login_google);
        mButtonLoginFacebook = findViewById(R.id.login_facebook);
        mButtonFacebookCustom = findViewById(R.id.btn_facebook_custom_login);
        mButtonGoggleCustom = findViewById(R.id.btn_google_custom_login);
    }

    private void initData() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initView() {
        mEditUser.addTextChangedListener(new TextWatcherSignIn());
        mEditPassword.addTextChangedListener(new TextWatcherSignIn());

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userInfo = mEditUser.getText().toString();
                String password = mEditPassword.getText().toString();
                if(!TextUtils.isEmpty(userInfo) || !TextUtils.isEmpty(password)){
                    mProgressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }, 2000);
                    mFirebaseAuth.signInWithEmailAndPassword(userInfo, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        updateUiWithUser(userInfo);
                                        sendToHome();

                                    } else {
                                        mProgressBar.setVisibility(View.GONE);
                                        String e = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, e, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "All fields can't not be null!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        //google
        mButtonLoginGoogle.setSize(SignInButton.SIZE_STANDARD);
        mButtonLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        mButtonGoggleCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                mButtonLoginGoogle.performClick();
            }
        });

        //facebook
        mButtonLoginFacebook.setReadPermissions(Arrays.asList(Constants.EMAIL));
        mButtonLoginFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String imgUrl = "https://graph.facebook.com/"
                        + loginResult.getAccessToken().getUserId()
                        + "/picture?return_ss1_resources=1";
                Log.d("Facebook", imgUrl);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        mButtonFacebookCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonLoginFacebook.performClick();
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                        } else {
                        }

                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void sendToHome(){
        SharedPreferences pref = getSharedPreferences("introPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened",true);
        editor.apply();

        SharedPreferences loginPres =getSharedPreferences(Constants.LOGIN_STATE, MODE_PRIVATE);
        SharedPreferences.Editor loginEditor = loginPres.edit();
        editor.putBoolean("isLogin", true);
        loginEditor.apply();

        ConnectionUtils.isLoginValid = true;

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra(Constants.LOGIN_STATE, true);
        startActivity(intent);
        finish();
    }

    private boolean validate() {
        boolean valid = true;
        String email = mEditUser.getText().toString();
        String password = mEditPassword.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEditUser.setError("enter a valid email address");
            valid = false;
        } else {
            mEditUser.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mEditPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mEditPassword.setError(null);
        }

        return valid;
    }

    private void updateUiWithUser(String username) {
        String welcome = getString(R.string.welcome) + username;
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private class TextWatcherSignIn implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            validate();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}