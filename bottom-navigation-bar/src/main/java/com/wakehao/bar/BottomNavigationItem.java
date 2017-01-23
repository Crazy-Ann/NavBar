package com.wakehao.bar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by WakeHao on 2017/1/5.
 */

public class BottomNavigationItem extends View {
    private @DrawableRes int iconRes;
    private @DrawableRes int iconRes2_selected;
    private String title;
    private Config config;
    private int mPosition;
    private @ColorInt int mShiftedColor;
    private boolean initFinished;
    private Paint mPaint;
    private final int mActiveMarginTop;
    private final int mScaleInactiveMarginTop;
    private final int mShiftInactiveMarginTop;
    private final int mActiveMarginBottom;
    private final int mIconSize;
    private final int mActiveTextSize;
    private final int mInactiveTextSize;

    private Bitmap mBitmap;
    private static final long ACTIVE_ANIMATION_DURATION_MS = 150L;
    private int activeItemWidth;
    private int inActiveItemWidth;


    //是否开启滑动渐变效果
    private boolean isSlide;

    public BottomNavigationItem(Context context) {
        this(context,null);
    }

    public BottomNavigationItem(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomNavigationItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Resources res=getResources();
        mActiveMarginTop=res.getDimensionPixelSize(R.dimen.item_active_marginTop);
        mScaleInactiveMarginTop=res.getDimensionPixelSize(R.dimen.item_scaleInactive_marginTop);
        mShiftInactiveMarginTop=res.getDimensionPixelSize(R.dimen.item_shiftInactive_marginTop);
        mActiveMarginBottom=res.getDimensionPixelSize(R.dimen.item_active_marginBottom);
        mIconSize=res.getDimensionPixelSize(R.dimen.item_icon_size);
        mActiveTextSize=res.getDimensionPixelSize(R.dimen.item_active_text_size);
        mInactiveTextSize=res.getDimensionPixelSize(R.dimen.item_inactive_text_size);

    }

    Bitmap bitmap_selected;
    private void initDefaultOption() {
        if(mPosition==0)isSelected=true;
        if(mPosition==0&&mShiftedColor!=0) ((BottomNavigationBar) getParent().getParent()).setFirstItemBackgroundColor(mShiftedColor);
        if(mShiftedColor==0)setItemBackground(config.itemBackGroundRes);//recall onDraw()
        mBitmap= BitmapFactory.decodeResource(getResources(),iconRes);
        initPaint();
        if(iconRes2_selected!=0){
            //change bitmap
            bitmap_selected=BitmapFactory.decodeResource(getResources(),iconRes2_selected);

//            Bitmap outBitmap = Bitmap.createBitmap (bitmap_selected.getWidth(), bitmap_selected.getHeight() , bitmap_selected.getConfig());
//            Canvas canvas = new Canvas(outBitmap);
//            Paint paint = new Paint();
//            paint.setColorFilter( new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)) ;
//            canvas.drawBitmap(bitmap_selected , 0, 0, paint) ;
//            bitmap_selected=outBitmap;
//            outBitmap.recycle();

//            tintSelectedBitmap(bitmap_selected,Color.WHITE);
//            bitmap_selected.eraseColor(Color.WHITE);
            initSecondPaint();
            if(isSelected){
                mUnSelectedIconPaint.setColor(Color.TRANSPARENT);
            }
            else {
                mSelectedIconPaint.setColor(Color.TRANSPARENT);
            }
        }
//        changeUnSelectedIconColorFilter(config.inActiveColor);
        init();

    }
    
    public void tintSelectedBitmap(){
        Bitmap outBitmap = Bitmap.createBitmap (bitmap_selected.getWidth(), bitmap_selected.getHeight() , bitmap_selected.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter( new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)) ;
        canvas.drawBitmap(bitmap_selected , 0, 0, paint) ;
        bitmap_selected.recycle();
        bitmap_selected=outBitmap;
    }

