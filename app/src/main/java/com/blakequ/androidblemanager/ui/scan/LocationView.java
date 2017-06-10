package com.blakequ.androidblemanager.ui.scan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.toy.example.BeaconDeviceLocation;
import com.toy.example.UserLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DorisLiu on 6/6/17.
 */

public class LocationView extends View {
    private Paint mPaint;
    private List<BeaconDeviceLocation> deviceLocations = new ArrayList<>();

    public LocationView(Context context) {
        super(context);
        init();
    }

    public LocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);       // 设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL);  // 设置画笔模式为填充
        mPaint.setStrokeWidth(10f);         // 设置画笔宽度为10px
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth() / 2, getHeight() / 2);
        for (BeaconDeviceLocation aDeviceLocation : deviceLocations) {
            canvas.drawCircle((float) aDeviceLocation.getLatitude() * 150, (float) aDeviceLocation.getLongitude() * 150, 20, mPaint);           //绘制圆形
        }
        canvas.drawCircle((float) UserLocation.getLatitude() * 150, (float) UserLocation.getLongitude() * 150, 20, mPaint);           //绘制圆形
    }

    public void setDeviceLocations(List<BeaconDeviceLocation> deviceLocations) {
        this.deviceLocations = deviceLocations;
    }
}
