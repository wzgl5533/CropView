package com.qlh.crop.cropviewlibrary.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.qlh.crop.cropviewlibrary.R;
import com.qlh.crop.cropviewlibrary.callback.CropBitmapCallBack;
import com.qlh.crop.cropviewlibrary.utils.BitmapUtils;
import com.qlh.crop.cropviewlibrary.utils.CropMode;
import com.qlh.crop.cropviewlibrary.utils.QLHUtils;

/**
 * 作者：dell on 2018/9/7 18:26
 * 描述：绘制拍照推荐裁剪框
 * <br>
 * 本库目前只是简单的绘制和裁剪
 * </br>
 */
public class CropView extends View {

    private static final String TAG = "CropView";
    //四个点坐标
    public static float[][] four_corner_coordinate_positions;
    static int point = -1;// 用户按下的点
    static int max;
    private static int NOW_MOVE_STATE = 1; //移动状态，默认为1，Y轴=1，X轴=2
    private static boolean MOVE_OR_ZOOM_STATE = true; //移动或缩放状态， true 为移动
    private int VIEW_HEIGHT;   //视图高
    private int VIEW_WIDTH; //视图宽
    private String VIEW_WIDTH_PERCENT, VIEW_HEIGHT_PERCENT;//视图宽高占屏幕比例

    //单位dp
    private int BORDER_LENGTH = 200; //边框长度
    private int RECT_BORDER_WITH = 3; //长方形框框粗
    private int RECT_CORNER_WITH = 6; //四个角的粗
    private int RECT_CORNER_HEIGHT = 20; //四个角的长度
    private int MIN_BORDER_LENGTH = RECT_CORNER_HEIGHT * 5;//最小边框长度
    private int CROP_MODE = CropMode.RECT.getId();//裁剪模式

    //显示控制
    private boolean IF_SCANNING_SHOW = false;//控制在触摸(down)时显示,松开（up）时隐藏
    private boolean CORNER_SHOW = true;//拐角线
    private boolean BORDER_SHOW = false;//矩形框
    private boolean MIDDLE_SHOW = false;//中间线
    private boolean SCAN_SHOW = false;//扫描线
    private boolean TOUCH_CORNER_LINE_SCALE = true;//是触摸拐角缩放
    private boolean TOUCH_MIDDLE_LINE_SCALE = true;//是触摸中间线缩放

    private int lastX = 0;  //上次按下的X位置
    private int lastY = 0;  //上次按下的Y位置
    private int offsetX = 0;    //X轴偏移量
    private int offsetY = 0;    //Y轴偏移量
    private int POINT_STATE = -1; //判断用户是缩小还是放大 0放大 1缩小
    //颜色
    private int OUTER_BG_COLOR;//外层背景色
    private int BORDER_LINE_COLOR;//矩形颜色
    private int CORNER_LINE_COLOR = Color.WHITE;//拐角颜色
    private int MIDDLE_LINE_COLOR = Color.WHITE;//中间线颜色
    private int SCAN_LINE_COLOR = Color.WHITE;//网格颜色

    private int SCAN_LINE_WIDTH = 1;//扫描线宽度

    //裁剪完成回调
    private CropBitmapCallBack cropBitmapCallBack;
    private Bitmap sourceBitmap;//原始图片


    public CropView(Context context) {
        this(context, null);
    }

