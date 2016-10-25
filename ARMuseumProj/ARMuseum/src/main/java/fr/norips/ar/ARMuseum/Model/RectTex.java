package fr.norips.ar.ARMuseum.Model;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES10;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 20/10/16.
 */

public class RectTex extends Rectangle{

    private boolean finished;
    private int[] textures;

    public RectTex(float pos[][],ArrayList<String> pathToTextures,Context context) {
        super(pos,pathToTextures,context);
    }

    /**
     * The object own drawing function.
     * Called from the renderer to redraw this instance
     * with possible changes in values.
     *
     * @param gl - The GL Context
     */
    public void draw(GL10 gl) {
        //Load texture only draw, expecting not all model will be view, it will increase performance I think
        if(finished == false) {
            loadGLTexture(gl,context,pathToTextures);
        } else {
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glFrontFace(GL10.GL_CW);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

            gl.glBindTexture(GLES10.GL_TEXTURE_2D, textures[currentTexture]);
            gl.glEnable(GL10.GL_TEXTURE_2D);
            gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
            gl.glDisable(GL10.GL_TEXTURE_2D);

            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisable(GL10.GL_CULL_FACE);
        }
    }

    /**
     * Load the textures
     *
     * @param gl      - The GL Context
     * @param context - The Activity context
     */
    public void loadGLTexture(GL10 gl, Context context,ArrayList<String> pathToTextures) {

        //Generate a number of texture, texture pointer...
        textures = new int[pathToTextures.size()];
        gl.glGenTextures(pathToTextures.size(), textures, 0);

        Bitmap bitmap = null;

        for (int i = 0; i < pathToTextures.size(); i++) {
            // Create a bitmap
            bitmap = getBitmapFromAsset(context, pathToTextures.get(i));

            //...and bind it to our array
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);

            //Create Nearest Filtered Texture
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

            //Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

            //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

            //Clean up
            bitmap = null;
        }
        finished = true;
    }

    /**
     * Return bitmap from file
     * @param context
     * @param filePath
     * @return Bitmap type
     */
    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }

        return bitmap;
    }
}

