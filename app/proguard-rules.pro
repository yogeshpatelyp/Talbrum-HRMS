# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\saran\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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
#-dontwarn javax.servlet.**
#-dontwarn org.joda.time.**
#-dontwarn org.w3c.dom.**

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-dontwarn com.squareup.okhttp.*
-keepattributes Signature
-keepattributes Exceptions
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class com.firebase.** { *; }
-ignorewarnings

-keep class * {
    public private *;
}
-keepclassmembers class * {
  public <init>(android.content.Context);
}
-dontwarn okio.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes *Annotation*

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**