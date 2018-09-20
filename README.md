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
	        compile 'com.github.wzgl5533:DropMenu:1.0.1'
	}
```
2. 添加DropDownMenu 到你的布局文件，如下：
```
<com.qlh.dropdownmenu.DropDownMenu
        android:id="@+id/dropDownMenu"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:dddividerColor="@color/divider" //分割线颜色
        app:ddmaskColor="@color/mask_color" //遮罩颜色，一般是半透明
        app:ddmenuBackgroundColor="@color/white" //tab 背景颜色
        app:ddmenuSelectedIcon="@drawable/drop_down_selected_icon" //tab选中状态图标
        app:ddmenuTextSize="@dimen/x40"   //tab字体大小
        app:ddmenuUnselectedIcon="@drawable/drop_down_unselected_icon" //tab未选中状态图标
        app:ddtextSelectedColor="@color/blue_light"  //tab选中颜色
        app:ddtextUnselectedColor="@color/drop_down_unselected" //tab未选中颜色
        app:ddunderlineColor="@color/divider"   //下划线颜色
        app:ddmenuMenuHeightPercent="0.6" //设置下拉弹框的最大高度比例，根据屏幕高度计算 />
```
