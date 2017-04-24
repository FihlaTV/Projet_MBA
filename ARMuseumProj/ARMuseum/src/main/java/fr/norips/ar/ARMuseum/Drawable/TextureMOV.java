package fr.norips.ar.ARMuseum.Drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.Surface;

import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import java.io.IOException;

/**
 * Created by norips on 23/02/17.
 */

public class TextureMOV extends Texture implements SurfaceTexture.OnFrameAvailableListener {
    private String TAG = "TextureMOV";
    private String path;
    private Context context;
    private MediaPlayer mMediaPlayer;
    private boolean finished = false;
    private int mTextureUniformHandle;
    protected int activeTexture = 0;
    public TextureMOV(Context c, String _path) {
        path = _path;
        context = c;
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void init() {
    }
    private boolean updateSurface = false;
    private SurfaceTexture mSurface;
    private ShaderProgram shader;
    public void setShader(ShaderProgram _shader) {
        shader = _shader;
    }
    @Override
    public void paint() {
        super.paint();
        if(finished == false) {
            loadGLTexture();
        } else {
            synchronized (this) {
                if (updateSurface) {
                    mSurface.updateTexImage();
                    updateSurface = false;
                }
            }
            mTextureUniformHandle = GLES20.glGetUniformLocation(shader.getShaderProgramHandle(), "u_Texture");
            GLES20.glUniform1i(mTextureUniformHandle, activeTexture);
        }

    }
    private int mTextureID = -1;
    private static int GL_TEXTURE_EXTERNAL_OES = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;

    private void loadGLTexture(){
        int textures[] = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        activeTexture = stack.removeFirst();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + activeTexture);
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
