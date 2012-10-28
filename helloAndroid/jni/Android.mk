
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog
LOCAL_MODULE := my-jni
LOCAL_SRC_FILES := my-jni.c
include $(BUILD_SHARED_LIBRARY)

