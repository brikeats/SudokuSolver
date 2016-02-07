package com.example.brian.sudokusolver;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 2/7/16.
 */
public class PuzzleImageExtractor {

    static final String TAG = "PuzzleImageExtractor";
    private final int SQUARE_SIZE = 100;  // pixels -- image will be 9*SQUARE_SIZE
    private Bitmap mBitmap;
    private Mat mImage;


    public PuzzleImageExtractor(Bitmap cameraBitmap){
        mBitmap = cameraBitmap;
        mImage = new Mat();
        Utils.bitmapToMat(cameraBitmap, mImage);

        // TODO: automatically get orientation right
        Core.flip(mImage.t(), mImage, 0);

        // detect puzzle in image
        Mat puzzleIm = getRectifiedPuzzle(mImage.clone());

        // set main image to the trimmed, rectified puzzle ROI
        mImage = trimEdges(puzzleIm);
    }


    private Mat getRectifiedPuzzle(Mat im){

//        Log.d(TAG, "Processing mat of size " + im.width() + " x " + im.height() + ", " + im.channels() + " channels");

        // Convert to grayscale, equalize histogram, threshold
        Imgproc.cvtColor(im, im, Imgproc.COLOR_RGB2GRAY);
        Imgproc.equalizeHist(im, im);
        Mat bw = new Mat();
        int kernelSize = 15;  // FIXME: should this be a fraction of the image size?
        int subtractionConstant = 5;  // TODO: is 5 always okay?
        Imgproc.adaptiveThreshold(im, bw, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, kernelSize, subtractionConstant);
        Core.bitwise_not(bw, bw);

        MatOfPoint2f square = getCornersOfLargestContour(bw);

        // warp to square
        int squareSize = 100;
        int margin = 0;
        Mat unwarpedIm = warpToSquare(im, square, 9 * SQUARE_SIZE, margin);

        return unwarpedIm;
    }


