# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class edu.cit.noel.expensetracker.data.model.** { *; }
-keepclassmembers class edu.cit.noel.expensetracker.data.model.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
