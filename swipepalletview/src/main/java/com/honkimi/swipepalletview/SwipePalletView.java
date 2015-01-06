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
public class SwipePalletView extends RelativeLayout {
    private Point size = new Point();
    private int startX = 0;
    private int maxScroll = 0;
    private int minScroll = 9999;
    private boolean isFromBase = true;
    private float scale = 0.5f;
    private int duration = 500;
    private PalletListener palletListener;

    public SwipePalletView(Context context) {
        super(context);
        init(null);
    }

    public SwipePalletView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SwipePalletView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display disp = wm.getDefaultDisplay();
        disp.getSize(size);

        setOnTouchListener(listener);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwipePalletView);
            scale = a.getFloat(R.styleable.SwipePalletView_widthScale, 0.5f);
            duration = a.getInteger(R.styleable.SwipePalletView_horizontalDuration, 500);

            a.recycle();
        }
    }

    public void setOnPalletListener(PalletListener palletListener) {
        this.palletListener = palletListener;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        getLayoutParams().width = (int) (size.x * scale);
        requestLayout();
    }


    private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (palletListener != null) {
                palletListener.onPalletTouched(event, isFromBase);
            }
            if (isFromBase) {
                moveRight(event);
            } else {
                moveLeft(event);
            }
            return true;
        }
    };

    private void moveRight(MotionEvent event) {
        if (startX == 0) {
            startX = (int) event.getRawX();
        }
        final int x = (int) event.getRawX();
        final int marginLeft = x - startX;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        (int) (size.x * scale),
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );
                if (maxScroll < marginLeft) {
                    maxScroll = marginLeft;
                }
                if (marginLeft < 0) {
                    params.setMargins(0, 0, 0, 0);
                } else {
                    params.setMargins(marginLeft, 0, 0, 0);
                }
                setLayoutParams(params);
                break;
            case MotionEvent.ACTION_UP:
                if (startX == x) {
                    // only tap case
                    return;
                }
                Animation animation;
                if (maxScroll == marginLeft) {
                    animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                            params.leftMargin = (int) (marginLeft + (size.x * (1.0 - scale) - marginLeft) * interpolatedTime);
                            setLayoutParams(params);
                        }
                    };
                    setAnimationListener(animation, false);
                } else {
                    animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                            params.leftMargin = (int) (marginLeft - marginLeft * interpolatedTime);
                            setLayoutParams(params);
                        }
                    };
                    setAnimationListener(animation, true);
                }
                animation.setDuration(duration);
                startAnimation(animation);
                startX = 0;
                maxScroll = 0;
                break;
        }
    }

    private void moveLeft(MotionEvent event) {
        if (startX == 0) {
            startX = (int) event.getRawX();
        }
        final int x = (int) event.getRawX();
        final int marginLeft = (int) (size.x * (1.0 - scale)) + (x - startX);

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        (int) (size.x * scale),
                        RelativeLayout.LayoutParams.MATCH_PARENT
                );
                if (minScroll > marginLeft) {
                    minScroll = marginLeft;
                }
                if (marginLeft > size.x * (1.0 - scale)) {
                    params.setMargins((int) (size.x * (1.0 - scale)), 0, 0, 0);
                } else {
                    params.setMargins(marginLeft, 0, 0, 0);
                }
                setLayoutParams(params);
                break;
            case MotionEvent.ACTION_UP:
                Animation animation;
                if (minScroll == marginLeft) {
                    animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                            params.leftMargin = (int) (marginLeft - marginLeft * interpolatedTime);
                            setLayoutParams(params);
                        }
                    };
                    setAnimationListener(animation, true);
                } else {
                    animation = new Animation() {
                        @Override
                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
                            params.leftMargin = (int) (marginLeft + (size.x * (1.0 - scale) - marginLeft) * interpolatedTime);
                            setLayoutParams(params);
                        }
                    };
                    setAnimationListener(animation, false);
                }
                animation.setDuration(duration); // duartion in ms
                startAnimation(animation);
                startX = 0;
                minScroll = 9999;
        }
    }

    private void setAnimationListener(Animation animation, final boolean isLeft) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (palletListener != null) {
                    palletListener.onAnimationStart(SwipePalletView.this.isFromBase);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (palletListener != null) {
                    palletListener.onAnimationEnd(SwipePalletView.this.isFromBase);
                }
                SwipePalletView.this.isFromBase = isLeft;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
