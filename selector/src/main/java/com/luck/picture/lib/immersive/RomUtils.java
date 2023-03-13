package com.luck.picture.lib.immersive;

import android.os.Build;
import android.text.TextUtils;

import com.luck.picture.lib.utils.ValueOf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * @author：luck
 * @data：2018/3/28 下午1:02
 * @描述: Rom版本管理
 */

public class RomUtils {

    private static final String[] ROM_SAMSUNG = {"samsung"};
    private final static String UNKNOWN = "unknown";

    public static class AvailableRomType {
        public static final int MIUI = 1;
        public static final int FLYME = 2;
        public static final int ANDROID_NATIVE = 3;
        public static final int NA = 4;
    }


    private static Integer romType;

    public static int getLightStatusBarAvailableRomType() {
        if (romType != null) {
            return romType;
        }

        if (isMIUIV6OrAbove()) {
            romType = AvailableRomType.MIUI;
            return romType;
        }

        if (isFlymeV4OrAbove()) {
            romType = AvailableRomType.FLYME;
            return romType;
        }

        if (isAndroid5OrAbove()) {
            romType = AvailableRomType.ANDROID_NATIVE;
            return romType;
        }

        romType = AvailableRomType.NA;
        return romType;
    }

    //Flyme V4的displayId格式为 [Flyme OS 4.x.x.xA]
    //Flyme V5的displayId格式为 [Flyme 5.x.x.x beta]
    private static boolean isFlymeV4OrAbove() {
        return (getFlymeVersion() >= 4);
    }


    //Flyme V4的displayId格式为 [Flyme OS 4.x.x.xA]
    //Flyme V5的displayId格式为 [Flyme 5.x.x.x beta]
    public static int getFlymeVersion() {
        String displayId = Build.DISPLAY;
        if (!TextUtils.isEmpty(displayId) && displayId.contains("Flyme")) {
            displayId = displayId.replaceAll("Flyme", "");
            displayId = displayId.replaceAll("OS", "");
            displayId = displayId.replaceAll(" ", "");


            String version = displayId.substring(0, 1);

            return stringToInt(version);
        }
        return 0;
    }

    //MIUI V6对应的versionCode是4
    //MIUI V7对应的versionCode是5
    private static boolean isMIUIV6OrAbove() {
        String miuiVersionCodeStr = getSystemProperty();
        if (!TextUtils.isEmpty(miuiVersionCodeStr)) {
            try {
                int miuiVersionCode = ValueOf.toInt(miuiVersionCodeStr);
                if (miuiVersionCode >= 4) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public static int getMIUIVersionCode() {
        String miuiVersionCodeStr = getSystemProperty();
        int miuiVersionCode = 0;
        if (!TextUtils.isEmpty(miuiVersionCodeStr)) {
            try {
                miuiVersionCode = ValueOf.toInt(miuiVersionCodeStr);
                return miuiVersionCode;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return miuiVersionCode;
    }


    //Android Api 23以上
    private static boolean isAndroid5OrAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }


    private static String getSystemProperty() {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + "ro.miui.ui.version.code");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    /**
     * Return whether the rom is made by samsung.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isSamsung() {
        final String brand = getBrand();
        final String manufacturer = getManufacturer();
        return isRightRom(brand, manufacturer, ROM_SAMSUNG);
    }

    private static boolean isRightRom(final String brand, final String manufacturer, final String... names) {
        for (String name : names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true;
            }
        }
        return false;
    }

    private static String getManufacturer() {
        try {
            String manufacturer = Build.MANUFACTURER;
            if (!TextUtils.isEmpty(manufacturer)) {
                return manufacturer.toLowerCase();
            }
        } catch (Throwable ignore) {/**/}
        return UNKNOWN;
    }

    private static String getBrand() {
        try {
            String brand = Build.BRAND;
            if (!TextUtils.isEmpty(brand)) {
                return brand.toLowerCase();
            }
        } catch (Throwable ignore) {/**/}
        return UNKNOWN;
    }

    /**
     * 匹配数值
     *
     * @param str
     * @return
     */
    public static int stringToInt(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]+$");
        return pattern.matcher(str).matches() ? ValueOf.toInt(str) : 0;
    }

}
