#!/bin/bash

warDir="$(dirname "$0")"

classpath="$warDir"
for jarFile in "$warDir"/WEB-INF/lib/*.jar*; do
	if [ -f "$jarFile" ]; then
		classpath="${classpath}:${jarFile}"
	fi
done

exec java -Xmx6G -cp "$classpath" 'org.cmdbuild.webapp.cli.Main' 'startedFromExplodedWar' "$warDir" "$@"

