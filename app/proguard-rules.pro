# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\dev\android-sdk_r22.6.2-windows\android-sdk-windows/tools/proguard/proguard-android.txt
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
#
-dontwarn kotlin.dom.**
-keep class android.support.v7.** { *; }
-keep class android.support.design.** { *; }
-keep interface android.support.v7.** { *; }
-keep class com.activeandroid.** { *; }
-keeppackagenames com.mikepenz.**
-keeppackagenames com.rey.material.**
-keepattributes *Annotation* , EnclosingMethod
-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepnames class * implements android.os.Parcelable {
	public static final ** CREATOR;
}