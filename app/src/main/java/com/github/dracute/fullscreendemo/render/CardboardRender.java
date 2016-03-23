package com.github.dracute.fullscreendemo.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.github.dracute.fullscreendemo.R;
import com.github.dracute.fullscreendemo.object.IObject;
import com.github.dracute.fullscreendemo.object.Sphere;
import com.github.dracute.fullscreendemo.object.Test;
import com.github.dracute.fullscreendemo.utils.RawResourceReader;
import com.github.dracute.fullscreendemo.utils.ShaderHelper;
import com.github.dracute.fullscreendemo.utils.TextureHelper;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by DrAcute on 2016/3/23.
 */
public class CardboardRender implements CardboardView.StereoRenderer {

    private final float[] projectionMatrix = new float[16];

    private final float rotate_degree = 0;

    private final float[] rotate_matrix = new float[] {
            (float) Math.cos(Math.toRadians(rotate_degree)), 0, (float) Math.sin(Math.toRadians(rotate_degree)), 0,
            0, 1, 0, 0,
            - (float) Math.sin(Math.toRadians(rotate_degree)), 0, (float) Math.cos(Math.toRadians(rotate_degree)), 0,
            0, 0, 0, 1
    };

    private final float[] sight_matrix = new float[] {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
    };

    private Context mContext;
    private IObject mObject;

    private int texture;
    private int shaderProgram;

    private boolean isDrawFrame = true;

    public CardboardRender(Context context) {
        this.mContext = context;
        this.mObject = new Sphere();
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        headTransform.getHeadView(rotate_matrix, 0);
        isDrawFrame = true;
    }

    @Override
    public void onDrawEye(Eye eye) {
        if (!isDrawFrame) return;

        isDrawFrame = true;

        int width = eye.getViewport().width;
        int height = eye.getViewport().height;
        final float aspectRatio = width > height ? (float)width / (float)height : (float)height / (float)width;
        if (width > height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }

        GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(shaderProgram);

        final int positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition");
        final int matrixHandle = GLES20.glGetUniformLocation(shaderProgram, "uMatrix");
        final int rotateHandle = GLES20.glGetUniformLocation(shaderProgram, "uRotate");
        final int sightHandle = GLES20.glGetUniformLocation(shaderProgram, "uSight");
        final int textureHandle = GLES20.glGetUniformLocation(shaderProgram, "texture");
        final int textureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "aTexCoordinate");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, projectionMatrix, 0);
        GLES20.glUniformMatrix4fv(rotateHandle, 1, false, rotate_matrix, 0);
        GLES20.glUniformMatrix4fv(sightHandle, 1, false, sight_matrix, 0);

        mObject.draw(positionHandle, textureCoordinateHandle);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        mObject.init();
        initShader();
        texture = TextureHelper.loadTexture(mContext, R.drawable.fullscreen);
    }

    @Override
    public void onRendererShutdown() {
        GLES20.glDeleteProgram(shaderProgram);
    }

    private void initShader() {
        final String vertexShader = RawResourceReader.readTextFileFromRawResource(mContext, R.raw.fullscreen_vertex_shader);
        final String fragmentShader = RawResourceReader.readTextFileFromRawResource(mContext, R.raw.fullscreen_fragment_shader);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);

        shaderProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"texture", "vPosition", "uMatrix", "uRotate", "uSight", "aTexCoordinate"});
    }
}
