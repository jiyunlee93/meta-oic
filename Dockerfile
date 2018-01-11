#!/bin/echo docker build . -f
# -*- coding: utf-8 -*-
#{
# Copyright 2018 Samsung Electronics France SAS
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#}

FROM debian:stable
MAINTAINER Philippe Coval (philippe.coval@osg.samsung.com)

ENV DEBIAN_FRONTEND noninteractive
ENV LC_ALL en_US.UTF-8
ENV LANG ${LC_ALL}

RUN echo "#log: Configuring locales" \
  && set -x \
  && apt-get update \
  && apt-get install -y locales \
  && echo "${LC_ALL} UTF-8" | tee /etc/locale.gen \
  && locale-gen ${LC_ALL} \
  && dpkg-reconfigure locales \
  && sync

ENV project meta-oic

RUN echo "#log: ${project}: Setup system" \
  && set -x \
  && apt-get update -y \
  && apt-get install -y \
  binutils-gold \
  build-essential \
  ccache \
  chrpath \
  cpio \
  curl \
  diffstat \
  gawk \
  gcc-multilib \
  git-core \
  libattr1-dev \
  libsdl1.2-dev \
  libwayland-dev \
  quilt \
  sudo \
  texinfo \
  unzip \
  wget \
  python \
  && apt-get clean \
  && sync


WORKDIR /usr/local/src/${project}/
RUN echo "#log: ${project}: Preparing sources" \
  && git clone --depth 1 http://git.yoctoproject.org/git/poky.git \
  && chown -R nobody . \
  && sync

USER nobody
ADD . /usr/local/src/${project}/${project}/
WORKDIR /usr/local/src/${project}/
RUN echo "#log: ${project}: Preparing sources (step: poky)" \
  && set -x \
  && cd poky \
  && . ./oe-init-build-env \
  && pwd \
  && bitbake -h \
  && bitbake core-image-minimal -g -u knotty \
  && sync

USER nobody
WORKDIR /usr/local/src/${project}/poky/
RUN echo "#log: ${project}: Preparing sources (step: ${project})" \
  && set -x \
  && printf "RELATIVE_DIR := \"\${@os.path.abspath(os.path.dirname(d.getVar('FILE', True)) + '/../../..')}\"\n" \
  | tee -a ./build/conf/bblayers.conf \
  && printf "BBLAYERS += \"\${RELATIVE_DIR}/${project}\"\n" \
  | tee -a ./build/conf/bblayers.conf \
  && printf "CORE_IMAGE_EXTRA_INSTALL += \" packagegroup-iotivity \"\n\n" \
  | tee -a ./build/conf/local.conf \
  && sync

USER nobody
WORKDIR /usr/local/src/${project}/poky/
RUN echo "#log: ${project}: Building sources" \
  && set -x \
  && . ./oe-init-build-env \
  && tail conf/bblayers.conf conf/local.conf \
  && bitbake packagegroup-iotivity -g -u knotty \
  && bitbake core-image-minimal \
  && ls build/tmp*/deploy ||: \
  && rm -rf * \
  && sync
