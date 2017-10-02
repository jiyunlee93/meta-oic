# TODO:
# echo 'PREFERRED_VERSION_iotivity="0.0.0"' >> build/conf/local.conf

#PR ?= "r2"
#SRCREV  = "${AUTOREV}"
#PV = "0+git${SRCPV}"
#S  = "${WORKDIR}/git"

SUMMARY = "IoTivity framework and SDK sponsored by the Open Connectivity Foundation."
DESCRIPTION = "IoTivity is an open source software framework enabling seamless device-to-device connectivity to address the emerging needs of the Internet of Things."
HOMEPAGE = "https://www.iotivity.org/"
DEPENDS = "boost virtual/gettext chrpath-replacement-native expat openssl util-linux curl glib-2.0 glib-2.0-native"
DEPENDS += "sqlite3"

EXTRANATIVEPATH += "chrpath-native"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=22bf216f3077c279aed7b36b1fa9e6d1"

branch_iotivity ?= "master"
baseurl_iotivity ?= "git://github.com/iotivity/iotivity.git"
SRCREV_iotivity = "${branch_iotivity}"
SRCREV = "${SRCREV_iotivity}"
url_iotivity ?= "${baseurl_iotivity};destsuffix=${S};branch=${branch_iotivity};protocol=http"
SRC_URI = "${url_iotivity}"

url_tinycbor = "git://github.com/01org/tinycbor.git"
SRCREV_tinycbor = "31c7f81d45d115d2007b1c881cbbd3a19618465c"
SRC_URI += "${url_tinycbor};name=tinycbor;destsuffix=${S}/extlibs/tinycbor/tinycbor;protocol=http"

url_gtest = "https://github.com/google/googletest/archive/release-1.7.0.zip"
SRC_URI[gtest.md5sum] = "ef5e700c8a0f3ee123e2e0209b8b4961"
SRC_URI[gtest.sha256sum] = "b58cb7547a28b2c718d1e38aee18a3659c9e3ff52440297e965f5edffe34b6d0"
SRC_URI += "${url_gtest};name=gtest;subdir=${BP}/extlibs/gtest"

url_hippomocks = "git://github.com/dascandy/hippomocks.git"
SRCREV_hippomocks = "dca4725496abb0e41f8b582dec21d124f830a8e5"
SRC_URI += "${url_hippomocks};name=hippomocks;destsuffix=${S}/extlibs/hippomocks/hippomocks;protocol=http"
SRC_URI += "file://hippomocks_mips_patch"

SRCREV_mbedtls = "85c2a928ed352845793db000e78e2b42c8dcf055"
url_mbedtls="git://github.com/ARMmbed/mbedtls.git"
SRC_URI += "${url_mbedtls};name=mbedtls;destsuffix=${S}/extlibs/mbedtls/mbedtls;protocol=http"

#TODO
url_rapidjson = "git://github.com/miloyip/rapidjson.git"
SRCREV_rapidjson = "3d5848a7cd3367c5cb451c6493165b7745948308"
SRC_URI += "${url_rapidjson};name=rapidjson;;nobranch=1;destsuffix=${S}/extlibs/rapidjson/rapidjson;protocol=http"

branch_libcoap = "IoTivity-1.2.1d"
SRCREV_libcoap = "${branch_libcoap}"
url_libcoap = "git://github.com/dthaler/libcoap.git"
SRC_URI += "${url_libcoap};name=libcoap;destsuffix=${S}/extlibs/libcoap/libcoap;protocol=http;nobranch=1"

#TODO: check
url_rapidjson = "https://github.com/miloyip/rapidjson/archive/v1.0.2.zip"
SRC_URI += "${url_rapidjson};name=rapidjson;subdir=${BP}/extlibs/rapidjson"
SRC_URI[rapidjson.md5sum] = "446a0673d58766e507d641412988dcaa"
SRC_URI[rapidjson.sha256sum] = "69e876bd07670189214f44475add2e0afb8374e5798270208488c973a95f501d"

inherit pkgconfig scons


