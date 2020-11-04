package  com.courierdriver.utils

import android.graphics.Typeface
import android.util.Log
import com.courierdriver.application.MyApplication
import java.util.*

//* Created by Saira on 03/07/2019.
class FontStyle {
    private var fontMap = HashMap<String, String>()

    fun addFont(alias: String, fontName: String) {
        fontMap[alias] = fontName
    }

    fun getFont(alias: String): Typeface? {
        val fontFilename = fontMap[alias]
        return if (fontFilename == null) {
            Log.e("", "Font not available with name $alias")
            null
        } else {
            Typeface.createFromAsset(MyApplication.instance.assets, "fonts/$fontFilename")
        }
    }

    companion object {
        private var customFontFamily: FontStyle? = null
        val instance: FontStyle
            get() {
                if (customFontFamily == null)
                    customFontFamily = FontStyle()
                return customFontFamily!!
            }
    }
}


