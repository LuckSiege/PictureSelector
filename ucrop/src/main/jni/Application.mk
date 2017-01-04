APP_STL := gnustl_static
APP_ABI := armeabi armeabi-v7a x86 x86_64 arm64-v8a
APP_CPPFLAGS += -frtti
APP_CPPFLAGS += -fexceptions
APP_CPPFLAGS += -DANDROID
APP_PLATFORM := android-14