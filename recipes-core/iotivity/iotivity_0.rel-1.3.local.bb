# TODO:
# echo 'PREFERRED_VERSION_iotivity="0.rel-1.3.local"' >> build/conf/local.conf

basebranch_iotivity ?= "next"
branch_iotivity ?= "sandbox/pcoval/on/${basebranch_iotivity}/yocto"
#branch_iotivity ?= "sandbox/pcoval/on/${basebranch_iotivity}/unsecured"
baseurl_iotivity ?= "git:///home/user/mnt/iotivity"
url_iotivity ?= "${baseurl_iotivity};destsuffix=${S};branch=${branch_iotivity};protocol=file"

include iotivity_0.rel-1.3.patch.bb
