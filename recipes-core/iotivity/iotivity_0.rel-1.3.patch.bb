# TODO:
# echo 'PREFERRED_VERSION_iotivity="0.rel-1.3.patch"' >> build/conf/local.conf

basebranch_iotivity ?= "next"
branch_iotivity ?= "sandbox/pcoval/on/${basebranch_iotivity}/yocto"
baseurl_iotivity ?= "git://github.com/tizenteam/iotivity.git"
uri_iotivity ?= "${baseurl_iotivity};destsuffix=${S};branch=${branch_iotivity};protocol=http"

include iotivity_0.rel-1.3.bb
