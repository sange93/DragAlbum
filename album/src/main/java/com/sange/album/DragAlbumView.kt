package com.sange.album

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.support.v4.view.ViewCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.AbsListView
import android.widget.GridLayout
import android.widget.ImageView
import java.util.*
import java.util.Collections.swap

/**
 *可拖拽的 相册View (全屏字帖 第一个字较大)
 * Created by ssq on 2018/2/25.
 */
class DragAlbumView : ViewGroup ,View.OnTouchListener{

    // 每个item之间的间隙
    var padding: Int = -1
    // 根据数据 获取的 最大可拖拽的控件
    private var maxSize: Int = 0
    // 其它item的宽高
    private var mItemWidth: Int = 0
    // 第一个最大的view的宽高
    private var mItemOne: Int = 0
    // 当前隐藏控件的位置
    private var hidePosition = -1
    // 当前控件 距离屏幕 顶点 的高度
    private var mTopHeight = -1
    // 动画处于停止状态
    private var mAnimationEnd = true

    // 刚开始拖拽的item对应的View
    private var mStartDragItemView: View? = null
    /**
     * 用于拖拽的镜像，这里直接用一个ImageView
     */
    private var mDragImageView: ImageView? = null
    /**
     * 我们拖拽的item对应的Bitmap
     */
    private var mDragBitmap: Bitmap? = null

    // 为了兼容小米系统 就不用WindowManager了 如果本类生成view就不能拖到全屏 所以我们在最外层生成一个view传递过来
    var mRootView: GridLayout? = null
    private val mImages = arrayListOf<PhotoItem>()
    var clickListener: IOnItemClickListener? = null

    private var itemDownX = -1
    private var itemDownY = -1
    private var strTime: Long = 0

//    private var mItemCount = 1
//    private var isReverse = false
    private var mViewHeight = 0

    // 正在拖拽的position
    private var mDragPosition: Int = 0
    private var mDownX: Int = 0
    private var mDownY: Int = 0
    private var isOnItemClick = false

    // x,y坐标的计算
    private var dragPointX: Int = 0
    private var dragPointY: Int = 0
    private var dragOffsetX: Int = 0
    private var dragOffsetY: Int = 0

    private var moveX: Int = 0
    private var moveY: Int = 0

