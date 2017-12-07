# TODO:
# echo 'PREFERRED_VERSION_iotivity="0.0.0.local"' >> build/conf/local.conf

basebranch_iotivity ?= "master"
branch_iotivity ?= "sandbox/pcoval/on/${basebranch_iotivity}/yocto"
baseurl_iotivity ?= "git:///home/user/mnt/iotivity"
url_iotivity ?= "${baseurl_iotivity};destsuffix=${S};branch=${branch_iotivity};protocol=file"

include iotivity_0.0.0.bb
