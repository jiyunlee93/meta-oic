PKG_CONFIG?=pkg-config

override CXXFLAGS+=$(shell $(PKG_CONFIG) iotivity --cflags)
override LDFLAGS+=$(shell $(PKG_CONFIG) iotivity --libs)
override LDFLAGS+=-lmraa -pthread
override CXXFLAGS+=-std=c++0x

all: sensorboard

%.o: %.cpp
	$(CXX) -c -o $@ $< $(CXXFLAGS)

sensorboard: server.o observer.o
	$(CXX) -o $@ $^ $(LDFLAGS) $(LDFLAGS)

clean:
	rm -rf sensorboard *.o
