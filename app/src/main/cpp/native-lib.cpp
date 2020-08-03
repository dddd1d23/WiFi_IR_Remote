#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_mikerush_dhy0077_wifi_1ir_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
