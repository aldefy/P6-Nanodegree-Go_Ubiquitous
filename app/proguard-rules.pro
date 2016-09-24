# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/aditlal/Documents/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-assumenosideeffects class techgravy.sunshine.utils.logger.Logger {
    public static *** d(...);
    public static *** v(...);
    public static *** t(...);
}
-keep public class techgravy.sunshine.utils.logger.Logger
-keep public class techgravy.sunshine.utils.logger.LogLevel

-dontpreverify
-optimizationpasses 5
-repackageclasses ''
-allowaccessmodification
-optimizations code/removal/simple,code/removal/advanced

## AppCompact
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

## CardView
-keep class android.support.v7.widget.RoundRectDrawable { *; }

## Design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

## Retrofit 1.X and Picasso

-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**

-keepclasseswithmembers class * {
 @retrofit.http.* <methods>;
}

# If in your rest service interface you use methods with Callback argument.
-keepattributes Exceptions

# If your rest service methods throw custom exceptions, because you've defined an ErrorHandler.
-keepattributes Signature

# Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.
# Here include the POJO's that have you have created for mapping JSON response to POJO for example.
-keep class techgravy.sunshine.model.**

## RXjava RxAndroid Rxbinding
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

## Butterknife 8+
# Retain generated class which implement Unbinder.
-keep public class * implements butterknife.Unbinder { public <init>(...); }

# Prevent obfuscation of types which use ButterKnife annotations since the simple name
# is used to reflectively look up the generated ViewBinding.
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

## GSON
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

## retroLambda
-dontwarn java.lang.invoke.*

## Joda
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString

## Acra
# Keep all the ACRA classes
-keep class org.acra.** { *; }

## RushORM
-keep public class * implements co.uk.rushorm.core.Rush { *; }

## Iconics Core
-keep class .R
-keep class **.R$* {
    <fields>;
}


