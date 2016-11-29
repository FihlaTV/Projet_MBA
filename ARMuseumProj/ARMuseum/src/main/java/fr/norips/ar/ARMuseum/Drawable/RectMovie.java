package fr.norips.ar.ARMuseum.Drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by norips on 24/10/16.
 */

public class RectMovie extends Rectangle implements SurfaceTexture.OnFrameAvailableListener{
    private String TAG = "RectMovie";
    private int textures[];
    private boolean finished = false;
    private int textureAct;

    private int mTextureUniformHandle;

    public RectMovie(float pos[][], ArrayList<String> pathToTextures, Context context) {
        super(pos,pathToTextures,context);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(pathToTextures.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean updateSurface = false;
    private SurfaceTexture mSurface;
    private MediaPlayer mMediaPlayer;
    @Override
    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {
        super.draw(projectionMatrix,modelViewMatrix);
        GLES20.glUseProgram(shaderProgram.getShaderProgramHandle());
        shaderProgram.setProjectionMatrix(projectionMatrix);
        shaderProgram.setModelViewMatrix(modelViewMatrix);
        if(finished == false) {
            loadGLTexture();
        } else {
            synchronized (this) {
                if (updateSurface) {
                    mSurface.updateTexImage();
                    updateSurface = false;
                }
            }
            mTextureUniformHandle = GLES20.glGetUniformLocation(shaderProgram.getShaderProgramHandle(), "u_Texture");
            GLES20.glUniform1i(mTextureUniformHandle, textureAct);
            shaderProgram.render(this.getmVertexBuffer(), this.getmTextureBuffer(), this.getmIndexBuffer());
        }

    }
    
    private void init(Bitmap first) {
        //Generate a number of texture, texture pointer...
        textures = new int[1];
        textureAct = stack.removeFirst();
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureAct);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, first,0);

    }
    private int mTextureID = -1;
    private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    private void loadGLTexture(){
        textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);

        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

            /*
             * Create the SurfaceTexture that will feed this textureID,
             * and pass it to the MediaPlayer
             */
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);

        Surface surface = new Surface(mSurface);
        mMediaPlayer.setSurface(surface);
        surface.release();
        mMediaPlayer.setLooping(true);

        try {
            mMediaPlayer.prepare();
        } catch (IOException t) {
            Log.e(TAG, "media player prepare failed");
        }

        synchronized(this) {
            updateSurface = false;
        }

        mMediaPlayer.start();
        finished = true;
    }

    @Override
    synchronized public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
    }
}
