FROM ubuntu:14.04
RUN dpkg --add-architecture i386
RUN echo "deb http://old-releases.ubuntu.com/ubuntu/ raring main restricted universe multiverse" > /etc/apt/sources.list.d/ia32-libs-raring.list
RUN apt-get install -y curl software-properties-common python3-software-properties
RUN add-apt-repository "deb http://archive.canonical.com/ $(lsb_release -sc) partner"
RUN apt-get update
RUN apt-get install -y ia32-libs

# Download the old android ndk
RUN curl http://dl.google.com/android/ndk/android-ndk-r8c-linux-x86.tar.bz2 | tar xvj -C /opt/
RUN curl http://dl.google.com/android/android-sdk_r22-linux.tgz | tar xvz -C /opt/

# some other dependencies for building the apk
RUN apt-get install -y python ant cython zlib1g-dev build-essential unzip

# install django to run pybuilder-hack
RUN apt-get install -y python-django python-pip python-django-south
RUN pip install pathlib

# download the actual sdk
RUN echo "y\n" | /opt/android-sdk-linux/tools/android update sdk -u -t 10,3,1,2

# build the zipped ka-lite

# install java
RUN apt-get install -y default-jdk

# build the APK
WORKDIR /root/
CMD pybuilder-hack/manage.py build -d ka-lite/ && \
    mv ka-lite.zip ka-lite-android/ && \
    ant -Dandroid-sdk=/opt/android-sdk-linux/ -Dandroid-ndk=/opt/android-ndk-r8c/ -Dandroid-api=14 -Dandroid-ndkver=r8c
