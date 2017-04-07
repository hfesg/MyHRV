package com.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {//一些工具方法可以扔在这儿
    private static final String TAG = "Utils";
    public static void shareApp(Context context) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://rodchong.azurewebsites.net/MobileRun/Download");
        shareIntent.setType("text/plain");

        //设置分享列表的标题，并且每次都显示分享列表
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }


    public static String cutLine(String line, int max) {
        String[] result = line.split("\n", max);
        if (result.length < max) {
            return line;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String tmp : result) {
                stringBuilder.append(tmp);
            }
            return stringBuilder.toString();
        }
    }

    /**
     * @param n             保留的位数
     * @param decimalToSave 保留的小数
     * @return String 保留x位小数
     */
    public static String saveXDecimal(int n, float decimalToSave) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) {
            s.append("0");
        }
        DecimalFormat df = new DecimalFormat("0." + s.toString());
        return df.format(decimalToSave);
    }

    public static String format2FloatWithoutZero(float number) {
        if (number - Math.floor(number) != 0) {
            return format2BitFloat(number);
        } else {
            return String.valueOf(Math.round(number));
        }
    }

    /**
     * @param number float
     * @return String like 0.00
     */
    public static String format2BitFloat(float number) {
        return saveXDecimal(2, number);
    }

    /**
     * @param number String
     * @return String like 0.00
     */
    public static String format2BitFloat(String number) {
        return format2BitFloat(Float.parseFloat(number));
    }

    /**
     * @param number String
     * @return float number like 0.000000
     */
    public static String format6BitFloat(String number) {
        return format6BitFloat(Float.parseFloat(number));
    }

    /**
     * @param number float
     * @return float number like 0.000000
     */
    public static String format6BitFloat(float number) {
        return saveXDecimal(6, number);
    }

    /**
     * @param second int
     * @return String like 0:00:00
     */
    public static String parseTime(int second) {
        int tmp = second / 3600;
        second = second - tmp * 3600;
        String h = tmp > 9 ? "" + tmp : "0" + tmp;
        if (Integer.valueOf(h) > 99) {
            h = "99";
        }
        tmp = second / 60;
        second = second - tmp * 60;
        String min = tmp > 9 ? "" + tmp : "0" + tmp;
        String sec = second > 9 ? "" + second : "0" + second;
        return h + ":" + min + ":" + sec;
    }




    /**
     * 判断LatLng对象是否相等的方法
     *
     * @param first  LatLng
     * @param second LatLng
     * @return isLatLngEqual
     */

    public static boolean isLatLngEqual(LatLng first, LatLng second) {
        return !(first == null || second == null) && (first.latitude == second.latitude && first.longitude == second.longitude);
    }

    /**
     * 获取当前的日期
     *
     * @return date like xxxx-xx-xx
     */
    public static String getDate() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH);
    }



    public static String base64_decode(String string) {
        return new String(Base64.decode(string, Base64.DEFAULT));
    }

    public static String base64_encode(String string) {
        return Base64.encodeToString(string.getBytes(), Base64.CRLF);
    }

    public static long getTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    //根据经纬度信息计算距离