python () {
    IOTIVITY_TARGET_ARCH = d.getVar("TARGET_ARCH", True)
    d.setVar("IOTIVITY_TARGET_ARCH", IOTIVITY_TARGET_ARCH)
    EXTRA_OESCONS = d.getVar("EXTRA_OESCONS", True)
    EXTRA_OESCONS += " TARGET_OS=yocto TARGET_ARCH=" + IOTIVITY_TARGET_ARCH + " RELEASE=1"
    EXTRA_OESCONS += " VERBOSE=1"
 #  EXTRA_OESCONS += " --install-sandbox=${D}"
    # Aligned to default configuration, but features can be changed here (at your own risk):
    # EXTRA_OESCONS += " -j1"
    # EXTRA_OESCONS += " ROUTING=GW"
    # EXTRA_OESCONS += " SECURED=0"
    # EXTRA_OESCONS += " TCP=1"
    d.setVar("EXTRA_OESCONS", EXTRA_OESCONS)
}


IOTIVITY_BIN_DIR = "/opt/${PN}"
IOTIVITY_BIN_DIR_D = "${D}${IOTIVITY_BIN_DIR}"

do_compile_prepend() {
    export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
    export PKG_CONFIG="PKG_CONFIG_SYSROOT_DIR=\"${PKG_CONFIG_SYSROOT_DIR}\" pkg-config"
    export LD_FLAGS="${LD_FLAGS}"
}

make_dir() {
    install -d $1
}

copy_file() {
    install -c -m 444 $1 $2
}

copy_exec() {
    install -c -m 555 $1 $2
}

copy_file_recursive() {
    cd $1 && find . -type d -exec install -d $2/"{}" \;
    cd $1 && find . -type f -exec install -c -m 444 "{}" $2/"{}" \;
}

copy_exec_recursive() {
    cd $1 && find . -executable -exec install -c -m 555 "{}" $2/"{}" \;
}

