package com.saitejajanjirala.mychatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.saitejajanjirala.mychatapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_viewfullimage.view.*

class ViewfullimageActivity : AppCompatActivity() {
    lateinit var image:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewfullimage)
        image=findViewById(R.id.imageviewr)
        val url=intent.getStringExtra("url")!!
        Picasso.get().load(url).error(android.R.drawable.stat_notify_error).into(image)
    }
}