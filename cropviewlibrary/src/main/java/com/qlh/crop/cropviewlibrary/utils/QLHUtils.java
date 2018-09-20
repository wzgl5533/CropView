package com.qlh.crop.cropviewlibrary.utils;


import java.util.Arrays;
import java.util.List;

/**
 * 作者：QLH on 2018/9/18 17:03
 * 描述：工具类
 */
public class QLHUtils {


    /**
     * 判断按下的点在圆圈内
     *
     * @param x                                按下的X坐标
     * @param y                                按下的Y坐标
     * @param four_corner_coordinate_positions 坐标顶点
     * @param region_radius                    响应区域半径
     * @param except                           不检测的顶点序号组
     * @return 返回按到的是哪个点, 没有则返回-1
     * 点阵示意：
     * 0   1
     * 2   3
     */
    public static int isInTheCornerCircle(float x, float y, float[][] four_corner_coordinate_positions, int region_radius, Integer[] except) {
        List<Integer> integerList=null;
        if (except!=null && except.length>0){ integerList = Arrays.asList(except);
        }
        for (int i = 0; i < four_corner_coordinate_positions.length; i++) {
            if (integerList!=null&&integerList.size()>0&&integerList.contains(i)){ continue; }
            float a = four_corner_coordinate_positions[i][0];
            float b = four_corner_coordinate_positions[i][1];
            float temp1 = (float) Math.pow((x - a), 2);
            float temp2 = (float) Math.pow((y - b), 2);
            if (((float) region_radius) >= Math.sqrt(temp1 + temp2)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 扩大缩放方法
     * 根据用户传来的点改变其他点的坐标
     * 按住某一个点，该点的坐标改变，其他2个点坐标跟着改变，对边的点坐标不变
     * 点阵示意：
     * 0  4  1
     * 6     5
     * 2  7  3
     *
     * @param point     用户按的点
     * @param offsetX   X轴偏移量
     * @param offsetY   Y轴偏移量
     * @param positions 坐标顶点
     * @param max       最大偏移量
     */
    public static void changePositions(int point, int offsetX, int offsetY, float[][] positions, float max) {

        switch (point) {
            case 0:
                if ((offsetX > 0 && offsetY < 0) || (offsetX > 0 && offsetY > 0)) {
                    //变化0点的位置   suoxiao
                    positions[0][0] += max;
                    positions[0][1] += max;
                    //变化1点的Y轴
                    positions[1][1] += max;
                    //变化2点的X轴
                    positions[2][0] += max;
                } else {
                    //变化0点的位置   kuoda
                    positions[0][0] -= max;
                    positions[0][1] -= max;
                    //变化1点的Y轴
                    positions[1][1] -= max;
                    //变化2点的X轴
                    positions[2][0] -= max;
                }
                //使用顶角坐标计算横线坐标
                //变化4点的X轴，Y轴
                positions[4][0] = (positions[0][0] +
                        positions[1][0]) / 2;
                positions[4][1] = positions[0][1];
                //变化5点的Y轴
                positions[5][1] = (positions[1][1] +
                        positions[3][1]) / 2;
                //变化6点的X轴，Y轴
                positions[6][0] = positions[0][0];
                positions[6][1] = (positions[0][1] +
                        positions[2][1]) / 2;
                //变化7点的X轴
                positions[7][0] = (positions[2][0] +
                        positions[3][0]) / 2;
                break;
            case 1:
                if ((offsetX > 0 && offsetY < 0) || (offsetX > 0 && offsetY > 0)) {
                    //变化1点的位置
                    positions[1][0] += max;
                    positions[1][1] -= max;
                    //变化0点的Y轴
                    positions[0][1] -= max;
                    //变化3点的X轴
                    positions[3][0] += max;
                } else if ((offsetX < 0 && offsetY > 0) || (offsetX < 0 && offsetY < 0)) {
                    //变化1点的位置
                    positions[1][0] -= max;
                    positions[1][1] += max;
                    //变化0点的Y轴
                    positions[0][1] += max;
                    //变化3点的X轴
                    positions[3][0] -= max;
                }
                //变化4点的X轴，Y轴
                positions[4][0] = (positions[0][0] +
                        positions[1][0]) / 2;
                positions[4][1] = positions[0][1];
                //变化5点的X轴，Y轴
                positions[5][0] = positions[1][0];
                positions[5][1] = (positions[1][1] +
                        positions[3][1]) / 2;
                //变化6点的Y轴
                positions[6][1] = (positions[0][1] +
                        positions[2][1]) / 2;
                //变化7点的X轴，Y轴
                positions[7][0] = (positions[2][0] +
                        positions[3][0]) / 2;
                positions[7][1] = positions[2][1];
                break;
            case 2:
                if ((offsetX > 0 && offsetY < 0) || (offsetX > 0 && offsetY > 0)) {
                    //变化2点的位置   suoxiao
                    positions[2][0] += max;
                    positions[2][1] -= max;
                    //变化0点的X轴
                    positions[0][0] += max;
                    //变化3点的Y轴
                    positions[3][1] -= max;
                } else {
                    //变化2点的位置   kuoda
                    positions[2][0] -= max;
                    positions[2][1] += max;
                    //变化0点的X轴
                    positions[0][0] -= max;
                    //变化3点的Y轴
                    positions[3][1] += max;
                }
                //变化4点的X轴
                positions[4][0] = (positions[0][0] +
                        positions[1][0]) / 2;
                //变化5点的Y轴
                positions[5][1] = (positions[1][1] +
                        positions[3][1]) / 2;
                //变化6点的XY轴
                positions[6][0] = positions[0][0];
                positions[6][1] = (positions[0][1] +
                        positions[2][1]) / 2;
                //变化7点的X轴,Y轴
                positions[7][0] = (positions[2][0] +
                        positions[3][0]) / 2;
                positions[7][1] = positions[2][1];
                break;
            case 3:
                if ((offsetX > 0 && offsetY < 0) || ((offsetX > 0 && offsetY > 0))) {
                    //变化3点的位置   kuoda
                    positions[3][0] += max;
                    positions[3][1] += max;
                    //变化1点的X轴
                    positions[1][0] += max;
                    //变化2点的Y轴
                    positions[2][1] += max;
                } else {
                    //变化3点的位置   suoxiao
                    positions[3][0] -= max;
                    positions[3][1] -= max;
                    //变化1点的X轴
                    positions[1][0] -= max;
                    //变化2点的Y轴
                    positions[2][1] -= max;
                }
                //变化4点的X轴
                positions[4][0] = (positions[0][0] +
                        positions[1][0]) / 2;
                //变化5点的X轴,Y轴
                positions[5][0] = positions[1][0];
                positions[5][1] = (positions[1][1] +
                        positions[3][1]) / 2;
                //变化6点的Y轴
                positions[6][1] = (positions[0][1] +
                        positions[2][1]) / 2;
                //变化7点的X轴,Y轴
                positions[7][0] = (positions[2][0] +
                        positions[3][0]) / 2;
                positions[7][1] = positions[2][1];
                break;
            case 4:
                if (offsetY < 0) {
                    //变化0点的位置
                    positions[0][1] -= max;
                    //变化1点的位置
                    positions[1][1] -= max;
                } else {
                    //变化0点的位置
                    positions[0][1] += max;
                    //变化1点的位置
                    positions[1][1] += max;
                }
                //使用顶角坐标计算横线坐标
                //变化4点的Y位置
                positions[4][1] = positions[0][1];
                //变化5点的Y位置
                positions[5][1] = (positions[1][1] +
                        positions[3][1]) / 2;
                //变化6点的Y位置
                positions[6][1] = (positions[0][1] +
                        positions[2][1]) / 2;
                break;
            case 5:
                if (offsetX < 0) {
                    //变化1点的位置
                    positions[1][0] -= max;
                    //变化3点的位置
                    positions[3][0] -= max;
                } else {
                    //变化1点的位置
                    positions[1][0] += max;
                    //变化3点的位置
                    positions[3][0] += max;
                }
                //变化4点的X位置
                positions[4][0] = (positions[0][0] +
                        positions[1][0]) / 2;
                //变化5点的X位置
                positions[5][0] = positions[1][0];
                //变化7点的X位置
                positions[7][0] = (positions[2][0] +
                        positions[3][0]) / 2;
                break;
            case 6:
                if (offsetX < 0) {
                    //变化0点的位置
                    positions[0][0] -= max;
                    //变化2点的位置
                    positions[2][0] -= max;
                } else {
                    //变化0点的位置
                    positions[0][0] += max;
                    //变化2点的位置
                    positions[2][0] += max;
                }
                //变化4点的X位置
                positions[4][0] = (positions[0][0] +
                        positions[1][0]) / 2;
                //变化6点的X位置
                positions[6][0] = positions[0][0];
                //变化7点的X位置
                positions[7][0] = (positions[2][0] +
                        positions[3][0]) / 2;
                break;
            case 7:
                if (offsetY < 0) {
                    //变化2点的位置
                    positions[2][1] -= max;
                    //变化3点的位置
                    positions[3][1] -= max;
                } else {
                    //变化2点的位置
                    positions[2][1] += max;
                    //变化3点的位置
                    positions[3][1] += max;
                }
                //变化5点的X位置
                positions[5][1] = (positions[1][1] +
                        positions[3][1]) / 2;
                //变化6点的X位置
                positions[6][1] = (positions[0][1] +
                        positions[2][1]) / 2;
                //变化7点的Y位置
                positions[7][1] = positions[2][1];
                break;
        }
    }
}
