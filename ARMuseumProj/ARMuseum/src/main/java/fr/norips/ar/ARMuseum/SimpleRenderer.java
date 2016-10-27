/*
 *  SimpleRenderer.java
 *  ARToolKit5
 *
 *  Disclaimer: IMPORTANT:  This Daqri software is supplied to you by Daqri
 *  LLC ("Daqri") in consideration of your agreement to the following
 *  terms, and your use, installation, modification or redistribution of
 *  this Daqri software constitutes acceptance of these terms.  If you do
 *  not agree with these terms, please do not use, install, modify or
 *  redistribute this Daqri software.
 *
 *  In consideration of your agreement to abide by the following terms, and
 *  subject to these terms, Daqri grants you a personal, non-exclusive
 *  license, under Daqri's copyrights in this original Daqri software (the
 *  "Daqri Software"), to use, reproduce, modify and redistribute the Daqri
 *  Software, with or without modifications, in source and/or binary forms;
 *  provided that if you redistribute the Daqri Software in its entirety and
 *  without modifications, you must retain this notice and the following
 *  text and disclaimers in all such redistributions of the Daqri Software.
 *  Neither the name, trademarks, service marks or logos of Daqri LLC may
 *  be used to endorse or promote products derived from the Daqri Software
 *  without specific prior written permission from Daqri.  Except as
 *  expressly stated in this notice, no other rights or licenses, express or
 *  implied, are granted by Daqri herein, including but not limited to any
 *  patent rights that may be infringed by your derivative works or by other
 *  works in which the Daqri Software may be incorporated.
 *
 *  The Daqri Software is provided by Daqri on an "AS IS" basis.  DAQRI
 *  MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 *  THE IMPLIED WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS
 *  FOR A PARTICULAR PURPOSE, REGARDING THE DAQRI SOFTWARE OR ITS USE AND
 *  OPERATION ALONE OR IN COMBINATION WITH YOUR PRODUCTS.
 *
 *  IN NO EVENT SHALL DAQRI BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL
 *  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION,
 *  MODIFICATION AND/OR DISTRIBUTION OF THE DAQRI SOFTWARE, HOWEVER CAUSED
 *  AND WHETHER UNDER THEORY OF CONTRACT, TORT (INCLUDING NEGLIGENCE),
 *  STRICT LIABILITY OR OTHERWISE, EVEN IF DAQRI HAS BEEN ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  Copyright 2015 Daqri, LLC.
 *  Copyright 2011-2015 ARToolworks, Inc.
 *
 *  Author(s): Julian Looser, Philip Lamb
 *
 */

package fr.norips.ar.ARMuseum;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.artoolkit.ar.base.ARToolKit;
import org.artoolkit.ar.base.NativeInterface;
import org.artoolkit.ar.base.rendering.gles20.ARRendererGLES20;
import org.artoolkit.ar.base.rendering.gles20.CubeGLES20;
import org.artoolkit.ar.base.rendering.gles20.LineGLES20;
import org.artoolkit.ar.base.rendering.gles20.ShaderProgram;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import fr.norips.ar.ARMuseum.Config.Canvas;
import fr.norips.ar.ARMuseum.Config.ConfigHolder;
import fr.norips.ar.ARMuseum.Config.Model;
import fr.norips.ar.ARMuseum.Drawable.RectTex;
import fr.norips.ar.ARMuseum.shader.SimpleFragmentShader;
import fr.norips.ar.ARMuseum.shader.SimpleShaderProgram;
import fr.norips.ar.ARMuseum.shader.SimpleVertexShader;

/**
 * A very simple Renderer that adds a marker and draws a cube on it.
 */
public class SimpleRenderer extends ARRendererGLES20 {
    private final static String TAG = "SimpleRenderer";
    private int markerID = -1;
    private CubeGLES20 cube;
    private ArrayList<LineGLES20> lines = new ArrayList<LineGLES20>();
    private RectTex rect;
    private Context context;
    private float tmpMatrix[] = new float[16];
    /**
     * This method gets called from the framework to setup the ARScene.
     * So this is the best spot to configure you assets for your AR app.
     * For example register used markers in here.
     */
    @Override
    public boolean configureARScene() {
        //Construction init, this will be done by a JSON Parser
        Canvas t = new Canvas("Pinball","Data/pinball");
        float[][] tab = {
                {0,100,0},
                {100,100,0},
                {100,0,0},
                {0,0,0},
        };
        ArrayList<String> tmp = new ArrayList<String>();
        tmp.add("Data/tex_pinball.png");
        tmp.add("Data/tex_pinball2.png");
        t.addModel(new Model("Sur tableau",tab,tmp,context));

        Canvas tournesol = new Canvas("Tournesol","Data/tournesol");
        float[][] tabTournesol = {
                {0,100,0},
                {100,100,0},
                {100,0,0},
                {0,0,0},
        };
        ArrayList<String> tmpTournesol = new ArrayList<String>();
        tmpTournesol.add("Data/tex_tournesol.jpg");
        tmpTournesol.add("Data/tex_tournesolbis.jpg");
        tournesol.addModel(new Model("Sur tableau",tabTournesol,tmpTournesol,context));

        Canvas tournesol2 = new Canvas("Tournesol2","Data/tournesol2");
        float[][] tabTournesol2 = {
                {0,100,0},
                {100,100,0},
                {100,0,0},
                {0,0,0},
        };
        ArrayList<String> tmpTournesol2 = new ArrayList<String>();
        tmpTournesol2.add("Data/tex_tournesol2.jpg");
        tmpTournesol2.add("Data/tex_tournesol2bis.jpg");
        tournesol2.addModel(new Model("Sur tableau",tabTournesol2,tmpTournesol2,context));
        //TODO: Use openGL 2.0 to show video
        //tmp.clear();
        //tmp.add("Data/movie.mp4");
        //t.addModel(new Model("Cote tableau",new RectMovie(tab2,tmp,context)));
        ArrayList<Canvas> tableaux = new ArrayList<Canvas>();
        tableaux.add(t);
        tableaux.add(tournesol);
        tableaux.add(tournesol2);
        ConfigHolder.getInstance().init(tableaux);
        Log.d(TAG,"Threshold mode = " + ARToolKit.getInstance().getThresholdMode());

        return true;
    }
    public SimpleRenderer(Context cont) {
        context = cont;
    }

    //Shader calls should be within a GL thread that is onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()
    //As the cube instantiates the shader during setShaderProgram call we need to create the cube here.
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);
        ShaderProgram shaderProgram = new SimpleShaderProgram(new SimpleVertexShader(), new SimpleFragmentShader());
        ConfigHolder.getInstance().setShaderProgram(shaderProgram);
    }

    /**
     * Override the render function from {@link ARRendererGLES20}.
     */
    @Override
    public void draw() {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        float[] projectionMatrix = ARToolKit.getInstance().getProjectionMatrix();
        //Rotate matrix
        Matrix.setIdentityM(tmpMatrix,0);
        Matrix.rotateM(tmpMatrix,0,90.0f, 0.0f, 0.0f, -1.0f);
        // /!\ tmpMatrix * projectionMatrix =/=  projectionMatrix * tmpMatrix
        Matrix.multiplyMM(projectionMatrix,0,tmpMatrix,0,projectionMatrix,0);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glFrontFace(GLES20.GL_CW);
        ConfigHolder.getInstance().draw(projectionMatrix);

    }
}