do_install_() {
#TODO: fix https://git.yoctoproject.org/cgit.cgi/poky/plain/meta/classes/scons.bbclass
    ${STAGING_BINDIR_NATIVE}/scons --prefix=${prefix} --install-sandbox=${D} install ${EXTRA_OESCONS}

    cd ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/
    #Resource C++ Apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    find resource/examples/ \
      -iname "*.dat"\
      -exec install -c -m 444 "{}"  ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp/ \;

    copy_exec examples/OICMiddle/OICMiddle ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/devicediscoveryclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/devicediscoveryserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/fridgeclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/fridgeserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/garageclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/garageserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/groupclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/groupserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/lightserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/presenceclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/presenceserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/roomclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/roomserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/simpleclient ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/simpleclientHQ ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/simpleclientserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/simpleserver ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/simpleserverHQ ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp
    copy_exec resource/examples/threadingsample ${IOTIVITY_BIN_DIR_D}/examples/resource/cpp

    #Resource CSDK Apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec resource/csdk/stack/samples/linux/SimpleClientServer/occlientcoll ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    if ${@bb.utils.contains('EXTRA_OESCONS', 'ROUTING=GW', 'true', 'false', d)}; then
        copy_exec resource/csdk/stack/samples/linux/SimpleClientServer/ocrouting ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    fi
    copy_exec resource/csdk/stack/samples/linux/SimpleClientServer/ocserver ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec resource/csdk/stack/samples/linux/SimpleClientServer/ocserverbasicops ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec resource/csdk/stack/samples/linux/SimpleClientServer/occlientslow ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec resource/csdk/stack/samples/linux/SimpleClientServer/ocserverslow ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec resource/csdk/stack/samples/linux/SimpleClientServer/occlientbasicops ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec resource/csdk/stack/samples/linux/SimpleClientServer/ocservercoll ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer
    copy_exec resource/csdk/stack/samples/linux/SimpleClientServer/occlient ${IOTIVITY_BIN_DIR_D}/examples/resource/c/SimpleClientServer

    if ${@bb.utils.contains('EXTRA_OESCONS', 'SECURED=0', 'false', 'true', d)}; then
        make_dir ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure
        copy_exec resource/csdk/stack/samples/linux/secure/ocamsservice ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure
        copy_exec resource/csdk/stack/samples/linux/secure/ocserverbasicops ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure
        copy_exec resource/csdk/stack/samples/linux/secure/occlientbasicops ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure

        find resource/csdk/stack/samples/linux \
          -iname "*.dat"\
          -exec install -c -m 444 {} ${IOTIVITY_BIN_DIR_D}/examples/resource/c/secure/ \;
    fi

    #Resource Tests
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/resource
    copy_exec resource/c_common/ocrandom/test/randomtests ${IOTIVITY_BIN_DIR_D}/tests/resource/ocrandom_tests
    copy_exec resource/unittests/unittests ${IOTIVITY_BIN_DIR_D}/tests/resource/oc_unittests
#   copy_file resource/unittests/oic_svr_db_client.dat ${IOTIVITY_BIN_DIR_D}/tests/resource
    copy_exec resource/csdk/stack/test/stacktests ${IOTIVITY_BIN_DIR_D}/tests/resource/octbstack_tests
    copy_exec resource/csdk/connectivity/test/catests ${IOTIVITY_BIN_DIR_D}/tests/resource/ca_tests
    copy_exec resource/oc_logger/examples/examples_cpp ${IOTIVITY_BIN_DIR_D}/tests/resource/logger_test_cpp
    copy_exec resource/oc_logger/examples/examples_c ${IOTIVITY_BIN_DIR_D}/tests/resource/logger_test_c
    if ${@bb.utils.contains('EXTRA_OESCONS', 'SECURED=0', 'false', 'true', d)}; then
        copy_exec resource/csdk/security/unittest/unittest ${IOTIVITY_BIN_DIR_D}/tests/resource/security_tests
        copy_exec resource/csdk/security/tool/json2cbor ${IOTIVITY_BIN_DIR_D}/
    fi

    #Samples
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/plugins/zigbee/
    copy_exec plugins/samples/linux/iotivityandzigbeeserver ${IOTIVITY_BIN_DIR_D}/examples/plugins/zigbee
    copy_exec plugins/samples/linux/iotivityandzigbeeclient ${IOTIVITY_BIN_DIR_D}/examples/plugins/zigbee

    #Tests
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/plugins/zigbee/
    copy_exec plugins/unittests/piunittests ${IOTIVITY_BIN_DIR_D}/tests/plugins/zigbee

    #Resource container sample apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec service/resource-container/HeightSensorApp ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec service/resource-container/THSensorApp ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec service/resource-container/THSensorApp1 ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec service/resource-container/WeightSensorApp ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec service/resource-container/ContainerSample ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_exec service/resource-container/ContainerSampleClient ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/
    copy_file service/resource-container/examples/ResourceContainerConfig.xml ${IOTIVITY_BIN_DIR_D}/examples/service/resource-container/

    #Resource container tests
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_exec service/resource-container/unittests/container_test ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_file service/resource-container/unittests/libTestBundle.so ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_file service/resource-container/unittests/ResourceContainerInvalidConfig.xml ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_file service/resource-container/unittests/ResourceContainerTestConfig.xml ${IOTIVITY_BIN_DIR_D}/tests/service/resource-container
    copy_file service/resource-container/unittests/libTestBundle.so ${D}${libdir}

    #Resource encapsulation sample apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation
    copy_exec service/resource-encapsulation/examples/linux/sampleResourceClient ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation
    copy_exec service/resource-encapsulation/examples/linux/sampleResourceServer ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation
    copy_exec service/resource-encapsulation/examples/linux/nestedAttributesClient ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation
    copy_exec service/resource-encapsulation/examples/linux/nestedAttributesServer ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation

    #Resource encapsulation test
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation/resource-broker
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation/resource-cache
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation/common
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation/server-builder
    copy_exec service/resource-encapsulation/unittests/rcs_client_test ${IOTIVITY_BIN_DIR_D}/tests/service/resource-encapsulation
    copy_exec service/resource-encapsulation/src/resourceBroker/unittest/broker_test ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation/resource-broker
    copy_exec service/resource-encapsulation/src/resourceCache/unittests/cache_test ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation/resource-cache
#TODO
    copy_exec service/resource-encapsulation/src/common/primitiveResource/unittests/rcs_common_test ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation/common
    copy_exec service/resource-encapsulation/src/serverBuilder/rcs_server_test ${IOTIVITY_BIN_DIR_D}/examples/service/resource-encapsulation/server-builder

    #Resource directory samples
    if ${@bb.utils.contains('EXTRA_OESCONS', 'RD_MODE=CLIENT,SERVER', 'true', 'false', d)}; then
        make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/resource-directory
        copy_exec_recursive service/resource-directory/samples ${IOTIVITY_BIN_DIR_D}/examples/service/resource-directory
    fi

    #Easy setup app
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/easy-setup
    copy_exec service/easy-setup/sampleapp/enrollee/linux/enrollee ${IOTIVITY_BIN_DIR_D}/examples/service/easy-setup
    copy_exec service/easy-setup/sampleapp/mediator/linux/richsdk_sample/mediator ${IOTIVITY_BIN_DIR_D}/examples/service/easy-setup
    copy_exec service/easy-setup/sampleapp/mediator/linux/richsdk_sample/submediator ${IOTIVITY_BIN_DIR_D}/examples/service/easy-setup

    #Easy setup tests
    if ${@bb.utils.contains('EXTRA_OESCONS', 'SECURED=0', 'true', 'false', d)}; then
        make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/easy-setup
        copy_exec service/easy-setup/mediator/richsdk/unittests/easysetup_mediator_test ${IOTIVITY_BIN_DIR_D}/tests/service/easy-setup
    fi

    #Notification app
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/notification
    copy_exec service/notification/examples/linux/notificationconsumer ${IOTIVITY_BIN_DIR_D}/examples/service/notification
    copy_exec service/notification/examples/linux/notificationprovider ${IOTIVITY_BIN_DIR_D}/examples/service/notification

    #Notification tests
    if ${@bb.utils.contains('EXTRA_OESCONS', 'SECURED=1', 'false', 'true', d)}; then
        make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/notification
        copy_exec service/notification/unittest/notification_consumer_test ${IOTIVITY_BIN_DIR_D}/tests/service/notification
        copy_exec service/notification/unittest/notification_provider_test ${IOTIVITY_BIN_DIR_D}/tests/service/notification
    fi

    #Scene manager apps
    make_dir ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager
    copy_exec service/scene-manager/sampleapp/linux/fanserver ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager
    copy_exec service/scene-manager/sampleapp/linux/lightserver ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager
    copy_exec service/scene-manager/sampleapp/linux/sceneclient ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager
    copy_exec service/scene-manager/sampleapp/linux/sceneserver ${IOTIVITY_BIN_DIR_D}/examples/service/scene-manager

    #Scene manager tests
    make_dir ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager
    copy_exec service/scene-manager/unittests/scene_action_test ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager
    copy_exec service/scene-manager/unittests/scene_collection_test ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager
    copy_exec service/scene-manager/unittests/scene_list_test ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager
    copy_exec service/scene-manager/unittests/scene_test ${IOTIVITY_BIN_DIR_D}/tests/service/scene-manager

    # rm ${D}${libdir}/libmbedtls.a
    # rm ${D}${libdir}/liboctbstack_internal.a
    # rm ${D}${libdir}/libocpmapi.a
    # rm ${D}${libdir}/liboc_internal.a
    # rm ${D}${libdir}/libmbedcrypto.a
    # rm ${D}${libdir}/libmbedx509.a
    # rm ${D}${libdir}/libnotification_provider_wrapper.a
    # rm ${D}${libdir}/libocpmapi_internal.a
    # rm ${D}${libdir}/libminipluginmanager.a
    # rm ${D}${libdir}/liboc_logger_internal.a
    # rm ${D}${libdir}/liblogger.a
    # rm ${D}${libdir}/libcjson.a
    # rm ${D}${libdir}/libnotification_consumer_wrapper.a
    # rm ${D}${libdir}/libESEnrolleeSDK.a
    # rm ${D}${libdir}/libmpmcommon.a
    # rm ${D}${libdir}/libconnectivity_abstraction_internal.a
    # rm ${D}${libdir}/libnotification_consumer.a
    # rm ${D}${libdir}/libESMediatorRich.a
    # rm ${D}${libdir}/libcoap_http_proxy.a
    # rm ${D}${libdir}/libipca_static.a
    # rm ${D}${libdir}/libnotification_provider.a

    #Adapt unaligned pkconfig (transitionnal)
#    sed -e 's|^prefix=.*|prefix=/usr|g' -i ${S}/iotivity.pc
 #   make_dir ${D}${libdir}/pkgconfig/
  #  copy_file ${S}/iotivity.pc ${D}${libdir}/pkgconfig/
    #Installed headers
#    make_dir ${D}${includedir}
#    copy_file_recursive \
 #      ${S}/out/yocto/${IOTIVITY_TARGET_ARCH}/release/include \
  #     ${D}${includedir}/iotivity

    # TODO: Support legacy path (transitional, use pkg-config)
    ln -s iotivity/{resource,service,c_common}  ${D}${includedir}/

    find "${D}" -type f -perm /u+x -exec chrpath -d "{}" \;
    find "${D}" -type f -iname "lib*.so" -exec chrpath -d "{}" \;
#TODO
    rm -rf ${D}/usr/src/debug/iotivity
}

