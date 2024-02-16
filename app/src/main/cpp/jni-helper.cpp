#include <jni.h>
#include "secret/string.h"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_fingerprintjs_android_fpjs_1pro_1demo_constants_Protected_stringFromJNI(JNIEnv *env,
                                                                                 jobject thiz) {
    return env->NewStringUTF(string);
}
