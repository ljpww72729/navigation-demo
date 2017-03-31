package com.ww.lp.navigationdemo;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class OneActivity extends AppCompatActivity {

    private static final String TAG = "lp_" + OneActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case android.R.id.home:
//                Intent upIntent = new Intent(this, MainActivity.class);
//                upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                NavUtils.navigateUpTo(this, upIntent);
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed: ");
        super.onBackPressed();

    }
}