#IOTIVITY packages:
#Resource: iotivity-resource, iotivity-resource-dev, iotivity-resource-thin-staticdev, iotivity-resource-dbg
#Resource Samples: iotivity-resource-samples, iotivity-resource-samples-dbg
#Service: iotivity-service, iotivity-service-dev, iotivity-service-staticdev, iotivity-service-dbg
#Service Samples: iotivity-service-samples, iotivity-service-samples-dbg
#Tests: iotivity-tests, iotivity-tests-dbg
#Misc: iotivity-tools

FILES_${PN}-resource-dev = "\
        ${includedir}/iotivity/resource \
        ${includedir}/iotivity/extlibs \
        ${libdir}/pkgconfig/iotivity.pc"

FILES_${PN}-resource-thin-staticdev = "\
        ${libdir}/libocsrm.a \
        ${libdir}/libconnectivity_abstraction.a \
        ${libdir}/liboctbstack.a \
        ${libdir}/libcoap.a \
        ${libdir}/libc_common.a \
        ${libdir}/libroutingmanager.a \
        ${libdir}/libtimer.a"

FILES_${PN}-plugins-staticdev = "\
        ${includedir}/iotivity/plugins \
        ${libdir}/libplugin_interface.a \
        ${libdir}/libzigbee_wrapper.a \
        ${libdir}/libtelegesis_wrapper.a"

