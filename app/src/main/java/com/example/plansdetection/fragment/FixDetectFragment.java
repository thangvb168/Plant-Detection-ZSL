package com.example.plansdetection.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.plansdetection.R;
import com.example.plansdetection.activity.CameraActivity;

import java.io.File;
import java.io.IOException;

public class FixDetectFragment extends Fragment {
    private ImageView ivPhotoDetect, ivCapture, ivAlbum;
    private TextView tvImage;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fix_detect, container, false);
        ivPhotoDetect = view.findViewById(R.id.ivPhotoDetect);
        ivCapture = view.findViewById(R.id.ivCapture);
        ivAlbum = view.findViewById(R.id.ivAlbum);
        tvImage = view.findViewById(R.id.tvImage);

        ivCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                startActivity(intent);
            }
        });

        ivAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            String imagePath = bundle.getString("EXTRA_IMAGE_PATH");

            // Hiển thị ảnh lên ImageView
            if (imagePath != null) {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivPhotoDetect.setImageBitmap(myBitmap);

                    // Tạo Uri từ đường dẫn của ảnh
                    Uri imageUri = Uri.fromFile(imgFile);
                    // Lấy hướng xoay của ảnh từ Uri
                    int orientation = getOrientationFromGallery(imageUri);
                    // Xoay ảnh nếu cần
                    rotateBitmap(myBitmap, orientation);
                    tvImage.setVisibility(View.GONE);
                }
            }
        }

        return view;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                    // Kiểm tra hướng xoay của ảnh và xoay nếu cần
                    int orientation = getOrientationFromGallery(imageUri);
                    rotateBitmap(bitmap, orientation);
                    // Hiển thị ảnh lên ImageView
                    ivPhotoDetect.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getOrientationFromGallery(Uri imageUri) {
        int orientation = ExifInterface.ORIENTATION_NORMAL;
        try {
            ExifInterface exif = new ExifInterface(requireActivity().getContentResolver().openInputStream(imageUri));
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return orientation;
    }

    private void rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                // Không cần xoay nếu hướng là mặc định
                return;
        }
        // Áp dụng ma trận biến đổi cho Bitmap
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle(); // Giải phóng bộ nhớ của bitmap ban đầu
        ivPhotoDetect.setImageBitmap(rotatedBitmap);
    }
}
