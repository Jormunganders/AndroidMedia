package me.juhezi.mediademo

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type

class StringConvertFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        return StringConverter()
    }

    inner class StringConverter : Converter<ResponseBody, String> {

        override fun convert(value: ResponseBody) = value.string()

    }

}