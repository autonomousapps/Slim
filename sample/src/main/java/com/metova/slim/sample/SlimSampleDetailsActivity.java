package com.metova.slim.sample;

import com.metova.slim.Slim;
import com.metova.slim.annotation.Extra;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SlimSampleDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "extra.message";

    @Extra(EXTRA_MESSAGE)
    String mMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slim.bindLayout(this);
        Slim.bindExtras(this);
    }
}
