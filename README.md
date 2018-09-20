# CropView

## 简介
绘制裁剪框（设置圆形，椭圆和矩形）

## 功能
* 圆形裁剪框
* 椭圆裁剪框
* 矩形裁剪框

## ScreenShot


## 使用
1. 添加依赖

Add it in your project build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add it in your module build.gradle
```
	dependencies {
	        implementation 'com.github.wzgl5533:CropView:1.0'
	}
```
2. 添加DropDownMenu 到你的布局文件，如下：
```
<com.qlh.crop.cropviewlibrary.view.CropView
        android:visibility="visible"
        android:id="@+id/circle_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:cv_border_line_color="@color/white"//边框颜色
        app:cv_corner_line_color="@color/white"//拐角颜色
        app:cv_middle_line_color="@color/white"//中间线颜色
        app:cv_outer_bg_color="@color/black_alpha_160"//外层背景色
        app:cv_scan_line_color="@color/white"//网格线颜色
        app:cv_scan_line_width="1dp"//扫描线宽度
        app:cv_border_length="247dp"//边框长度,初始化正方形
        app:cv_border_line_width="1dp"//边框线宽度
        app:cv_corner_line_with="3dp"//拐角线宽度
        app:cv_corner_line_height="13dp"//拐角线高度
        app:cv_min_border_length="100dp"//最小边框长度
        app:cv_crop_model="rect"//裁剪模式rect  oval  circle
        app:cv_is_show_corner_line="true"//是否显示拐角线
        app:cv_is_show_border_line="true"//是否显示矩形框
        app:cv_is_show_middle_line="true"//是否显示中间线
        app:cv_is_show_scan_line="true"//是否显示扫描线
        app:cv_is_touch_corner_line_scale="false"//是触摸拐角缩放
        app:cv_is_touch_middle_line_scale="false"//是触摸中间线缩放
	/>
```
