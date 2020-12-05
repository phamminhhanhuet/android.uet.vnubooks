package com.uet.android.mouspad.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Base64;
import android.view.View;

import com.uet.android.mouspad.R;

import java.io.InputStream;
import java.net.URL;

public final class URLUtils {
    public static class URLDrawable extends BitmapDrawable {
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if(drawable != null) {
                drawable.draw(canvas);
            }
        }
    }


    public static class URLImageParser implements Html.ImageGetter {
        Context context;
        View container;

        public URLImageParser(View container, Context context) {
            this.context = context;
            this.container = container;
        }

        public Drawable getDrawable(String source) {
            if(source.matches("data:image.*base64.*")) {
                String base_64_source = source.replaceAll("data:image.*base64", "");
                byte[] data = Base64.decode(base_64_source, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Drawable image = new BitmapDrawable(context.getResources(), bitmap);
                image.setBounds(0, 0, 0 + image.getIntrinsicWidth(), 0 + image.getIntrinsicHeight());
                return image;
            } else {
                URLDrawable urlDrawable = new URLDrawable();
                ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);
                asyncTask.execute(source);
                return urlDrawable; //return reference to URLDrawable where We will change with actual image from the src tag
            }
        }

        public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
            URLDrawable urlDrawable;

            public ImageGetterAsyncTask(URLDrawable d) {
                this.urlDrawable = d;
            }

            @Override
            protected Drawable doInBackground(String... params) {
                String source = params[0];
                return fetchDrawable(source);
            }

            @Override
            protected void onPostExecute(Drawable result) {
                if(result != null){
                    urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(), 0 + result.getIntrinsicHeight()); //set the correct bound according to the result from HTTP call
                    urlDrawable.drawable = result; //change the reference of the current drawable to the result from the HTTP call
                } else {
                    urlDrawable.drawable = context.getDrawable(R.drawable.background);
                }
                URLImageParser.this.container.invalidate(); //redraw the image by invalidating the container
            }

            public Drawable fetchDrawable(String urlString) {
                try {
                    InputStream is = (InputStream) new URL(urlString).getContent();
                    Drawable drawable = Drawable.createFromStream(is, "src");
                    drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(), 0 + drawable.getIntrinsicHeight());
                    return drawable;
                } catch (Exception e) {
                    return null;
                }
            }
        }
    }

    public static class URLImageGetter implements Html.ImageGetter{

        private Context context;
        public URLImageGetter(Context context){
            this.context = context;
        }
        @Override
        public Drawable getDrawable(String s) {
            Drawable drawable =null;
            URLUtils.FetchImageUrl fiu = new URLUtils.FetchImageUrl(context,s);
            try {
                fiu.execute().get();
                drawable = fiu.GetImage();
            }
            catch (Exception e) {
                drawable = context.getResources().getDrawable(R.drawable.default_avatar, null);
            }
            // to display image,center of screen
//                            int imgH = drawable.getIntrinsicHeight();
//                            int imgW = drawable.getIntrinsicWidth();
//                            int padding =20;
//                            int realWidth = ScreenW-(2*padding);
//                            int realHeight = imgH * realWidth/imgW;
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            return drawable;        }
    }

    public static class FetchImageUrl extends AsyncTask<String, String, Boolean> {
        String imageUrl;
        Context context;
        protected Drawable image;

        public FetchImageUrl(Context context, String url) {
            this.imageUrl = url;
            image = null;
            this.context = context;
        }

        public Drawable GetImage() {
            return image;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                InputStream input_stream = (InputStream) new URL(imageUrl).getContent();
                image = Drawable.createFromStream(input_stream, "src name");
                return true;
            }
            catch (Exception e) {
                image = null;
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean result) {
        }}

}
