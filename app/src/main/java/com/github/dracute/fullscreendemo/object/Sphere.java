package com.github.dracute.fullscreendemo.object;

import android.opengl.GLES20;
import android.util.Log;

import com.github.dracute.fullscreendemo.utils.Constant;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 * Created by DrAcute on 2016/3/21.
 */
public class Sphere implements IObject{

    static final int size = 72;
    static final float r = 1f;

    FloatBuffer vertexBuffer;
    FloatBuffer textureCoordsBuffer;
    ShortBuffer middleDrawOrderBuffer;
    ShortBuffer topDrawOrderBuffer;
    ShortBuffer bottomDrawOrderBuffer;
    short[] topDrawOrder = new short[size * 3];
    short[] bottomDrawOrder = new short[size * 3];
    short[] middleDrawOrder = new short[size * 2 * 3];

    public void init() {

        for (int i = 0; i < size; i ++) {
            topDrawOrder[3 * i] = (short) i;
            topDrawOrder[3 * i + 1] = (short) (i + size + 1);
            topDrawOrder[3 * i + 2] = (short) (i + size + 2);
        }

        for (int i = 0; i < size; i ++) {
            bottomDrawOrder[3 * i] = (short) i;
            bottomDrawOrder[3 * i + 1] = (short) (i + 1);
            bottomDrawOrder[3 * i + 2] = (short) (i + size + 1);
        }

        for (int i = 0; i < size; i ++) {
            middleDrawOrder[6 * i] = (short) i;
            middleDrawOrder[6 * i + 1] = (short) (i + size + 1);
            middleDrawOrder[6 * i + 2] = (short) (i + size + 2);
            middleDrawOrder[6 * i + 3] = (short) i;
            middleDrawOrder[6 * i + 4] = (short) (i + size + 2);
            middleDrawOrder[6 * i + 5] = (short) (i + 1);
        }

        ByteBuffer vBb = ByteBuffer.allocateDirect((size + 1) * 4 * 2 * Constant.BYTE_PER_FLOAT);
        vBb.order(ByteOrder.nativeOrder());

        vertexBuffer = vBb.asFloatBuffer();

        ByteBuffer tBb = ByteBuffer.allocateDirect(topDrawOrder.length * Constant.BYTE_PER_SHORT);
        tBb.order(ByteOrder.nativeOrder());

        topDrawOrderBuffer = tBb.asShortBuffer();
        topDrawOrderBuffer.put(topDrawOrder);
        topDrawOrderBuffer.position(0);

        ByteBuffer bBb = ByteBuffer.allocateDirect(bottomDrawOrder.length * Constant.BYTE_PER_SHORT);
        bBb.order(ByteOrder.nativeOrder());

        bottomDrawOrderBuffer = bBb.asShortBuffer();
        bottomDrawOrderBuffer.put(bottomDrawOrder);
        bottomDrawOrderBuffer.position(0);

        ByteBuffer mBb = ByteBuffer.allocateDirect(middleDrawOrder.length * Constant.BYTE_PER_SHORT);
        mBb.order(ByteOrder.nativeOrder());

        middleDrawOrderBuffer = mBb.asShortBuffer();
        middleDrawOrderBuffer.put(middleDrawOrder);
        middleDrawOrderBuffer.position(0);

        ByteBuffer drawBuffer = ByteBuffer.allocateDirect((size + 1) * 4 * 2 * Constant.BYTE_PER_FLOAT);
        drawBuffer.order(ByteOrder.nativeOrder());

        textureCoordsBuffer = drawBuffer.asFloatBuffer();
    }

    public void draw(int positionHandle, int textureCoordinateHandle) {
        GLES20.glVertexAttribPointer(positionHandle, 4, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        float[] top = new float[(size + 1) * 4];
        float[] bottom = new float[(size + 1) * 4];

        for (int i = 0; i < size; i ++ ) {
            vertexBuffer.position(0);

            calculateTop(top, bottom, i);
            calculateBottom(bottom, i);

            vertexBuffer.position(0);
            vertexBuffer.put(top, 0, top.length);
            vertexBuffer.put(bottom, 0, bottom.length);
            vertexBuffer.position(0);

            textureCoordsBuffer.position(0);
            calculateTextureCoords(i);
            textureCoordsBuffer.position(0);

            GLES20.glVertexAttribPointer(textureCoordinateHandle, 4, GLES20.GL_FLOAT, false, 0, textureCoordsBuffer);
            if (i == 0) {
                drawTop();
            } else if (i == size - 1) {
                drawBottom();
            } else {
                drawMiddle();
            }
        }
    }

    private void calculateTop(float[] top, float[] bottom, int i) {
        if (i == 0) {
            for (int j = 0; j < size; j ++) {
                top[4 * j] = 0f;
                top[4 * j + 1] = r;
                top[4 * j + 2] = 0f;
                top[4 * j + 3] = 0f;
            }
        } else {
            System.arraycopy(bottom, 0, top, 0, top.length);
        }

//        Log.d("Tag", Arrays.toString(top));
    }

    private void calculateBottom(float[] bottom, int i) {
        if (i + 1 == size) {
            for (int j = 0; j < size + 1; j ++) {
                bottom[4 * j] = 0f;
                bottom[4 * j + 1] = -r;
                bottom[4 * j + 2] = 0f;
                bottom[4 * j + 3] = 0f;
            }
        } else {
            float angleA = 90f - (i + 1) * 180f / (float)size ;
            float y = (float) (Math.sin(Math.toRadians(angleA))) * r;
            float ry = (float) (Math.cos(Math.toRadians(angleA))) * r;
            float angleB;
            for (int j = 0; j < size + 1; j ++) {
                angleB = j * 360f / (float)size;
                bottom[4 * j] = (float) (ry * Math.sin(Math.toRadians(angleB)));
                bottom[4 * j + 1] = y;
                bottom[4 * j + 2] = (float) (ry * Math.cos(Math.toRadians(angleB)));
                bottom[4 * j + 3] = 1f;
            }
        }
//        Log.d("Tag", Arrays.toString(bottom));
    }

    private void calculateTextureCoords(int i) {
        for (int j = 0; j < size + 1; j ++) {
            textureCoordsBuffer.put(j / (float) size);
            textureCoordsBuffer.put(i / (float) size);
            textureCoordsBuffer.put(0f);
            textureCoordsBuffer.put(1f);
        }
        for (int j = 0; j < size + 1; j ++) {
            textureCoordsBuffer.put(j / (float) size);
            textureCoordsBuffer.put((i + 1) / (float) size);
            textureCoordsBuffer.put(0f);
            textureCoordsBuffer.put(1f);
        }
        textureCoordsBuffer.position(0);
    }

    private void drawTop() {
        vertexBuffer.position(0);
        topDrawOrderBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, topDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, topDrawOrderBuffer);
    }

    private void drawBottom() {
        vertexBuffer.position(0);
        bottomDrawOrderBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, bottomDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, bottomDrawOrderBuffer);
    }

    private void drawMiddle() {
        vertexBuffer.position(0);
        middleDrawOrderBuffer.position(0);
//        Log.d("TAG", Arrays.toString(middleDrawOrder));
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, middleDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, middleDrawOrderBuffer);
    }
}
