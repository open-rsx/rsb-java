#!/bin/bash

# This script implements the RSB specific versioning scheme on top of Maven.
# Since the project.version can't be changed inside a Maven build, this has
# to be done externally.
#
# Just run this before you use Maven to build.

GITVERSION=`git describe --tags --long --match release-*.* 2>/dev/null`
if [ $? -eq 0 ]; then
    echo $GITVERSION > rsb-java/gitversion
fi

GITBRANCH=`git rev-parse --abbrev-ref HEAD 2>/dev/null`
if [ $? -eq 0 ]; then
    echo $GITBRANCH > rsb-java/gitbranch
else
    GITBRANCH=`cat rsb-java/gitbranch`
fi

GITPATCH=`perl -p -e 's/-g[0-9a-fA-F]+$//;s/^.*-//' rsb-java/gitversion`

RELBRANCH=`perl -p -e 's/^[0-9]+\.[0-9]+$//' rsb-java/gitbranch`

if [ -z "$GITPATCH" ] || [ -z "$GITBRANCH" ]; then
    echo "Could not get version/branch information. Is this either a git checkout or an official source archive?" 1>&2
    exit 1
fi


if [ -z "$RELBRANCH" ]; then
    FULLVERSION=$GITBRANCH.$GITPATCH
    echo "We are on a release branch, version is $FULLVERSION"
    mvn versions:set -DnewVersion=$FULLVERSION
    echo "Changed the version in the pom.xml, you are now ready to build!"
else
    echo "We are not on a release branch, not changing the version. You can build now!"
fi

# Workaround to detect correct protobuf version on unix systems
# as dependency versions cannot be modified with the maven properties plugin
VERSION_OUT="$(protoc --version)"
VERSION_MAJOR="$(echo "${VERSION_OUT}" | sed -r 's/.* ([0-9]+)\.[0-9]+.*/\1/')"
VERSION_MINOR="$(echo "${VERSION_OUT}" | sed -r 's/.* [0-9]+\.([0-9]+).*/\1/')"
sed -i.bak -e 's#<pbuf\.minversion>.*</pbuf\.minversion>#<pbuf.minversion>'"${VERSION_MAJOR}.${VERSION_MINOR}"'</pbuf.minversion>#;s#<pbuf\.maxversion>.*</pbuf\.maxversion>#<pbuf.maxversion>'"${VERSION_MAJOR}.$((${VERSION_MINOR}+1))"'</pbuf.maxversion>#' pom.xml
