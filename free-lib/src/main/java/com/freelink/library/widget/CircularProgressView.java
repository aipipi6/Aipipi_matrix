package com.freelink.library.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.freelink.library.R;

public class CircularProgressView extends View {
    /**
     * Factor to convert the factor to paint the arc.
     * <p/>
     * In this way the developer can use a more user-friendly [0..1f] progress
     */
    public static final int PROGRESS_FACTOR = -360;
    /**
     * Property Progress of the outer circle.
     * <p/>
     * <p/>
     * The progress of the circle. If  is set
     * to FALSE, this property will be used to indicate the completion of the outer circle [0..1f].
     * <p/>
     * If set to TRUE, the drawable will activate the loading mode, where the drawable will
     * show a 90º arc which will be spinning around the outer circle as much as progress goes.
     */
    public static final String PROGRESS_PROPERTY = "progress";
    /**
     * Rectangle where the filling ring will be drawn into.
     */
    protected final RectF arcElements;
    /**
     * Paint object to draw the element.
     */
    protected final Paint paint;
    /**
     * Width of the filling ring.
     */
    protected int ringWidth;
    /**
     * Ring progress.
     */
    protected float progress;
    /**
     * Color for the completed ring.
     */
    protected int ringColor;

    /**
     * Color for the background of ring
     */

    protected int ringBgColor;

    /**
     * Ring progress title.
     */
    protected String progressTitle;

    protected int progressTitleSize = (int) spToPx(16);
    protected int progressTitleColor = Color.RED;

    protected int textPadding = (int) dpToPx(16);

    protected int progressTextSize = (int) spToPx(30);

    protected int progressTextColor = Color.RED;

    protected boolean showText = true;

    /**
     * default gradient color for the progress ring.
     */
    private LinearGradient shader;
    private Rect rec;

    public CircularProgressView(Context context) {
        this(context, null);
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CircularProgressView);
        ringColor = array.getColor(R.styleable.CircularProgressView_ringColor, 0);
        ringBgColor = array.getColor(R.styleable.CircularProgressView_ringBgColor, Color.GRAY);
        ringWidth = array.getDimensionPixelSize(R.styleable.CircularProgressView_ringWidth, (int) dpToPx(4));
        progressTitle = array.getString(R.styleable.CircularProgressView_progressTitle);
        progressTitleColor = array.getColor(R.styleable.CircularProgressView_progressTitleColor, progressTitleColor);
        progressTitleSize = array.getDimensionPixelSize(R.styleable.CircularProgressView_progressTitleSize, progressTitleSize);

        textPadding  = array.getDimensionPixelSize(R.styleable.CircularProgressView_textPadding, textPadding);
        progressTextSize = array.getDimensionPixelSize(R.styleable.CircularProgressView_progressTextSize, progressTextSize);
        progressTextColor = array.getColor(R.styleable.CircularProgressView_progressTextColor, progressTextColor);

        showText = array.getBoolean(R.styleable.CircularProgressView_showText, showText);
        array.recycle();
        this.progress = 0;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.arcElements = new RectF();
        rec = new Rect();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Calculations on the different components sizes
        int size = Math.min(canvas.getHeight(), canvas.getWidth());
        float outerRadius = (size / 2) - (ringWidth / 2);
        float offsetX = (canvas.getWidth() - outerRadius * 2) / 2;
        float offsetY = (canvas.getHeight() - outerRadius * 2) / 2;

        int halfRingWidth = ringWidth / 2;
        float arcX0 = offsetX + halfRingWidth;
        float arcY0 = offsetY + halfRingWidth;
        float arcX = offsetX + outerRadius * 2 - halfRingWidth;
        float arcY = offsetY + outerRadius * 2 - halfRingWidth;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ringWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        arcElements.set(arcX0, arcY0, arcX, arcY);
        paint.setColor(ringBgColor);
        canvas.drawArc(arcElements, 0, 360, false, paint);
        if (ringColor != 0) {
            paint.setColor(ringColor);
            canvas.drawArc(arcElements, -90, -progress, false, paint);
        } else {
            if (shader == null) {
                shader = new LinearGradient(0, offsetY, 0, offsetY + outerRadius * 2, new int[]{Color.parseColor("#B4ED50"),
                        Color.parseColor("#429321")},
                        null, Shader.TileMode.CLAMP);
            }
            paint.setShader(shader);
            canvas.drawArc(arcElements, -90, -progress, false, paint);
        }

