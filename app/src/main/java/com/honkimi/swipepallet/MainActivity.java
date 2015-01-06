package com.honkimi.swipepallet;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.honkimi.swipepalletview.PalletListener;
import com.honkimi.swipepalletview.SwipePalletVerticalView;
import com.honkimi.swipepalletview.SwipePalletView;


public class MainActivity extends ActionBarActivity {
    private TextView palletStatusText;
    private SwipePalletView swipePalletView;
    private SwipePalletVerticalView swipePalletVerticalView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        palletStatusText = (TextView) findViewById(R.id.pallet_status);
        swipePalletView = (SwipePalletView) findViewById(R.id.horizontal_pallet);
        swipePalletVerticalView = (SwipePalletVerticalView) findViewById(R.id.vertical_pallet);

        swipePalletView.setOnPalletListener(listener);
        swipePalletVerticalView.setOnPalletListener(listener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (swipePalletVerticalView.getVisibility() == View.VISIBLE) {
                swipePalletVerticalView.setVisibility(View.GONE);
                swipePalletView.setVisibility(View.VISIBLE);
            } else {
                swipePalletVerticalView.setVisibility(View.VISIBLE);
                swipePalletView.setVisibility(View.GONE);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
