package com.luck.picture.lib.language

import java.util.*

/**
 * @author：luck
 * @date：2019-11-25 21:58
 * @describe：语言转换
 */
object LocaleTransform {
    fun getLanguage(language: Language): Locale {
        return when (language) {
            Language.ENGLISH ->                 // 英语-美国
                Locale.ENGLISH
            Language.TRADITIONAL_CHINESE ->                 // 繁体中文
                Locale.TRADITIONAL_CHINESE
            Language.KOREA ->                 // 韩语
                Locale.KOREA
            Language.GERMANY ->                 // 德语
                Locale.GERMANY
            Language.FRANCE ->                 // 法语
                Locale.FRANCE
            Language.JAPAN ->                 // 日语
                Locale.JAPAN
            Language.VIETNAM ->                 // 越南语
                Locale("vi")
            Language.SPANISH ->                 // 西班牙语
                Locale("es", "ES")
            Language.PORTUGAL ->                 // 葡萄牙语
                Locale("pt", "PT")
            Language.AR ->                 // 阿拉伯语
                Locale("ar", "AE")
            Language.RU ->                 // 俄语
                Locale("ru", "rRU")
            Language.CS ->                 // 捷克
                Locale("cs", "rCZ")
            Language.KK ->                 // 哈萨克斯坦
                Locale("kk", "rKZ")
            else ->                 // 简体中文
                Locale.CHINESE
        }
    }
}