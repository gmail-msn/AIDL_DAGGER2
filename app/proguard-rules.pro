# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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


-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

-libraryjars  libs/android-support-v4.jar
-libraryjars  libs/bcprov-jdk14-133.jar
-libraryjars  libs/capturelib.jar
-libraryjars  libs/cdcacmdriver.jar
-libraryjars  libs/core-3.1.0.jar
-libraryjars  libs/sscl.jar

# baidu push
-libraryjars libs/pushservice-4.1.0.jar
-dontwarn com.baidu.**
-keep class com.baidu.** { *; }

# aync http
-libraryjars libs/android-async-http-1.4.6.jar
-dontwarn com.loopj.android.**
-keep class com.loopj.android.** { *; }

# zbar
-libraryjars libs/zbar.jar
-dontwarn net.sourceforge.zbar.**
-keep class net.sourceforge.zbar.** { *; }

# zxing
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *; }

# universal image loader
-libraryjars  libs/universal-image-loader-1.9.3-with-sources.jar
-dontwarn com.nostra13.universalimageloader.**
-keep class com.nostra13.universalimageloader.** { *; }

# umeng
-libraryjars  libs/umeng-analytics-v5.4.1.jar
-dontwarn com.umeng.analytics.**
-keep class com.umeng.analytics.** { *; }

# usips
-dontwarn com.chinaums.mis.**
-keep class com.chinaums.mis.** { *; }

-keepclassmembers class * {
   public void *(android.view.View);
}

-assumenosideeffects class android.util.Log {
    public static *** i(...);
    public static *** d(...);
    public static *** v(...);
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.LinearLayout
-keep public class * extends android.app.Service
-keep public class * extends android.app.Dialog
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * implements android.content.ServiceConnection

-keep public class org.**{
	public *;
}

-keep class cn.koolcloud.jni.** {
	*;
}

-keepclasseswithmembernames class * {
    native <methods>;
}

# for jni methods
-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class *.R

-keep public class **.R$*{
  	public static <fields>;
}

-keep com.koolyun.smartpos.sdk.service.ApmpService;
-keep com.koolyun.smartpos.sdk.service.BATPayService;
-keep com.koolyun.smartpos.sdk.service.POSPTransactionService;

-keep public final class **.*$*{
  public static final <fields>;
}

-keep public  class * extends android.**{
    !private <init>(...);
    @Override !static <methods>;
}

-keep class * extends android.**{
    !private <init>(...);
    @Override !static <methods>;
}

-keep public  interface * implements android.**{
    !private <methods>;
}

-keep interface * implements android.**{
    !private <methods>;
}

# Don't keep the local variables attributes (LocalVariableTable and LocalVariableTypeTable are dropped).
# -keepattributes Exceptions,Signature,Deprecated,SourceFile,SourceDir,LineNumberTable,Synthetic,EnclosingMethod,RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,AnnotationDefault,InnerClasses,*Annotation*
# -keepattributes InnerClasses

-ignorewarnings 
