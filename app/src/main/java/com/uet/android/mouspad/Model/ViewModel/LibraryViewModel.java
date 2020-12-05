package com.uet.android.mouspad.Model.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.Service.Notifications.Token;

import java.util.List;

public class LibraryViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {
    private MutableLiveData<LibraryStoryModel> libraryModelData = new MutableLiveData<>();

    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);

    public LiveData<LibraryStoryModel> getModelData() {
        return libraryModelData;
    }

    public LibraryViewModel(){
        firebaseRepository.getStoryLibraryData();
    }

    @Override
    public void categoryListDataAdded(List<CategoryModel> categoryModels) {
    }

    @Override
    public void libraryDataAdded(LibraryStoryModel libraryStoryModel) {
        this.libraryModelData.setValue(libraryStoryModel);
    }

    @Override
    public void tokenDataAdded(List<Token> tokenModel, List<Boolean> flNotification, List<User> userFls) {

    }

    @Override
    public void onError(Exception e) {

    }
}
