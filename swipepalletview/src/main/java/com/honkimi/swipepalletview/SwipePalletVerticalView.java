package com.honkimi.swipepalletview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * Created by honkimi on 15/01/06.
 */
public class SwipePalletVerticalView extends RelativeLayout {
    private Point size = new Point();
    private int startY = 0;
    private int maxScroll = 0;
    private int minScroll = 9999;
    private boolean isFromBase = true;
    private float scale = 0.5f;
    private int duration = 500;
    private PalletListener palletListener;

    public SwipePalletVerticalView(Context context) {
        super(context);
        init(null);
    }

    public SwipePalletVerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SwipePalletVerticalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        disp.getSize(size);

        setOnTouchListener(listener);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwipePalletVerticalView);
            scale = a.getFloat(R.styleable.SwipePalletVerticalView_heightScale, 0.5f);
            duration = a.getInteger(R.styleable.SwipePalletVerticalView_verticalDuration, 500);

            a.recycle();
        }
    }

    public void setOnPalletListener(PalletListener palletListener) {
        this.palletListener = palletListener;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        getLayoutParams().height = (int) (size.y * scale);
        requestLayout();
    }


    private OnTouchListener listener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (palletListener != null) {
                palletListener.onPalletTouched(event, isFromBase);
            }
            if (isFromBase) {
                moveBottom(event);
            } else {
                moveTop(event);
            }
            return true;
        }
    };

    private void moveBottom(MotionEvent event) {
        if (startY == 0) {
            startY = (int) event.getRawY();
        }
        final int y = (int) event.getRawY();
        final int marginTop = y - startY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        (int) (size.y * scale)
                );
                if (maxScroll < marginTop) {
                    maxScroll = marginTop;
                }
                if (marginTop < 0) {
                    params.setMargins(0, 0, 0, 0);
                } else {
                    params.setMargins(0, marginTop, 0, 0);
                }
                setLayoutParams(params);
                break;
            case MotionEvent.ACTION_UP:
                if (startY == y) {
                    // only tap case
                    return;
                }
                Animation animation;
                if (maxScroll == marginTop) {
                    animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            LayoutParams params = (LayoutParams) getLayoutParams();
                            params.topMargin = (int) (marginTop + (size.y * (1.0 - scale) - marginTop) * interpolatedTime);
                            setLayoutParams(params);
                        }
                    };
                    setAnimationListener(animation, false);
                } else {
                    animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            LayoutParams params = (LayoutParams) getLayoutParams();
                            params.topMargin = (int) (marginTop - marginTop * interpolatedTime);
                            setLayoutParams(params);
                        }
                    };
                    setAnimationListener(animation, true);
                }
                animation.setDuration(duration);
                startAnimation(animation);
                startY = 0;
                maxScroll = 0;
                break;
        }
    }

    private void moveTop(MotionEvent event) {
        if (startY == 0) {
            startY = (int) event.getRawY();
        }
        final int y = (int) event.getRawY();
        final int marginTop = (int) (size.y * (1.0 - scale)) + (y - startY);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        (int) (size.y * scale)
                );
                if (minScroll > marginTop) {
                    minScroll = marginTop;
                }
                if (marginTop > size.y * (1.0 - scale)) {
                    params.setMargins(0, (int) (size.y * (1.0 - scale)), 0, 0);
                } else {
                    params.setMargins(0, marginTop, 0, 0);
                }
                setLayoutParams(params);
                break;
            case MotionEvent.ACTION_UP:
                Animation animation;
                if (minScroll == marginTop) {
                    animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            LayoutParams params = (LayoutParams) getLayoutParams();
                            params.topMargin = (int) (marginTop - marginTop * interpolatedTime);
                            setLayoutParams(params);
                        }
                    };
                    setAnimationListener(animation, true);
                } else {
                    animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            LayoutParams params = (LayoutParams) getLayoutParams();
                            params.topMargin = (int) (marginTop + (size.y * (1.0 - scale) - marginTop) * interpolatedTime);
                            setLayoutParams(params);
                        }
                    };
                    setAnimationListener(animation, false);
                }
                animation.setDuration(duration); // duartion in ms
                startAnimation(animation);
                startY = 0;
                minScroll = 9999;
        }
    }

    private void setAnimationListener(Animation animation, final boolean isLeft) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (palletListener != null) {
                    palletListener.onAnimationStart(SwipePalletVerticalView.this.isFromBase);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (palletListener != null) {
                    palletListener.onAnimationEnd(SwipePalletVerticalView.this.isFromBase);
                }
                SwipePalletVerticalView.this.isFromBase = isLeft;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
