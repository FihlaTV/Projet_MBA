package fr.norips.ar.ARMuseum.Drawable;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 24/10/16.
 */

public class RectMovie extends Rectangle{
    private MediaMetadataRetriever mRetriever;
    private int textures[];
    private long currentPos=0;
    private boolean init = false;
    private long lenMs;
    public RectMovie(float pos[][], ArrayList<String> pathToTextures, Context context) {
        super(pos,pathToTextures,context);
        mRetriever = new MediaMetadataRetriever();
        AssetManager assetManager = context.getAssets();
        try {
            final AssetFileDescriptor fd = context.getAssets().openFd(pathToTextures.get(0));
            mRetriever.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getLength());
            String lengthMsStr = mRetriever
                    .extractMetadata(mRetriever.METADATA_KEY_DURATION);
            lenMs = Long.parseLong(lengthMsStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {

        shaderProgram.setProjectionMatrix(projectionMatrix);
        shaderProgram.setModelViewMatrix(modelViewMatrix);

        shaderProgram.render(this.getmVertexBuffer(), this.getmTextureBuffer(), this.getmIndexBuffer());

    }
    public void draw(GL10 gl) {
        loadGLTexture(gl);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glFrontFace(GL10.GL_CW);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
        gl.glDisable(GL10.GL_TEXTURE_2D);

        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisable(GL10.GL_CULL_FACE);
    }
    private void init(GL10 gl,Bitmap first) {
        //Generate a number of texture, texture pointer...
        textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, first,0);
        init = true;

    }
    private void loadGLTexture(GL10 gl){
        Bitmap bitmap = null;
        // Retrieve a bitmap
        bitmap = mRetriever.getFrameAtTime(currentPos*1000,MediaMetadataRetriever.OPTION_CLOSEST);
        if(init == false){
            Log.d("RectMovie","Lenms: " + lenMs);
            init(gl,bitmap);
            return;
        }
        currentPos+=25; //Assuming 25 fps
        Log.d("RectMovie","Current pos: "+currentPos);
        if(currentPos>=lenMs){
            currentPos=0;
        }
        //...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        //Create Nearest Filtered Texture
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

        //Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, bitmap);

        //Clean up
        bitmap = null;
    }
}
