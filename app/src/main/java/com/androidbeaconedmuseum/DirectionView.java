package com.androidbeaconedmuseum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.blakequ.androidblemanager.R;

/**
 * Created by toy on 20/06/2017.
 */

public class DirectionView extends android.support.v7.widget.AppCompatImageView {
    Bitmap image;
    Paint paint = new Paint();
    Rect rect = new Rect();
    int angleToRotate = 0;

    public DirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        BitmapDrawable drawble = (BitmapDrawable) ContextCompat.getDrawable(getContext(),
                R.drawable.direction);
        image = drawble.getBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth() / 2, getHeight() / 2);
        super.onDraw(canvas);

        float w = (float) image.getWidth();
        float h = (float) image.getHeight();
        canvas.rotate(angleToRotate, rect.width() / 2, rect.height() / 2);
        canvas.drawBitmap(image, ((rect.width() - w) / 2), ((rect.height() - h) / 2), paint);
    }

    public void updateAngle(float radAngleToNorth) {
        angleToRotate = (int) (radAngleToNorth * 180 / Math.PI);
        invalidate();
    }
}
