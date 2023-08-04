package com.example.czyjatomelodia.Base;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.czyjatomelodia.R;

import java.util.ArrayList;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;


public class BaseActivity extends AppCompatActivity {
    private static final long DOUBLE_CLICK_TIME_DELTA = 2000;
    private long lastClickTime = 0;
    private static List<Activity> activityList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_CzyjaToMelodia_Fullscreen);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        activityList.add(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }

    @Override
    public void onBackPressed() {
        long clickTime = System.currentTimeMillis();
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            finishAllActivities();
        } else {
            StyleableToast.makeText(getApplicationContext(), "Naciśnij przycisk wstecz ponownie, aby zamknąć aplikację", Toast.LENGTH_LONG, R.style.backToast).show();

        }
        lastClickTime = clickTime;
    }

    private void finishAllActivities() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }
}
