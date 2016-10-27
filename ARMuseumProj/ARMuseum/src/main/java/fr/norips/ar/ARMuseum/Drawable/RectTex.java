package fr.norips.ar.ARMuseum.Drawable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 20/10/16.
 */

public class RectTex extends Rectangle{

    private boolean finished = false;
    private int[] textures;

    public RectTex(float pos[][],ArrayList<String> pathToTextures,Context context) {
        super(pos,pathToTextures,context);
    }
    /** This will be used to pass in the texture. */
    private int mTextureUniformHandle;

    /** This will be used to pass in model position information. */
    private int mPositionHandle;

    /** This will be used to pass in model texture coordinate information. */
    private int mTextureCoordinateHandle;
    /**
     * The object own drawing function.
     * Called from the renderer to redraw this instance
     * with possible changes in values.
     *
     */
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            reInitLoad();
        }
    };
    private void reInitLoad(){
        finished = false;
    }

    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,1000);
        GLES20.glUseProgram(shaderProgram.getShaderProgramHandle());
        shaderProgram.setProjectionMatrix(projectionMatrix);
        shaderProgram.setModelViewMatrix(modelViewMatrix);
        if(finished == false) {
            loadGLTexture(context,pathToTextures);
        } else {
            mTextureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram.getShaderProgramHandle(), "a_TexCoordinate");
            mTextureUniformHandle = GLES20.glGetUniformLocation(shaderProgram.getShaderProgramHandle(), "u_Texture");
            GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textures[currentTexture]);
            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
            GLES20.glUniform1i(mTextureUniformHandle, currentTexture);

            shaderProgram.render(this.getmVertexBuffer(),this.getmTextureBuffer() , this.getmIndexBuffer());
        }

    }

    /**
     * Load the textures
     *
     * @param context - The Activity context
     * @param pathToTextures ArrayList of path to textures
     */
    public void loadGLTexture(Context context,ArrayList<String> pathToTextures) {

        //Generate a number of texture, texture pointer...
        textures = new int[pathToTextures.size()];
        GLES20.glGenTextures(pathToTextures.size(), textures, 0);

        Bitmap bitmap = null;

        for (int i = 0; i < pathToTextures.size(); i++) {
            // Create a bitmap
            bitmap = getBitmapFromAsset(context, pathToTextures.get(i));

            //...and bind it to our array
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textures[i]);

            //Create Nearest Filtered Texture
            GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

            //Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
            GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
            GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

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

