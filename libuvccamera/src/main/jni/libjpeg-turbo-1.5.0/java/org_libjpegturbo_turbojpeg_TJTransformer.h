/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_libjpegturbo_turbojpeg_TJTransformer */

#ifndef _Included_org_libjpegturbo_turbojpeg_TJTransformer
#define _Included_org_libjpegturbo_turbojpeg_TJTransformer
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_libjpegturbo_turbojpeg_TJTransformer
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_libjpegturbo_turbojpeg_TJTransformer_init
        (JNIEnv *, jobject);

/*
 * Class:     org_libjpegturbo_turbojpeg_TJTransformer
 * Method:    transform
 * Signature: ([BI[[B[Lorg/libjpegturbo/turbojpeg/TJTransform;I)[I
 */
JNIEXPORT jintArray JNICALL Java_org_libjpegturbo_turbojpeg_TJTransformer_transform
        (JNIEnv *, jobject, jbyteArray, jint, jobjectArray, jobjectArray, jint);

#ifdef __cplusplus
}
#endif
#endif