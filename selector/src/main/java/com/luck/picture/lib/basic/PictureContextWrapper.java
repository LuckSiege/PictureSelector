package com.luck.picture.lib.basic;

import android.content.Context;
import android.content.ContextWrapper;

import com.luck.picture.lib.language.LanguageConfig;
import com.luck.picture.lib.language.PictureLanguageUtils;

/**
 * @author：luck
 * @date：2019-12-15 19:34
 * @describe：ContextWrapper
 */
public class PictureContextWrapper extends ContextWrapper {

    public PictureContextWrapper(Context base) {
        super(base);
    }

    public static ContextWrapper wrap(Context context, int language, int defaultLanguage) {
        if (language != LanguageConfig.UNKNOWN_LANGUAGE) {
            PictureLanguageUtils.setAppLanguage(context, language, defaultLanguage);
        }
        return new PictureContextWrapper(context);
    }

    @Override
    public Object getSystemService(String name) {
        if (Context.AUDIO_SERVICE.equals(name)) {
            return getApplicationContext().getSystemService(name);
        }
        return super.getSystemService(name);
    }
}
