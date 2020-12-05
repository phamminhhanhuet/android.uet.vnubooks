package com.uet.android.mouspad.Model.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.Service.Notifications.Token;

import java.util.List;

public class TokenFlViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {
    private MutableLiveData<List<Token>> tokenModelData  = new MutableLiveData<>();
    private MutableLiveData<List<Boolean>> flNotifcationData = new MutableLiveData<>();
    private MutableLiveData<List<User>> userFollowData = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);

    public LiveData<List<Token>> getTokensData() {
        return tokenModelData;
    }

    public LiveData<List<Boolean>> getNotificationData() {
        return flNotifcationData;
    }

    public LiveData<List<User>> getUsersFlData() {
        return userFollowData;
    }

    public TokenFlViewModel(){
        firebaseRepository.getTokensFollowing(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    public void categoryListDataAdded(List<CategoryModel> categoryModels) {

    }

    @Override
    public void libraryDataAdded(LibraryStoryModel libraryStoryModel) {

    }

    @Override
    public void tokenDataAdded(List<Token> tokenModel, List<Boolean> flNotification, List<User> userFls) {
        this.tokenModelData.setValue(tokenModel);
        this.flNotifcationData.setValue(flNotification);
        this.userFollowData.setValue(userFls);
    }


    @Override
    public void onError(Exception e) {

    }
}
