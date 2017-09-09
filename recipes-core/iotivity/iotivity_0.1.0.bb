include iotivity_0.0.0.bb

# TODO:
# echo 'PREFERRED_VERSION_iotivity="0.1.0"' >> build/conf/local.conf

#{ TODO
SRC_URI = "git://github.com/tizenteam/iotivity.git;destsuffix=${S};branch=${branch_iotivity};protocol=http"
branch_iotivity = "sandbox/pcoval/on/master/review"
#branch_iotivity = "1.3-rel"
#branch_iotivity = "sandbox/pcoval/on/master/yocto"
#branch_iotivity = "sandbox/pcoval/on/next/patch"
#branch_iotivity = "sandbox/pcoval/on/${branch_iotivity}/patch"
SRCREV = "${branch_iotivity}"
SRC_URI = "git:///home/user/mnt/iotivity;destsuffix=${S};branch=${branch_iotivity};protocol=file"
#} TODO