    private void tintUnSelectedBitmap(){
        Bitmap outBitmap = Bitmap.createBitmap (mBitmap.getWidth(), mBitmap.getHeight() , mBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter( new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)) ;
        canvas.drawBitmap(mBitmap , 0, 0, paint) ;
        mBitmap.recycle();
        mBitmap=outBitmap;
    }



    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public void setIconResSelected(int iconRes2_selected){
        this.iconRes2_selected=iconRes2_selected;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Config getConfig() {
        return config;
    }

    public int getPosition() {
        return mPosition;
    }


    public void setPosition(int mPosition) {
        this.mPosition = mPosition;

    }

    public void setShiftedColor(int mShiftedColor) {
        this.mShiftedColor=mShiftedColor;

    }

    public int getShiftedColor() {
        return mShiftedColor;
    }

    public void setInActiveItemWidth(int inActiveItemWidth) {
        this.inActiveItemWidth = inActiveItemWidth;
    }

    public void setActiveItemWidth(int activeItemWidth) {
        this.activeItemWidth = activeItemWidth;
    }

    //标志item初始化完毕
    public void finishInit() {
        initFinished=true;
        initDefaultOption();
    }

    /*
    0-->1 activeColor-->inActiveColor
    1-->0 inActiveColor-->activeColor
     */
    public void textAlphaAnim(float positionOffset){
        mPaint.setColor(BarUtils.getOffsetColor(positionOffset,config.activeColor,config.inActiveColor,10));
        if(isRefresh)return;
//        invalidate();
    }

    private void iconAlphaAnim(float positionOffset){
        changeUnSelectedIconColorFilter(BarUtils.getIconColor(positionOffset, Color.TRANSPARENT, config.activeColor,Color.GRAY, 10));
        changeSelectedIconColorFilter(BarUtils.getIconColor(positionOffset, config.activeColor, Color.TRANSPARENT, Color.TRANSPARENT, 10));
//        invalidate();
    }

    private boolean flag;

    public void alphaAnim(float positionOffset) {
//        if(isSetCalled)return;
        if(!config.isSlide)return;
        if(!flag){
            tintSelectedBitmap();
            tintUnSelectedBitmap();
            flag=true;
        }
        iconAlphaAnim(positionOffset);
        textAlphaAnim(positionOffset);
        invalidate();
        if(isSelected&&positionOffset>=0.99){
            setSelected(false);
        }
        else if(!isSelected&&positionOffset<=0.01){
            setSelected(true);
        }
    }

    //selected bitmap
    private Paint mSelectedIconPaint;
    //unSelected bitmap
    private Paint mUnSelectedIconPaint;
    private void initSecondPaint() {
        if(mSelectedIconPaint==null){
            mSelectedIconPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

            mSelectedIconPaint.setFilterBitmap(true);
            mUnSelectedIconPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
            mUnSelectedIconPaint.setFilterBitmap(true);
        }
    }


    public static class Config
    {
        private int activeColor;
        private int inActiveColor;
        private int itemBackGroundRes;
        private int switchMode;
        private boolean isSlide;

        public Config(Build build) {
            activeColor=build.activeColor;
            inActiveColor=build.inActiveColor;
            itemBackGroundRes=build.itemBackGroundRes;
            switchMode=build.switchMode;
            isSlide=build.isSlide;
        }

        public int getSwitchMode() {
            return switchMode;
        }

        public static class Build{
            private int activeColor;
            private int inActiveColor;
            private int itemBackGroundRes;
            private int switchMode;
            private boolean isSlide;
            public Build setActiveColor(int activeColor) {
                this.activeColor = activeColor;
                return this;
            }

            public Build setInActiveColor(int inActiveColor) {
                this.inActiveColor = inActiveColor;
                return this;
            }

            public Build setItemBackGroundRes(int itemBackGroundRes) {
                this.itemBackGroundRes = itemBackGroundRes;
                return this;
            }

            public Build setSwitchMode(int switchMode) {
                this.switchMode = switchMode;
                return this;
            }

            public Build setIsSlide(boolean isSlide) {
                this.isSlide = isSlide;
                return this;
            }

            public Config build()
            {
                return new Config(this);
            }


        }
    }

    public void setConfig(Config config)
    {
        this.config=config;
    }

    public void setSelected(boolean isSelected,boolean isSlide){

        this.isSelected=isSelected;
        return;
    }

    private boolean isSetCalled;
    private boolean isSelected;
    public void setSelected(boolean isSelected){
        isSetCalled=true;
        this.isSelected=isSelected;
        changeColor(isSelected?config.activeColor:config.inActiveColor);
        if(config.isSlide){
            if(!flag){
                tintSelectedBitmap();
                tintUnSelectedBitmap();
                flag=true;
            }
            if(isSelected){
                changeSelectedIconColorFilter(config.activeColor);
                mUnSelectedIconPaint.setColor(Color.TRANSPARENT);
            }
            else {
                changeUnSelectedIconColorFilter(config.inActiveColor);
                mSelectedIconPaint.setColor(Color.TRANSPARENT);
            }
        }
        switch (config.getSwitchMode()){
            case 0:
                scaleAnim();
                break;
            case 1:
                translateAnim();
                break;
            case 2:
                invalidate();
                break;
        }

    }


    private float scaleFraction;
    private void scaleAnim() {
        final ValueAnimator scaleAnimator;
        if(isSelected){
            scaleAnimator=ValueAnimator.ofFloat(mScaleInactiveMarginTop,mActiveMarginTop);
        }
        else {
            if(mPaint.getTextSize()==mInactiveTextSize)return;
            scaleAnimator=ValueAnimator.ofFloat(mActiveMarginTop,mScaleInactiveMarginTop);
        }
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                isRefresh=true;
                scaleFraction=animation.getAnimatedFraction();
                float change=scaleFraction*(mScaleInactiveMarginTop-mActiveMarginTop);
                if(isSelected){
                    rectF.set(getWidth()/2-mIconSizeWidth/2,mScaleInactiveMarginTop-change,getWidth()/2+mIconSizeWidth/2,mScaleInactiveMarginTop-change+mIconSizeHeight);
                }
                else {
                    rectF.set(getWidth()/2-mIconSizeWidth/2,mActiveMarginTop+change,getWidth()/2+mIconSizeWidth/2,mActiveMarginTop+change+mIconSizeHeight);
                }
                invalidate();
            }
        });
        scaleAnimator.setDuration(ACTIVE_ANIMATION_DURATION_MS);
        scaleAnimator.start();
    }

    public void setItemBackground(int background) {
        Drawable backgroundDrawable = background == 0
                ? null : ContextCompat.getDrawable(getContext(), background);
        ViewCompat.setBackground(this, backgroundDrawable);
    }

    PaintFlagsDrawFilter paintFlagsDrawFilter;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