    private var resultSet: AnimatorSet? = null
    // 必须实现此接口 否则图片不显示
    private var imageLoader: IAlbumImageLoader? = null

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        padding = dp2px(4)
        initUI()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context,attrs,0)
    constructor(context: Context) : this(context,null)

    /**
     * 设置显示的图像集
     */
    fun setImages(images: MutableList<PhotoItem>,loader: IAlbumImageLoader){
        imageLoader = loader
        mImages.clear()
        mImages.addAll(images)
        initUI()
    }

    fun getImages() : MutableList<PhotoItem> = mImages
    /**
     * 开始往ViewGroup里面填充子控件，并设置Tag和触摸事件
     */
    private fun initUI() {
        removeAllViews()
        for (i in mImages.indices){
            val view = ImageView(context)
            view.setBackgroundColor(Color.WHITE)
            view.scaleType = ImageView.ScaleType.FIT_XY
//            view.tag = i
            view.setTag(R.id.id_drag_album_view_2_position,i)
            view.setOnTouchListener(this)
            if (!TextUtils.isEmpty(mImages[i].imageUri)) maxSize = i
            imageLoader?.displayImage(mImages[i].imageUri,view)
//            ImageLoaderUtil.with(context).displayImage(mImages[i].imageUri,view)
            addView(view)
        }
    }

    /**
     * 1、View通过layout方法来确认自己在父容器中的位置
     * 2、ViewGroup通过onLayout 方法来确定View在容器中的位置
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var l = l
        var t = t
        mItemWidth = measuredWidth / 5 - padding - padding / 5
        var i = 0
        val size = childCount
        while (i < size) {
            val view = getChildAt(i)
            when(i){
                0 -> {
                    mItemOne = mItemWidth * 2 + padding
                    l += padding
                    t += padding
                    view.layout(l, t, l + mItemOne, t + mItemOne)
                    l += mItemOne + padding
                }
                1,2,4,5 -> {
                    view.layout(l, t, l + mItemWidth, t + mItemWidth)
//                    t += mItemWidth + padding
                    l += mItemWidth + padding
                }
                3-> {
                    view.layout(l, t, l + mItemWidth, t + mItemWidth)
                    t += mItemWidth + padding
                    l = mItemOne + padding * 2
                }
                6 -> {
                    view.layout(l, t, l + mItemWidth, t + mItemWidth)
                    t += mItemWidth + padding
                    l = padding
                }
                in 7 .. 11 ->{
                    view.layout(l, t, l + mItemWidth, t + mItemWidth)
//                    t += mItemWidth + padding
                    l += mItemWidth + padding
                }
                /*else -> {
                    view.layout(l, t, l + mItemWidth, t + mItemWidth)
                    if (mItemCount % 5 == 0) {
                        isReverse = !isReverse
                        t += mItemWidth + padding
                    } else {
                        if (isReverse) {
                            l += mItemWidth + padding
                        } else {
                            l -= mItemWidth + padding
                        }
                    }
                    mItemCount++
                }*/
            }

            if (i == hidePosition) {
                view.visibility = View.GONE
                mStartDragItemView = view
            }
            i++
        }
        mViewHeight = t
    }

    /**
     * 分发触摸事件
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        return false// 关闭触摸事件
        when(ev?.action){
            MotionEvent.ACTION_DOWN -> {
                isOnItemClick = false
                removeCallbacks(mDragRunnable)
                mDownX = ev.x.toInt()
                mDownY = ev.y.toInt()
                mDragPosition = pointToPosition(mDownX, mDownY)
                if (mDragPosition > maxSize) {
                    return super.dispatchTouchEvent(ev)
                }
                if (mDragPosition == -1) {
                    return super.dispatchTouchEvent(ev)
                }
                // 根据position获取该item所对应的View
                mStartDragItemView = getChildAt(mDragPosition)
                // 获取BitMap
                mStartDragItemView?.isDrawingCacheEnabled = true
                mDragBitmap = Bitmap.createBitmap(mStartDragItemView?.drawingCache)
                mStartDragItemView?.destroyDrawingCache()

                dragPointX = mStartDragItemView!!.left + mStartDragItemView!!.width / 2 - mStartDragItemView!!.left
                dragPointY = mStartDragItemView!!.top + mStartDragItemView!!.height / 2 - mStartDragItemView!!.top
                dragOffsetX = (ev.rawX - mDownX).toInt()
                dragOffsetY = (ev.rawY - mDownY).toInt()
                postDelayed(mDragRunnable, 50)
            }
            MotionEvent.ACTION_MOVE -> {
                moveX = ev.x.toInt()
                moveY = ev.y.toInt()
                if (mDragImageView != null) {
                    onDragItem(moveX - dragPointX + dragOffsetX, moveY - dragPointY + dragOffsetY - mTopHeight)
                    onSwapItem(moveX, moveY)
                }
            }
            MotionEvent.ACTION_UP -> {
                onStopDrag()
                removeCallbacks(mDragRunnable)
            }
            MotionEvent.ACTION_CANCEL -> {
                onStopDrag()
                removeCallbacks(mDragRunnable)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                itemDownX = event.x.toInt()
                itemDownY = event.y.toInt()
                strTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_UP -> {
                val dragPosition: Int = v?.getTag(R.id.id_drag_album_view_2_position) as Int
                if (dragPosition <= maxSize){
                    val absMoveDistanceX = Math.abs(event.x - itemDownX)
                    val absMoveDistanceY = Math.abs(event.y - itemDownY)
                    if (absMoveDistanceX < 20 && absMoveDistanceY < 20 && System.currentTimeMillis() - strTime < 200) {
                        if (clickListener != null) {
                            isOnItemClick = true
                            clickListener!!.onItemClick(getChildAt(dragPosition), dragPosition, true)
                        } else {
                            isOnItemClick = false
                        }
                    } else {
                        isOnItemClick = false
                    }
                }else{
                    if (clickListener != null) {
                        isOnItemClick = true
                        clickListener!!.onItemClick(getChildAt(dragPosition), dragPosition, false)
                    } else {
                        isOnItemClick = false
                    }
                }
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        var resWidth: Int
//        var resHeight: Int
        /**
         * 根据传入的参数，分别获取测量模式和测量值
         */
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
//        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)

        val height = View.MeasureSpec.getSize(heightMeasureSpec)
