# TODO:
# echo 'PREFERRED_VERSION_iotivity="0.0.0.patch" >> build/conf/local.conf

branch_iotivity ?= "sandbox/pcoval/on/master/yocto"
baseurl_iotivity ?= "git://github.com/tizenteam/iotivity.git"
#uri_iotivity ?= "${baseurl_iotivity};destsuffix=${S};branch=${branch_iotivity};protocol=http"

include iotivity_0.0.0.bb
