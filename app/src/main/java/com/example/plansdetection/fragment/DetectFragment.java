package com.example.plansdetection.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.plansdetection.R;
import com.example.plansdetection.activity.CameraActivity;
import com.example.plansdetection.helper.ProcessingImage;
import com.example.plansdetection.helper.ScanQR;
import com.example.plansdetection.model.Classifier;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;


public class DetectFragment extends Fragment {
    private ImageView ivPhotoDetect, ivCapture, ivAlbum;
    private TextView tvImage, tvResult, tvConfidence, tvOrigin;
    private CardView cardOrigin;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
//    PROCESSING IMAGE CLASS
    ProcessingImage processingImage;
//    SCAN QR CODE
    ScanQR scanQR;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_detect, container, false);
        addControls(view);
        addEvents();

        return view;
    }

    private void addEvents() {
//        START CAMERA
        ivCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CameraActivity.class);
                startActivity(intent);
            }
        });
//        SHOW LIBRARIES PICTURE
        ivAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

//        GET IMAGE FROM BUNDLE
        Bundle bundle = getArguments();
        if (bundle != null) {
            String imagePath = bundle.getString("EXTRA_IMAGE_PATH");
            if (imagePath != null) {
                tvImage.setVisibility(View.GONE);
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ivPhotoDetect.setImageBitmap(myBitmap);
                    // Tạo Uri từ đường dẫn của ảnh
                    Uri imageUri = Uri.fromFile(imgFile);
                    // Lấy hướng xoay của ảnh từ Uri
                    int orientation = getOrientationFromGallery(imageUri);
                    // Xoay ảnh nếu cần
//                    rotateBitmap(myBitmap, orientation);
                    handleImage(myBitmap);
                }
            }
//            SET QR CODE
            String qrCodeData = bundle.getString("QR_CODE_DATA");
            if (qrCodeData != null) {
                cardOrigin.setVisibility(View.VISIBLE);
                tvOrigin.setText(qrCodeData);
            }
        }
    }

    private void addControls(View view) {
        ivPhotoDetect = view.findViewById(R.id.ivPhotoDetect);
        ivCapture = view.findViewById(R.id.ivCapture);
        ivAlbum = view.findViewById(R.id.ivAlbum);
        tvImage = view.findViewById(R.id.tvImage);
        tvResult = view.findViewById(R.id.tvResult);
        tvConfidence = view.findViewById(R.id.tvConfidence);
        cardOrigin = view.findViewById(R.id.cardOrigin);
        tvOrigin = view.findViewById(R.id.tvOrigin);
        cardOrigin.setVisibility(View.GONE);

        processingImage = new ProcessingImage(getContext());
        scanQR = new ScanQR();
    }

    private void handleImage(Bitmap bitmap) {
//


        Bitmap imageHandled = processingImage.handleImage(bitmap);
//        CHECK QRCODE
        scanQR.decode(bitmap, new ScanQR.ScanCallback() {
            @Override
            public void onSuccess(String qrCodeValue) {
                displayQRCode(qrCodeValue);
            }

            @Override
            public void onFailure(Exception e) {
                noDisplayQRCode();
            }
        });

//        CHECK CLASSIFIER
        Log.d("HANDLE_IMAGE", "SIZE OF IMAGE : " + imageHandled.getHeight() + "x" + imageHandled.getWidth());
        showPrediction(imageHandled);
    }

    private void displayQRCode(String msg) {
        cardOrigin.setVisibility(View.VISIBLE);
        tvOrigin.setText(msg);
    }

    private void noDisplayQRCode() {
        cardOrigin.setVisibility(View.GONE);
        tvOrigin.setText("");
    }

    private void showPrediction(Bitmap bitmap) {
        try {
            Classifier classifier = new Classifier(getContext());
            String[] rs = classifier.predict(bitmap);
            Log.d("HANDLE_IMAGE", "PREDICTION::" + rs[0]);
            tvResult.setText(rs[0]);
            tvConfidence.setText(rs[1]);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                    tvImage.setVisibility(View.GONE);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                    // Kiểm tra hướng xoay của ảnh và xoay nếu cần
                    handleImage(bitmap);
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
        bitmap.recycle();
        ivPhotoDetect.setImageBitmap(rotatedBitmap);
    }
}