//        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        // 如果宽或者高的测量模式非精确值
        /*if (widthMode != View.MeasureSpec.EXACTLY || heightMode != View.MeasureSpec.EXACTLY) {
            // 主要设置为背景图的高度
            resWidth = suggestedMinimumWidth
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resWidth = if (resWidth == 0) getDefaultWidth() else resWidth
            resHeight = suggestedMinimumHeight
            // 如果未设置背景图片，则设置为屏幕宽高的默认值
            resHeight = if (resHeight == 0) mViewHeight else resHeight
        } else {
            // 如果都设置为精确值，则直接取小值；
            resHeight = Math.min(width, height)
            resWidth = resHeight
        }
        resWidth = width
        resHeight = height*/
        setMeasuredDimension(width, height)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (mTopHeight <= 0) {
            mTopHeight = getTopHeight()
        }
    }

    /**
     * 获取当前控件 距离屏幕 顶点 的高度
     */
    private fun getTopHeight(): Int {
        val location = IntArray(2)
        getLocationOnScreen(location)
        var statusHeight = location[1]
        if (0 == statusHeight) {
            getLocationInWindow(location)
            statusHeight = location[1]
        }
        return statusHeight
    }

    // 用来处理是否为长按的Runnable
    private val mDragRunnable = Runnable {
        // 根据我们按下的点显示item镜像
        if (isOnItemClick)
            return@Runnable
        if (mStartDragItemView!!.isShown) {
            createDragImage()
            mStartDragItemView!!.visibility = View.GONE
        }
    }

    private var frame = Rect()

    /**
     * 判断按下的位置是否在Item上 并返回Item的位置 [#pointToPosition(int, int)][AbsListView]
     */
    private fun pointToPosition(x: Int, y: Int): Int {
        val count = childCount
        for (i in count - 1 downTo 0) {
            val child = getChildAt(i)
            if (child.visibility == View.VISIBLE) {
                child.getHitRect(frame)
                if (frame.contains(x, y)) {
                    return i
                }
            }
        }
        return -1
    }

    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private fun onStopDrag() {
        removeDragImage()
        hidePosition = -1
    }

    /**
     * 从界面上面移动拖动镜像
     */
    private fun removeDragImage() {
        if (mDragImageView != null) {
            mRootView?.removeView(mDragImageView)
            mDragImageView = null
            if (mStartDragItemView != null)
                mStartDragItemView?.visibility = View.VISIBLE
        }
    }

    private var translationAnimator: ObjectAnimator? = null

    /**
     * 创建拖动的镜像
     */
    @SuppressLint("ObjectAnimatorBinding")
    private fun createDragImage() {
        mStartDragItemView ?: return
        val location = IntArray(2)
        mStartDragItemView?.getLocationOnScreen(location)
        val drX = location[0].toFloat()
        val drY = (location[1] - mTopHeight).toFloat()
        if (mDragImageView == null) {
            mDragImageView = ImageView(context)
        } else {
            val parent = mDragImageView!!.parent as ViewGroup
            parent.removeView(mDragImageView)
        }
        mDragImageView?.setImageBitmap(mDragBitmap)
        mRootView?.addView(mDragImageView)
        val scale = (mItemWidth * 0.8 / mStartDragItemView!!.width).toFloat()
        val endX = (mDownX - dragPointX + dragOffsetX).toFloat()
        val endY = (mDownY - dragPointY + dragOffsetY - mTopHeight).toFloat()
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, scale)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, scale)
        val scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragImageView,scaleX,scaleY)
        scaleAnimator.interpolator = AccelerateDecelerateInterpolator()
        scaleAnimator.setDuration(320).start()
        translationAnimator = ObjectAnimator.ofPropertyValuesHolder(mDragImageView,
                PropertyValuesHolder.ofFloat("translationX", drX, endX),
                PropertyValuesHolder.ofFloat("translationY", drY, endY))
        translationAnimator!!.interpolator = AccelerateDecelerateInterpolator()
        translationAnimator!!.setDuration(200).start()
    }

    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     */
    private fun onDragItem(X: Int, Y: Int) {
        if (mDragImageView != null) {
            if (translationAnimator != null && translationAnimator!!.isRunning) {
                translationAnimator!!.end()
            }
            ViewCompat.setTranslationX(mDragImageView, X.toFloat())
            ViewCompat.setTranslationY(mDragImageView, Y.toFloat())
        }
    }

    /**
     * 交换item
     *
     * @param moveX
     * @param moveY
     */
    private fun onSwapItem(moveX: Int, moveY: Int) {
        if (mDragImageView != null) {
            val tempPosition = pointToPosition(moveX, moveY)
            if (tempPosition > maxSize) {
                return
            }
            if (mDragPosition != -1 && tempPosition != mDragPosition && tempPosition != -1 && mAnimationEnd) {
                animateReorder(mDragPosition, tempPosition)
            }
        }
    }


