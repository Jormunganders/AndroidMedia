object Versions {
    const val autoValue = "1.6.5"
    const val rxlifecycle = "4.0.2"
    const val glide = "4.11.0"
    const val room = "2.3.0"
    const val paging = "3.0.1"
    const val arrow = "1.0.0"
}

object Libs {
    const val autoValue = "com.google.auto.value:auto-value-annotations:${Versions.autoValue}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"

    val retrofitLibs = arrayListOf(
        "com.squareup.retrofit2:retrofit:2.9.0",
        "com.squareup.retrofit2:adapter-rxjava3:2.9.0",
        "com.squareup.retrofit2:converter-gson:2.6.2",
        "com.google.code.gson:gson:2.8.6",
        "com.squareup.okhttp3:logging-interceptor:4.9.0"
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

    /**
     * For SlowCut Module
     */
    val roomLibs = arrayListOf(
        "androidx.room:room-runtime:${Versions.room}",
        "androidx.room:room-rxjava3:${Versions.room}",
        "androidx.room:room-guava:${Versions.room}",
//        "androidx.room:room-paging:2.4.0-alpha05"
    )

    val pagingLibs = arrayListOf(
        "androidx.paging:paging-runtime:${Versions.paging}",
        "androidx.paging:paging-rxjava3:${Versions.paging}",
        "androidx.paging:paging-guava:${Versions.paging}",
    )

    val arrowLibs = arrayListOf(
        "io.arrow-kt:arrow-fx-coroutines:${Versions.arrow}"
    )

}

/**
 * 注解处理器
 */
object AnnotationProcessors {
    const val autoValue = "com.google.auto.value:auto-value:${Versions.autoValue}"
    const val room = "androidx.room:room-compiler:${Versions.room}"
}