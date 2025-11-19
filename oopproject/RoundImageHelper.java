package com.example.oopproject;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

public class RoundImageHelper {

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int squareSize = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(squareSize, squareSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        float left = (squareSize - width) / 2f;
        float top = (squareSize - height) / 2f;

        canvas.translate(-left, -top);
        paint.setShader(shader);

        float radius = squareSize / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        return output;
    }
}
