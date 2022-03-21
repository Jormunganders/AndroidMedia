object Versions {
    const val autoValue = "1.6.5"
    const val rxlifecycle = "4.0.2"
    const val glide = "4.11.0"
    const val room = "2.4.2"
    const val hilt = "2.28-alpha"
}

object Libs {
    const val autoValue = "com.google.auto.value:auto-value-annotations:${Versions.autoValue}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val gson = "com.google.code.gson:gson:2.8.6"
    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"

    val retrofitLibs = arrayListOf(
        "com.squareup.retrofit2:retrofit:2.9.0",
        "com.squareup.retrofit2:adapter-rxjava3:2.9.0",
        "com.squareup.retrofit2:converter-gson:2.6.2",
        gson
    )

    val rxLibs = arrayListOf(
        "io.reactivex.rxjava3:rxjava:3.0.9",
        "io.reactivex.rxjava3:rxandroid:3.0.0",
        "io.reactivex.rxjava3:rxkotlin:3.0.1"
    )

    val roomLibs = arrayListOf(
        "androidx.room:room-runtime:${Versions.room}",
        "androidx.room:room-rxjava3:${Versions.room}",

    )

    val lifeCycleLibs = arrayListOf(
        "com.trello.rxlifecycle4:rxlifecycle:${Versions.rxlifecycle}",
        "com.trello.rxlifecycle4:rxlifecycle-android:${Versions.rxlifecycle}",
        "com.trello.rxlifecycle4:rxlifecycle-components:${Versions.rxlifecycle}",
        "com.trello.rxlifecycle4:rxlifecycle-kotlin:${Versions.rxlifecycle}"
    )

    // 基本组件库
    val baseLibs = arrayListOf(
        "androidx.core:core-ktx:1.7.0",
        "androidx.appcompat:appcompat:1.3.0",
        "com.google.android.material:material:1.4.0",
        "androidx.constraintlayout:constraintlayout:2.0.4",
        "androidx.lifecycle:lifecycle-livedata-ktx:2.3.1",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1"
    )

    val ktxLibs = arrayListOf(
        "androidx.collection:collection-ktx:1.2.0",
        "androidx.fragment:fragment-ktx:1.4.1",
        "androidx.lifecycle:lifecycle-runtime-ktx:2.5.0-alpha03",
        "androidx.lifecycle:lifecycle-livedata-ktx:2.5.0-alpha03",
        "androidx.lifecycle:lifecycle-reactivestreams-ktx:2.5.0-alpha03",
        "androidx.room:room-ktx:2.4.2",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0-alpha03",
    )

}

/**
 * 注解处理器
 */
object AnnotationProcessors {
    const val autoValue = "com.google.auto.value:auto-value:${Versions.autoValue}"
    const val room = "androidx.room:room-compiler:${Versions.room}"
    const val hilt = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
}