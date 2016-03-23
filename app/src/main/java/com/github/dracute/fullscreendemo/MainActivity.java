package com.github.dracute.fullscreendemo;

import android.os.Bundle;

import com.github.dracute.fullscreendemo.render.CardboardRender;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

public class MainActivity extends CardboardActivity {

    private CardboardView cardboardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);

        cardboardView.setRestoreGLStateEnabled(false);
//        cardboardView.setDistortionCorrectionEnabled(false);
        cardboardView.setRenderer(new CardboardRender(this));

        setCardboardView(cardboardView);
    }
}
