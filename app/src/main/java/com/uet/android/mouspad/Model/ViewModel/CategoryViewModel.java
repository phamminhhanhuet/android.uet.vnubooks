package com.uet.android.mouspad.Model.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uet.android.mouspad.Model.User;
import com.uet.android.mouspad.Service.Notifications.Token;

import java.util.List;

public class CategoryViewModel extends ViewModel implements FirebaseRepository.OnFirestoreTaskComplete {
    private MutableLiveData<List<CategoryModel>> categoryModelData  = new MutableLiveData<>();
    private FirebaseRepository firebaseRepository = new FirebaseRepository(this);

    public LiveData<List<CategoryModel>> getCategoryData() {
        return categoryModelData;
    }

    public CategoryViewModel(){
        firebaseRepository.getStoryCategoryData();
    }
    @Override
    public void categoryListDataAdded(List<CategoryModel> categoryModels) {
        categoryModelData.setValue(categoryModels);
    }

    @Override
    public void libraryDataAdded(LibraryStoryModel libraryStoryModel) {
    }

    @Override
    public void tokenDataAdded(List<Token> tokenModel, List<Boolean> flNotification, List<User> userFls) {

    }

    @Override
    public void onError(Exception e) {

    }
}
