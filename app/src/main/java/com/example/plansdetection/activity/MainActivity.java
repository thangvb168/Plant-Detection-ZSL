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

        ivManage.setImageResource(R.drawable.newspaper_selected);
        ivDetect.setImageResource(R.drawable.bio_energy);
        ivUser.setImageResource(R.drawable.group);
        tvManage.setTextSize(16);
        tvManage.setTextColor(Color.parseColor("#1194AA"));
        tvDetect.setTextSize(14);
        tvDetect.setTextColor(Color.parseColor("#000000"));
        tvUser.setTextSize(14);
        tvUser.setTextColor(Color.parseColor("#000000"));
        TabClick = true;
        Log.v(TAG, "onCreate Main");
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EXTRA_IMAGE_PATH")) {
            String imagePath = intent.getStringExtra("EXTRA_IMAGE_PATH");
            String qrCodeValue = null;

            // Kiểm tra xem có EXTRA_QR_CODE trong Intent không
            if (intent.hasExtra("QR_CODE_DATA")) {
                qrCodeValue = intent.getStringExtra("QR_CODE_DATA");
            }

            // Tạo Bundle để truyền dữ liệu đến DetectFragment
            Bundle bundle = new Bundle();
            bundle.putString("EXTRA_IMAGE_PATH", imagePath);

            // Nếu có giá trị QR Code, thêm vào Bundle
            if (qrCodeValue != null) {
                bundle.putString("QR_CODE_DATA", qrCodeValue);
            }

            // Khởi tạo DetectFragment và gửi dữ liệu thông qua Bundle
            DetectFragment detectFragment = new DetectFragment();
            detectFragment.setArguments(bundle);

            // Thay thế Fragment hiện tại bằng DetectFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_layout, detectFragment)
                    .commit();

            // Đảm bảo chỉ một Fragment được hiển thị tại một thời điểm
            ivManage.setImageResource(R.drawable.newspaper);
            ivDetect.setImageResource(R.drawable.bio_energy_selected);
            ivUser.setImageResource(R.drawable.group);
            tvManage.setTextSize(14);
            tvManage.setTextColor(Color.parseColor("#000000"));
            tvDetect.setTextSize(16);
            tvDetect.setTextColor(Color.parseColor("#4FBD77"));
            tvUser.setTextSize(14);
            tvUser.setTextColor(Color.parseColor("#000000"));
        } else {
            // Nếu không có EXTRA_IMAGE_PATH trong Intent, chuyển sang Fragment quản lý
            Log.v(TAG, "Run Manager");
            replaceFragment(new ManageFragment());
        }

        ivManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TabClick){
                    ivManage.setImageResource(R.drawable.newspaper_selected);
                    ivDetect.setImageResource(R.drawable.bio_energy);
                    ivUser.setImageResource(R.drawable.group);
                    tvManage.setTextSize(16);
                    tvManage.setTextColor(Color.parseColor("#1194AA"));
                    tvDetect.setTextSize(14);
                    tvDetect.setTextColor(Color.parseColor("#000000"));
                    tvUser.setTextSize(14);
                    tvUser.setTextColor(Color.parseColor("#000000"));
                    replaceFragment(new ManageFragment());
                    TabClick = true;
                }
            }
        });

        ivDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ivManage.setImageResource(R.drawable.newspaper);
                    ivDetect.setImageResource(R.drawable.bio_energy_selected);
                    ivUser.setImageResource(R.drawable.group);
                    tvManage.setTextSize(14);
                    tvManage.setTextColor(Color.parseColor("#000000"));
                    tvDetect.setTextSize(16);
                    tvDetect.setTextColor(Color.parseColor("#4FBD77"));
                    tvUser.setTextSize(14);
                    tvUser.setTextColor(Color.parseColor("#000000"));
                    replaceFragment(new DetectFragment());
                    TabClick = false;
            }
        });

        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivManage.setImageResource(R.drawable.newspaper);
                ivDetect.setImageResource(R.drawable.bio_energy);
                ivUser.setImageResource(R.drawable.group_selected);
                tvManage.setTextSize(14);
                tvManage.setTextColor(Color.parseColor("#000000"));
                tvDetect.setTextSize(14);
                tvDetect.setTextColor(Color.parseColor("#000000"));
                tvUser.setTextSize(16);
                tvUser.setTextColor(Color.parseColor("#F6C90B"));
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
