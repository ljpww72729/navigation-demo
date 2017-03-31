package com.ww.lp.thirdapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ww.lp.thirdapp.databinding.ActivityMainBinding;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.btnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent resolveIntent = MainActivity.this.getPackageManager().getLaunchIntentForPackage("com.ww.lp.navigationdemo");// 这里的packname就是从上面得到的目标apk的包名
//                resolveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // You need this if starting，该flag起关键作用
//                // the activity from a service
//                resolveIntent.setAction(Intent.ACTION_MAIN);
//                resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                startActivity(resolveIntent);

                String uri = "navigationdemo://";
                try {
                    Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
                    startActivity(intent);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