FILES_${PN}-plugins-dbg = "\
        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/plugins \
        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/bridging"
#TODO

FILES_${PN}-resource = "\
        ${libdir}/libconnectivity_abstraction.so \
        ${libdir}/liboc.so \
        ${libdir}/liboctbstack.so \
        ${libdir}/liboc_logger.so \
        ${libdir}/liboc_logger_core.so \
        ${@bb.utils.contains('EXTRA_OESCONS', 'SECURED=0', '', '${libdir}/libocprovision.so', d)} \
        ${@bb.utils.contains('EXTRA_OESCONS', 'SECURED=0', '', '${libdir}/libocpmapi.so', d)} \
        ${libdir}/libresource_directory.so"

FILES_${PN}-resource-dbg = "\
        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/resource \
        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/extlibs \
        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/examples \
        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/out \
        ${libdir}/.debug/liboc.so \
        ${libdir}/.debug/liboctbstack.so \
        ${libdir}/.debug/liboc_logger.so \
        ${libdir}/.debug/liboc_logger_core.so \
        ${@bb.utils.contains('EXTRA_OESCONS', 'SECURED=0', '', '${libdir}/.debug/libocprovision.so', d)} \
        ${@bb.utils.contains('EXTRA_OESCONS', 'SECURED=0', '', '${libdir}/.debug/libocpmapi.so', d)}"

FILES_${PN}-resource-samples-dbg = "\
        ${IOTIVITY_BIN_DIR}/examples/resource/cpp/.debug \
        ${IOTIVITY_BIN_DIR}/examples/resource/c/SimpleClientServer/.debug \
        ${IOTIVITY_BIN_DIR}/examples/resource/c/secure/.debug"

FILES_${PN}-resource-samples = "\
        ${IOTIVITY_BIN_DIR}/examples/resource"

FILES_${PN}-plugins-samples = "\
        ${IOTIVITY_BIN_DIR}/examples/plugins"

FILES_${PN}-plugins-samples-dbg = "\
        ${IOTIVITY_BIN_DIR}/examples/plugins/zigbee/.debug"

FILES_${PN}-service-dbg = "\
        ${prefix}/src/debug/${PN}/${EXTENDPE}${PV}-${PR}/${PN}-${PV}/service \
        ${libdir}/.debug"

FILES_${PN}-service-dev = "\
        ${includedir}/iotivity/service"

