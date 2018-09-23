package com.qlh.crop.cropviewlibrary.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 作者：QLH on 2018/9/20 10:37
 * 描述：处理图片工具类
 */
public class BitmapUtils {

    /**
     * 利用正确视角（和预览视角相同）的裁剪图片
     *
     * @param bitmap    原始图片
     * @param preWidth  预览视图宽
     * @param preHeight 预览视图高
     * @param frameRect 裁剪框
     * @param mCropMode 裁剪模式
     **/
    public static Bitmap getCropPicture(Bitmap bitmap, float preWidth, float preHeight, RectF frameRect, int mCropMode) {

        //原始照片的宽高
        float picWidth = bitmap.getWidth();
        float picHeight = bitmap.getHeight();

        //预览界面和照片的比例
        float preRW = picWidth / preWidth;
        float preRH = picHeight / preHeight;

        //裁剪框的位置和宽高
        float frameLeft = frameRect.left;
        float frameTop = frameRect.top;
        float frameWidth = frameRect.width();
        float frameHeight = frameRect.height();

        int cropLeft = (int) (frameLeft * preRW);
        int cropTop = (int) (frameTop * preRH);
        int cropWidth = (int) (frameWidth * preRW);
        int cropHeight = (int) (frameHeight * preRH);

        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropLeft, cropTop, cropWidth, cropHeight);

        if (mCropMode == CropMode.OVAL.getId()){
            cropBitmap = getOvalBitmap(cropBitmap);
        }else if ( mCropMode== CropMode.CIRCLE.getId()){
            cropBitmap = getCircularBitmap(cropBitmap);
        }
        return cropBitmap;
    }

    /**
     * 获取圆形图片
     * 只有图片是正方形的才能完美匹配
     * 如果图片尺寸不是1:1，最终的截图会存在在高度或者宽度上部分截取不到的情况
     * 这是因为圆形图片需要裁剪对应的矩形，半径只能以矩形最小的边
     */
    public static Bitmap getCircularBitmap(Bitmap square) {
        if (square == null) return null;
        Bitmap output = Bitmap.createBitmap(square.getWidth(), square.getHeight(), Bitmap.Config.ARGB_4444);

        final Rect rect = new Rect(0, 0, square.getWidth(), square.getHeight());
        Canvas canvas = new Canvas(output);

        int halfWidth = square.getWidth() / 2;
        int halfHeight = square.getHeight() / 2;

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        canvas.drawCircle(halfWidth, halfHeight, Math.min(halfWidth, halfHeight), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(square, rect, rect, paint);
        return output;
    }

    /**
     * 获取椭圆图片
     *
     */
    public static Bitmap getOvalBitmap(Bitmap square) {
        if (square == null) return null;
        Bitmap output = Bitmap.createBitmap(square.getWidth(), square.getHeight(), Bitmap.Config.ARGB_4444);

        final Rect rect = new Rect(0, 0, square.getWidth(), square.getHeight());
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        canvas.drawOval(new RectF(rect.left,rect.top,rect.right,rect.bottom),paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(square, rect, rect, paint);
        return output;
    }
}
