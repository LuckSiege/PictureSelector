package com.luck.picture.lib.language;

import java.util.Locale;

/**
 * @author：luck
 * @date：2019-11-25 21:58
 * @describe：语言转换
 */
public class LocaleTransform {
    public static Locale getLanguage(int language) {
        switch (language) {
            case LanguageConfig.ENGLISH:
                // 英语-美国
                return Locale.ENGLISH;
            case LanguageConfig.TRADITIONAL_CHINESE:
                // 繁体中文
                return Locale.TRADITIONAL_CHINESE;
            case LanguageConfig.KOREA:
                // 韩语
                return Locale.KOREA;
            case LanguageConfig.GERMANY:
                // 德语
                return Locale.GERMANY;
            case LanguageConfig.FRANCE:
                // 法语
                return Locale.FRANCE;
            case LanguageConfig.JAPAN:
                // 日语
                return Locale.JAPAN;
            case LanguageConfig.VIETNAM:
                // 越南语
                return new Locale("vi");
            case LanguageConfig.SPANISH:
                // 西班牙语
                return new Locale("es", "ES");
            case LanguageConfig.PORTUGAL:
                // 葡萄牙语
                return new Locale("pt", "PT");
            case LanguageConfig.AR:
                // 阿拉伯语
                return new Locale("ar", "AE");
            case LanguageConfig.RU:
                // 俄语
                return new Locale("ru", "rRU");
            case LanguageConfig.CS:
                // 捷克
                return new Locale("cs", "rCZ");
            case LanguageConfig.KK:
                // 哈萨克斯坦
                return new Locale("kk", "rKZ");
            default:
                // 简体中文
                return Locale.CHINESE;
        }
    }
}
