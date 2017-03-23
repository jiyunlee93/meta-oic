PKG_CONFIG?=pkg-config

YOCTOCXXFLAGS+=$(shell $(PKG_CONFIG) iotivity --cflags)
YOCTOCXXFLAGS+=-std=c++0x

YOCTOLDFLAGS+=$(shell $(PKG_CONFIG) iotivity --libs)

all: simpleclient

simpleclient.o: simpleclient.cpp
ifeq ($(PKG_CONFIG_SYSROOT_DIR),)
	echo "Error: Yocto cross-toolchain environment not initialized"
	exit 1 
endif
	$(CXX) -c -o $@ $< $(YOCTOCXXFLAGS)

simpleclient: simpleclient.o
	$(CXX) -o $@ $^ $(LDFLAGS) $(YOCTOLDFLAGS)

clean:
	rm -rf simpleclient *.o
