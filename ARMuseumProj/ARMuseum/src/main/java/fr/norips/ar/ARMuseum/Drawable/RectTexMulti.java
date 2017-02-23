package fr.norips.ar.ARMuseum.Drawable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Handler;
import android.util.Log;

import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by norips on 20/10/16.
 */

public class RectTexMulti extends Rectangle {
    private final static String TAG = "RectTexMulti";
    protected List<Texture> arrTexture;
    public RectTexMulti(float pos[][], Context context) {
        super(pos,new ArrayList<String>(),context);
        arrTexture = new ArrayList<Texture>();
    }

    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {
        if(arrTexture.get(currentTexture) instanceof TextureMOV) {
            GLES20.glUseProgram(shaderMovie.getShaderProgramHandle());
            shaderMovie.setProjectionMatrix(projectionMatrix);
            shaderMovie.setModelViewMatrix(modelViewMatrix);
            arrTexture.get(currentTexture).paint();
            shaderMovie.render(this.getmVertexBuffer(),this.getmTextureBuffer() , this.getmIndexBuffer());
        } else {
            GLES20.glUseProgram(shaderProgram.getShaderProgramHandle());
            shaderProgram.setProjectionMatrix(projectionMatrix);
            shaderProgram.setModelViewMatrix(modelViewMatrix);
            arrTexture.get(currentTexture).paint();
            shaderProgram.render(this.getmVertexBuffer(),this.getmTextureBuffer() , this.getmIndexBuffer());
        }


    }
    public void setShaderProgram(ShaderProgram shaderProgram, ShaderProgram movieShader) {
        this.shaderProgram = shaderProgram;
        Log.d(TAG,"Called");
        for (Texture t:
             arrTexture) {
            if(t instanceof TextureIMG) {
                TextureIMG timg = (TextureIMG) t;
                timg.setShader(shaderProgram);
            }
            else if (t instanceof TextureMOV) {
                shaderMovie = movieShader;
                TextureMOV tmov = (TextureMOV) t;
                tmov.setShader(movieShader);
            }
        }
    }
    public void init() {
        for(Texture t: arrTexture) {
            t.init();
        }
    }

    public void addTexture(Texture t) {
        arrTexture.add(t);
    }
    public Texture getTexture(int index) {
        return arrTexture.get(index);
    }

    public void nextTexture(){
        if(currentTexture >= arrTexture.size()-1){
            currentTexture = 0;
        } else {
            currentTexture++;
        }

    }
    public void previousTexture(){
        if(currentTexture == 0){
            currentTexture = arrTexture.size()-1;
        } else {
            currentTexture--;
        }

    }
}

