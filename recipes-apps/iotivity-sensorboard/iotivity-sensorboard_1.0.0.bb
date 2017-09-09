PR = "r1"
SUMMARY = "Iotivity SensorBoard"
DESCRIPTION = "Iotivity Server application for Edison which demonstrates Iotivity server capabilities through the integration of an add-on breadboard that hosts temperature, ambient light and LED resources"
HOMEPAGE = "https://www.iotivity.org/"
DEPENDS = "iotivity mraa"
SECTION = "apps"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://server.cpp;beginline=1;endline=19;md5=a692dd0c72bcfa341a4ba826b37caf15"

SRC_URI = "file://iotivity-sensorboard.tar.gz \
        file://0001-Build-Use-LDFLAGS-variable-from-env-and-add-pthread-.patch \
        file://0003-server-Port-to-iotivity-1.2.0.patch \
        file://0004-build-Use-pkg-config.patch \
          "
inherit pkgconfig

S = "${WORKDIR}/iotivity-sensorboard"

IOTIVITY_BIN_DIR = "/opt/iotivity"
IOTIVITY_BIN_DIR_D = "${D}${IOTIVITY_BIN_DIR}"

do_compile_prepend() {
    export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
    export PKG_CONFIG="PKG_CONFIG_SYSROOT_DIR=\"${PKG_CONFIG_SYSROOT_DIR}\" pkg-config"
    export LD_FLAGS="${LD_FLAGS}"
}

do_install() {
    install -d ${IOTIVITY_BIN_DIR_D}/apps/iotivity-sensorboard
    install -c -m 555 ${S}/sensorboard ${IOTIVITY_BIN_DIR_D}/apps/iotivity-sensorboard
}

FILES_${PN} = "${IOTIVITY_BIN_DIR}/apps/iotivity-sensorboard/sensorboard" 
FILES_${PN}-dbg = "${IOTIVITY_BIN_DIR}/apps/iotivity-sensorboard/.debug"
RDEPENDS_${PN} += "iotivity-resource mraa"
BBCLASSEXTEND = "native nativesdk"
PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"
