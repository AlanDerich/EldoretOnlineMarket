package com.rayson.eldoretonlinemarket.customviews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by User on 3/4/2018.
 */

public class MyDragShadowBuilder extends View.DragShadowBuilder{

    private static Drawable shadow;

    public MyDragShadowBuilder(View v, String imageResource) {
        super(v);
StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
StrictMode.setThreadPolicy(policy);
try {
    URL url = new URL(imageResource);
    Bitmap bp = BitmapFactory.decodeStream((InputStream)url.getContent());
    shadow=new BitmapDrawable(bp);
}
catch (IOException e){

}
    }


    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {
        int width, height, imageRatio;

        imageRatio = shadow.getIntrinsicHeight() / shadow.getIntrinsicWidth();

        width = getView().getWidth() / 2;

        height = width * imageRatio;

        shadow.setBounds(0, 0, width, height);

        size.set(width, height);

        touch.set(width / 2, height / 2);
    }


    @Override
    public void onDrawShadow(Canvas canvas) {

        shadow.draw(canvas);
    }


}


















