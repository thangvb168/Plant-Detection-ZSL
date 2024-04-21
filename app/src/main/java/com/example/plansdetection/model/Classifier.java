package com.example.plansdetection.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.plansdetection.ml.Resnet50Model2;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Classifier {
    private static final String TAG = "Classifier";
    private static final int IMAGE_SIZE = 224;
    private final List<String> classes;
    private Resnet50Model2 model;
    public Classifier(Context context) throws IOException {
        Log.d(TAG, "Hello! I'm here");
        String FILE_CLASSES_PATH = "labels.txt";
        classes = loadClasses(context, FILE_CLASSES_PATH);
        loadModel(context);
    }

    private List<String> loadClasses(Context context, String path) throws IOException {
        BufferedReader reader = null;
        List<String> classes = new ArrayList<>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(path), "UTF-8"));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                classes.add(mLine.trim());
            }
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return classes;
    }

    private void loadModel(Context context) {
        try {
            model = Resnet50Model2.newInstance(context);
        } catch (IOException e) {
            model = null;
            throw new RuntimeException(e);
        }
    }

    public String[] predict(Bitmap image) {
        int indexOfClassifier = -1;
        float perOfClassifier = 0.0f;
        TensorImage tensorImage = processImage(image);
        Log.d("HANDLE_IMAGE", "CLASSIFIER::SIZE OF IMAGE : " + image.getHeight() + "x" + image.getWidth());
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, IMAGE_SIZE, IMAGE_SIZE, 3}, DataType.FLOAT32);
        inputFeature0.loadBuffer(tensorImage.getBuffer());

        Resnet50Model2.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

//        Find max classifier features
        float[] resultArr = outputFeature0.getFloatArray();
        for(int i = 0; i < resultArr.length; i++) {
            if(perOfClassifier < resultArr[i]) {
                indexOfClassifier = i;
                perOfClassifier = resultArr[i];
            }
        }
        if(indexOfClassifier == -1) return null;
        String foundClasss = classes.get(indexOfClassifier);
        Log.v(TAG, "foundClass::" + indexOfClassifier);
//        String foundClasss = Integer.toString(indexOfClassifier);
        return new String[]{foundClasss, Float.toString(perOfClassifier)};
    }

    private TensorImage processImage(Bitmap image) {
        if(image != null) {
            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);

            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                    .build();
            tensorImage.load(image);
            tensorImage = imageProcessor.process(tensorImage);
            return tensorImage;
        }
        return null;
    }
}