package com.uet.android.mouspad.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.uet.android.mouspad.Model.ViewModel.LibraryChapterModel;
import com.uet.android.mouspad.Model.ViewModel.LibraryStoryModel;
import com.uet.android.mouspad.Model.LibraryItem;
import com.uet.android.mouspad.Model.Story;
import com.uet.android.mouspad.Model.StoryChapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public final class DataUtils {
    public static void saveToInternalStorage(Context context, LibraryStoryModel libraryStoryModel, String filename) {
        try {
            Log.d("Savetofile", "start");

            JSONArray root = new JSONArray();

            for(int i = 0 ;i <  libraryStoryModel.getStories().size(); i ++){

                Story story = libraryStoryModel.getStories().get(i);
                JSONObject storyJS = story.toJSON();
                Log.d("Savetofile",storyJS.toString() );

                JSONArray chapterJSArray = new JSONArray();
                JSONObject libraryItemJS = libraryStoryModel.getLibraryItems().get(i).toJSON();
                storyJS.put("library_item", libraryItemJS);
                for(StoryChapter storyChapter: libraryStoryModel.getLibraryChapterModel().get(i).getStoryChapters()){
                    JSONObject chapterJS = storyChapter.toJSON();
                    chapterJSArray.put(chapterJS);
                }
                storyJS.put(story.getStory_id(), chapterJSArray);
                root.put(storyJS);
            }
            Writer writer = null;
            try {
                OutputStream out = context
                        .openFileOutput("Savefile", Context.MODE_PRIVATE);
                writer = new OutputStreamWriter(out);
                writer.write(root.toString());
            } finally {
                if (writer != null)
                    writer.close();
            }
            Log.d("Savetofile", root.toString());
        } catch (Exception e) {
            Log.e("Save", " got error saving: ", e);
        }
    }

    public static LibraryStoryModel loadDataFromInternalStorage(Context context, LibraryStoryModel libraryStoryModel) throws IOException, JSONException {
        BufferedReader reader = null;
        try {
            InputStream in = context.openFileInput("Savefile");
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();

            Log.d("LoadStory", array.toString());
            Log.d("LoadStory", String.valueOf(array.length()));

            ArrayList<Story> storyArrayList = new ArrayList<>();
            ArrayList<LibraryItem> itemArrayList = new ArrayList<>();


            for (int i = 0; i < array.length(); i++) {
                JSONObject storyJS = array.getJSONObject(i);
                Story story = new Story(storyJS);
                storyArrayList.add(story);
                libraryStoryModel.getStories().add(story);

                JSONObject libraryJS = storyJS.getJSONObject("library_item");
                LibraryItem libraryItem = new LibraryItem(libraryJS);
                itemArrayList.add(libraryItem);
                libraryStoryModel.getLibraryItems().add(libraryItem);

                String story_id = story.getStory_id();
                JSONArray chapterArray = storyJS.getJSONArray(story_id);
                if(chapterArray != null){
                    ArrayList<StoryChapter> storyChapterArrayList = new ArrayList<>();
                    for(int j = 0; j <chapterArray.length(); j ++){
                        JSONObject chapterJS = chapterArray.getJSONObject(j);
                        StoryChapter storyChapter = new StoryChapter(chapterJS);
                        storyChapterArrayList.add(storyChapter);
                    }
                    LibraryChapterModel libraryChapterModel = new LibraryChapterModel.Builder().
                            setStoryChapters(storyChapterArrayList).build();
                    libraryStoryModel.getLibraryChapterModel().add(libraryChapterModel);
                }
            }
        } catch (FileNotFoundException e) {
            Log.d("LoadS e", e.getMessage());
        } finally {
            if (reader != null)
                reader.close();
        }
        return libraryStoryModel;
    }

    public static int checkFileExists(String directory, String file) {
        File dir = new File(directory);
        File[] dir_contents = dir.listFiles();
        if(dir_contents != null){
            for(int i = 0; i<dir_contents.length;i++) {
                boolean isExist = new File(directory, file).exists();
                if(isExist)
                    return Constants.FILE_EXISTS;
            }
        }
        return Constants.FILE_DOES_NOT_EXIST;
    }

    public static void deleteFile(String directory, String filename){
        File dir = new File(directory);
        File[] dir_contents = dir.listFiles();
        if(dir_contents != null){
            for(int i = 0; i<dir_contents.length;i++) {
                File file = new File(directory, filename);
                boolean isExist = new File(directory, filename).exists();
                if(isExist){
                    file.delete();
                }
            }
        }

        if(dir.delete()){
           Log.d("Deletebook", "successfully");
        }else {
            Log.d("Deletebook", "failed");
        }
    }

    public static void printKeyHash(Context context) throws NameNotFoundException, NoSuchAlgorithmException {
        PackageInfo info = context.getPackageManager().getPackageInfo("com.uet.android.mouspad"
                            , PackageManager.GET_SIGNATURES);
        for(Signature signature: info.signatures){
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(signature.toByteArray());
            Log.d("KeyHash", Base64.encodeToString(messageDigest.digest(),Base64.DEFAULT));
        }
    }
}
