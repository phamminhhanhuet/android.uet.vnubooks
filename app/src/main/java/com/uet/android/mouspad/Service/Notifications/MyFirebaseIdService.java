package com.uet.android.mouspad.Service.Notifications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser != null){
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
       // Token token = new Token(refreshToken);
        //reference.child(firebaseUser.getUid()).setValue(token);
        Map<String, Object> map = new HashMap<>();
        Token token = new Token(refreshToken);
        map.put("token", refreshToken);
//        reference.child(mFirebaseAuth.getCurrentUser().getUid()).setValue(token1);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("tokens/").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(map);
    }
}
