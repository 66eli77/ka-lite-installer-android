KA Lite Wrapper for Android
===============

Android port of KA Lite (an offline version of Khan Academy), encapsulating the Django project.
please run $ git clone —recursive https://github.com/learningequality/ka-lite-installer-android.git
to clone the submodule inside this repo

Version info
------------
*This version is only for testing.*
[Kivy](https://github.com/kivy/kivy) and
[Python for Android](https://github.com/kivy/python-for-android)
is used to build Python powered apk.


Build Requirements
------------------
See [Python for Android prerequisites](http://python-for-android.readthedocs.org/en/latest/prerequisites/).

Installed Python, Git, Apache Ant, Android SDK, and Android NDK are definitely needed.

You'll need to ensure you have the development headers for sqlite3 installed: `sudo apt-get install libsqlite3-dev`.

You also want to ensure you have ia32-libs installed. If you are running Ubuntu 14.04, you need to run the following commands in order to install it.

	sudo -i
	cd /etc/apt/sources.list.d
	echo "deb http://old-releases.ubuntu.com/ubuntu/ raring main restricted universe multiverse" >ia32-libs-raring.list
	apt-get update
	apt-get install ia32-libs
	rm /etc/apt/sources.list.d/ia32-libs-raring.list
	apt-get update
	exit
	sudo apt-get install gcc-multilib

Here's a link to download the right Linux NDK version(http://dl.google.com/android/ndk/android-ndk-r8c-linux-x86.tar.bz2)

Build
-----
To build the apk, first setup the environment with the following command:

	export ANDROIDAPI=<Android API version(must be 14 !!)>
	export ANDROIDNDKVER=<Android NDK version>
	export ANDROIDSDK=<Path to the Android SDK>
	export ANDROIDNDK=<Path to the Android NDK(default is r8c)>

Navigate to the ka-lite-installer-android folder, then run the build-script(this is a SHELL script file) file from that folder

Build for Mac
-------------

Make sure you have the right NDK version, you can get one here for Mac(http://dl.google.com/android/ndk/android-ndk-r8c-darwin-x86.tar.bz2)

For Mac users, you don’t have and need ia32-libs, instead, you need the right version of Cython, Cython 0.17.1

If your Mac run OSX Mavericks, you cannot install Cython normally. Run the following command will fix this

	$ sudo CFLAGS=-Wunused-command-line-argument-hard-error-in-future pip install cython==0.17.1

then run the _ant_  as above mentioned.

Debug
-----
Crashes can be observed with `adb logcat`.

Kivy logs are stored in `/mnt/sdcard/org.kalite.test/.kivy/logs` directory.


Run without building
--------------------
To run _ka-lite-android_ without building Android application:

   * install Kivy (see [Kivy installation](http://kivy.org/docs/installation/installation.html))
   * prepare KA Lite: `ant ka-lite`
   * launch `ka-lite-android/main.py`
