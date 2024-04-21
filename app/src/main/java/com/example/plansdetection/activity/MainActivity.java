package com.example.plansdetection.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plansdetection.R;
import com.example.plansdetection.fragment.DetectFragment;
import com.example.plansdetection.fragment.ManageFragment;
import com.example.plansdetection.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ImageView ivManage, ivDetect, ivUser;
    TextView tvManage, tvDetect, tvUser;
    private int tabIndex = 0;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();

        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        if(intent != null) {
            if(intent.hasExtra("EXTRA_IMAGE_PATH")) {
                tabIndex = 1;
                String imagePath = intent.getStringExtra("EXTRA_IMAGE_PATH");
                bundle.putString("EXTRA_IMAGE_PATH", imagePath);
            }

            if(intent.hasExtra("QR_CODE_DATA")) {
                String qrCodeValue = intent.getStringExtra("QR_CODE_DATA");
                bundle.putString("QR_CODE_DATA", qrCodeValue);
            }
        }
        if(tabIndex != 0) {
            setNavBar(bundle);
        } else {
            setNavBar();
        }
    }

    private void addControls() {
        ivManage = findViewById(R.id.ivManage);
        ivDetect = findViewById(R.id.ivDetect);
        ivUser = findViewById(R.id.ivUser);
        tvManage = findViewById(R.id.tvManage);
        tvDetect = findViewById(R.id.tvDetect);
        tvUser = findViewById(R.id.tvUser);
    }

    private void addEvents() {
        ivManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(0);
            }
        });

        ivDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(1);
            }
        });

        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(2);
            }
        });
    }

    private void switchTab(int index) {
        tabIndex = index;
//        HANDLE WHEN SWITCHING TAB
//        ...
        setNavBar();
    }

    private void replaceFragment(Fragment fragment, Bundle bundle) {
        if (currentFragment != fragment) {
            if(bundle != null) {
                fragment.setArguments(bundle);
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_layout, fragment);
            fragmentTransaction.commit();
            currentFragment = fragment;
        }
    }

    private void setDefaultNavBar() {
        //        SETUP VIEW DEFAULT
        ivManage.setImageResource(R.drawable.newspaper);
        ivDetect.setImageResource(R.drawable.bio_energy);
        ivUser.setImageResource(R.drawable.group);
        tvManage.setTextSize(14);
        tvManage.setTextColor(Color.parseColor("#000000"));
        tvDetect.setTextSize(14);
        tvDetect.setTextColor(Color.parseColor("#000000"));
        tvUser.setTextSize(14);
        tvUser.setTextColor(Color.parseColor("#000000"));
    }

    private void setNavBar(Bundle bundle) {
        setDefaultNavBar();
        if (tabIndex == 0) {
            replaceFragment(new ManageFragment(), bundle);
            ivManage.setImageResource(R.drawable.newspaper_selected);
            tvManage.setTextSize(16);
            tvManage.setTextColor(Color.parseColor("#1194AA"));
        } else if (tabIndex == 1) {
            replaceFragment(new DetectFragment(), bundle);
            ivDetect.setImageResource(R.drawable.bio_energy_selected);
            tvDetect.setTextSize(16);
            tvDetect.setTextColor(Color.parseColor("#4FBD77"));
        } else if (tabIndex == 2) {
            replaceFragment(new SettingFragment(), bundle);
            ivUser.setImageResource(R.drawable.group_selected);
            tvUser.setTextSize(16);
            tvUser.setTextColor(Color.parseColor("#F6C90B"));
        }
    }

    private void setNavBar() {
        setNavBar(null);
    }
}
