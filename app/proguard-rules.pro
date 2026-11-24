# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Gradle build file.

# Keep Room entities and DAOs
-keep class com.limithue.familyguard.data.** { *; }
