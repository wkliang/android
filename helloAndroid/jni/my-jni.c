/*
 * my-jni.c 
 *
 * ref: http://changyy.pixnet.net/blog/post/29437517 
 *
 * could ref: http://code.google.com/p/hdict/
 * for more jni example
 */
#include <string.h>
#include <jni.h>
#include <android/log.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, __FILE__, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, __FILE__, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, __FILE__, __VA_ARGS__))

jstring Java_com_github_wkliang_android_hello_helloAndroid_stringFromJNI(
	JNIEnv*env, jobject thiz)
{
	LOGI("%d: stringFromJNI()", __LINE__);

	return (*env)->NewStringUTF(env,"Hello from My JNI!\n");
}

// ref:
// http://huenlil.pixnet.net/blog/post/23802146
// http://blog.csdn.net/zhenyongyuan123/article/details/5862054

#if 0
jint JNI_OnUnload(JavaVM* vm, void* reserved)
{
}
#endif

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv* env = NULL;
	jint result = -1;

	LOGI("%d: JNI_OnLoad()", __LINE__);

	if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("ERROR: GetEnv failed\n");
        	goto bail;
	}
	/* success -- return valid version number */
	result = JNI_VERSION_1_4;
bail:
	return result;
}


