LOCAL_PATH:= $(call my-dir)

#$(info $(foreach f,$(wildcard $(LOCAL_PATH)/nfc_conformance/DTA_Config/AT4/*),$(f):data/misc/nfc_conformance/DTA_Config/AT4/$(notdir $(f))))

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
#LOCAL_STATIC_JAVA_LIBRARIES := guava android-support-v4

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PACKAGE_NAME := CSDTEST
LOCAL_CERTIFICATE := platform
LOCAL_JAVA_LIBRARIES := bouncycastle \
                        conscrypt \
                        telephony-common \
                        ims-common \
                        mediatek-framework 
LOCAL_JAVA_LIBRARIES += wifi-service
#LOCAL_EMMA_COVERAGE_FILTER := @$(LOCAL_PATH)/emma_filter.txt

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
