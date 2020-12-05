package com.uet.android.mouspad.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PointF;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.uet.android.mouspad.Model.StoryChapter;
import com.uet.android.mouspad.R;

import java.io.Serializable;
import java.util.ArrayList;

public class LayoutUtils {
    public static void inflateRecyclerViewIntoLayout(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager, DividerItemDecoration dividerItemDecoration){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public static ListView inflateListViewStringDialogIntoLayout(Context context, ArrayList<String> arrayListContent, int stringId){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogChapter = layoutInflater.inflate(R.layout.fragment_list_dialog,null);
        ListView listView = dialogChapter.findViewById(R.id.listViewDialog);
        TextView textView = dialogChapter.findViewById(R.id.txtNameListDialog);
        textView.setText(context.getResources().getString(stringId));
        ArrayAdapter<String> arrayAdapter;
        ArrayList<String> arrayList = arrayListContent;
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        alertDialog.setView(dialogChapter);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        return listView;
    }

    public static void inflateDataToBlankEditText(Context context, final ArrayList<String> arrayListContent, int stringId){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View blankFragment = layoutInflater.inflate(R.layout.fragment_blank_edittext, null);
        final EditText editText = blankFragment.findViewById(R.id.editBlankEditText);
        Button positiveButton = blankFragment.findViewById(R.id.btnPositiveBlankEditText);
        Button negativeButton = blankFragment.findViewById(R.id.btnNegativeBlankEditText);
        Editable editable = editText.getText();
        for(String string : arrayListContent){
            CharSequence charSequence = string.subSequence(0, string.length()) + ", ";
            editable.append(charSequence);
        }
        alertDialog.setView(blankFragment);
        final  AlertDialog dialogParam = alertDialog.create();
        dialogParam.show();

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogParam.hide();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WidgetsUtils.splitStringToWords(editText.getText().toString(), arrayListContent);
                dialogParam.hide();
            }
        });
    }

    public static ListView inflateListViewDataDialogIntoLayout(Context context, ArrayList<String> arrayListContent, int stringId){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogChapter = layoutInflater.inflate(R.layout.fragment_list_dialog,null);
        ListView listView = dialogChapter.findViewById(R.id.listViewDialog);
        TextView textView = dialogChapter.findViewById(R.id.txtNameListDialog);
        textView.setText(context.getResources().getString(stringId));
        ArrayAdapter<String> arrayAdapter;
       // ArrayList<StoryChapter> arrayList = arrayListContent;
       // ArrayList<String> titles = new ArrayList<>();
//        for(StoryChapter storyChapter: arrayList){
//            titles.add(storyChapter.getTitle());
//        }
        ArrayList<String> titles = arrayListContent;
        Log.d("Storyuser size", String.valueOf(titles.size()));
        arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        alertDialog.setView(dialogChapter);
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        return listView;
    }

    public static class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager {

        public LinearLayoutManagerWithSmoothScroller(Context context) {
            super(context, VERTICAL, false);
        }

        public LinearLayoutManagerWithSmoothScroller(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                           int position) {
            RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }

        private class TopSnappedSmoothScroller extends LinearSmoothScroller {
            public TopSnappedSmoothScroller(Context context) {
                super(context);
            }

            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return LinearLayoutManagerWithSmoothScroller.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected int getVerticalSnapPreference() {
                return SNAP_TO_START;
            }
        }
    }

    public static class Constant implements Serializable {
        public static int nav_clicked = 0;
        public static Boolean isNavClicked = false;
        public static Boolean isToggle = true;
        public static int color = 0xffFFC107;
        public static int colorPrimaryDark = 0xffFA4D16;
        public static int colorPrimary = 0xffFF9800;
        public static int theme = R.style.AppThemeNoActionBar;
    }

    public static class Method {
        public static void setColorTheme(){
            switch (Constant.color) {
                case 0xffF44336:
                    Constant.theme = R.style.AppTheme_red;
                    Constant.colorPrimaryDark = 0xffDF0000;
                    Constant.colorPrimary = 0xffff0000;
                    break;
                case 0xffE91E63:
                    Constant.theme = R.style.AppTheme_pink;
                    Constant.colorPrimaryDark = 0xffCB1755;
                    Constant.colorPrimary = 0xffe91a63;
                    break;
                case 0xff9C27B0:
                    Constant.theme = R.style.AppTheme_darkpink;
                    Constant.colorPrimaryDark = 0xff7a1ea1;
                    Constant.colorPrimary = 0xff9b26af;
                    break;
                case 0xff673AB7:
                    Constant.theme = R.style.AppTheme_violet;
                    Constant.colorPrimaryDark = 0xff502ca7;
                    Constant.colorPrimary = 0xff6639b6;
                    break;
                case 0xff3F51B5:
                    Constant.theme = R.style.AppTheme_blue;
                    Constant.colorPrimaryDark = 0xff3445A2;
                    Constant.colorPrimary = 0xff3F51B5;
                    break;
                case 0xff03A9F4:
                    Constant.theme = R.style.AppTheme_skyeblue;
                    Constant.colorPrimaryDark = 0xff0374A6;
                    Constant.colorPrimary = 0xff0E97D5;
                    break;
                case 0xff4CAF50:
                    Constant.theme = R.style.AppTheme_green;
                    Constant.colorPrimaryDark = 0xff419744;
                    Constant.colorPrimary = 0xf4CAF50;
                    break;
                case 0xffFF9800:
                    Constant.theme = R.style.AppThemeNoActionBar;
                    Constant.colorPrimaryDark = 0xffFA4D16;
                    Constant.colorPrimary = 0xffFF9800;
                    break;
                case 0xff9E9E9E:
                    Constant.theme = R.style.AppTheme_grey;
                    Constant.colorPrimaryDark = 0xff63453B;
                    Constant.colorPrimary = 0xff686868;
                    break;
                case 0xff795548:
                    Constant.theme = R.style.AppTheme_brown;
                    Constant.colorPrimaryDark = 0xff63453B;
                    Constant.colorPrimary = 0xff795548;
                    break;
                default:
                    Constant.theme = R.style.AppThemeNoActionBar;
                    Constant.colorPrimaryDark = 0xffFA4D16;
                    Constant.colorPrimary = 0xffFF9800;
                    break;
            }
        }
    }

    public static void changeStatusBarBackgroundColor(Activity activity){
        Window window = activity.getWindow();

    // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

    // finally change the color
        window.setStatusBarColor(Constant.colorPrimaryDark);
    }



}
