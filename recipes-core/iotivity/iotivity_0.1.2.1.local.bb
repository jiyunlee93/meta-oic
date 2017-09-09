# TODO:
# echo 'PREFERRED_VERSION_iotivity="0.1.2.1.local"' >> build/conf/local.conf

basebranch_iotivity ?= "previous"
branch_iotivity ?= "sandbox/pcoval/on/${basebranch_iotivity}/yocto"

baseurl_iotivity ?= "git:///home/user/mnt/iotivity"
uri_iotivity ?= "${baseurl_iotivity};destsuffix=${S};branch=${branch_iotivity};protocol=http"

#TARGET_LDFLAGS_append = " -Wl,-rpath-link=\${ORIGIN}"
#TARGET_LDFLAGS_append = ' -Wl,-rpath-link=$${ORIGIN}'
#TARGET_LDFLAGS_append = ' -Wl,-rpath-link=${ORIGIN}'
TARGET_LDFLAGS_append = ' -Wl,-rpath-link=\$\${ORIGIN}'

include iotivity_1.2.1.bb
