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

        /*
        mImageView = new ImageView(this);
        mImageView.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.puzzle, 512, 512));
        setContentView(mImageView);
        */

        setContentView(R.layout.activity_main);

        // set puzzle
        String puzzle = "400000805" + "030000000" + "000700000" +
                "020000060" + "000080400" + "000010000" +
                "000603070" + "500200000" + "104000000";
        String march26 = "007400091" + "300000000" + "040165070" +
                "060720004" + "500006000" + "009000208" +
                "000200403" + "000050007" + "000000010";
//        setPuzzle(march26);
        setPuzzle(puzzle);
        highlightOccupiedSquares();


        // set up number buttons
        List<String> numbers = new ArrayList<>(Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine"));
        for (String number : numbers) {
            String buttonStr = number + "_button";
            Button button = getButton(buttonStr);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNumberButtonClicked(v);
                }
            });
        }


        // add Delete button functionality
        Button button = (Button) findViewById(R.id.del_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                String buttonText = button.getText().toString();
                if (highlightedSquare != null)
                    highlightedSquare.setText("");
            }
        });


        // set up highlighting
        List<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I"));
        numbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        for (String letter : letters) {
            for (String number : numbers) {
                String squareStr = letter + number;
                TextView square = getSquare(squareStr);
                square.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setHighlightedSquare(v);
                    }
                });
            }


        }


        // solve the puzzle when the button is pressed
        Button solveButton = (Button) findViewById(R.id.solve_button);
        solveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solvePuzzle();
            }
        });

        // set clear functionality
        Button cancelButton = (Button) findViewById(R.id.clear_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String clearStr = "000000000" + "000000000" + "000000000" +
                        "000000000" + "000000000" + "000000000" +
                        "000000000" + "000000000" + "000000000";
                setPuzzle(clearStr);
                clearSquareHighlights();
                highlightedSquare = null;
            }
        });
    }


    private Button getButton(String buttonStr){
        switch (buttonStr) {
            case "one_button":
                return (Button) findViewById(R.id.one_button);
            case "two_button":
                return (Button) findViewById(R.id.two_button);
            case "three_button":
                return (Button) findViewById(R.id.three_button);
            case "four_button":
                return (Button) findViewById(R.id.four_button);
            case "five_button":
                return (Button) findViewById(R.id.five_button);
            case "six_button":
                return (Button) findViewById(R.id.six_button);
            case "seven_button":
                return (Button) findViewById(R.id.seven_button);
            case "eight_button":
                return (Button) findViewById(R.id.eight_button);
            case "nine_button":
                return (Button) findViewById(R.id.nine_button);
        }
        return null;
    }


    protected void onNumberButtonClicked(View v) {
        Button button = (Button) v;
        String buttonText = button.getText().toString();
        String dispText;
        int number = 0;
        if (buttonText.equals("Del")) {
            dispText = "";
        } else {
            dispText = buttonText;
            number = Integer.parseInt(buttonText);
        }
        Log.d(TAG, "Add " + dispText + " to square " + highlightedSquare);
        if (highlightedSquare != null) {
            highlightedSquare.setText(dispText);
        }
    }


    protected void solvePuzzle() {
        String puzzle = getPuzzle();
        Log.d(TAG, "puzzle = " + puzzle);
        SudokuSolver solver = new SudokuSolver();
        String solution = solver.solvePuzzle(puzzle);
        if (solution == null) {
            Log.d(TAG, "Unsolvable/contradictory puzzle!");
            Toast.makeText(this, "Unsolvable/contradictory puzzle!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "solution = " + solution);
            setPuzzle(solution);
        }
    }


    protected void setHighlightedSquare(View v) {
        if (highlightedSquare != null) {
            String text = highlightedSquare.getText().toString();
            if (!text.equals(""))
                highlightedSquare.setBackgroundColor(Color.LTGRAY);
            else
                highlightedSquare.setBackgroundColor(Color.WHITE);
        }
        ((TextView) v).setBackgroundColor(Color.CYAN);
        String squareStr = v.getResources().getResourceName(v.getId());
        squareStr = squareStr.split("/")[1];  // get rid of package name, etc.
        highlightedSquare = (TextView) v;
    }


    protected void highlightOccupiedSquares() {
        // set up highlighting
        List<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I"));
        List<String> numbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        for (String letter : letters) {
            for (String number : numbers) {
                String squareStr = letter + number;
                TextView square = getSquare(squareStr);
                String text = square.getText().toString();
                if (!text.equals(""))
                    square.setBackgroundColor(Color.LTGRAY);
                else
                    square.setBackgroundColor(Color.WHITE);
            }

        }
    }


    protected void clearSquareHighlights() {
        // set up highlighting
        List<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I"));
        List<String> numbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        for (String letter : letters) {
            for (String number : numbers) {
                String squareStr = letter + number;
                TextView square = getSquare(squareStr);
                square.setBackgroundColor(Color.WHITE);
            }

        }
    }

    protected void setPuzzle(String puzzle) {
        Log.d(TAG, "Setting puzzle to "+puzzle);
        List<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I"));
        List<String> numbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        for (int r = 0; r < 9; r++) {
            String letterStr = letters.get(r);
            for (int c = 0; c < 9; c++) {
                Character numChar = puzzle.charAt(9 * r + c);
                int number = Integer.parseInt(numChar.toString());
                String numStr = numbers.get(c);
                String squareStr = letterStr + numStr;
                Log.d(TAG, "setPuzzle: Setting "+squareStr+" to "+number);
                setSquareNumber(squareStr, number);
            }
        }
    }


    protected String getPuzzle() {
        List<String> letters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I"));
        List<String> numbers = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        StringBuilder stringBuilder = new StringBuilder();
        for (String rowLetter : letters) {
            for (String columnNumber : numbers) {
                Integer number = getSquareNumber(rowLetter + columnNumber);
                stringBuilder.append(number);
            }
        }
        String puzzleStr = stringBuilder.toString();
        return puzzleStr;
    }


    private void setSquareNumber(String squareStr, int number) {
        TextView square = getSquare(squareStr);
        if (number == 0)
            square.setText("");
        else
            square.setText(String.valueOf(number));
    }


    private TextView getSquare(String squareStr){
        switch (squareStr){
            case "A1":
                return (TextView) findViewById(R.id.A1);
            case "A2":
                return (TextView) findViewById(R.id.A2);
            case "A3":
                return (TextView) findViewById(R.id.A3);
            case "A4":
                return (TextView) findViewById(R.id.A4);
            case "A5":
                return (TextView) findViewById(R.id.A5);
            case "A6":
                return (TextView) findViewById(R.id.A6);
            case "A7":
                return (TextView) findViewById(R.id.A7);
            case "A8":
                return (TextView) findViewById(R.id.A8);
            case "A9":
                return (TextView) findViewById(R.id.A9);

            case "B1":
                return (TextView) findViewById(R.id.B1);
            case "B2":
                return (TextView) findViewById(R.id.B2);
            case "B3":
                return (TextView) findViewById(R.id.B3);
            case "B4":
                return (TextView) findViewById(R.id.B4);
            case "B5":
                return (TextView) findViewById(R.id.B5);
            case "B6":
                return (TextView) findViewById(R.id.B6);
            case "B7":
                return (TextView) findViewById(R.id.B7);
            case "B8":
                return (TextView) findViewById(R.id.B8);
            case "B9":
                return (TextView) findViewById(R.id.B9);

            case "C1":
                return (TextView) findViewById(R.id.C1);
            case "C2":
                return (TextView) findViewById(R.id.C2);
            case "C3":
                return (TextView) findViewById(R.id.C3);
            case "C4":
                return (TextView) findViewById(R.id.C4);
            case "C5":
                return (TextView) findViewById(R.id.C5);
            case "C6":
                return (TextView) findViewById(R.id.C6);
            case "C7":
                return (TextView) findViewById(R.id.C7);
            case "C8":
                return (TextView) findViewById(R.id.C8);
            case "C9":
                return (TextView) findViewById(R.id.C9);

            case "D1":
                return (TextView) findViewById(R.id.D1);
            case "D2":
                return (TextView) findViewById(R.id.D2);
            case "D3":
                return (TextView) findViewById(R.id.D3);
            case "D4":
                return (TextView) findViewById(R.id.D4);
            case "D5":
                return (TextView) findViewById(R.id.D5);
            case "D6":
                return (TextView) findViewById(R.id.D6);
            case "D7":
                return (TextView) findViewById(R.id.D7);
            case "D8":
                return (TextView) findViewById(R.id.D8);
            case "D9":
                return (TextView) findViewById(R.id.D9);

            case "E1":
                return (TextView) findViewById(R.id.E1);
            case "E2":
                return (TextView) findViewById(R.id.E2);
            case "E3":
                return (TextView) findViewById(R.id.E3);
            case "E4":
                return (TextView) findViewById(R.id.E4);
            case "E5":
                return (TextView) findViewById(R.id.E5);
            case "E6":
                return (TextView) findViewById(R.id.E6);
            case "E7":
                return (TextView) findViewById(R.id.E7);
            case "E8":
                return (TextView) findViewById(R.id.E8);
            case "E9":
                return (TextView) findViewById(R.id.E9);

            case "F1":
                return (TextView) findViewById(R.id.F1);
            case "F2":
                return (TextView) findViewById(R.id.F2);
            case "F3":
                return (TextView) findViewById(R.id.F3);
            case "F4":
                return (TextView) findViewById(R.id.F4);
            case "F5":
                return (TextView) findViewById(R.id.F5);
            case "F6":
                return (TextView) findViewById(R.id.F6);
            case "F7":
                return (TextView) findViewById(R.id.F7);
            case "F8":
                return (TextView) findViewById(R.id.F8);
            case "F9":
                return (TextView) findViewById(R.id.F9);

            case "G1":
                return (TextView) findViewById(R.id.G1);
            case "G2":
                return (TextView) findViewById(R.id.G2);
            case "G3":
                return (TextView) findViewById(R.id.G3);
            case "G4":
                return (TextView) findViewById(R.id.G4);
            case "G5":
                return (TextView) findViewById(R.id.G5);
            case "G6":
                return (TextView) findViewById(R.id.G6);
            case "G7":
                return (TextView) findViewById(R.id.G7);
            case "G8":
                return (TextView) findViewById(R.id.G8);
            case "G9":
                return (TextView) findViewById(R.id.G9);

            case "H1":
                return (TextView) findViewById(R.id.H1);
            case "H2":
                return (TextView) findViewById(R.id.H2);
            case "H3":
                return (TextView) findViewById(R.id.H3);
            case "H4":
                return (TextView) findViewById(R.id.H4);
            case "H5":
                return (TextView) findViewById(R.id.H5);
            case "H6":
                return (TextView) findViewById(R.id.H6);
            case "H7":
                return (TextView) findViewById(R.id.H7);
            case "H8":
                return (TextView) findViewById(R.id.H8);
            case "H9":
                return (TextView) findViewById(R.id.H9);

            case "I1":
                return (TextView) findViewById(R.id.I1);
            case "I2":
                return (TextView) findViewById(R.id.I2);
            case "I3":
                return (TextView) findViewById(R.id.I3);
            case "I4":
                return (TextView) findViewById(R.id.I4);
            case "I5":
                return (TextView) findViewById(R.id.I5);
            case "I6":
                return (TextView) findViewById(R.id.I6);
            case "I7":
                return (TextView) findViewById(R.id.I7);
            case "I8":
                return (TextView) findViewById(R.id.I8);
            case "I9":
                return (TextView) findViewById(R.id.I9);
        }
        return null;
    }


    private Integer getSquareNumber(String squareStr) {
        TextView square = getSquare(squareStr);
        String text = square.getText().toString();
        if (text.length() == 0)
            return 0;
        else {
            Character numChar = text.charAt(0);
            try {
                int number = Integer.parseInt(numChar.toString());
                return number;
            } catch (NumberFormatException e) {
                // FIXME: better error handling
                return 0;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    */

}