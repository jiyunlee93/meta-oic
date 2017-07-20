YOCTOCXXFLAGS=-I$(PKG_CONFIG_SYSROOT_DIR)/usr/include/iotivity/resource/ -I$(PKG_CONFIG_SYSROOT_DIR)/usr/include/iotivity/resource/stack -I$(PKG_CONFIG_SYSROOT_DIR)/usr/include/iotivity/resource/ocrandom -I$(PKG_CONFIG_SYSROOT_DIR)/usr/include/iotivity/resource/logger -I$(PKG_CONFIG_SYSROOT_DIR)/usr/include/iotivity/resource/oc_logger

YOCTOLDFLAGS=-loc -loctbstack -loc_logger -lmraa

all: sensorboard

%.o: %.cpp
ifeq ($(PKG_CONFIG_SYSROOT_DIR),)
	echo "Error: Yocto cross-toolchain environment not initialized"
	exit 1 
endif
	$(CXX) -std=c++0x -c -o $@ $< $(YOCTOCXXFLAGS)

sensorboard: server.o observer.o
	$(CXX) -o sensorboard server.o observer.o $(YOCTOLDFLAGS)

clean:
	rm -rf sensorboard *.o
