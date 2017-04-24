package fr.norips.ar.ARMuseum.Drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Handler;
import android.util.Log;

import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 23/02/17.
 */

public class TextureIMG extends Texture {
    /** This will be used to pass in the texture. */

    private String TAG = "TextureIMG";
    protected int mTextureUniformHandle;
    protected int activeTexture = 0;
    protected boolean finished = false;

    /**
     * The object own drawing function.
     * Called from the renderer to redraw this instance
     * with possible changes in values.
     *
     */
    protected Handler handler = null;
    protected Runnable runnable = null;
    protected void reInitLoad(){
            stack.addFirst(activeTexture);
        finished = false;
    }


    public void init(){
        if(handler == null) {
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    reInitLoad();
                    Log.d(TAG,"reInitLoad called");
                }
            };
        }
    }
    protected ShaderProgram shader;
    protected Context context;
    protected String pathToTexture;

    public TextureIMG(Context c,String _pathToTexture) {
        context = c;
        pathToTexture = _pathToTexture;
    }
    public void setShader(ShaderProgram _shader) {
        shader = _shader;
    }
    @Override
    public void paint() {
        super.paint();

        if(handler!=null) {
            //Time out
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
        }
        if(finished == false) {
            Log.d(TAG,"loadGLTexture called");
            loadGLTexture();
            Log.d(TAG,"loadGLTexture exited");
        } else {
            mTextureUniformHandle = GLES20.glGetUniformLocation(shader.getShaderProgramHandle(), "u_Texture");
            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit textureAct[currentTexture].
            GLES20.glUniform1i(mTextureUniformHandle, activeTexture);
            Log.d(TAG,"Current texture" + activeTexture);
        }

    }
    protected void loadGLTexture() {
        //Generate a number of texture, texture pointer...
        int textures[] = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        Bitmap bitmap = getBitmapFromAsset(context, pathToTexture);
        //...and bind it to our array
        activeTexture = stack.removeFirst();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + activeTexture);
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

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
        finished = true;
    }

    /**
     * Return bitmap from file
     * @param context
     * @param path Index of file to load
     * @return Bitmap type
     */
    protected Bitmap getBitmapFromAsset(Context context, String path) {
        return BitmapFactory.decodeFile(path);
    }

}
