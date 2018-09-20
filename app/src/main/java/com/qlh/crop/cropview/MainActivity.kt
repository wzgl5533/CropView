package com.qlh.crop.cropview

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //rect()
        cirCle()
    }

    private fun cirCle(){
        circle_view.setSourceBitmap(BitmapFactory.decodeResource(resources,R.drawable.d))
        circle_view.setCropBitmapCallBack {
            crop_img.setImageBitmap(it)
        }
    }

    private fun rect(){
        val b = BitmapFactory.decodeResource(resources,R.drawable.d)
//        //图片尺寸
//        val pw=b.width
//        val ph=b.height
//        val vto = bg.viewTreeObserver
//        vto.addOnPreDrawListener(object :ViewTreeObserver.OnPreDrawListener{
//            override fun onPreDraw(): Boolean {
//                //TODO("not implemented")
//                //imgView尺寸
//                val imgW = bg.width
//                val imgH = bg.height
//                //比例
//                val bx = imgW*1.0f/pw
//                val bh = imgH*1.0f/ph
//                //取最小值
//                val lp = RelativeLayout.LayoutParams((min(bx,bh)*pw).toInt(), (min(bx,bh)*ph).toInt())
//                lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE)
//                cut_rect.layoutParams = lp
//                return true
//            }
//        })

//        cut_rect.setCropBitmapCallBack {
//            crop_img.setImageBitmap(it)
//        }
    }

}
