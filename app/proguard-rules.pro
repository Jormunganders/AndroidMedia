# 指定压缩等级
-optimizationpasses 5
# 不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers
# 混淆时采用的算法
-optimizations !code/simplification/arithemetic, !field/*, !class/merging/*
# 把混淆类中的方法名也混淆了
-useuniqueclassmembernames
# 优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
# 将文件来源重命名为 "SourceFile" 字符串
-renamesourcefileattribute
# 保留行号
-keepattributes SourceFile,LinkNumberTable

# 打印日志相关

-dontpreverify
# 混淆时是否记录日志
-verbose
# apk 包内所有 class 的内部结构
-dump class_files.txt
# 未混淆的类和成员
-printseeds seeds.txt
# 列出从 APK 中删除的代码
-printusage unused.txt
# 混淆前后的映射
-printmapping mapping.txt

# 四大组件不混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# 注解不混淆
-keepattributes *Annotation*

# 不混淆枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# native 方法不混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# JS 调用的 Java 方法不混淆
-keepattributes *JavascriptInterface*

# Parcelable 的 Creator 静态成员变量不混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Serialzable 反序列化的一些属性和方法
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-dontskipnonpubliclibraryclassmembers
-printconfiguration
-keep,allowobfuscation @interface androidx.annotation.Keep
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
