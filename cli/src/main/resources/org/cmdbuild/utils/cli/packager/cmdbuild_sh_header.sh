#!/bin/bash

# header size is set from builder process
header_size=0000

# these parameters are used by this code AND by builder process
java_archive_url=https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u202-b08/OpenJDK8U-jre_x64_linux_hotspot_8u202b08.tar.gz
java_archive_size=40856726
java_archive_checksum=7835ae2316e46539798a93da2f511ac5045fd579
maven_archive_url=https://archive.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
maven_archive_size=8491533
maven_archive_checksum=5b4c117854921b527ab6190615f9435da730ba05

if ! java -version 2>&1 | egrep -q 'version "1[.](8|9|1.)[.]*'; then
	jdk_temp_dir="/tmp/cm_$(echo "cmdbuild_cli_java_${java_archive_checksum}_$(whoami)" | md5sum | cut -d' ' -f1)"
	if ! [ -e "${jdk_temp_dir}/ok" ]; then
		echo "java binary (1.8 or later) not found; preparing embedded java runtime..." >&2
		rm -rf "${jdk_temp_dir}"
		mkdir -p "${jdk_temp_dir}"
		dd bs=1M if="$0" iflag=skip_bytes,count_bytes skip=${header_size} count=${java_archive_size} status=none | tar -C "${jdk_temp_dir}" -xz || { rm -rf "${jdk_temp_dir}"; exit 1; }
		touch "${jdk_temp_dir}/ok"
	fi
	java_binary="$(find "${jdk_temp_dir}" -name java | sort | head -n1)"
	$java_binary -version 2>&1 | egrep -q 'version "1[.](8|9|1.)[.]*' || exit 1
else
	java_binary="`which java`"
fi

if ! mvn -version 2>&1 | egrep -q 'Apache Maven 3.3.*'; then
	mvn_temp_dir="/tmp/cm_$(echo "cmdbuild_cli_mvn_${maven_archive_checksum}_$(whoami)" | md5sum | cut -d' ' -f1)"
	if ! [ -e "${mvn_temp_dir}/ok" ]; then
		echo "mvn binary (3.3.x) not found; preparing embedded mvn runtime..." >&2
		rm -rf "${mvn_temp_dir}"
		mkdir -p "${mvn_temp_dir}"
		dd bs=1M if="$0" iflag=skip_bytes,count_bytes skip=$[header_size+java_archive_size] count=${maven_archive_size} status=none | tar -C "${mvn_temp_dir}" -xz || { rm -rf "${mvn_temp_dir}"; exit 1; }
		touch "${mvn_temp_dir}/ok"
	fi
	mvn_binary="$(find "${mvn_temp_dir}" -name mvn | sort | head -n1)"
	$mvn_binary -version 2>&1 | egrep -q 'Apache Maven 3.3.*' || exit 1
    PATH=$(dirname $mvn_binary):$PATH
    export PATH
fi

checksum=$(echo "`stat -c %Y "$0"`_`du -b "$0"`_`whoami`" | md5sum | cut -d' ' -f1)
tdir="/tmp/cm_${checksum}";
tempfile="${tdir}/file.jar"
okfile="${tdir}/ok"

if ! [ -e "${okfile}" ]; then
	echo "preparing cli..." >&2
	mkdir -p "$tdir"
	dd bs=1M if="$0" of="${tempfile}" iflag=skip_bytes skip=$[header_size+java_archive_size+maven_archive_size] status=none || exit 1
	unzip -tqq "${tempfile}" || exit 1
	touch "${okfile}"
fi

#echo "using java $java_binary $( $java_binary -version 2>&1 | head -n1 )" >&2

exec "$java_binary" -Xmx6G -jar "$tempfile" "$@"

# === after this marker there will be some empty lines, and then raw war(zip) file data === #


