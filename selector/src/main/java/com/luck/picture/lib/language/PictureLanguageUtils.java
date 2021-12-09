package com.luck.picture.lib.language;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: PictureLanguageUtils
 */
public class PictureLanguageUtils {

    private static final String KEY_LOCALE = "KEY_LOCALE";
    private static final String VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM";
    private static final String SP_NAME = "PictureSpUtils";
    private static SharedPreferences  pictureSpUtils;
    private PictureLanguageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * init app the language
     *
     * @param context
     * @param languageId
     */
    public static void setAppLanguage(Context context, int languageId) {
        WeakReference<Context> contextWeakReference = new WeakReference<>(context);
        if (languageId >= 0) {
            applyLanguage(contextWeakReference.get(), LocaleTransform.getLanguage(languageId));
        } else {
            setDefaultLanguage(contextWeakReference.get());
        }
    }

    /**
     * Apply the language.
     *
     * @param locale The language of locale.
     */
    private static void applyLanguage(@NonNull Context context, @NonNull final Locale locale) {
        applyLanguage(context, locale, false);
    }


    private static void applyLanguage(@NonNull Context context, @NonNull final Locale locale,
                                      final boolean isFollowSystem) {
        if (isFollowSystem) {
            pictureSpUtils = context.getSharedPreferences("PictureSpUtils", Context.MODE_PRIVATE);
            getSp(context).edit().putString(KEY_LOCALE, VALUE_FOLLOW_SYSTEM).apply();
        } else {
            String localLanguage = locale.getLanguage();
            String localCountry = locale.getCountry();
            pictureSpUtils = context.getSharedPreferences("PictureSpUtils", Context.MODE_PRIVATE);
            getSp(context).edit().putString(KEY_LOCALE, localLanguage + "$" + localCountry).apply();
        }
        updateLanguage(context, locale);
    }

    private static SharedPreferences getSp(Context context) {
        if (pictureSpUtils == null) {
            pictureSpUtils = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return pictureSpUtils;
    }

    private static void updateLanguage(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        Locale contextLocale = config.locale;
        if (equals(contextLocale.getLanguage(), locale.getLanguage())
                && equals(contextLocale.getCountry(), locale.getCountry())) {
            return;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.setLocale(locale);
        context.createConfigurationContext(config);
        resources.updateConfiguration(config, dm);
    }

    /**
     * set default language
     *
     * @param context
     */
    private static void setDefaultLanguage(Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.setLocale(Locale.getDefault());
        context.createConfigurationContext(config);
        resources.updateConfiguration(config, dm);
    }

    private static boolean equals(final CharSequence s1, final CharSequence s2) {
        if (s1 == s2) return true;
        int length;
        if (s1 != null && s2 != null && (length = s1.length()) == s2.length()) {
            if (s1 instanceof String && s2 instanceof String) {
                return s1.equals(s2);
            } else {
                for (int i = 0; i < length; i++) {
                    if (s1.charAt(i) != s2.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }
}
