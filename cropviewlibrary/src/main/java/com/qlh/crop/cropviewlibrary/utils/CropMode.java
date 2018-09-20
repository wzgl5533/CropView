package com.qlh.crop.cropviewlibrary.utils;

/**
 * 作者：QLH on 2018/9/20 15:24
 * 描述：裁剪模式
 * 目前存在的：SQUARE(3)，OVAL(10)，CIRCLE(8)
 * <br>
  未来可以扩展的：
  FIT_IMAGE, RATIO_4_3, RATIO_3_4,
  RATIO_16_9, RATIO_9_16, FREE, CUSTOM, CIRCLE_SQUARE
  </br>rectangle
 */
public enum CropMode {

    RECT(1),OVAL(2),CIRCLE(3);
    private final int ID;

    CropMode(final int id) {
        this.ID = id;
    }

    public int getId() {
        return ID;
    }


}
