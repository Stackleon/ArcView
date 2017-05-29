package com.example.circledemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by lishiwang on 2017/5/27.
 */

public class ArcView extends View {

    private final static int DEFAULT_WIDTH = 200;
    private final static int DEFAULT_HEIGHT = 200;
    private final static int START_ANGLE = 270;
    private final static int DEFAULT_MAX = 100;
    private final static int OFFSET = 53;

    private int mArcColor;
    private int mArcBackgroundDrawableResId;
    private int mArcCenterDrawableResId;
    private float mTextSize;
    private int mTextColor;
    private Context mContext;

    private int mMax;
    private int mProgress;
    private boolean mIsShowPrecent;



    public ArcView(Context context) {
        super(context);
        init(context,null,0);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr){

        mContext = context;

        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.ArcView);

            if(typedArray.hasValue(R.styleable.ArcView_acrColor)){
                mArcColor = typedArray.getColor(R.styleable.ArcView_acrColor, Color.YELLOW);
                mArcBackgroundDrawableResId = typedArray.getResourceId(R.styleable.ArcView_acrBackgroundDrawable,R.mipmap.circle_arc_yellow);
                mArcCenterDrawableResId = typedArray.getResourceId(R.styleable.ArcView_acrCenterDrawable,R.mipmap.thunder);
                mTextSize = typedArray.getDimension(R.styleable.ArcView_textSize,20);
                mTextColor = typedArray.getColor(R.styleable.ArcView_textColor,Color.WHITE);
                mMax = typedArray.getInteger(R.styleable.ArcView_acrMax,DEFAULT_MAX);
                mProgress = typedArray.getInteger(R.styleable.ArcView_acrProgress,0);
                mIsShowPrecent = typedArray.getBoolean(R.styleable.ArcView_showPrecent,true);
            }

            typedArray.recycle();
        }



    }

    public Bitmap getBitmap(Context context,int imageResId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),imageResId,options);


        int heightRatio = (int) Math.ceil(options.outHeight / DEFAULT_HEIGHT);
        int widthRatio = (int) Math.ceil(options.outWidth / DEFAULT_WIDTH);

        if (heightRatio > 1 && widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio;
            } else {
                options.inSampleSize = widthRatio;
            }
        }
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(),imageResId,options);
        return bitmap;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec),measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec){
        int result = 0;

        int size = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);


        if(mode == MeasureSpec.EXACTLY){
            result = size;
        } else {
            result = 200;
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(result,size);
            }
        }

        return result;
    }

    private int measureHeight(int heightMeasureSpec){
        int result = 0;

        int size = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);


        if(mode == MeasureSpec.EXACTLY){
            result = size;
        } else {
            result = 200;
            if(mode == MeasureSpec.AT_MOST){
                result = Math.min(result,size);
            }
        }

        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        Rect rect = new Rect(0,0,getWidth(),getHeight());

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),mArcBackgroundDrawableResId);

        if(bitmap != null){
            canvas.drawBitmap(bitmap,rect,rect,mPaint);
        }


        if(!isShowPrecent() && mArcCenterDrawableResId != 0){
            Bitmap arcCenterBitmap = BitmapFactory.decodeResource(mContext.getResources(),mArcCenterDrawableResId);
            canvas.drawBitmap(arcCenterBitmap,rect,rect,mPaint);
        } else {
            drawText(canvas);
        }

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(getArcColor());
        mPaint.setStrokeWidth(15.0f);

        RectF rectF = new RectF(OFFSET,OFFSET,getWidth()-OFFSET,getHeight()-OFFSET);
        canvas.drawArc(rectF,START_ANGLE,getSweepAngle(),false,mPaint);

    }

    private void drawText(Canvas canvas){
        Paint textPaint = new Paint();

        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(getTextColor());
        textPaint.setTextSize(getTextSize());
        int precent = (int) (getPrecent()*100);

        String precentString = new StringBuffer().append(precent).append("%").toString();
        Rect textBound = new Rect();
        textPaint.getTextBounds(precentString,0,precentString.length(),textBound);

        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(precentString,getMeasuredWidth() / 2 - textBound.width() / 2, baseline, textPaint);
    }



    private float getPrecent(){
        int max = getMax();
        int progress = getProgress();

        return ((float)progress/max);
    }

    private int getSweepAngle(){
        float precent = getPrecent();
        return (int) (360*precent);
    }


    public int getArcBackgroundDrawableResId() {
        return mArcBackgroundDrawableResId;
    }

    public void setArcBackgroundDrawableResId(int arcBackgroundDrawableResId) {
        mArcBackgroundDrawableResId = arcBackgroundDrawableResId;
    }

    public int getArcCenterDrawableResId() {
        return mArcCenterDrawableResId;
    }

    public void setArcCenterDrawableResId(int arcCenterDrawableResId) {
        mArcCenterDrawableResId = arcCenterDrawableResId;
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        mProgress = progress;
    }

    public boolean isShowPrecent() {
        return mIsShowPrecent;
    }

    public void setShowPrecent(boolean showPrecent) {
        mIsShowPrecent = showPrecent;
    }

    public int getArcColor() {
        return mArcColor;
    }

    public void setArcColor(int arcColor) {
        mArcColor = arcColor;
    }


    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }
}

