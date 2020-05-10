package com.guch8017.eqtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    SharedPreferences mSP;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.about_app);
        mSP = getSharedPreferences("settings", Context.MODE_PRIVATE);
        Switch forwardSW = findViewById(R.id.forward);
        Switch blankSW = findViewById(R.id.blank);
        forwardSW.setChecked(mSP.getBoolean("forward", false));
        blankSW.setChecked(mSP.getBoolean("hide_blank", false));
        forwardSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = mSP.edit();
                editor.putBoolean("forward", b);
                editor.apply();
            }
        });
        blankSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = mSP.edit();
                editor.putBoolean("hide_blank", b);
                editor.apply();
            }
        });
    }
}
