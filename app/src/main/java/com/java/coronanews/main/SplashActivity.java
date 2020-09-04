package com.java.coronanews.main;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.java.coronanews.R;
//import com.java.coronanews.data.Manager;


//import io.reactivex.functions.Consumer;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        long start = System.currentTimeMillis();
//        Manager.I.waitForInit()
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        System.out.println("waitForInit | " + (System.currentTimeMillis() - start));
//
//                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
//                        SplashActivity.this.startActivity(mainIntent);
//                        SplashActivity.this.finish();
//                    }
//                });

         Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
         SplashActivity.this.startActivity(mainIntent);
         SplashActivity.this.finish();
    }
}
