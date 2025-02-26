package com.example.testtaskapplication.Utils

import android.util.SparseArray
import android.view.View
import android.widget.TextView
import java.lang.reflect.Constructor
import java.math.BigDecimal

class FNObjectUtil {
    companion object {

        const val EMPTY_STRING = ""
        fun isEmpty(o: Any?): Boolean {
            if (null == o) {
                return true
            }
            if (o is String) {
                return isEmptyStr(o as String?)
            }
            if (o is CharSequence) {
                return o.length == 0
            }
            if (o is SparseArray<*>) {
                return o.size() == 0
            }
            if (o is Map<*, *>) {
                return o.size == 0
            }
            if (o is Collection<*>) {
                return o.isEmpty()
            }
            return if (o.javaClass.isArray) {
                (o as Array<*>).isEmpty()
            } else false
        }

        fun size(o: Any?): Int {
            if (null == o) {
                return 0
            }
            if (o is String) {
                return o.length
            }
            if (o is CharSequence) {
                return o.length
            }
            if (o is SparseArray<*>) {
                return o.size()
            }
            if (o is Map<*, *>) {
                return o.size
            }
            if (o is Collection<*>) {
                return o.size
            }
            return if (o.javaClass.isArray) {
                (o as Array<*>).size
            } else 0
        }

        fun isEmptyStr(_v: String?): Boolean {
            return _v == null || _v.trim().isEmpty() || _v.equals("null", true)
        }

        fun isNonEmptyStr(_v: String?): Boolean {
            return !isEmptyStr(_v)
        }

        fun isNumber(v: Any): Boolean {
            if (isEmpty(v)) {
                return false
            }
            if (v is Number) {
                return true
            }
            val cs = v as CharSequence
            val length = cs.length
            for (i in 0 until length) {
                if (!Character.isDigit(cs[i])) {
                    return false
                }
            }
            return true
        }

        fun stringValue(value: Any?): String {
            if (value == null) {
                return " "
            }
            if (value is String) {
                return value.toString()
            }
            return if (value is Array<*>) {
                stringValueForArray(value as Array<Any>?)
            } else value.toString()
        }

        private fun stringValueForArray(array: Array<Any>?): String {
            if (array == null) {
                return " "
            }
            val sb = StringBuilder(array.size * 16)
            sb.append("( ")
            var isFirst = true
            for (o in array) {
                var s: String = stringValue(o)
                if (s == " ") {
                    s = "[null]"
                }
                if (isFirst) {
                    isFirst = false
                } else {
                    sb.append(", ")
                }
                sb.append(s)
            }
            sb.append(" )")
            return sb.toString()
        }

        fun isEmptyView(view: View?): Boolean {
            return isEmptyStr(
                getTextFromView(
                    view
                )
            )
        }

        fun getTextFromView(view: View?): String {
            return if (view is TextView) view.text.toString().trim { it <= ' ' } else ""
        }

        fun intValue(v: Any?): Int {
            if (v == null) {
                return 0
            }
            if (v is Number) {
                return v.toInt()
            }
            if (v is String) {
                val s = v
                return if (s.trim { it <= ' ' }.isEmpty()) {
                    0
                } else try {
                    if (s.indexOf('.') >= 0) {
                        BigDecimal(s).toInt()
                    } else s.toInt()
                } catch (e: NumberFormatException) {
                    0
                }
            }
            return intValue(v.toString())
        }

        fun doubleValue(v: Any?): Double {
            if (v == null) {
                return 0.0
            }
            if (v is Number) {
                return v.toDouble()
            }
            if (v is String) {
                val s = v
                return if (s.trim { it <= ' ' }.length == 0) {
                    0.0
                } else try {
                    if (s.indexOf('.') >= 0) {
                        BigDecimal(s).toDouble()
                    } else s.toDouble()
                } catch (e: java.lang.NumberFormatException) {
                    0.0
                }
            }
            return doubleValue(v.toString())
        }

        fun toPercentage(value: Float, total: Float): Int {
            return toPercentage(value, total, 0).toInt()
        }
        fun toPercentage(value: Float, total: Float, scale: Int): Float {
            val result = if (total != 0.0f) value / total * 100.0f else 0.0f
            return if (result > 0.0f) BigDecimal.valueOf(result.toDouble()).setScale(scale, 4)
                .toFloat() else 0.0f
        }
        fun <T> objectForClass(className: String?): T? {
            var customObj: Any? = null
            try {
                val classObj = Class.forName(className)
                customObj = objectForClass(classObj)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            return customObj as T?
        }

        fun <T> objectForClass(classObj: Class<T>): T? {
            try {
                if (classObj.modifiers == 0) {
                    try {
                        val ctors: Array<Constructor<*>> = classObj.declaredConstructors
                        var ctor: Constructor<*>
                        for (ctor1 in ctors) {
                            ctor = ctor1
                            if (ctor.genericParameterTypes.isEmpty()) {
                                break
                            }
                        }
                        ctor = classObj.getDeclaredConstructor()
                        try {
                            ctor.isAccessible = true
                            return ctor.newInstance()
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    return classObj.newInstance()
                }
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            return null
        }
    }
}