    Mat trimEdges(Mat img){
        int width = img.width();
        int height = img.height();
        int squareSize = width/7;

        // Top-Left
        Rect TL = new Rect(0, 0, squareSize, squareSize);
        Mat TLim = new Mat(img.clone(), TL);
        Imgproc.threshold(TLim, TLim, 0, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
        MatOfPoint2f TLsquare = getCornersOfLargestContour(TLim.clone());
        Point TLpt = selectPoint(TLsquare, "TL");

        // Top-Right
        Rect TR = new Rect(width-squareSize, 0, squareSize, squareSize);
        Mat TRim = new Mat(img.clone(), TR);
        Imgproc.threshold(TRim, TRim, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        MatOfPoint2f TRsquare = getCornersOfLargestContour(TRim.clone());
        Point TRpt = selectPoint(TRsquare, "TR");
        TRpt.x = width - squareSize + TRpt.x;

        // Bottom-Left
        Rect BL = new Rect(0, height-squareSize, squareSize, squareSize);
        Mat BLim = new Mat(img.clone(), BL);
        Imgproc.threshold(BLim, BLim, 0, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
        MatOfPoint2f BLsquare = getCornersOfLargestContour(BLim.clone());
        Point BLpt = selectPoint(BLsquare, "BL");
        BLpt.y = height - squareSize + BLpt.y;


        // Bottom-Right
        Rect BR = new Rect(width-squareSize, height-squareSize, squareSize, squareSize);
        Mat BRim = new Mat(img.clone(), BR);
        Imgproc.threshold(BRim, BRim, 0, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
        MatOfPoint2f BRsquare = getCornersOfLargestContour(BRim.clone());
        Point BRpt = selectPoint(BRsquare, "BR");
        BRpt.x = width - squareSize + BRpt.x;
        BRpt.y = height - squareSize + BRpt.y;

//        Imgproc.circle(img, TLpt, 5, new Scalar(255,0,0), 2);
//        Imgproc.circle(img, TRpt, 5, new Scalar(255,0,0), 2);
//        Imgproc.circle(img, BLpt, 5, new Scalar(255,0,0), 2);
//        Imgproc.circle(img, BRpt, 5, new Scalar(255,0,0), 2);

        MatOfPoint2f insideCorners = new MatOfPoint2f(TLpt, BLpt, BRpt, TRpt);
        int square_sz = 100;
        img = warpToSquare(img, insideCorners, 9*SQUARE_SIZE, 0);

        return img;

    }


    Point selectPoint(MatOfPoint2f points, String pointName){

        double maxX = 0;
        for (Point pt: points.toArray())
            if (pt.x > maxX) maxX = pt.x;
        double maxY = 0;
        for (Point pt: points.toArray())
            if (pt.y > maxY) maxY = pt.y;

        double maxRsqr = 0;
        double minRsqr = 1e6;

        switch (pointName){
            case "TL":
                Point TLpt = new Point();
                for (Point pt: points.toArray()){
                    double rSqr = pt.x*pt.x + pt.y*pt.y;
                    if (rSqr < minRsqr){
                        minRsqr = rSqr;
                        TLpt = pt;
                    }
                }
                return TLpt;
            case "TR":
                Point TRpt = new Point();
                for (Point pt: points.toArray()){
                    double rSqr = pt.x*pt.x + (maxY-pt.y)*(maxY-pt.y);
                    if (rSqr > maxRsqr){
                        maxRsqr = rSqr;
                        TRpt = pt;
                    }
                }
                return TRpt;
            case "BR":
                Point BRpt = new Point();
                for (Point pt: points.toArray()){
                    double rSqr = pt.x*pt.x + pt.y*pt.y;
                    if (rSqr > maxRsqr){
                        maxRsqr = rSqr;
                        BRpt = pt;
                    }
                }
                return BRpt;
            case "BL":
                Point BLpt = new Point();
                for (Point pt: points.toArray()){
                    double rSqr = (maxX-pt.x)*(maxX-pt.x) + pt.y*pt.y;
                    if (rSqr > maxRsqr){
                        maxRsqr = rSqr;
                        BLpt = pt;
                    }
                }
                return BLpt;
        }
        return points.toArray()[0];
    }


    MatOfPoint2f getCornersOfLargestContour(Mat bwImg){

        // largest contour
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(bwImg, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
//        Log.d(TAG, "Got " + contours.size() + " contours");
        MatOfPoint largestContour = contours.get(0);
        double largestArea = Imgproc.contourArea(largestContour);
        for(MatOfPoint contour: contours){
            double area = Imgproc.contourArea(contour);
            if(area > largestArea){
                largestArea = area;
                largestContour = contour;
            }
        }
//        Log.d(TAG, "largest contour has area "+largestArea);

        // reduce to square
        double eps = 30;  // FIXME: auto-select this value to always return 4 points
        MatOfPoint2f  largestContour2f = new MatOfPoint2f(largestContour.toArray());
        MatOfPoint2f square = new MatOfPoint2f();
        Imgproc.approxPolyDP(largestContour2f, square, eps, true);
        return square;
    }


    Mat warpToSquare(Mat im, MatOfPoint2f square, int size, int margin){

        Point TL = selectPoint(square, "TL");
        Point TR = selectPoint(square, "TR");
        Point BL = selectPoint(square, "BL");
        Point BR = selectPoint(square, "BR");
        square = new MatOfPoint2f(TL, BL, BR, TR);

        float marginf = (float) margin;
        float sz = size + 2f * margin;
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
        //Log.d(TAG, "post-warp image size: " + squareIm.height() + " x " + squareIm.width() + ", " + squareIm.channels() + " channels");
        return squareIm;
    }


    public static Bitmap matToBitmap(Mat im){
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(im.width(), im.height(), conf);
        Utils.matToBitmap(im, bitmap);
        return bitmap;
    }


    Bitmap getProcessedBitmap(){
        return matToBitmap(mImage);
    }


    public Mat[][] getSquareImages(){
        Mat[][] squareImList = new Mat[9][9];
        for(int row=0; row<9; row++){
            for(int col=0; col<9; col++){
                Mat squareIm = getSquare(row, col);
                squareImList[row][col] = squareIm;
            }
        }
        return squareImList;
    }


    Mat getSquare(int row, int col){

        int margin = SQUARE_SIZE/3;
        int colSta = SQUARE_SIZE*col - margin/2;
        int rowSta = SQUARE_SIZE*row - margin/2;
        colSta = Math.max(0, colSta);
        rowSta = Math.max(0, rowSta);

        int squareSizeRow = SQUARE_SIZE + margin;
        int squareSizeCol = SQUARE_SIZE + margin;
        if (row == 8) squareSizeRow = mImage.height() - rowSta;
        if (col == 8) squareSizeCol = mImage.width() - colSta;

        // select approximate image
        Mat approxSquareIm = new Mat(mImage, new Rect(colSta, rowSta, squareSizeCol, squareSizeRow));

        // Convert to grayscale, equalize histogram, threshold
        int kernelSize = 15;  // FIXME: should this be a fraction of the image size?
        int subtractionConstant = 5;  // TODO: is 5 always okay?
        Mat bw = new Mat();
        Imgproc.adaptiveThreshold(approxSquareIm, bw, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, kernelSize, subtractionConstant);
        MatOfPoint2f square = getCornersOfLargestContour(bw.clone());

        Mat squareIm = warpToSquare(approxSquareIm, square, SQUARE_SIZE, 0);
//        Log.d(TAG, "returning image of size "+squareIm.width()+" x "+squareIm.height());

        return squareIm;
    }

}
