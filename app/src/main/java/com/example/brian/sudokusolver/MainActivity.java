package com.example.brian.sudokusolver;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

/*
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    static final String TAG = "MainActivity";
    private TextView highlightedSquare = null;
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mImageView = new ImageView(this);
        mImageView.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle, 512, 512));
        setContentView(mImageView);
    }



    /*
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

            //
            Bitmap originalImage = drawableToBitmap(mImageView.getDrawable());
            Bitmap processedImage = processImage(originalImage);
            mImageView.setImageBitmap(processedImage);
        }
    }


    private Bitmap processImage(Bitmap bm){
        Mat im = new Mat();
        Utils.bitmapToMat(bm, im);
        Log.d(TAG, "Loaded mat of size " + im.width() + " x " + im.height() + ", " + im.channels() + " channels");

        // do image processing here
        Imgproc.cvtColor(im, im, Imgproc.COLOR_RGB2GRAY);
        Imgproc.equalizeHist(im, im);
        Mat bw = new Mat();
        Imgproc.adaptiveThreshold(im, bw, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, 15, 5);
        Core.bitwise_not(bw, bw);

        // largest contour
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bw, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Log.d(TAG, "Got "+contours.size()+" contours");
        MatOfPoint largestContour = contours.get(0);
        double largestArea = Imgproc.contourArea(largestContour);
        for(MatOfPoint contour: contours){
            double area = Imgproc.contourArea(contour);
            if(area > largestArea){
                largestArea = area;
                largestContour = contour;
            }
        }
        Log.d(TAG, "largest contour has area "+largestArea);

        // reduce to square
        double eps = 30;  //FIXME: auto-select this value to always return 4 points
        MatOfPoint2f  largestContour2f = new MatOfPoint2f(largestContour.toArray());
        MatOfPoint2f square = new MatOfPoint2f();
        Imgproc.approxPolyDP(largestContour2f, square, eps, true);
        for(Point point: square.toList())
            Log.d(TAG, point.toString());

        // warp to square
        int square_sz = 100;
        int margin = 50;
        Mat square_im = warpToSquare(im, square, square_sz, margin);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap outBm = Bitmap.createBitmap(square_im.width(), square_im.height(), conf);
        Utils.matToBitmap(square_im, outBm);
        return outBm;
    }


    Mat warpToSquare(Mat im, MatOfPoint2f square, int square_sz, int margin){
        float marginf = (float) margin;
        float sz = 9f * square_sz + 2f * margin;
        ArrayList<Point> dest = new ArrayList<>();
        dest.add(new Point(marginf, marginf));
        dest.add(new Point(marginf, sz - marginf));
        dest.add(new Point(sz - marginf, sz - marginf));
        dest.add(new Point(sz - marginf, marginf));
        MatOfPoint2f destMat = new MatOfPoint2f();
        destMat.fromList(dest);
        Mat trans = Imgproc.getPerspectiveTransform(square, destMat);
        Mat squareIm = new Mat();
        Imgproc.warpPerspective(im, squareIm, trans, new Size(sz, sz));
        Log.d(TAG, "post-warp image size: "+squareIm.height()+" x "+squareIm.width()+", "+squareIm.channels()+" channels");
        return squareIm;
    }

    */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


}