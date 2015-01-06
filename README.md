# SwipePalletView

## DEMO
![Swipepalletview GIF](https://github.com/honkimi/SwipePalletView/blob/master/demo.gif)

## Setup

```
cd /path/to/your/project
mkdir library
cp -r /path/to/swipepalletview library/
```

in your settings.gradle, add `, ':library:swipepalletview'`  
in your app's build.gradle `dependencies`, add `compile project(':library:swipepalletview')`


## Usage
#### Set SwipePalletView

In the top element of the xml file, add `xmlns:custom="http://schemas.android.com/apk/res-auto`.

```
    <com.honkimi.swipepalletview.SwipePalletView
        android:id="@+id/horizontal_pallet"
        custom:widthScale="0.3"
        custom:verticalDuration="700
        android:background="#5526e2ff"
        android:layout_width="wrap_content"
        android:layout_height="match_parent">

        <!-- Relative Layout Contents -->

    </com.honkimi.swipepalletview.SwipePalletView>
```

#### Set Listener

```
   private PalletListener listener = new PalletListener() {
        @Override
        public void onPalletTouched(MotionEvent event, boolean isFromBase) {
            palletStatusText.setText("pallet touched.\n isFromBase:" + isFromBase);
        }

        @Override
        public void onAnimationStart(boolean isFromBase) {
            palletStatusText.setText("pallet started.\n isFromBase:" + isFromBase);
        }

        @Override
        public void onAnimationEnd(boolean isFromBase) {
            palletStatusText.setText("pallet ended.\n isFromBase:" + isFromBase);
        }
    };

    SwipePalletView swipePalletView = (SwipePalletView) findViewById(R.id.horizontal_pallet);
    swipePalletView.setOnPalletListener(listener);
```

