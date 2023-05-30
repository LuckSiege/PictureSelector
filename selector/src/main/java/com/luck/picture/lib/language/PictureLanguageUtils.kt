package com.luck.picture.lib.language

import android.content.Context
import com.luck.picture.lib.language.LocaleTransform.getLanguage
import java.util.*

/**
 * @author：luck
 * @data：2018/3/28 下午1:00
 * @描述: PictureLanguageUtils
 */
object PictureLanguageUtils {

    /**
     * init app the language
     *
     * @param context
     * @param language
     * @param defaultLanguage
     */
    fun setAppLanguage(context: Context, language: Language, defaultLanguage: Language) {
        if (language != Language.SYSTEM_LANGUAGE || language != Language.UNKNOWN_LANGUAGE) {
            applyLanguage(context, getLanguage(language))
        } else {
            if (defaultLanguage != Language.SYSTEM_LANGUAGE || defaultLanguage != Language.UNKNOWN_LANGUAGE) {
                applyLanguage(context, getLanguage(defaultLanguage))
            } else {
                setDefaultLanguage(context)
            }
        }
    }

    /**
     * Apply the language.
     *
     * @param locale The language of locale.
     */
    private fun applyLanguage(context: Context, locale: Locale) {
        val resources = context.resources
        val config = resources.configuration
        val contextLocale = config.locale
        if (equals(contextLocale.language, locale.language) && equals(
                contextLocale.country,
                locale.country
            )
        ) {
            return
        }
        val dm = resources.displayMetrics
        config.setLocale(locale)
        context.createConfigurationContext(config)
        resources.updateConfiguration(config, dm)
    }

    /**
     * set default language
     *
     * @param context
     */
    private fun setDefaultLanguage(context: Context) {
        val resources = context.resources
        val config = resources.configuration
        val dm = resources.displayMetrics
        config.setLocale(Locale.getDefault())
        context.createConfigurationContext(config)
        resources.updateConfiguration(config, dm)
    }

    private fun equals(s1: CharSequence?, s2: CharSequence?): Boolean {
        if (s1 === s2) return true
        var length = 0
        return if (s1 != null && s2 != null && s1.length.also { length = it } == s2.length) {
            if (s1 is String && s2 is String) {
                s1 == s2
            } else {
                for (i in 0 until length) {
                    if (s1[i] != s2[i]) return false
                }
                true
            }
        } else false
    }
}