//    private var isAniReverse1 = true
//    private var isAniReverse2 = true
    /**
     * item的交换动画效果
     *
     * @param oldPosition
     * @param newPosition
     */
    private fun animateReorder(oldPosition: Int, newPosition: Int) {
        val isForward = newPosition > oldPosition
        val resultList = LinkedList<Animator>()
        if (isForward) {// 如果是向后的动画，就依次交换后面的item
            for (pos in oldPosition + 1..newPosition) {
                val view = getChildAt(pos)
                if (pos < 12) {// 如果在第一屏内
                    when(pos){
                        1 -> {// 向左 放大动画
                            val h = (view.width / 2).toFloat()
                            val mSpacing = (padding / 2).toFloat()
                            val w = getChildAt(0).width.toFloat()
                            val scale = w / view.width
                            resultList.add(createTranslationAnimations(view, 0f, -(view.width.toFloat() + padding.toFloat() + mSpacing + h),
                                    0f, h + mSpacing, scale, scale))
                        }
                        2,3,5,6,8,9,10,11 -> {// 向左移动动画
                            resultList.add(createTranslationAnimations(view, 0f, (-(view.width + padding)).toFloat(), 0f, 0f))
                        }
                        4 -> {// 向上一行的最右侧移动动画（右上移动）
                            resultList.add(createTranslationAnimations(view, 0f, (2 * (view.width + padding)).toFloat(), 0f, (-(view.width + padding)).toFloat()))
                        }
                        7 -> {// 向上一行的最右侧移动动画（右上移动）
                            resultList.add(createTranslationAnimations(view, 0f, (4 * (view.width + padding)).toFloat(), 0f, (-(view.width + padding)).toFloat()))
                        }
                    }
                }
                swap(mImages, pos, pos - 1)
            }
        } else {// 如果是向前的动画，就依次交换前面的item
            for (pos in newPosition until oldPosition) {// 倒序
                val view = getChildAt(pos)
                if (pos < 11) {// 如果在第一屏内
                    when(pos){
                        0 -> {// 向右 缩小动画
                            val h = (getChildAt(1).width / 2).toFloat()
                            val mSpacing = (padding / 2).toFloat()
                            val w = getChildAt(0).width.toFloat()
                            val scale = getChildAt(1).width / w
                            resultList.add(createTranslationAnimations(view, 0f,
                                    getChildAt(1).width.toFloat() + padding.toFloat() + mSpacing + h, 0f, -(h + mSpacing), scale, scale))
                        }
                        1,2,4,5,7,8,9,10 -> {// 向右移动
                            resultList.add(createTranslationAnimations(view, 0f, (view.width + padding).toFloat(), 0f, 0f))
                        }
                        3 -> {// 向下一行的最左侧移动动画（左下移动）
                            resultList.add(createTranslationAnimations(view, 0f, (-(2 * (view.width + padding))).toFloat(), 0f, (view.width + padding).toFloat()))
                        }
                        6 -> {// 向下一行的最左侧移动动画（左下移动）
                            resultList.add(createTranslationAnimations(view, 0f, (-(4 * (view.width + padding))).toFloat(), 0f, (view.width + padding).toFloat()))
                        }
                    }
                }
            }
            for (i in oldPosition downTo newPosition + 1) {
                swap(mImages, i, i - 1)
            }
        }

        hidePosition = newPosition
        if (resultSet == null) {
            resultSet = AnimatorSet()
        }
        resultSet?.playTogether(resultList)
        resultSet?.duration = 250// 动画时长
        resultSet?.interpolator = OvershootInterpolator(1.6f)
        resultSet?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                mAnimationEnd = false
            }

            override fun onAnimationEnd(arg0: Animator) {
                if (!mAnimationEnd) {
                    onRefresh()
                    resultSet?.removeAllListeners()
                    resultSet?.clone()
                    mDragPosition = hidePosition
                }
                mAnimationEnd = true
            }
        })
        resultSet?.start()
        resultList.clear()
    }

    private fun onRefresh() {
        initUI()
    }

    /**
     * 创建移动动画
     *
     * @param view
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    private fun createTranslationAnimations(view: View, startX: Float, endX: Float, startY: Float, endY: Float): AnimatorSet {
        val animSetXY = AnimatorSet()
        animSetXY.playTogether(
                ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat("translationX", startX, endX),
                        PropertyValuesHolder.ofFloat("translationY", startY, endY)))
        return animSetXY
    }

    /**
     * 创建缩放动画
     */
    private fun createTranslationAnimations(view: View, startX: Float, endX: Float, startY: Float, endY: Float,
                                            scaleX: Float, scaleY: Float): AnimatorSet {
        val animSetXY = AnimatorSet()
        animSetXY.playTogether(
                ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat("translationX", startX, endX),
                        PropertyValuesHolder.ofFloat("translationY", startY, endY),
                        PropertyValuesHolder.ofFloat("scaleX", 1.0f, scaleX),
                        PropertyValuesHolder.ofFloat("scaleY", 1.0f, scaleY)))
        return animSetXY
    }

    /**
     * 获得默认该layout的尺寸
     *
     * @return
     */
    /*private fun getDefaultWidth(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels)
    }*/

    private fun dp2px(dp: Int) : Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),context.resources.displayMetrics).toInt()
}