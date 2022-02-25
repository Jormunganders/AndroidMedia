object Versions {
    const val autoValue = "1.6.5"
    const val rxlifecycle = "4.0.2"
    const val glide = "4.11.0"
}

object Libs {
    const val autoValue = "com.google.auto.value:auto-value-annotations:${Versions.autoValue}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    val retrofitLibs = arrayListOf(
        "com.squareup.retrofit2:retrofit:2.9.0",
        "com.squareup.retrofit2:adapter-rxjava3:2.9.0",
        "com.squareup.retrofit2:converter-gson:2.6.2",
        "com.google.code.gson:gson:2.8.6"
    )

    val rxLibs = arrayListOf(
        "io.reactivex.rxjava3:rxjava:3.0.9",
        "io.reactivex.rxjava3:rxandroid:3.0.0",
        "io.reactivex.rxjava3:rxkotlin:3.0.1"
    )

    val lifeCycleLibs = arrayListOf(
        "com.trello.rxlifecycle4:rxlifecycle:${Versions.rxlifecycle}",
        "com.trello.rxlifecycle4:rxlifecycle-android:${Versions.rxlifecycle}",
        "com.trello.rxlifecycle4:rxlifecycle-components:${Versions.rxlifecycle}",
        "com.trello.rxlifecycle4:rxlifecycle-kotlin:${Versions.rxlifecycle}"
    )

}

/**
 * 注解处理器
 */
object AnnotationProcessors {
    const val autoValue = "com.google.auto.value:auto-value:${Versions.autoValue}"
}