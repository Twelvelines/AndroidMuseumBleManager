package com.androidbeaconedmuseum;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.blakequ.androidblemanager.R;
import com.blakequ.androidblemanager.ui.scan.ScanFragment;

public class ScrollingActivity extends AppCompatActivity {
    private int textId, imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // dynamically setting title,
        setTitle(getIntent().getStringExtra(ScanFragment.ARTWORK_MESSAGE_NAME));
        // text,
        textId = getIntent().getIntExtra(ScanFragment.ARTWORK_MESSAGE_TEXT, -1);
        TextView articleView = (TextView) findViewById(R.id.article_view);
        articleView.setText(textId);
        // and image
        imageId = getIntent().getIntExtra(ScanFragment.ARTWORK_MESSAGE_IMAGE, -1);
        if (imageId != -1) {
            AppBarLayout imageArea = (AppBarLayout) findViewById(R.id.app_bar);
            imageArea.setBackgroundResource(imageId);
        }
    }
}
