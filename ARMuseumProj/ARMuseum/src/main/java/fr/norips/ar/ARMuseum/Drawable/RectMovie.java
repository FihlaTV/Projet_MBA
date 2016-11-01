package fr.norips.ar.ARMuseum.Drawable;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by norips on 24/10/16.
 */

public class RectMovie extends Rectangle implements SurfaceTexture.OnFrameAvailableListener{
    private FFmpegMediaMetadataRetriever mRetriever;
    private String TAG = "RectMovie";
    private int textures[];
    private long currentPos=0;
    private boolean init = false;
    private long lenMs;
    private boolean finished = false;

    private int mTextureUniformHandle;

    public RectMovie(float pos[][], ArrayList<String> pathToTextures, Context context) {
        super(pos,pathToTextures,context);
        mRetriever = new FFmpegMediaMetadataRetriever();
        //AssetManager assetManager = context.getAssets();
            //final AssetFileDescriptor fd = context.getAssets().openFd(pathToTextures.get(0));
        mRetriever.setDataSource(pathToTextures.get(0));
        String lengthMsStr = mRetriever
                .extractMetadata(mRetriever.METADATA_KEY_DURATION);
        lenMs = Long.parseLong(lengthMsStr);
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
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
            mTextureUniformHandle = GLES20.glGetUniformLocation(shaderProgram.getShaderProgramHandle(), "u_Texture");
            GLES20.glUniform1i(mTextureUniformHandle, currentTexture);
            shaderProgram.render(this.getmVertexBuffer(), this.getmTextureBuffer(), this.getmIndexBuffer());
        }

    }
    
    private void init(Bitmap first) {
        //Generate a number of texture, texture pointer...
        textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, first,0);
        init = true;

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
