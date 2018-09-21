package com.qlh.crop.cropview

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cirCle()
    }

    private fun cirCle(){
        crop_view.setSourceBitmap(BitmapFactory.decodeResource(resources,R.drawable.d))
        crop_view.setCropBitmapCallBack {
            crop_img.setImageBitmap(it)
            Toast.makeText(this,"裁剪完成",Toast.LENGTH_SHORT).show()
        }
    }
}
