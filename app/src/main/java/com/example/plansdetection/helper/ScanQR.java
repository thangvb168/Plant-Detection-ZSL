package com.example.plansdetection.helper;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ScanQR {
    BarcodeScanner scanner;
    public ScanQR() {
        BarcodeScannerOptions options;
        options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC)
                .build();
        scanner = BarcodeScanning.getClient(options);
    }

    public void decode(Bitmap bitmap, ScanCallback callback) {
        getInfoFromQRCode(bitmap, callback);
    }

    public interface ScanCallback {
        void onSuccess(String qrCodeValue);
        void onFailure(Exception e);
    }

    private void getInfoFromQRCode(Bitmap image, ScanCallback callback) {
        scanner.process(image, 0)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        if (barcodes.isEmpty()) {
                            callback.onFailure(new Exception("No QR code found"));
                            return;
                        }
                        String qrCodeValue = barcodes.get(0).getRawValue();
                        callback.onSuccess(qrCodeValue);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e);
                    }
                });
    }
}
