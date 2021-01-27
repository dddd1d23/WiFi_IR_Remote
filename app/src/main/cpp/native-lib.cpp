#include <jni.h>
#include <string>
#include <sign_api.h>
#include <cstring>
#define PRODUCT_KEY "a1UXTORPgGu"
#define PRODUCT_SECERT "oZNNZnDa4791TUFj"
using namespace std;

char*   Jstring2CStr(JNIEnv*   env,   jstring   jstr)
{
    char*   rtn   =   NULL;
    jclass   clsstring   =   env->FindClass("java/lang/String");  //String
    jstring   strencode   =   env->NewStringUTF("GB2312"); //"gb2312"
    jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B"); //getBytes(Str);
    jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode); // String .getByte("GB2312");
    jsize   alen   =   env->GetArrayLength(barr);
    jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);
    if(alen   >   0)
    {
        rtn   =   (char*)malloc(alen+1);         //"\0"
        memcpy(rtn,ba,alen);
        rtn[alen]=0;
    }
    env->ReleaseByteArrayElements(barr,ba,0);  //释放内存空间
    return rtn;
}

extern "C" JNIEXPORT jstring JNICALL
Java_mikerush_dhy0077_wifi_1ir_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_mikerush_dhy0077_wifi_1ir_MainActivity_IOT_1Sign_1MQTT(
        JNIEnv* env,
        jobject obj,jobject iotx_dev_meta_info,jobject iotx_sign_mqtt) {
    jclass clazz_idev,clazz_isign;
    jfieldID fid;jstring tmp;
    clazz_idev=env->GetObjectClass(iotx_dev_meta_info);
    clazz_isign=env->GetObjectClass(iotx_sign_mqtt);
    iotx_dev_meta_info_t idev;
    iotx_sign_mqtt_t isign;
    //--get data from java
    fid=env->GetFieldID(clazz_idev,"product_key","Ljava/lang/String;");
    tmp=(jstring)env->GetObjectField(iotx_dev_meta_info,fid);
    strcpy((char*)idev.product_key,Jstring2CStr(env,tmp));
    fid=env->GetFieldID(clazz_idev,"product_secret","Ljava/lang/String;");
    tmp=(jstring)env->GetObjectField(iotx_dev_meta_info,fid);
    strcpy((char*)idev.product_secret,Jstring2CStr(env,tmp));
    fid=env->GetFieldID(clazz_idev,"device_name","Ljava/lang/String;");
    tmp=(jstring)env->GetObjectField(iotx_dev_meta_info,fid);
    strcpy((char*)idev.device_name,Jstring2CStr(env,tmp));
    fid=env->GetFieldID(clazz_idev,"device_secret","Ljava/lang/String;");
    tmp=(jstring)env->GetObjectField(iotx_dev_meta_info,fid);
    strcpy((char*)idev.device_secret,Jstring2CStr(env,tmp));
    //--IOT_Sign_MQTT
    IOT_Sign_MQTT(IOTX_CLOUD_REGION_SHANGHAI, &idev, &isign);
    //--return value
    fid=env->GetFieldID(clazz_isign,"hostname","Ljava/lang/String;");
    tmp=env->NewStringUTF(isign.hostname);
    env->SetObjectField(iotx_sign_mqtt,fid,tmp);
    fid=env->GetFieldID(clazz_isign,"port","I");
    env->SetIntField(iotx_sign_mqtt,fid,(int)isign.port);
    fid=env->GetFieldID(clazz_isign,"clientid","Ljava/lang/String;");
    tmp=env->NewStringUTF(isign.clientid);
    env->SetObjectField(iotx_sign_mqtt,fid,tmp);
    fid=env->GetFieldID(clazz_isign,"password","Ljava/lang/String;");
    tmp=env->NewStringUTF(isign.password);
    env->SetObjectField(iotx_sign_mqtt,fid,tmp);
    fid=env->GetFieldID(clazz_isign,"username","Ljava/lang/String;");
    tmp=env->NewStringUTF(isign.username);
    env->SetObjectField(iotx_sign_mqtt,fid,tmp);
}
extern "C"
JNIEXPORT void JNICALL
Java_mikerush_dhy0077_wifi_1ir_MyApp_IOT_1Sign_1MQTT(JNIEnv *env, jobject thiz,
                                                     jobject iotx_dev_meta_info,
                                                     jobject iotx_sign_mqtt) {
    // TODO: implement IOT_Sign_MQTT()
    jclass clazz_idev,clazz_isign;
    jfieldID fid;jstring tmp;
    clazz_idev=env->GetObjectClass(iotx_dev_meta_info);
    clazz_isign=env->GetObjectClass(iotx_sign_mqtt);
    iotx_dev_meta_info_t idev;
    iotx_sign_mqtt_t isign;
    //--get data from java
    strcpy((char*)idev.product_key,PRODUCT_KEY);
    strcpy((char*)idev.product_secret,PRODUCT_SECERT);
    fid=env->GetFieldID(clazz_idev,"device_name","Ljava/lang/String;");
    tmp=(jstring)env->GetObjectField(iotx_dev_meta_info,fid);
    strcpy((char*)idev.device_name,Jstring2CStr(env,tmp));
    fid=env->GetFieldID(clazz_idev,"device_secret","Ljava/lang/String;");
    tmp=(jstring)env->GetObjectField(iotx_dev_meta_info,fid);
    strcpy((char*)idev.device_secret,Jstring2CStr(env,tmp));
    //--IOT_Sign_MQTT
    IOT_Sign_MQTT(IOTX_CLOUD_REGION_SHANGHAI, &idev, &isign);
    //--return value
    fid=env->GetFieldID(clazz_isign,"hostname","Ljava/lang/String;");
    tmp=env->NewStringUTF(isign.hostname);
    env->SetObjectField(iotx_sign_mqtt,fid,tmp);
    fid=env->GetFieldID(clazz_isign,"port","I");
    env->SetIntField(iotx_sign_mqtt,fid,(int)isign.port);
    fid=env->GetFieldID(clazz_isign,"clientid","Ljava/lang/String;");
    tmp=env->NewStringUTF(isign.clientid);
    env->SetObjectField(iotx_sign_mqtt,fid,tmp);
    fid=env->GetFieldID(clazz_isign,"password","Ljava/lang/String;");
    tmp=env->NewStringUTF(isign.password);
    env->SetObjectField(iotx_sign_mqtt,fid,tmp);
    fid=env->GetFieldID(clazz_isign,"username","Ljava/lang/String;");
    tmp=env->NewStringUTF(isign.username);
    env->SetObjectField(iotx_sign_mqtt,fid,tmp);
}