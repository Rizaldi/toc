package com.gts.toc.utility;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.android.gms.maps.model.LatLng;
import com.gts.toc.GlobalApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Created by warsono on 02/15/16.
 */

public class Utility {
    public static boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) GlobalApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null) {
                for (int i = 0; i < info.length;i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean EmailValidation(String email) {
        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static int getScreenWidth() {
        DisplayMetrics metrics  = new DisplayMetrics();
        WindowManager wm        = (WindowManager) GlobalApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        int mScreenWidth        = metrics.widthPixels;;
        return mScreenWidth;
    }

    public static int getScreenHeight() {
        DisplayMetrics metrics  = new DisplayMetrics();
        WindowManager wm        = (WindowManager) GlobalApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        int mScreenHeight       = metrics.heightPixels;;
        return mScreenHeight;
    }

    public static boolean isTabletDevice(Context activityContext) {
        boolean device_large    = ((activityContext.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) ||
                ((activityContext.getResources().getConfiguration().screenLayout &
                        Configuration.SCREENLAYOUT_SIZE_MASK) ==
                        Configuration.SCREENLAYOUT_SIZE_XLARGE);

        if (device_large) {
            DisplayMetrics metrics  = new DisplayMetrics();
            Activity activity       = (Activity) activityContext;
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
                    || metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
                    || metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
                    || metrics.densityDpi == DisplayMetrics.DENSITY_TV
                    || metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                return true;
            }
        }
        return false;
    }

    public static String localizeTime(String inputTime) {
        Date date   = new Date();
        SimpleDateFormat dateFormatLocale   = new SimpleDateFormat("dd MMM yyyy hh:mm:ss", GlobalApplication.getContext().getResources().getConfiguration().locale);
        SimpleDateFormat dateFormat         = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            date = dateFormat.parse(inputTime);
        } catch (ParseException e) {
            return inputTime;
        }
        return dateFormatLocale.format(date);
    }

    public static String doubleToStringNoDecimal(double d) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);;
        formatter.applyPattern("###,###.00");
        return formatter.format(d);
    }
    public static Uri getPhotoFileUri(String fileName) {
        if (isExternalStorageAvailable()) {
            String AppDirectory	    = Environment.getExternalStorageDirectory().toString()+"/"+"Android/data/"+ GlobalApplication.getContext().getPackageName();
            File mediaStorageDir    = new File(AppDirectory, "Camera");
            return Uri.fromFile(new File(mediaStorageDir.getPath().toString(),fileName));
        }
        return null;
    }
    public static Uri getPhotoFileCamera(String fileName) {
        if (isExternalStorageAvailable()) {
            File mediaStorageDir    = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
            return Uri.fromFile(new File(mediaStorageDir.getPath().toString(),fileName));
        }
        return null;
    }
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
    public static Bitmap rotateBitmapOrientation(String photoFilePath) {
        BitmapFactory.Options bounds    = new BitmapFactory.Options();
        bounds.inJustDecodeBounds       = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts      = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {}
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation     = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        Matrix matrix   = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        return rotatedBitmap;
    }
    public static File createFileFromBitmap(byte[] bitmapdata, String FileName){
        File mFileResult = new File(GlobalApplication.getContext().getCacheDir(), FileName);
        try {
            mFileResult.createNewFile();
            FileOutputStream fos 	= null;
            try {
                fos = new FileOutputStream(mFileResult);
            } catch (FileNotFoundException e) {}
            try {
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {}
        } catch (IOException e) {}
        return mFileResult;
    }
    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius  = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a    = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c    = 2 * Math.asin(Math.sqrt(a));
        return Radius * c;
    }
    public static boolean isNightTime(){
        Calendar cal    = Calendar.getInstance();
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);
        if (currentHour > 21){
            return true;
        }else{
            return false;
        }
    }
//    private String getDistanceOnRoad(double latitude, double longitude,
//                                     double prelatitute, double prelongitude) {
//        String result_in_kms = "";
//        String url = "http://maps.google.com/maps/api/directions/xml?origin="
//                + latitude + "," + longitude + "&destination=" + prelatitute
//                + "," + prelongitude + "&sensor=false&units=metric";
//        String tag[]            = { "text" };
//        HttpResponse response   = null;
//        try {
//            HttpClient httpClient       = new DefaultHttpClient();
//            HttpContext localContext    = new BasicHttpContext();
//            HttpPost httpPost           = new HttpPost(url);
//            response                    = httpClient.execute(httpPost, localContext);
//            InputStream is              = response.getEntity().getContent();
//            DocumentBuilder builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            Document doc                = builder.parse(is);
//            if (doc != null) {
//                NodeList nl;
//                ArrayList args = new ArrayList();
//                for (String s : tag) {
//                    nl = doc.getElementsByTagName(s);
//                    if (nl.getLength() > 0) {
//                        Node node = nl.item(nl.getLength() - 1);
//                        args.add(node.getTextContent());
//                    } else {
//                        args.add(" - ");
//                    }
//                }
//                result_in_kms = String.format("%s", args.get(0));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result_in_kms;
//    }
}
