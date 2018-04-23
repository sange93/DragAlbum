# DragAlbum
一个可拖拽的图片展示控件
目前只支持12个图片展示  第一个图片占四个小图位置

[![](https://jitpack.io/v/sange93/DragAlbum.svg)](https://jitpack.io/#sange93/DragAlbum)

## Preview
![](https://github.com/sange93/DragAlbum/blob/master/images/album.gif)
## Dependency
Step 1.Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
  
Step 2.Add the dependency

	dependencies {
	        implementation 'com.github.sange93:DragAlbum:1.1.3'
	}

## Usage
Step 1. Add the view to your xml file
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.sange.album.DragAlbumView
        android:id="@+id/dav_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#a0a0a0"/>
    <GridLayout
        android:id="@+id/gl_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</android.support.constraint.ConstraintLayout>
```

Step 2. Set the view in your java/kotlin file.
```kotlin
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
```
  
  That's it!
