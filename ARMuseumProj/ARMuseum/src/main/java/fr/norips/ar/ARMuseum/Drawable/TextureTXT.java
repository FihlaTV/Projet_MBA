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

/**
 * Created by norips on 23/02/17.
 */

public class TextureTXT extends TextureIMG {
    public TextureTXT(Context c,String text) {
        super(c,text);
    }
    @Override
        protected Bitmap getBitmapFromAsset(Context context, String text) {
        float scale = context.getResources().getDisplayMetrics().density;


        TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        paint.setTextSize((int) (30 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        // set text width to canvas width minus 16dp padding
        //int textWidth = canvas.getWidth() - (int) (16 * scale);

        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // get a canvas to paint over the bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        // init StaticLayout for text
        StaticLayout textLayout = new StaticLayout(
                text, paint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);


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
