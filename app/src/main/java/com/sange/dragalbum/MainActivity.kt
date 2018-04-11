package com.sange.dragalbum

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.sange.album.IAlbumImageLoader
import com.sange.album.IOnItemClickListener
import com.sange.album.PhotoItem
import com.sange.dragalbum.util.LogUtil
import com.sange.dragalbum.util.MyUtil
import com.sange.dragalbum.util.imageLoader.ImageLoaderUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 初始化rootView
        dav_view.mRootView = gl_view
        // 设置图片数据 和 加载图片实现类
        dav_view.setImages(MyUtil().moreItems(12,getData()),object : IAlbumImageLoader{
            override fun displayImage(url: String, imageView: ImageView) {
                // 图片加载框架（这里是二次封装的glide框架）
                ImageLoaderUtil.with(this@MainActivity).displayImage(url, imageView)
            }
        })
        // item点击事件
        dav_view.clickListener = object : IOnItemClickListener{
            override fun onItemClick(view: View, position: Int, isPhoto: Boolean) {
                LogUtil.i(MainActivity::class.java, "点击了$position")
            }
        }
    }

    private fun getData(): MutableList<PhotoItem> {
        val mDataList = arrayListOf<PhotoItem>()
        var item = PhotoItem()
        item.imageUri = "android.resource://com.sange.dragalbum/drawable/"+R.drawable.test2// "http://www.qqpk.cn/Article/UploadFiles/201203/20120301134818217.jpg"
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "android.resource://com.sange.dragalbum/drawable/"+R.drawable.test2// "http://img.duoziwang.com/2016/08/09/23042617311.png"
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "android.resource://com.sange.dragalbum/drawable/"+R.drawable.test2// "http://imgsrc.baidu.com/forum/w%3D580/sign=e62489e1ccbf6c81f7372ce08c3fb1d7/2d82d5cec3fdfc03e8e38e31d43f8794a5c2261a.jpg"
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "android.resource://com.sange.dragalbum/drawable/"+R.drawable.test2// "http://img.zcool.cn/community/01b38d554100a6000001e71bcdb382.jpg"
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "android.resource://com.sange.dragalbum/drawable/"+R.drawable.test2// "http://www.ld12.com/upimg358/allimg/20160303/mfqwumd52fh14087.jpg"
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "android.resource://com.sange.dragalbum/drawable/"+R.drawable.test2// "http://img.qqtouxiang8.net/uploads/allimg/c150313/142622Jc49C0-1Bb44.jpg"
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "http://www.qqpk.cn/Article/UploadFiles/201112/20111202144857495.jpg"
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "https://pic3.zhimg.com/80/v2-e2ae9159cf260da0ae61951bf1926abe_hd.jpg"
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "https://pic2.zhimg.com/80/v2-2b86ee8ba2e3a8f2a6654832f96379f6_hd.jpg"
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "android.resource://com.sange.dragalbum/drawable/"+R.drawable.test2
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "android.resource://com.sange.dragalbum/drawable/"+R.drawable.test2
        mDataList.add(item)

        item = PhotoItem()
        item.imageUri = "android.resource://com.sange.dragalbum/drawable/"+R.drawable.test2
        mDataList.add(item)
        return mDataList
    }
}