FILES_${PN}-service = "\
        ${libdir}/lib*plugin.so \
        ${libdir}/libBMISensorBundle.so \
        ${libdir}/libDISensorBundle.so \
        ${libdir}/libESEnrolleeSDK.so \
        ${libdir}/libESMediatorRich.so \
        ${libdir}/libHueBundle.so \
        ${libdir}/libTestBundle.so \
        ${libdir}/libipca.so \
        ${libdir}/libnotification_*.so \
        ${libdir}/librcs_client.so \
        ${libdir}/librcs_common.so \
        ${libdir}/librcs_container.so \
        ${libdir}/librcs_server.so"

FILES_${PN}-service-staticdev = "\
        ${libdir}/librcs_client.a \
        ${libdir}/librcs_server.a \
        ${libdir}/librcs_common.a \
        ${libdir}/librcs_container.a \
        ${libdir}/libresource_directory.a \
        ${libdir}/libscene_manager.a"

FILES_${PN}-service-samples-dbg = "\
        ${IOTIVITY_BIN_DIR}/examples/service/resource-encapsulation/.debug \
        ${IOTIVITY_BIN_DIR}/examples/service/resource-container/.debug \
        ${IOTIVITY_BIN_DIR}/examples/service/resource-directory/.debug \
        ${IOTIVITY_BIN_DIR}/examples/service/easy-setup/.debug \
        ${IOTIVITY_BIN_DIR}/examples/service/notification/.debug \
        ${IOTIVITY_BIN_DIR}/examples/service/scene-manager/.debug"

FILES_${PN}-service-samples = "\
        ${IOTIVITY_BIN_DIR}/examples/service"

FILES_${PN}-tests-dbg = "\
        ${libdir}/.debug/libgtest.so \
        ${libdir}/.debug/libgtest_main.so \
        ${IOTIVITY_BIN_DIR}/tests/service/easy-setup/.debug \
        ${IOTIVITY_BIN_DIR}/tests/service/notification/.debug \
        ${IOTIVITY_BIN_DIR}/tests/resource/.debug \
        ${IOTIVITY_BIN_DIR}/tests/service/resource-container/.debug \
        ${IOTIVITY_BIN_DIR}/tests/service/resource-encapsulation/.debug \
        ${IOTIVITY_BIN_DIR}/tests/service/scene-manager/.debug \
        ${IOTIVITY_BIN_DIR}/tests/plugins/zigbee/.debug"

FILES_${PN}-tests = "\
        ${IOTIVITY_BIN_DIR}/tests \
        ${libdir}/liboctbstack_test.so"

FILES_${PN}-tools = "\
        ${@bb.utils.contains('EXTRA_OESCONS', 'SECURED=0', '', '${IOTIVITY_BIN_DIR}/json2cbor', d)}"

PACKAGES = "${PN}-tests-dbg ${PN}-tests ${PN}-plugins-dbg ${PN}-plugins-staticdev ${PN}-plugins-samples-dbg ${PN}-plugins-samples ${PN}-resource-dbg ${PN}-resource ${PN}-resource-dev ${PN}-resource-thin-staticdev ${PN}-resource-samples-dbg ${PN}-resource-samples ${PN}-service-dbg ${PN}-service ${PN}-service-dev ${PN}-service-staticdev ${PN}-service-samples-dbg ${PN}-service-samples ${PN}-dev ${PN} ${PN}-tools"
ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "boost"
RRECOMMENDS_${PN} += "${PN}-resource ${PN}-service"
RRECOMMENDS_${PN}-dev += "${PN}-resource-dev ${PN}-resource-thin-staticdev ${PN}-plugins-staticdev ${PN}-service-dev ${PN}-service-staticdev"
RDEPENDS_${PN}-resource += "glib-2.0"
RRECOMMENDS_${PN}-plugins-staticdev += "${PN}-resource-dev ${PN}-resource-thin-staticdev ${PN}-resource"
RRECOMMENDS_${PN}-resource-thin-staticdev += "${PN}-resource-dev"
RRECOMMENDS_${PN}-service-dev += "${PN}-service ${PN}-service-staticdev ${PN}-resource"
RDEPENDS_${PN}-plugins-samples += "${PN}-resource glib-2.0"
RDEPENDS_${PN}-resource-samples += "${PN}-resource glib-2.0"
RDEPENDS_${PN}-tests += "${PN}-resource ${PN}-service glib-2.0"
RDEPENDS_${PN}-service-samples += "${PN}-service ${PN}-resource glib-2.0"
RDEPENDS_${PN}-service += "${PN}-resource glib-2.0"
BBCLASSEXTEND = "native nativesdk"