//        if(paintFlagsDrawFilter==null){
//            paintFlagsDrawFilter= new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
//            canvas.setDrawFilter(paintFlagsDrawFilter);
//        }
        if(initFinished){
            switch (config.getSwitchMode()){
                case 0:
                    drawScaledIcon(canvas);
                    drawScaledText(canvas);
                    break;
                case 1:
                    drawShiftedIcon(canvas);
                    drawShiftedText(canvas);
                    break;
                case 2:
                    drawStillIcon(canvas);
                    drawStillText(canvas);
                    break;
            }
        }

    }

    private void drawStillText(Canvas canvas) {
        updateTextPaint(mActiveTextSize);
        canvas.drawText(title,getWidth()/2-textRect.width()/2,BarUtils.dip2px(getContext(),46),mPaint);
    }

    private void drawStillIcon(Canvas canvas) {
        rectF.set(getWidth()/2-mIconSizeWidth/2,mActiveMarginTop,getWidth()/2+mIconSizeWidth/2,mActiveMarginTop+mIconSizeHeight);
        if(config.isSlide&&iconRes2_selected!=0){
            canvas.drawBitmap(mBitmap, rect, rectF, mUnSelectedIconPaint);
            canvas.drawBitmap(bitmap_selected, rect, rectF, mSelectedIconPaint);
            return;
        }
        if(iconRes2_selected!=0){
            if(isSelected) canvas.drawBitmap(bitmap_selected, rect, rectF, mPaint);
            else  canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
            return;
        }
        canvas.drawBitmap(mBitmap, rect, rectF, mPaint);

    }

    private void init() {
        if (textRect == null) {
            textRect = new Rect();
            mPaint.getTextBounds(title, 0, title.length(), textRect);
        }

        if(rect==null){
            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();
            //校正图片不是正方形变形问题
            if(width>height){
                mIconSizeWidth=mIconSize;
                mIconSizeHeight=(((float)height/(float)width))*mIconSize;
            }
            else if(width<height)
            {
                mIconSizeHeight=mIconSize;
                mIconSizeWidth=(width/height)*mIconSize;
            }
            else {
                mIconSizeWidth=mIconSize;
                mIconSizeHeight=mIconSize;
            }
            rect=new Rect(0,0,width,height);

        }
        if(rectF==null){
            rectF=new RectF();
        }

        if(mPosition==0)changeColor(config.activeColor);
        else changeColor(config.inActiveColor);

    }

    private void updateTextPaint(float textSize){
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(title, 0, title.length(), textRect);
    }
    private void drawScaledText(Canvas canvas) {
        if(isRefresh){
            if(isSelected){
                updateTextPaint(mInactiveTextSize+(mActiveTextSize-mInactiveTextSize)*scaleFraction);
            }
            else {

                updateTextPaint(mActiveTextSize-(mActiveTextSize-mInactiveTextSize)*scaleFraction);
            }
            canvas.drawText(title,getWidth()/2-textRect.width()/2,BarUtils.dip2px(getContext(),46),mPaint);
            return;
        }
        updateTextPaint(mPosition==0?mActiveTextSize:mInactiveTextSize);

        canvas.drawText(title,getWidth()/2-textRect.width()/2,BarUtils.dip2px(getContext(),46),mPaint);
    }

    private float mIconSizeWidth;
    private float mIconSizeHeight;

    private void drawScaledIcon(Canvas canvas) {
        if(isRefresh){
            if(iconRes2_selected!=0){
                canvas.drawBitmap(isSelected?bitmap_selected:mBitmap, rect, rectF, mPaint);
                return;
            }
            canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
            return;
        }
        if(isSelected){
            rectF.set(getWidth()/2-mIconSizeWidth/2,mActiveMarginTop,getWidth()/2+mIconSizeWidth/2,mActiveMarginTop+mIconSizeHeight);

            if(iconRes2_selected!=0){
                canvas.drawBitmap(bitmap_selected, rect, rectF, mPaint);
                return;
            }

        }
        else {
            rectF.set(getWidth()/2-mIconSizeWidth/2,mScaleInactiveMarginTop,getWidth()/2+mIconSizeWidth/2,mScaleInactiveMarginTop+mIconSizeHeight);

        }
        canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
    }

    private void drawShiftedText(Canvas canvas) {
        if (isRefresh) {
            if (isSelected) {
//                mPaint.setTextSize(mActiveTextSize * animatedFraction);
                updateTextPaint(mActiveTextSize * animatedFraction);
            } else {
//                mPaint.setTextSize(mActiveTextSize - mActiveTextSize * animatedFraction);
                updateTextPaint(mActiveTextSize - mActiveTextSize * animatedFraction);
            }
            canvas.drawText(title, getWidth() / 2 - textRect.width() / 2, BarUtils.dip2px(getContext(), 46), mPaint);
            return;
        }

        if (mPosition == 0) {
            canvas.drawText(title, getWidth() / 2 - textRect.width() / 2, BarUtils.dip2px(getContext(), 46), mPaint);
        }
    }


    private void drawShiftedIcon(Canvas canvas) {
        if(isRefresh){
            canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
            return;
        }

        if(mPosition==0){
            rectF.set(getWidth()/2-mIconSizeWidth/2,mActiveMarginTop,getWidth()/2+mIconSizeWidth/2,mActiveMarginTop+mIconSizeHeight);
        }
        else {
            rectF.set(getWidth()/2-mIconSizeWidth/2,mShiftInactiveMarginTop,getWidth()/2+mIconSizeWidth/2,mShiftInactiveMarginTop+mIconSizeHeight);
        }

        canvas.drawBitmap(mBitmap, rect, rectF, mPaint);
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setTextSize(mPosition==0?mActiveTextSize:mInactiveTextSize);

    }


    RectF rectF;
    Rect rect;
    boolean isRefresh;
    Rect textRect;
    float animatedFraction;


    private void changeColor(@ColorInt int color) {

        if(iconRes2_selected!=0){
            mPaint.setColor(color);
            return;
        }

        ColorFilter filter = new LightingColorFilter(color, 1);
        mPaint.setColorFilter(filter);
        mPaint.setColor(color);
    }
