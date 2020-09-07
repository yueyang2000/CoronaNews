package com.java.coronanews.main;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.java.coronanews.R;
import com.java.coronanews.data.Config;
import com.java.coronanews.data.Epidemic;
import com.java.coronanews.data.Manager;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
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
        Single<JSONObject> single = null;
        single = Manager.I.fetchData();
        single.subscribe(new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject simpleNewses) throws Exception {
                Epidemic.data = simpleNewses;
                System.out.println("download epidemic data");
                for (Iterator<String> it = simpleNewses.keys(); it.hasNext(); ) {
                    String key = it.next();
                    String[] places = key.split("\\|");

                    String country = places[0];
                    String province = (places.length<2) ? "all": places[1];
                    Epidemic.countries.add(country);
                    TreeSet<String> map = Epidemic.place_map.get(country);
                    if(map == null) map = new TreeSet<>();
                    map.add(province);
                    Epidemic.place_map.put(country, map);
                }

                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        });

    }
}
