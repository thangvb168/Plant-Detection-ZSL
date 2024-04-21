package com.example.plansdetection.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class ProcessingImage {
    Context mContext;
    public ProcessingImage(Context context) {
        this.mContext = context;
    }
    public Bitmap handleImage(Bitmap bitmap) {
        Bitmap croppedImage = getSquareCroppedBitmap(bitmap);
        Bitmap denoisedImage = denoiseImage(croppedImage);
//        return sharpenAndEnhance(denoisedImage);
        return denoisedImage;
    }

    private Bitmap getSquareCroppedBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        int minEdgeLength = Math.min(bitmap.getWidth(), bitmap.getHeight());
        int startX = 0;
        int startY = 0;

        if (bitmap.getWidth() > minEdgeLength) {
            startX = (bitmap.getWidth() - minEdgeLength) / 2;
        }

        if (bitmap.getHeight() > minEdgeLength) {
            startY = (bitmap.getHeight() - minEdgeLength) / 2;
        }

        return Bitmap.createBitmap(bitmap, startX, startY, minEdgeLength, minEdgeLength, null, true);
    }
    private Bitmap denoiseImage(Bitmap bitmap) {
        Bitmap resultBitmap = bitmap.copy(bitmap.getConfig(), true);

        RenderScript rs = RenderScript.create(this.mContext);
        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createTyped(rs, input.getType());

        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blurScript.setInput(input);
        blurScript.setRadius(25f);
        blurScript.forEach(output);
        output.copyTo(resultBitmap);

        rs.destroy();
        return resultBitmap;
    }
}
