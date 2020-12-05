package com.uet.android.mouspad.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;

import com.uet.android.mouspad.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WidgetsUtils {
    public static String getCurrentWordFromEditText(EditText editText) {
        Spannable textSpan = editText.getText();
        final int selection = editText.getSelectionStart();
        final Pattern pattern = Pattern.compile("\\w+");
        final Matcher matcher = pattern.matcher(textSpan);
        int start = 0;
        int end = 0;

        String currentWord = "";
        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
            if (start <= selection && selection <= end) {
                currentWord = textSpan.subSequence(start, end).toString();
                break;
            }
        }
        return currentWord;
    }

    public static void appendImageToEditText(EditText editText, SpannableString spannableString){
        Editable editable = editText.getText();
        editable.insert(editText.getSelectionStart(), spannableString);
    }

    public static SpannableString transIntentDataToSpannableString(Intent data, Activity activity, Uri uri){
        Uri imageUri = data.getData();
        SpannableString spannableString = new SpannableString("abc\n");
        try {
            final InputStream imageStream;
            imageStream = activity.getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            Bitmap bitmap = selectedImage.copy(selectedImage.getConfig(), true);
            String source = uri.toString();
            Bitmap scaledBitmap = scaledBitmap(activity, selectedImage);
            Drawable drawable = new BitmapDrawable(activity.getResources(), scaledBitmap) ;
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable, source, ImageSpan.ALIGN_BASELINE);
            spannableString.setSpan(imageSpan, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ImageSpan transIntentDataToImageSpannable(Intent data, Activity activity, Bitmap bitmap){
        Uri imageUri = data.getData();
        SpannableString spannableString = new SpannableString("abc\n");
        try {
            final InputStream imageStream;
            imageStream = activity.getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            bitmap = selectedImage;
            Drawable drawable = new BitmapDrawable(activity.getResources(), selectedImage) ;
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            spannableString.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            return span;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final Uri getUriToResource(@NonNull Context context, @AnyRes int resId) throws Resources.NotFoundException {
        /** Return a Resources instance for your application's package. */
        Resources res = context.getResources();
        /**
         * Creates a Uri which parses the given encoded URI string.
         * @param uriString an RFC 2396-compliant, encoded URI
         * @throws NullPointerException if uriString is null
         * @return Uri for this given uri string
         */
        Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + res.getResourcePackageName(resId)
                + '/' + res.getResourceTypeName(resId)
                + '/' + res.getResourceEntryName(resId));
        /** return uri */
        return resUri;
    }

    public static void splitStringToWords(String source, ArrayList<String> oldArray){
        ArrayList<String> results = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(source);
        oldArray.clear();
        while (matcher.find()) {
            oldArray.add(matcher.group());
            results.add(matcher.group());
        }
    }

    public static boolean validateEditText(EditText editText, String warn){

            boolean valid = true;
            String comment = editText.getText().toString();

            if (comment.isEmpty() ) {
                editText.setError(warn);
                valid = false;
            } else {
                editText.setError(null);
            }

            return valid;

    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap){
        if(bitmap != null){
            Drawable drawable = new BitmapDrawable(context.getResources(),bitmap) ;
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            Log.e("Html" , "Bitmap not null");
            return drawable;
        } else {
            Log.e("Html" , "Bitmap null");
        }
        return null;
    }

    public static Bitmap scaledBitmap(Activity activity, Bitmap bitmap){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int heightScreen = displayMetrics.heightPixels;
        int widthScreen = displayMetrics.widthPixels;
        float ratio = Math.min(
                (float)  widthScreen/ bitmap.getWidth(),
                (float) heightScreen / bitmap.getHeight());
        if(ratio > 1){
            int width = Math.round((float) 0.75  * bitmap.getWidth() );
            int height = Math.round((float) 0.75  * bitmap.getHeight() );

            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width,
                    height, true);
            return newBitmap;
        } else {
            int width = Math.round((float) 0.75  * bitmap.getWidth() * ratio);
            int height = Math.round((float) 0.75  * bitmap.getHeight() * ratio);

            Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, width,
                    height, true);
            return newBitmap;
        }
    }

    public static String getSelectedWordFromEditText(EditText editText){
        int startSelection=editText.getSelectionStart();
        int endSelection=editText.getSelectionEnd();
        String selectedText = editText.getText().subSequence(startSelection, endSelection).toString();
        return selectedText;
    }

    public static void setTextBoldInEditText(EditText editText){
        int startSelection=editText.getSelectionStart();
        int endSelection=editText.getSelectionEnd();
        String selectedText = editText.getText().subSequence(startSelection, endSelection).toString();
        final SpannableStringBuilder sb = new SpannableStringBuilder(selectedText);
        final StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sb.setSpan(bold, 0, sb.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        Editable editable = editText.getText();
        editable.replace(startSelection, endSelection,sb);
    }

    public static void setTextItalicInEditText(EditText editText){
        int startSelection=editText.getSelectionStart();
        int endSelection=editText.getSelectionEnd();
        CharSequence selectedText = editText.getText().subSequence(startSelection, endSelection);
        final SpannableStringBuilder sb = new SpannableStringBuilder(selectedText);
        final StyleSpan italic = new StyleSpan(android.graphics.Typeface.ITALIC); // Span to make text italic
        sb.setSpan(italic, 0, sb.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        Editable editable = editText.getText();
        editable.replace(startSelection, endSelection,sb);
    }

    public static void setTextNormalInEditText(EditText editText){
        int startSelection=editText.getSelectionStart();
        int endSelection=editText.getSelectionEnd();
        String selectedText = editText.getText().subSequence(startSelection, endSelection).toString();
        final SpannableStringBuilder sb = new SpannableStringBuilder(selectedText);
        sb.clearSpans();
        Editable editable = editText.getText();
        StyleSpan[] styleSpans = editable.getSpans(startSelection, endSelection, StyleSpan.class);
        for(int i = 0 ;i < styleSpans.length; i ++){
            editable.removeSpan(styleSpans[i]);
        }
        editable.replace(startSelection, endSelection,sb);
    }

    public static void setTextAlignLeftInEditText(EditText editText){
        int startSelection=editText.getSelectionStart();
        int endSelection=editText.getSelectionEnd();
        String selectedText = editText.getText().subSequence(startSelection,endSelection).toString();
        final SpannableStringBuilder sb = new SpannableStringBuilder(selectedText);
        Editable editable = editText.getText();
        StyleSpan[] styleSpans = editable.getSpans(startSelection, endSelection, StyleSpan.class);
        for(int i = 0 ;i < styleSpans.length; i ++){
            editable.removeSpan(styleSpans[i]);
        }
        sb.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), 0,
                sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editable.replace(startSelection, endSelection,sb);
    }

    public static void setTextAlignCenterInEditText(EditText editText){
        int startSelection=editText.getSelectionStart();
        int endSelection=editText.getSelectionEnd();
        String selectedText = editText.getText().subSequence(startSelection, endSelection).toString();
        final SpannableStringBuilder sb = new SpannableStringBuilder(selectedText);
        sb.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0,
                sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Editable editable = editText.getText();
        editable.replace(startSelection, endSelection,sb);
    }

    public static void setTextAlignRightInEditText(EditText editText){
        int startSelection=editText.getSelectionStart();
        int endSelection=editText.getSelectionEnd();
        String selectedText = editText.getText().subSequence(startSelection, endSelection).toString();
        final SpannableStringBuilder sb = new SpannableStringBuilder(selectedText);
        sb.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0,
                sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Editable editable = editText.getText();
        editable.replace(startSelection, endSelection,sb);
    }
    
    public static void copyToClipBoard(Activity activity){
        String selectedString = null;
        ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("NormalText", selectedString);
        clipboardManager.setPrimaryClip(clipData);
        String plainText = clipboardManager.getPrimaryClipDescription().toString();
    }

    public Bitmap setEditTextToBitmap (EditText editText, Context context){
        StringBuffer stringBuffer = new StringBuffer(10);
        stringBuffer.append(editText.getText());
        LinearLayout layout = new LinearLayout(context);
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        //editText.measure(spec,spec);
        //editText.layout(0, 0, editText.getMeasuredWidth(), editText.getMeasuredHeight());

        Bitmap b = Bitmap.createBitmap(editText.getWidth(), editText.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);

        //You have to remove a view from it's parent before grab it to linear layout
        ViewGroup viewGroup = (ViewGroup) editText.getParent();
        viewGroup.removeView(editText);
        layout.addView(editText);
        layout.measure(canvas.getWidth(), canvas.getHeight());
        layout.layout(0, 0, canvas.getWidth(), canvas.getHeight());
        layout.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // ====not user cause layout's size is so large that hardware's cache cannot contain=====
        //layout.draw(canvas);
        //layout.setDrawingCacheEnabled(true);
        //layout.buildDrawingCache();
        //Bitmap bitmap = layout.getDrawingCache();
        Bitmap bitmap1 = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap1);
        layout.draw(c);
        //layout.setDrawingCacheEnabled(false);
        return bitmap1;
    }

    public static ProgressDialog initProgressDialog(Context context, ProgressDialog progressDialog) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getResources().getString(R.string.text_retrieving_data));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        return progressDialog;
    }
}