//    ColorFilter filter;
    private void changeUnSelectedIconColorFilter(@ColorInt int color){

        ColorFilter filter = new LightingColorFilter(color, 1);
        mUnSelectedIconPaint.setColorFilter(filter);
//        mUnSelectedIconPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        mUnSelectedIconPaint.setColor(color);
    }
    private void changeSelectedIconColorFilter(int color){


        ColorFilter filter = new LightingColorFilter(color, 1);
        mSelectedIconPaint.setColorFilter(filter);
//        mSelectedIconPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        mSelectedIconPaint.setColor(color);
    }
    public void translateAnim(){
        ValueAnimator valueAnimator;
        if(isSelected){
            valueAnimator=ValueAnimator.ofFloat(inActiveItemWidth,activeItemWidth);
        }
        else {
            //宽度没改变的不执行动画
            if(inActiveItemWidth==getWidth()||Math.abs(getWidth()-inActiveItemWidth)<=1)return;
            valueAnimator=ValueAnimator.ofFloat(getWidth(),inActiveItemWidth);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                animatedFraction = animation.getAnimatedFraction();
                float change = animatedFraction*(mShiftInactiveMarginTop-mActiveMarginTop);

                if(isSelected){
                    rectF.set(animatedValue/2-mIconSizeWidth/2,(mShiftInactiveMarginTop-change),animatedValue/2+mIconSizeWidth/2,(mShiftInactiveMarginTop-change)+mIconSizeHeight);
                        //not work
//                    mPaint.setTextSize(mActiveTextSize*animatedFraction);
                }
                else {
                    rectF.set(animatedValue/2-mIconSizeWidth/2,(mActiveMarginTop+change),animatedValue/2+mIconSizeWidth/2,(mActiveMarginTop+change)+mIconSizeHeight);
//                    mPaint.setTextSize(mActiveTextSize-mActiveTextSize*animatedFraction);
                }

                ViewGroup.LayoutParams params = getLayoutParams();
                if (params == null) return;

                params.width = Math.round((float) animation.getAnimatedValue());
                setLayoutParams(params);
                isRefresh=true;

                invalidate();

            }
        });
        valueAnimator.setDuration(ACTIVE_ANIMATION_DURATION_MS);
        valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                isRefresh=false;
            }
        });
        valueAnimator.start();
    }

}
