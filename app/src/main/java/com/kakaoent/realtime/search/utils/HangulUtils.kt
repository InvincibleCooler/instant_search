package com.kakaoent.realtime.search.utils


/**
 * 한글 형태소 분석을 위한 Util
 */
class HangulUtils {
    companion object {
        private const val TAG = "HangulUtils"

        private const val HANGUL_BEGIN_UNICODE = 44032
        private const val HANGUL_END_UNICODE = 55203
        private const val HANGUL_BASE_UNIT = 588

        private var choSung = 0
        private var jungSung = 0
        private var jongSung = 0

        const val TYPE_CHOSUNG = 0
        const val TYPE_JUNGSUNG = 1
        const val TYPE_JONGSUNG = 2

        private val INITIAL = charArrayOf(
            'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ',
            'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
        )

        // ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ
        private val CHO = charArrayOf(
            0x3131.toChar(), 0x3132.toChar(), 0x3134.toChar(), 0x3137.toChar(), 0x3138.toChar(), 0x3139.toChar(), 0x3141.toChar(), 0x3142.toChar(),
            0x3143.toChar(), 0x3145.toChar(), 0x3146.toChar(), 0x3147.toChar(), 0x3148.toChar(), 0x3149.toChar(), 0x314a.toChar(), 0x314b.toChar(),
            0x314c.toChar(), 0x314d.toChar(), 0x314e.toChar()
        )

        // ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅘ ㅙ ㅚ ㅛ ㅜ ㅝ ㅞ ㅟ ㅠ ㅡ ㅢ ㅣ
        private val JUNG = charArrayOf(
            0x314f.toChar(), 0x3150.toChar(), 0x3151.toChar(), 0x3152.toChar(), 0x3153.toChar(), 0x3154.toChar(), 0x3155.toChar(), 0x3156.toChar(),
            0x3157.toChar(), 0x3158.toChar(), 0x3159.toChar(), 0x315a.toChar(), 0x315b.toChar(), 0x315c.toChar(), 0x315d.toChar(), 0x315e.toChar(),
            0x315f.toChar(), 0x3160.toChar(), 0x3161.toChar(), 0x3162.toChar(), 0x3163.toChar()
        )

        // ㄱ ㄲ ㄳ ㄴ ㄵ ㄶ ㄷ ㄹ ㄺ ㄻ ㄼ ㄽ ㄾ ㄿ ㅀ ㅁ ㅂ ㅄ ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ
        private val JONG = charArrayOf(
            0.toChar(), 0x3131.toChar(), 0x3132.toChar(), 0x3133.toChar(), 0x3134.toChar(), 0x3135.toChar(), 0x3136.toChar(), 0x3137.toChar(),
            0x3139.toChar(), 0x313a.toChar(), 0x313b.toChar(), 0x313c.toChar(), 0x313d.toChar(), 0x313e.toChar(), 0x313f.toChar(), 0x3140.toChar(),
            0x3141.toChar(), 0x3142.toChar(), 0x3144.toChar(), 0x3145.toChar(), 0x3146.toChar(), 0x3147.toChar(), 0x3148.toChar(), 0x314a.toChar(),
            0x314b.toChar(), 0x314c.toChar(), 0x314d.toChar(), 0x314e.toChar()
        )

        private fun getHangul(type: Int, value: String): String {
            return divideKorean(type, value)
        }

        private fun divideKorean(type: Int, value: String): String {
            var hangul = ""
            for (element in value) {
                if (element.code in 0xAC00..0xD7A3) { // "AC00:가" ~ "D7A3:힣" 에 속한 글자면
                    // 분해
                    jongSung = element.code - 0xAC00
                    choSung = jongSung / (21 * 28)
                    jongSung %= (21 * 28)
                    jungSung = jongSung / 28
                    jongSung %= 28
                    when (type) {
                        TYPE_CHOSUNG -> {
                            hangul += CHO[choSung]
                        }
                        TYPE_JUNGSUNG -> {
                            hangul += JUNG[jungSung]
                        }
                        TYPE_JONGSUNG -> {
                            if (JONG[jongSung] != JONG[0]) {
                                hangul += JONG[jongSung]
                            }
                        }
                    }
                } else { // 한글이 아닐 경우..
                    hangul += element
                }
            }
            return hangul
        }

        private fun convertCharToUnicode(ch: Char): Int {
            return ch.code
        }

        private fun convertStringToUnicode(str: String?): IntArray? {
            var unicodeList: IntArray? = null
            if (!str.isNullOrEmpty()) {
                unicodeList = IntArray(str.length)
                for (i in str.indices) {
                    unicodeList[i] = convertCharToUnicode(str[i])
                }
            }
            return unicodeList
        }

        private fun convertUnicodeToChar(unicode: Int): Char {
            return Integer.toHexString(unicode).toInt(16).toChar()
        }

        fun getHangulInitial(value: String?): String {
            val result = StringBuilder()
            val unicodeList = convertStringToUnicode(value) ?: return ""

            for (unicode in unicodeList) {
                if (unicode in HANGUL_BEGIN_UNICODE..HANGUL_END_UNICODE) {
                    val tmp = unicode - HANGUL_BEGIN_UNICODE
                    val index = tmp / HANGUL_BASE_UNIT
                    result.append(INITIAL[index])
                } else {
                    result.append(convertUnicodeToChar(unicode))
                }
            }
            return result.toString()
        }

        fun getChoSungList(name: String?): BooleanArray? {
            if (name.isNullOrEmpty()) {
                return null
            }
            val choList = BooleanArray(name.length)
            for (i in name.indices) {
                val c = name[i]
                var isCho = false
                for (cho in INITIAL) {
                    if (c == cho) {
                        isCho = true
                        break
                    }
                }
                choList[i] = isCho
            }
            return choList
        }

        fun isChoSungList(name: String?): Boolean {
            if (name.isNullOrEmpty()) {
                return false
            }

            var isCho = false
            for (i in name.indices) {
                val c = name[i]
                for (cho in INITIAL) {
                    if (c == cho) {
                        isCho = true
                        break
                    }
                }
            }
            return isCho
        }

        fun getHangulInitial(value: String?, searchKeyword: String?): String? {
            return getHangulInitial(value, getChoSungList(searchKeyword))
        }

        fun getHangulInitial(value: String?, isChoList: BooleanArray?): String {
            val result = StringBuilder()
            val unicodeList = convertStringToUnicode(value) ?: return ""
            for (idx in unicodeList.indices) {
                val unicode = unicodeList[idx]
                if (isChoList != null && idx <= isChoList.size - 1) {
                    if (isChoList[idx]) {
                        if (unicode in HANGUL_BEGIN_UNICODE..HANGUL_END_UNICODE) {
                            val tmp = unicode - HANGUL_BEGIN_UNICODE
                            val index = tmp / HANGUL_BASE_UNIT
                            result.append(INITIAL[index])
                        } else {
                            result.append(convertUnicodeToChar(unicode))
                        }
                    } else {
                        result.append(convertUnicodeToChar(unicode))
                    }
                } else {
                    result.append(convertUnicodeToChar(unicode))
                }
            }
            return result.toString()
        }

        fun breakKorean2Elements(original: String): String {
            val sb = StringBuilder()

            for (element in original) {
                if (element.code in 0xAC00..0xD7A3) {
                    val str = element.toString()
                    sb.append(getHangul(TYPE_CHOSUNG, str))
                    sb.append(getHangul(TYPE_JUNGSUNG, str))
                    sb.append(getHangul(TYPE_JONGSUNG, str))
                } else {
                    sb.append(element)
                }
            }
            return sb.toString()
        }
    }
}