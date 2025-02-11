package com.example.plansdetection.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.plansdetection.R;
import com.example.plansdetection.fragment.DetectFragment;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.HybridBinarizer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;
    public static final String EXTRA_IMAGE_PATH = "image_path";

    ImageButton capture, toggleFlash, flipCamera, arrowBack, ivTips;
    ImageView capturedImageView;
    Button btnSave;
    Dialog dialog;
    private PreviewView previewView;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
//    RESULT OF QRCODE
    String QR_CODE = null;
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                startCamera(cameraFacing);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.cameraPreview);
        capture = findViewById(R.id.capture);
        capturedImageView = findViewById(R.id.capturedImageView);
        toggleFlash = findViewById(R.id.toggleFlash);
        flipCamera = findViewById(R.id.flipCamera);
        arrowBack = findViewById(R.id.arrowBack);

        ivTips = findViewById(R.id.ivTips);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_round_corner);
        dialog.setCancelable(false);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }

        setupBackBtn();
        dialog.findViewById(R.id.btnDialogOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ivTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
                } else {
                    cameraFacing = CameraSelector.LENS_FACING_BACK;
                }
                startCamera(cameraFacing);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imagePath = (String) capturedImageView.getTag();
                if (imagePath != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("EXTRA_IMAGE_PATH", imagePath);
                    if(QR_CODE != null) {
                        bundle.putString("QR_CODE_DATA", QR_CODE);
                    }
                    Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                    intent.putExtras(bundle);



                    startActivity(intent);
                } else {
                    Toast.makeText(CameraActivity.this, "Don't have image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupBackBtn() {
        arrowBack.setOnClickListener(v -> onBackPressed());
    }
    public void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = listenableFuture.get();

                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                ImageCapture imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(Surface.ROTATION_0)
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                capture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }
                        takePicture(imageCapture);

                    }
                });
                toggleFlash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setFlashIcon(camera);
                    }
                });

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    private void setFlashIcon(Camera camera) {
        if (camera.getCameraInfo().hasFlashUnit()) {
            if (camera.getCameraInfo().getTorchState().getValue() == 0) {
                camera.getCameraControl().enableTorch(true);
                toggleFlash.setImageResource(R.drawable.baseline_flash_on_24);
            } else {
                camera.getCameraControl().enableTorch(false);
                toggleFlash.setImageResource(R.drawable.baseline_flash_off_24);
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CameraActivity.this, "Flash is not available currently", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void takePicture(ImageCapture imageCapture) {
        final File file = new File(getExternalFilesDir(null), System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        GET IMAGE FROM CAMERA
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                        String qrCodeData = decodeQRCodeFromBitmap(bitmap);
//                        QR_CODE = decodeQRCodeFromBitmap(bitmap);

//                        REBUILD CAMERA
                        previewView.setVisibility(View.GONE);
                        capturedImageView.setVisibility(View.VISIBLE);
                        ExifInterface exif = null;
                        try {
                            exif = new ExifInterface(file.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        int orientation = ExifInterface.ORIENTATION_NORMAL;
                        if (exif != null) {
                            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        }
                        bitmap = rotateBitmap(bitmap, orientation);
                        capturedImageView.setImageBitmap(bitmap);
                        capturedImageView.setTag(file.getAbsolutePath());
                        saveImageToGallery(bitmap);
                        btnSave.setVisibility(View.VISIBLE);
                        Toast.makeText(CameraActivity.this, "Tap the screen to retake the photo", Toast.LENGTH_LONG).show();
//                        }
                    }
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "Failed to save: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                startCamera(cameraFacing);
            }
        });
        capturedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewView.setVisibility(View.VISIBLE);
                capturedImageView.setVisibility(View.GONE);
                startCamera(cameraFacing);
                btnSave.setVisibility(View.GONE);
            }
        });
    }

    // Phương thức quét mã QR từ bitmap
//    private String decodeQRCodeFromBitmap(Bitmap bitmap) {
//        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
//        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
//
//        try {
//            com.google.zxing.Reader reader = new MultiFormatReader();
//            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), getRGBLuminanceSource(bitmap))));
//            Log.d("QRCODE", "6");
//            Result result = reader.decode(binaryBitmap, hints);
//            Log.d("QRCODE", "7");
//            return result.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    private String decodeQRCodeFromBitmap(Bitmap bitmap) {
//        Log.v("HANDLE_IMAGE", "START decodeQRCodeFromBitmap");
//        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
//        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
//        Log.v("HANDLE_IMAGE", "START decodeQRCodeFromBitmap 2");
//        // Optional: Add other hints like PURE_BARCODE or ALLOWED_CHARACTER_SETS
//
//        try {
//            int[] pixels = getRGBLuminanceSource(bitmap);
//            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels)));
//            // Consider using QRCodeReader instead of MultiFormatReader for better accuracy
//            QRCodeReader reader = new QRCodeReader();
//            Result result = reader.decode(binaryBitmap, hints);
//            Log.v("HANDLE_IMAGE", "RESULT QRCODE : " + result.toString());
//            Log.v("HANDLE_IMAGE", "RESULT QRCODE : " + result.getText());
//            return result.getText();
//        } catch (NotFoundException e) {
//            Log.v("HANDLE_IMAGE", "ERROR : " + e.getMessage());
//            // Handle not finding a QR code gracefully (e.g., return null)
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//
//
//    // Phương thức trả về mảng các giá trị RGB từ bitmap
//    private int[] getRGBLuminanceSource(Bitmap bitmap) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int[] pixels = new int[width * height];
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
//        return pixels;
//    }
    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
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
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    private void saveImageToGallery(Bitmap bitmap) {
        // Tạo một tệp tin mới cho ảnh
        String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        ContentResolver resolver = getContentResolver();
        Uri imageUri = null;
        try {
            // Insert ảnh vào MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (imageUri != null) {
                OutputStream outputStream = resolver.openOutputStream(imageUri);
                if (outputStream != null) {
                    // Lưu ảnh vào OutputStream
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