//    public static float getDistance(List<AMapLocation> list) {
//        float distance = 0;
//        if (list == null || list.size() == 0) {
//            return distance;
//        }
//        for (int i = 0; i < list.size() - 1; i++) {
//            AMapLocation firstPoint = list.get(i);
//            AMapLocation secondPoint = list.get(i + 1);
//            LatLng firstLatLng = new LatLng(firstPoint.getLatitude(),
//                    firstPoint.getLongitude());
//            LatLng secondLatLng = new LatLng(secondPoint.getLatitude(),
//                    secondPoint.getLongitude());
//            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
//                    secondLatLng);
//            distance = (float) (distance + betweenDis);
//        }
//        return distance / 1000f;
//    }

    /**
     * 根据经纬度计算距离，忽略经纬度为-1的点（无效点）
     *
     * @param list AMapLocation list
     * @return distance km
     */
    public static float getDistance(List<AMapLocation> list) {
        double betweenDis = 0;
        if (list == null || list.size() == 0) {
            return 0f;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstPoint = list.get(i);
            AMapLocation secondPoint = list.get(i + 1);
            LatLng firstLatLng = new LatLng(firstPoint.getLatitude(),
                    firstPoint.getLongitude());
            LatLng secondLatLng = new LatLng(secondPoint.getLatitude(),
                    secondPoint.getLongitude());
            if (secondPoint.getLatitude() == -1) {
                if (i + 2 < list.size()) {
                    i = i + 1;
                }
            } else if (firstPoint.getLatitude() == -1) {
                continue;
            } else {
                betweenDis += AMapUtils.calculateLineDistance(firstLatLng,
                        secondLatLng);
            }
        }
        return (float) (betweenDis / 1000f);
    }


    /**
     * 根据经纬度数组，计算一段轨迹的中心点,貌似只能在用一个半球有效
     *
     * @return 轨迹中心的经纬度
     */
    public static LatLng getPathCenter(List<LatLng> latLngs) {
        double latitude = 0;
        double longitude = 0;
        int i;
        int j = 0;//有效点的个数
        for (i = 0; i < latLngs.size(); i++) {
            if (latLngs.get(i).latitude != -1) {
                latitude += latLngs.get(i).latitude;
                longitude += latLngs.get(i).longitude;
                j++;
            }

            Log.d(TAG, "getPathCenter: ("+latLngs.get(i).latitude+","+latLngs.get(i).longitude+")");
        }
        return new LatLng(latitude / j, longitude / j);
    }

    /**
     * 将时间戳转换为时间
     *
     * @param timestamp timestamp 单位：秒
     * @return HH:mm
     */
    public static String stampToDate(String timestamp) {
        String result;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        long lt = Long.valueOf(timestamp) * 1000;
        Date date = new Date(lt);
        result = simpleDateFormat.format(date);

        return result;
    }

    //10进制转16进制
    public static String IntToHex(int n) {
        char[] ch = new char[20];
        int nIndex = 0;
        while (true) {
            int m = n / 16;
            int k = n % 16;
            if (k == 15)
                ch[nIndex] = 'F';
            else if (k == 14)
                ch[nIndex] = 'E';
            else if (k == 13)
                ch[nIndex] = 'D';
            else if (k == 12)
                ch[nIndex] = 'C';
            else if (k == 11)
                ch[nIndex] = 'B';
            else if (k == 10)
                ch[nIndex] = 'A';
            else
                ch[nIndex] = (char) ('0' + k);
            nIndex++;
            if (m == 0)
                break;
            n = m;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ch, 0, nIndex);
        stringBuilder.reverse();
        return stringBuilder.toString();
    }

    //16进制转10进制
    public static int HexToInt(String strHex) {
        int nResult = 0;
        if (!IsHex(strHex))
            return nResult;
        String str = strHex.toUpperCase();
        if (str.length() > 2) {
            if (str.charAt(0) == '0' && str.charAt(1) == 'X') {
                str = str.substring(2);
            }
        }
        int nLen = str.length();
        for (int i = 0; i < nLen; ++i) {
            char ch = str.charAt(nLen - i - 1);
            try {
                nResult += (GetHex(ch) * GetPower(16, i));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return nResult;
    }

    //计算16进制对应的数值
    public static int GetHex(char ch) throws Exception {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        throw new Exception("error param");
    }

    //计算幂
    public static int GetPower(int nValue, int nCount) throws Exception {
        if (nCount < 0)
            throw new Exception("nCount can't small than 1!");
        if (nCount == 0)
            return 1;
        int nSum = 1;
        for (int i = 0; i < nCount; ++i) {
            nSum = nSum * nValue;
        }
        return nSum;
    }

    //判断是否是16进制数
    public static boolean IsHex(String strHex) {
        int i = 0;
        if (strHex.length() > 2) {
            if (strHex.charAt(0) == '0' && (strHex.charAt(1) == 'X' || strHex.charAt(1) == 'x')) {
                i = 2;
            }
        }
        for (; i < strHex.length(); ++i) {
            char ch = strHex.charAt(i);
            if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f'))
                continue;
            return false;
        }
        return true;
    }

    /**
     * 判断是否开启了GPS
     *
     * @param context context
     * @return isOpenGps
     */
    public static boolean isOpenGps(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
//        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        return gps || network;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 引导用户打开GPS
     *
     * @param context context
     */
    public static void openGPS(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }
}