        if(showText) {
            int progressText = -(int) (progress / 3.6);
            String v = progressText + "%";
            paint.setShader(null);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(progressTextColor);
            paint.setTextSize(progressTextSize);
            paint.getTextBounds(v, 0, v.length(), rec);
            int textwidth = rec.width();
            int textheight = rec.height();
            // draw the center words
            if (progressTitle != null && !progressTitle.isEmpty()) {
                canvas.drawText(v, (canvas.getWidth() - textwidth) / 2, (canvas.getHeight() + textheight) / 2 - textPadding, paint);
                paint.setTextSize(progressTitleSize);
                paint.setColor(progressTitleColor);
                paint.getTextBounds(progressTitle, 0, progressTitle.length(), rec);
                int textwidth1 = rec.width();
                int textheight1 = rec.height();
                canvas.drawText(progressTitle, (canvas.getWidth() - textwidth1) / 2, (canvas.getHeight() + textheight1) / 2 + textPadding, paint);

            } else {
                canvas.drawText(v, (canvas.getWidth() - textwidth) / 2, (canvas.getHeight() + textheight) / 2, paint);
            }
        }
    }

    /**
     * Change sp to px.
     *
     * @param sp the sp value.
     * @return the px value.
     */
    private float spToPx(int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics());
    }

    /**
     * Change dp to px.
     *
     * @param dp the dp value.
     * @return the px value.
     */
    private float dpToPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    /**
     * Returns the progress of the outer ring.
     * <p/>
     * Will output a correct value only when the indeterminate mode is set to FALSE.
     *
     * @return Progress of the outer ring.
     */
    public float getProgress() {
        return progress / PROGRESS_FACTOR;
    }

    /**
     * Sets the progress [0..1f].
     *
     * @param progress Sets the progress.
     */
    @Keep
    public void setProgress(float progress) {
        this.progress = PROGRESS_FACTOR * progress;
        invalidate();
    }

    /**
     * Gets the filled ring color.
     *
     * @return Returns the filled ring color.
     */
    public int getRingColor() {
        return ringColor;
    }

    /**
     * Sets the progress ring  color.
     *
     * @param ringColor Ring color in #AARRGGBB format.
     */
    public void setRingColor(int ringColor) {
        this.ringColor = ringColor;
        invalidate();
    }

    /**
     * Sets the ring width.
     *
     * @param ringWidth Default ring width.
     */
    public void setRingWidth(int ringWidth) {
        this.ringWidth = ringWidth;
        invalidate();
    }

    /**
     * Gets the ring width.
     *
     * @return Returns the ring width.
     */
    public int getRingWidth() {
        return ringWidth;
    }

    /**
     * Sets the progress title.
     *
     * @param progressTitle Sets the progress.
     */
    public void setProgressTitle(String progressTitle) {
        this.progressTitle = progressTitle;
        invalidate();
    }

    /**
     * Start progress animation or show the progress directly.
     *
     * @param progress the progress you set.
     * @param isAnim   weather to show the animation.
     */
    public void startAnim(float progress, boolean isAnim) {
        AnimatorSet animation = new AnimatorSet();

        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(this, CircularProgressView.PROGRESS_PROPERTY,
                0f, progress);
        progressAnimation.setDuration(isAnim ? 1000 : 0);
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        //another kind of animation
//        ObjectAnimator colorAnimator = ObjectAnimator.ofInt(drawable, CircularProgressDrawable.RING_COLOR_PROPERTY,
//                getResources().getColor(android.R.color.holo_red_dark),
//                getResources().getColor(android.R.color.holo_green_light));
//        colorAnimator.setEvaluator(new ArgbEvaluator());
//        colorAnimator.setDuration(3600);
//        animation.playTogether(progressAnimation, colorAnimator);
        animation.play(progressAnimation);
        animation.start();
    }

}