package fr.norips.ar.ARMuseum.Drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

/**
 * Created by norips on 23/02/17.
 */

public class TextureTXT extends TextureIMG {
    private float ratioX;
    public TextureTXT(Context c,String text,float ratioWidth) {
        super(c,text);
        ratioX = ratioWidth;
    }
    @Override
        protected Bitmap getBitmapFromAsset(Context context, String text) {
        float scale = context.getResources().getDisplayMetrics().density;


        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color
        paint.setColor(Color.BLACK);
        // text size in pixels
        int textSize = 480;
        paint.setTextSize(textSize);
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.BLACK);
        // set text width to canvas width minus 16dp padding
        //int textWidth = canvas.getWidth() - (int) (16 * scale);

        /*float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText("Four word per line") + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);*/

        float height = 1024;
        float width = height*ratioX;

        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(
                text, paint,(int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        while(height < textLayout.getHeight()) {
            textSize = textSize-10;
            paint.setTextSize(textSize);
            textLayout = new StaticLayout(
                    text, paint,(int) width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        Bitmap bitmap = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);
        // get a canvas to paint over the bitmap
        Canvas canvas = new Canvas(bitmap);
        int alpha = (int)(0.5 * 255.0f);
        //White semi transparent
        canvas.drawColor(Color.argb(alpha, 255, 255, 255));

        // get position of text's top left corner
        float x = (bitmap.getWidth() - width) / 2;
        float y = 0;




        // draw text to the Canvas center
        canvas.save();
        canvas.translate(x, y);
        textLayout.draw(canvas);
        canvas.restore();
        return bitmap;
    }
}
