package com.example.plansdetection.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plansdetection.R;
import com.example.plansdetection.fragment.FixDetectFragment;
import com.example.plansdetection.fragment.ManageFragment;
import com.example.plansdetection.fragment.DetectFragment;
import com.example.plansdetection.fragment.SettingFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ImageView ivManage, ivDetect, ivUser;
    TextView tvManage, tvDetect, tvUser;
    private boolean TabClick = true;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivManage = findViewById(R.id.ivManage);
        ivDetect = findViewById(R.id.ivDetect);
        ivUser = findViewById(R.id.ivUser);
        tvManage = findViewById(R.id.tvManage);
        tvDetect = findViewById(R.id.tvDetect);
        tvUser = findViewById(R.id.tvUser);

        ivManage.setImageResource(R.drawable.farm_management_tab_selected);
        ivDetect.setImageResource(R.drawable.disease_detection_tab);
        ivUser.setImageResource(R.drawable.user_profile_tab);
        tvManage.setTextSize(16);
        tvDetect.setTextSize(14);
        tvUser.setTextSize(14);
        TabClick = true;
        Log.v(TAG, "onCreate Main");
        Intent intent = getIntent();
        if (intent.hasExtra("EXTRA_IMAGE_PATH")) {
            String imagePath = intent.getStringExtra("EXTRA_IMAGE_PATH");
            FixDetectFragment fixDetectFragment = new FixDetectFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString("EXTRA_IMAGE_PATH", imagePath);
            fixDetectFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_layout, fixDetectFragment);
            fragmentTransaction.commit();
            ivManage.setImageResource(R.drawable.farm_management_tab);
            ivDetect.setImageResource(R.drawable.disease_detection_tab_selected);
            ivUser.setImageResource(R.drawable.user_profile_tab);
        } else {
            Log.v(TAG, "Run Manager");
            replaceFragment(new ManageFragment());
        }

        ivManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TabClick){
                    ivManage.setImageResource(R.drawable.farm_management_tab_selected);
                    ivDetect.setImageResource(R.drawable.disease_detection_tab);
                    ivUser.setImageResource(R.drawable.user_profile_tab);
                    tvManage.setTextSize(16);
                    tvDetect.setTextSize(14);
                    tvUser.setTextSize(14);
                    replaceFragment(new ManageFragment());
                    TabClick = true;
                }
            }
        });

        ivDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ivManage.setImageResource(R.drawable.farm_management_tab);
                    ivDetect.setImageResource(R.drawable.disease_detection_tab_selected);
                    ivUser.setImageResource(R.drawable.user_profile_tab);
                    tvManage.setTextSize(14);
                    tvDetect.setTextSize(16);
                    tvUser.setTextSize(14);
                    replaceFragment(new FixDetectFragment());
                    TabClick = false;
            }
        });

        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivManage.setImageResource(R.drawable.farm_management_tab);
                ivDetect.setImageResource(R.drawable.disease_detection_tab);
                ivUser.setImageResource(R.drawable.user_profile_tab_selected);
                tvManage.setTextSize(14);
                tvDetect.setTextSize(14);
                tvUser.setTextSize(16);
                replaceFragment(new SettingFragment());
                TabClick = false;
            }
        });
    }

    // Phương thức thay thế Fragment
    private void replaceFragment(Fragment fragment) {
        if (currentFragment != fragment) { // Kiểm tra fragment hiện tại
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_layout, fragment);
            fragmentTransaction.commit();
            currentFragment = fragment;
        }
    }
}
