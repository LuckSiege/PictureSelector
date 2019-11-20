package com.luck.picture.lib.immersive;

import android.os.Build;
import android.text.TextUtils;
import com.luck.picture.lib.tools.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author：luck
 * @data：2018/3/28 下午1:02
 * @描述: Rom版本管理
 */

public class RomUtils {
    public class AvailableRomType {
        public static final int MIUI = 1;
        public static final int FLYME = 2;
        public static final int ANDROID_NATIVE = 3;
        public static final int NA = 4;
    }


    private static Integer romType;

    public static int getLightStatausBarAvailableRomType() {
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

            if (version != null) {
                return StringUtils.stringToInt(version);
            }
        }
        return 0;
    }

    //MIUI V6对应的versionCode是4
    //MIUI V7对应的versionCode是5
    private static boolean isMIUIV6OrAbove() {
        String miuiVersionCodeStr = getSystemProperty("ro.miui.ui.version.code");
        if (!TextUtils.isEmpty(miuiVersionCodeStr)) {
            try {
                int miuiVersionCode = Integer.parseInt(miuiVersionCodeStr);
                if (miuiVersionCode >= 4) {
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }


    public static int getMIUIVersionCode() {
        String miuiVersionCodeStr = getSystemProperty("ro.miui.ui.version.code");
        int miuiVersionCode = 0;
        if (!TextUtils.isEmpty(miuiVersionCodeStr)) {
            try {
                miuiVersionCode = Integer.parseInt(miuiVersionCodeStr);
                return miuiVersionCode;
            } catch (Exception e) {
            }
        }
        return miuiVersionCode;
    }


    //Android Api 23以上
    private static boolean isAndroid5OrAbove() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        return false;
    }


    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
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
                }
            }
        }
        return line;
    }

}
