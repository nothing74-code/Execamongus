#include <jni.h>
#include <android/log.h>
#include <string>

#define LOG_TAG "NativeExecutor"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jboolean JNICALL
Java_com_myexecutor_app_FloatingService_nativeInitHooks(JNIEnv* env, jobject thiz) {
    LOGI("Native hooks initialized.");
    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_myexecutor_app_FloatingService_nativeInjectLua(JNIEnv* env, jobject thiz, jstring luaScript) {
    const char* script = env->GetStringUTFChars(luaScript, nullptr);
    LOGI("Executing injected script: %s", script);
    env->ReleaseStringUTFChars(luaScript, script);
}