    public CropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);

        float density = getResources().getDisplayMetrics().density;  //屏幕像素密度

        handleStyleable(context, attrs, defStyleAttr, density);
    }


    private void handleStyleable(Context context, AttributeSet attrs, int defStyle, float mDensity) {

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CropView, defStyle, 0);
        OUTER_BG_COLOR = ta.getColor(R.styleable.CropView_cv_outer_bg_color, context.getResources().getColor(R.color.black_alpha_160));
        BORDER_LINE_COLOR = ta.getColor(R.styleable.CropView_cv_border_line_color, context.getResources().getColor(R.color.grey_cc));
        CORNER_LINE_COLOR = ta.getColor(R.styleable.CropView_cv_corner_line_color, CORNER_LINE_COLOR);
        MIDDLE_LINE_COLOR = ta.getColor(R.styleable.CropView_cv_middle_line_color, MIDDLE_LINE_COLOR);
        SCAN_LINE_COLOR = ta.getColor(R.styleable.CropView_cv_scan_line_color, SCAN_LINE_COLOR);

        RECT_BORDER_WITH = ta.getDimensionPixelSize(R.styleable.CropView_cv_border_line_width,
                (int) (mDensity * RECT_BORDER_WITH));
        RECT_CORNER_WITH = ta.getDimensionPixelSize(R.styleable.CropView_cv_corner_line_with,
                (int) (mDensity * RECT_CORNER_WITH));
        RECT_CORNER_HEIGHT = ta.getDimensionPixelSize(R.styleable.CropView_cv_corner_line_height,
                (int) (mDensity * RECT_CORNER_HEIGHT));

        SCAN_LINE_WIDTH = ta.getDimensionPixelSize(R.styleable.CropView_cv_scan_line_width,
                (int) (mDensity * SCAN_LINE_WIDTH));

        BORDER_LENGTH = ta.getDimensionPixelSize(R.styleable.CropView_cv_border_length,
                (int) (mDensity * BORDER_LENGTH));

        MIN_BORDER_LENGTH = ta.getDimensionPixelSize(R.styleable.CropView_cv_min_border_length,
                (RECT_CORNER_HEIGHT * 5));
        //设置最小值
        if (MIN_BORDER_LENGTH < RECT_CORNER_HEIGHT * 5) {
            MIN_BORDER_LENGTH = RECT_CORNER_HEIGHT * 5;
        }

        CROP_MODE = ta.getInt(R.styleable.CropView_cv_crop_model, CROP_MODE);
        //线条显示
        CORNER_SHOW = ta.getBoolean(R.styleable.CropView_cv_is_show_corner_line, true);
        BORDER_SHOW = ta.getBoolean(R.styleable.CropView_cv_is_show_border_line, false);
        MIDDLE_SHOW = ta.getBoolean(R.styleable.CropView_cv_is_show_middle_line, false);
        SCAN_SHOW = ta.getBoolean(R.styleable.CropView_cv_is_show_scan_line, true);
        //触摸缩放
        TOUCH_CORNER_LINE_SCALE = ta.getBoolean(R.styleable.CropView_cv_is_touch_corner_line_scale, true);
        TOUCH_MIDDLE_LINE_SCALE = ta.getBoolean(R.styleable.CropView_cv_is_touch_middle_line_scale, true);
        //宽高占屏幕比例
        VIEW_WIDTH_PERCENT = ta.getString(R.styleable.CropView_cv_width_percent);
        VIEW_HEIGHT_PERCENT = ta.getString(R.styleable.CropView_cv_height_percent);
		
		ta.recycle();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (SCAN_SHOW) {
                    IF_SCANNING_SHOW = true;//显示扫描线
                }
                if (isInTheCornerCircle(event.getX(), event.getY()) != -1) {
                    //开始缩放操作
                    MOVE_OR_ZOOM_STATE = false; //设置false为缩放状态
                    point = isInTheCornerCircle(event.getX(), event.getY());
                }
                lastX = x;
                lastY = y;
                //invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = x - lastX;
                offsetY = y - lastY;
                //判断当前是扩大还是缩小操作
                judgementXandY();
                //限定移动范围
                //移动状态：只有在移动状态下才能移动
                if (MOVE_OR_ZOOM_STATE) {
                    getoffsetXandoffsetY();
                    //四个点的坐标信息也要随之改变
                    for (int i = 0; i < four_corner_coordinate_positions.length; i++) {
                        four_corner_coordinate_positions[i][0] += offsetX;
                        four_corner_coordinate_positions[i][1] += offsetY;

                        //更新回调接口
                        /*
                        onImageDetailsSizeChanggedl.onBorderSizeChangged(
                                (int) four_corner_coordinate_positions[0][0],
                                (int) four_corner_coordinate_positions[0][1],
                                (int) BORDER_LENGTH
                        );
                        */

                        invalidate();
                    }
                    // this.scrollBy(-offsetX, -offsetY);   //这里弃用，后面改用了四点坐标移动代替背景移动
                }
                //在缩放状态下
                else {
                    //按住某一个点，该点的坐标改变，其他2个点坐标跟着改变，对点坐标不变
                    //新加入4个点的坐标后，对点和边上中点坐标不变，剩余点坐标改变
                    max = Math.abs(offsetX) >= Math.abs(offsetY) ? Math.abs(offsetX) : Math.abs(offsetY);
                    //只有在扩大操作才进行边界范围判断
                    if (POINT_STATE == 0) {
                        getoffsetXandoffsetY(); //边界范围判断
                    }
                    //缩小操作时进行边界不能太小判断
                    else if (POINT_STATE == 1) {
                        //如果边长+max太小，直接返回
                        //由于加入中间控制点，因此需要乘以2
                        if (BORDER_LENGTH - max <= MIN_BORDER_LENGTH) {
                            max = 0;
                        }
                        //加入中间控制点后，不能只以对角线判断，还需要判断长边和短边
                        float a = four_corner_coordinate_positions[0][0];
                        float b = four_corner_coordinate_positions[0][1];
                        float c = four_corner_coordinate_positions[1][0];
                        float d = four_corner_coordinate_positions[2][1];
                        float t1 = c - a;
                        float t2 = d - b;
                        if (t1 - max < RECT_CORNER_HEIGHT * 4) {
                            max = 0;
                        }
                        if (t2 - max < RECT_CORNER_HEIGHT * 4) {
                            max = 0;
                        }
                    }

                    //改变坐标
                    //changgeFourCoodinatePosition(point, offsetX, offsetY);
                    changeEightCoordinatePositions(point, offsetX, offsetY);
                    //更新边长
                    notifyNowBorderLength();
                    //更新回调接口
                    /*
                    onImageDetailsSizeChanggedl.onBorderSizeChangged(
                            (int) four_corner_coordinate_positions[0][0],
                            (int) four_corner_coordinate_positions[0][1],
                            (int) BORDER_LENGTH
                    );
                    */
                    invalidate();
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (SCAN_SHOW) {
                    IF_SCANNING_SHOW = false; //不显示扫描线
                }
                MOVE_OR_ZOOM_STATE = true; //回归为默认的移动状态
                invalidate();
//                EventBus.getDefault().post(new MovePhotoMessage());
                getCropBitMap();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (CROP_MODE == CropMode.RECT.getId()) {//矩形
            drawRect(canvas);
        } else if (CROP_MODE == CropMode.OVAL.getId()) {//椭圆
            drawOval(canvas);
        } else if (CROP_MODE == CropMode.CIRCLE.getId()) {//圆形
            drawCircle(canvas);
        }
    }

    /**
     * 初始化布局
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //如果没有改变不用重新绘制，否则会导致移动裁剪框时重绘，不能保持当前状态
        if (!changed) {
            return;
        }
        //获取视图尺寸
        VIEW_HEIGHT = this.getHeight();
        VIEW_WIDTH = this.getWidth();
        //检测裁剪框边长是否超出视图宽高范围
        validateSize();

        //初始化四个点的坐标
        four_corner_coordinate_positions = new float[][]{
                {(VIEW_WIDTH - BORDER_LENGTH) / 2, (VIEW_HEIGHT - BORDER_LENGTH) / 2}, //左上
                {(VIEW_WIDTH + BORDER_LENGTH) / 2, (VIEW_HEIGHT - BORDER_LENGTH) / 2}, //右上
                {(VIEW_WIDTH - BORDER_LENGTH) / 2, (VIEW_HEIGHT + BORDER_LENGTH) / 2}, //左下
                {(VIEW_WIDTH + BORDER_LENGTH) / 2, (VIEW_HEIGHT + BORDER_LENGTH) / 2}, //右上
                {(VIEW_WIDTH - BORDER_LENGTH) / 2 + BORDER_LENGTH / 2, (VIEW_HEIGHT - BORDER_LENGTH) / 2}, //上中
                {(VIEW_WIDTH + BORDER_LENGTH) / 2, (VIEW_HEIGHT - BORDER_LENGTH) / 2 + BORDER_LENGTH / 2}, //右中
                {(VIEW_WIDTH - BORDER_LENGTH) / 2, (VIEW_HEIGHT - BORDER_LENGTH) / 2 + BORDER_LENGTH / 2}, //左中
                {(VIEW_WIDTH - BORDER_LENGTH) / 2 + BORDER_LENGTH / 2, (VIEW_HEIGHT + BORDER_LENGTH) / 2}  //下中
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //检查百分比设置是否生效
        String reg = "^[1-9][0-9]{1,2}%[wh]";
        //如果是精确的，好说，是多少，就给多少；
        if (widthMode == MeasureSpec.EXACTLY && widthSize == 0){
            if (!QLHUtils.isEmpty(VIEW_WIDTH_PERCENT) && VIEW_WIDTH_PERCENT.matches(reg)) {
                String[] widths = VIEW_WIDTH_PERCENT.split("%");
                //获取数值
                int value = Integer.valueOf(widths[0]);
                //获取参考系
                String reference = widths[1];
                if ("w".equals(reference)) {//参考宽度
                    widthSize = value * QLHUtils.getScreenWidth(getContext())/100 > QLHUtils.getScreenWidth(getContext())
                            ? QLHUtils.getScreenWidth(getContext())
                            : value * QLHUtils.getScreenWidth(getContext())/100;
                }
                if ("h".equals(reference)){
                    widthSize = value * QLHUtils.getScreenHeight(getContext())/100 > QLHUtils.getScreenHeight(getContext())
                            ? QLHUtils.getScreenHeight(getContext())
                            : value * QLHUtils.getScreenHeight(getContext())/100;
                }
            }
        }

        if (heightMode == MeasureSpec.EXACTLY && heightSize == 0){
            if (!QLHUtils.isEmpty(VIEW_HEIGHT_PERCENT) && VIEW_HEIGHT_PERCENT.matches(reg)) {
                String[] heights = VIEW_HEIGHT_PERCENT.split("%");
                //获取数值
                int value = Integer.valueOf(heights[0]);
                //获取参考系
                String reference = heights[1];
                if ("w".equals(reference)) {//参考宽度
                    heightSize = value * QLHUtils.getScreenWidth(getContext())/100 > QLHUtils.getScreenWidth(getContext())
                            ? QLHUtils.getScreenWidth(getContext())
                            : value * QLHUtils.getScreenWidth(getContext())/100;
                }
                if ("h".equals(reference)){
                    heightSize = value * QLHUtils.getScreenHeight(getContext())/100 > QLHUtils.getScreenHeight(getContext())
                            ? QLHUtils.getScreenHeight(getContext())
                            : value * QLHUtils.getScreenHeight(getContext())/100;
                }
            }
        }

        //最后不要忘记了，调用父类的测量方法
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 验证裁剪框的尺寸是否超出视图范围
     * **/

    private void validateSize(){
        if (BORDER_LENGTH<MIN_BORDER_LENGTH){
            BORDER_LENGTH = MIN_BORDER_LENGTH;
        }
        if (BORDER_LENGTH+RECT_CORNER_WITH*2>=Math.min(VIEW_WIDTH,VIEW_HEIGHT)){
            BORDER_LENGTH = Math.min(VIEW_WIDTH,VIEW_HEIGHT)-RECT_CORNER_HEIGHT*3;
            //MIN_BORDER_LENGTH = BORDER_LENGTH;
        }
        if (RECT_CORNER_HEIGHT*3>=BORDER_LENGTH){
            RECT_CORNER_HEIGHT = (int) (BORDER_LENGTH/5);
        }

    }

    /**
     * 矩形裁剪模式
     **/
    private void drawRect(Canvas canvas) {

        //先绘制黑色透明度背景
        drawAlphaBg(canvas);
        //矩形框
        if (BORDER_SHOW) {
            drawBorderRect(canvas);
        }
        //四个拐角线
        if (CORNER_SHOW) {
            drawCornerLine(canvas);
        }
        //四个中间线
        if (MIDDLE_SHOW) {
            drawMiddleLine(canvas);
        }
        //画扫描线
        if (SCAN_SHOW && IF_SCANNING_SHOW) {
            drawScanLine(canvas);
        }
    }

    /**
     * 矩形裁剪模式
     **/
    private void drawOval(Canvas canvas) {

        //圆形裁剪框
        drawOvalUsePath(canvas);
        //绘制矩形框
        if (BORDER_SHOW) {
            drawBorderRect(canvas);
        }
        //四个拐角线
        if (CORNER_SHOW) {
            drawCornerLine(canvas);
        }
        //四个中间线
        if (MIDDLE_SHOW) {
            drawMiddleLine(canvas);
        }
    }

    /**
     * 圆形裁剪模式
     **/
    private void drawCircle(Canvas canvas) {

        //圆形裁剪框
        drawCircleUsePath(canvas);
        //四个拐角线
        if (CORNER_SHOW) {
            drawCornerLine(canvas);
        }
        //四个横线
        if (MIDDLE_SHOW) {
            drawMiddleLine(canvas);
        }
    }

    //-----------------------------------------绘制方法START----------------------------------------//

    /**
     * 绘制黑色半透明背景
     * 根据四个角的坐标范围，绘制多个矩形拼接
     **/
    private void drawAlphaBg(Canvas canvas) {
        Paint paintRect = new Paint();  //初始化画笔
        //画边框的画笔
        paintRect.setColor(OUTER_BG_COLOR);    //颜色
        paintRect.setStyle(Paint.Style.FILL); //设置实心
        //上半部分背景
        canvas.drawRect(0, 0,
                getWidth(),
                four_corner_coordinate_positions[0][1],
                paintRect);
        //下半部分背景
        canvas.drawRect(0, four_corner_coordinate_positions[2][1],
                getWidth(),
                getHeight(),
                paintRect);
        //左边背景
        canvas.drawRect(0,
                four_corner_coordinate_positions[0][1],
                four_corner_coordinate_positions[2][0],
                four_corner_coordinate_positions[2][1],
                paintRect);
        //右边背景
        canvas.drawRect(four_corner_coordinate_positions[1][0],
                four_corner_coordinate_positions[1][1],
                getWidth(),
                four_corner_coordinate_positions[3][1],
                paintRect);

        paintRect.reset();
    }

    /**
     * 绘制矩形框
     **/
    private void drawBorderRect(Canvas canvas) {

        Paint paintRect = new Paint();  //初始化画笔
        //画边框的画笔
        paintRect.setColor(BORDER_LINE_COLOR);    //颜色
        paintRect.setStrokeWidth(RECT_BORDER_WITH);    //宽度
        paintRect.setAntiAlias(true);   //抗锯齿
        paintRect.setStyle(Paint.Style.STROKE); //设置空心
        canvas.drawRect(four_corner_coordinate_positions[0][0],
                four_corner_coordinate_positions[0][1],
                four_corner_coordinate_positions[3][0],
                four_corner_coordinate_positions[3][1],
                paintRect);

        paintRect.reset();
    }

    /**
     * 绘制四个拐角线
     **/
    private void drawCornerLine(Canvas canvas) {
        Paint paintRect = new Paint();
        //画四个角的画笔
        paintRect.setColor(CORNER_LINE_COLOR);
        paintRect.setStrokeWidth(RECT_CORNER_WITH);
        paintRect.setAntiAlias(true);
        //左上角的两根
        //左
        canvas.drawLine(four_corner_coordinate_positions[0][0] - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[0][1] - RECT_CORNER_WITH,
                four_corner_coordinate_positions[0][0] - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[0][1] + RECT_CORNER_HEIGHT - RECT_CORNER_WITH,
                paintRect);
        //上
        canvas.drawLine(four_corner_coordinate_positions[0][0] - RECT_CORNER_WITH,
                four_corner_coordinate_positions[0][1] - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[0][0] + RECT_CORNER_HEIGHT - RECT_CORNER_WITH,
                four_corner_coordinate_positions[0][1] - RECT_CORNER_WITH / 2,
                paintRect);

        //左下角的两根
        //左
        canvas.drawLine(four_corner_coordinate_positions[2][0] - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[2][1] + RECT_CORNER_WITH,
                four_corner_coordinate_positions[2][0] - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[2][1] - RECT_CORNER_HEIGHT + RECT_CORNER_WITH,
                paintRect);
        //下
        canvas.drawLine(four_corner_coordinate_positions[2][0] - RECT_CORNER_WITH,
                four_corner_coordinate_positions[2][1] + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[2][0] + RECT_CORNER_HEIGHT - RECT_CORNER_WITH,
                four_corner_coordinate_positions[2][1] + RECT_CORNER_WITH / 2,
                paintRect);

        //右上角的两根
        //上
        canvas.drawLine(four_corner_coordinate_positions[1][0] + RECT_CORNER_WITH,
                four_corner_coordinate_positions[1][1] - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[1][0] + RECT_CORNER_WITH - RECT_CORNER_HEIGHT,
                four_corner_coordinate_positions[1][1] - RECT_CORNER_WITH / 2,
                paintRect);
        //右
        canvas.drawLine(four_corner_coordinate_positions[1][0] + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[1][1] - RECT_CORNER_WITH,
                four_corner_coordinate_positions[1][0] + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[1][1] + RECT_CORNER_HEIGHT - RECT_CORNER_WITH
                , paintRect);

        //右下角的两根
        //下
        canvas.drawLine(four_corner_coordinate_positions[3][0] + RECT_CORNER_WITH,
                four_corner_coordinate_positions[3][1] + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[3][0] + RECT_CORNER_WITH - RECT_CORNER_HEIGHT,
                four_corner_coordinate_positions[3][1] + RECT_CORNER_WITH / 2,
                paintRect);
        //右
        canvas.drawLine(four_corner_coordinate_positions[3][0] + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[3][1] + RECT_CORNER_WITH,
                four_corner_coordinate_positions[3][0] + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[3][1] + RECT_CORNER_WITH - RECT_CORNER_HEIGHT,
                paintRect);

        paintRect.reset();

    }

    /**
     * 绘制四个横线
     **/
    private void drawMiddleLine(Canvas canvas) {
        Paint paintRect = new Paint();
        //画四个角的画笔
        paintRect.setColor(MIDDLE_LINE_COLOR);
        paintRect.setStrokeWidth(RECT_CORNER_WITH);
        paintRect.setAntiAlias(true);
        //上中
        canvas.drawLine(four_corner_coordinate_positions[4][0] - RECT_CORNER_HEIGHT / 2,
                four_corner_coordinate_positions[4][1] - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[4][0] + RECT_CORNER_HEIGHT / 2,
                four_corner_coordinate_positions[4][1] - RECT_CORNER_WITH / 2,
                paintRect);

        //右中
        canvas.drawLine(four_corner_coordinate_positions[5][0] + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[5][1] - RECT_CORNER_HEIGHT / 2,
                four_corner_coordinate_positions[5][0] + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[6][1] + RECT_CORNER_HEIGHT / 2,
                paintRect);

        //左中
        canvas.drawLine(four_corner_coordinate_positions[6][0] - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[6][1] - RECT_CORNER_HEIGHT / 2,
                four_corner_coordinate_positions[6][0] - RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[6][1] + RECT_CORNER_HEIGHT / 2,
                paintRect);

        //下中
        canvas.drawLine(four_corner_coordinate_positions[7][0] - RECT_CORNER_HEIGHT / 2,
                four_corner_coordinate_positions[7][1] + RECT_CORNER_WITH / 2,
                four_corner_coordinate_positions[7][0] + RECT_CORNER_HEIGHT / 2,
                four_corner_coordinate_positions[7][1] + RECT_CORNER_WITH / 2,
                paintRect);

        paintRect.reset();
    }

    /**
     * 绘制扫描线
     **/
    private void drawScanLine(Canvas canvas) {
        Paint paintRect = new Paint();
        paintRect.setColor(SCAN_LINE_COLOR);
        paintRect.setStrokeWidth(SCAN_LINE_WIDTH);
        paintRect.setAntiAlias(true);
        paintRect.setStyle(Paint.Style.STROKE);
        //共四根线
        //竖1
        canvas.drawLine((four_corner_coordinate_positions[0][0] + four_corner_coordinate_positions[4][0]) / 2,
                four_corner_coordinate_positions[0][1],
                (four_corner_coordinate_positions[0][0] + four_corner_coordinate_positions[4][0]) / 2,
                four_corner_coordinate_positions[2][1],
                paintRect);
        //竖2
        canvas.drawLine(four_corner_coordinate_positions[4][0],
                four_corner_coordinate_positions[4][1],
                four_corner_coordinate_positions[7][0],
                four_corner_coordinate_positions[7][1],
                paintRect);
        //竖3
        canvas.drawLine((four_corner_coordinate_positions[1][0] + four_corner_coordinate_positions[4][0]) / 2,
                four_corner_coordinate_positions[1][1],
                (four_corner_coordinate_positions[1][0] + four_corner_coordinate_positions[4][0]) / 2,
                four_corner_coordinate_positions[3][1],
                paintRect);
        //横1
        canvas.drawLine(four_corner_coordinate_positions[0][0],
                (four_corner_coordinate_positions[0][1] + four_corner_coordinate_positions[6][1]) / 2,
                four_corner_coordinate_positions[1][0],
                (four_corner_coordinate_positions[0][1] + four_corner_coordinate_positions[6][1]) / 2,
                paintRect);
        //横2
        canvas.drawLine(four_corner_coordinate_positions[6][0],
                four_corner_coordinate_positions[6][1],
                four_corner_coordinate_positions[5][0],
                four_corner_coordinate_positions[5][1],
                paintRect);
        //横3
        canvas.drawLine(four_corner_coordinate_positions[0][0],
                (four_corner_coordinate_positions[2][1] + four_corner_coordinate_positions[6][1]) / 2,
                four_corner_coordinate_positions[1][0],
                (four_corner_coordinate_positions[2][1] + four_corner_coordinate_positions[6][1]) / 2,
                paintRect);

        paintRect.reset();
    }

    /**
     * 绘制椭圆（路径剪切法）
     * 如果是几何形的预览框，
     * 那么首推限制绘画区域的方案,
     * 内存占用率低。
     **/
    private void drawOvalUsePath(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //创建圆形预览框
        Path path = new Path();
        path.addOval(new RectF(
                        four_corner_coordinate_positions[0][0],
                        four_corner_coordinate_positions[0][1],
                        four_corner_coordinate_positions[1][0],
                        four_corner_coordinate_positions[2][1]
                ),
                Path.Direction.CW);
        //保存当前canvas 状态
        canvas.save();
        //将当前画布可以绘画区域限制死为预览框外的区域
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        //绘画半透明遮罩
        canvas.drawColor(OUTER_BG_COLOR);
        //还原画布状态
        canvas.restore();

    }

    /**
     * 绘制圆形（路径剪切法）
     * 如果是几何形的预览框，
     * 那么首推限制绘画区域的方案,
     * 内存占用率低。
     **/
    private void drawCircleUsePath(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        //创建圆形预览框
        Path path = new Path();
        path.addCircle(
                (four_corner_coordinate_positions[0][0] + four_corner_coordinate_positions[1][0]) / 2,
                (four_corner_coordinate_positions[0][1] + four_corner_coordinate_positions[2][1]) / 2,
                BORDER_LENGTH / 2,
                Path.Direction.CW);
        //保存当前canvas 状态
        canvas.save();
        //将当前画布可以绘画区域限制死为预览框外的区域
        canvas.clipPath(path, Region.Op.DIFFERENCE);
        //绘画半透明遮罩
        canvas.drawColor(OUTER_BG_COLOR);
        //还原画布状态
        canvas.restore();

    }

    //-----------------------------------------绘制方法END----------------------------------------//

    /**
     * 判断按下的点在圆圈内
     *
     * @param x 按下的X坐标
     * @param y 按下的Y坐标
     * @return 返回按到的是哪个点, 没有则返回-1
     * 点阵示意：
     * 0   1
     * 2   3
     */
    private int isInTheCornerCircle(float x, float y) {
        Integer[] except = null;
        if (TOUCH_CORNER_LINE_SCALE && TOUCH_MIDDLE_LINE_SCALE) {//全部触发缩放
            except = null;
        } else if (TOUCH_CORNER_LINE_SCALE) {//拐角触发缩放
            except = new Integer[]{4, 5, 6, 7};
        } else if (TOUCH_MIDDLE_LINE_SCALE) {//中间线触发缩放
            except = new Integer[]{0, 1, 2, 3};
        } else {
            except = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7};
        }
        return QLHUtils.isInTheCornerCircle(x, y,
                four_corner_coordinate_positions,
                RECT_CORNER_HEIGHT, except);
    }

    /**
     * POINT_STATE 为0放大， 1缩小
     */
    private void judgementXandY() {
        switch (point) {
            case 0:
                if ((offsetX <= 0 && offsetY <= 0) || (offsetX <= 0 && offsetY >= 0)) {
                    POINT_STATE = 0;//扩大
                } else {
                    POINT_STATE = 1;//缩小
                }
                break;
            case 1:
                if ((offsetX >= 0 && offsetY <= 0) || (offsetX >= 0 && offsetY >= 0)) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;
            case 2:
                if ((offsetX <= 0 && offsetY >= 0) || (offsetX <= 0 && offsetY <= 0)) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;
            case 3:
                if ((offsetX >= 0 && offsetY >= 0) || (offsetX >= 0 && offsetY <= 0)) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;

            //新加的中间控制点
            //上中
            case 4:
                if (offsetY <= 0) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;
            //右中
            case 5:
                if (offsetX >= 0) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;
            //左中
            case 6:
                if (offsetX <= 0) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;
            //下中
            case 7:
                if (offsetY >= 0) {
                    POINT_STATE = 0;
                } else {
                    POINT_STATE = 1;
                }
                break;
        }
    }

    /**
     * 防止X和Y溢出边界的算法
     */
    private void getoffsetXandoffsetY() {
        //如果是移动状态
        if (MOVE_OR_ZOOM_STATE) {
            if ((four_corner_coordinate_positions[0][0] + offsetX <= 0) ||
                    (four_corner_coordinate_positions[1][0] + offsetX >= VIEW_WIDTH)
                    ) {
                offsetX = 0;
            }

            if ((four_corner_coordinate_positions[0][1] + offsetY <= 0) ||
                    (four_corner_coordinate_positions[2][1] + offsetY >= VIEW_HEIGHT)
                    ) {
                offsetY = 0;
            }
        }
        //如果是缩放状态
        else {

            switch (point) {
                case 0:
                    if ((four_corner_coordinate_positions[0][0] - max <= 0) ||
                            (four_corner_coordinate_positions[0][1] - max <= 0)
                            ) {
                        max = 0;
                    }
                    break;
                case 1:
                    if ((four_corner_coordinate_positions[1][0] + max >= VIEW_WIDTH) ||
                            (four_corner_coordinate_positions[1][1] - max <= 0)
                            ) {
                        max = 0;
                    }
                    break;
                case 2:
                    if ((four_corner_coordinate_positions[2][0] - max <= 0) ||
                            (four_corner_coordinate_positions[2][1] + max >= VIEW_HEIGHT)
                            ) {
                        max = 0;
                    }
                    break;
                case 3:
                    if ((four_corner_coordinate_positions[3][0] + max >= VIEW_WIDTH) ||
                            (four_corner_coordinate_positions[3][1] + max >= VIEW_HEIGHT)
                            ) {
                        max = 0;
                    }
                    break;
                //新增中间控制点的溢出算法
                case 4:
                    if ((four_corner_coordinate_positions[4][1] - max <= 0)) {
                        max = 0;
                    }
                    break;
                case 5:
                    if ((four_corner_coordinate_positions[5][0] + max >= VIEW_WIDTH)) {
                        max = 0;
                    }
                    break;
                case 6:
                    if ((four_corner_coordinate_positions[6][0] - max <= 0)) {
                        max = 0;
                    }
                    break;
                case 7:
                    if ((four_corner_coordinate_positions[7][1] + max >= VIEW_HEIGHT)) {
                        max = 0;
                    }
                    break;
            }
        }
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
     * @param point   用户按的点
     * @param offsetX X轴偏移量
     * @param offsetY Y轴偏移量
     */
    private void changeEightCoordinatePositions(int point, int offsetX, int offsetY) {
        QLHUtils.changePositions(point, offsetX, offsetY, four_corner_coordinate_positions, max);
    }

    /**
     * 更新矩形框对角线的方法
     */
    private void notifyNowBorderLength() {
        float a = four_corner_coordinate_positions[0][0];
        float b = four_corner_coordinate_positions[0][1];
        float c = four_corner_coordinate_positions[1][0];
        float d = four_corner_coordinate_positions[1][1];
        float temp1 = (float) Math.pow(a - c, 2);
        float temp2 = (float) Math.pow(b - d, 2);
        BORDER_LENGTH = (int) Math.sqrt(temp1 + temp2);
    }


    //裁剪图片
    private void getCropBitMap() {

        if (cropBitmapCallBack != null && sourceBitmap != null) {
            cropBitmapCallBack.getBitmap(BitmapUtils.getCropPicture(sourceBitmap,
                    VIEW_WIDTH,
                    VIEW_HEIGHT,
                    new RectF(four_corner_coordinate_positions[0][0],
                            four_corner_coordinate_positions[0][1],
                            four_corner_coordinate_positions[1][0],
                            four_corner_coordinate_positions[2][1]),
                    CROP_MODE));
        }
    }

    public void setCropBitmapCallBack(CropBitmapCallBack callBack) {
        cropBitmapCallBack = callBack;
    }

    public void setSourceBitmap(Bitmap source) {
        sourceBitmap = source;
    }
}

