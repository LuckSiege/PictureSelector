package com.luck.picture.lib.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import com.luck.picture.lib.utils.SpUtils;
import java.util.Locale;

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: PictureLanguageUtils
 */
public class PictureLanguageUtils {

    private static final String KEY_LOCALE = "KEY_LOCALE";
    private static final String VALUE_FOLLOW_SYSTEM = "VALUE_FOLLOW_SYSTEM";

    private PictureLanguageUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * init app the language
     *
     * @param context
     * @param languageId
     * @param defaultLanguageId
     */
    public static void setAppLanguage(Context context, int languageId, int defaultLanguageId) {
        if (languageId >= 0) {
            applyLanguage(context, LocaleTransform.getLanguage(languageId));
        } else {
            if (defaultLanguageId >= 0) {
                applyLanguage(context, LocaleTransform.getLanguage(defaultLanguageId));
            } else {
                setDefaultLanguage(context);
            }
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
            SpUtils.putString(context, KEY_LOCALE, VALUE_FOLLOW_SYSTEM);
        } else {
            String localLanguage = locale.getLanguage();
            String localCountry = locale.getCountry();
            SpUtils.putString(context, KEY_LOCALE, localLanguage + "$" + localCountry);
        }
        updateLanguage(context, locale);
    }



    private static void updateLanguage(@NonNull Context context, Locale locale) {
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
