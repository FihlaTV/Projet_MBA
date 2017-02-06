package fr.norips.ar.ARMuseum.Drawable;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;

/**
 * Created by norips on 05/02/17.
 */

public class RectTexte extends RectTex {
    public RectTexte(float pos[][], List<String> textToShow, Context context) {
        super(pos,textToShow,context);
    }

    /**
     * Quick hack to use bitmap from text instead of bitmap from file
     * @param context Not use
     * @param text Text to draw
     * @return
     */
    @Override
    protected Bitmap getBitmapFromAsset(Context context, String text) {
        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
// get a canvas to paint over the bitmap
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);

// Draw the text
        Paint textPaint = new Paint();
        textPaint.setTextSize(32);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
// draw the text centered
        canvas.drawText(text, 16,112, textPaint);
        return bitmap;
    }
}
