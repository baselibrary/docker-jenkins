#!/bin/bash
set -e

major=2
version="$(curl -fsSL "http://pkg.jenkins-ci.org/debian-stable/binary/Packages.gz" | gunzip | awk -v pkgname="jenkins" -F ': ' '$1 == "Package" { pkg = $2 } pkg == pkgname && $1 == "Version" { print $2 }' | sort -rV | head -n1 )"

set -x
sed '
	s/%%JENKINS_MAJOR%%/'"$major"'/g;
	s/%%JENKINS_VERSION%%/'"$version"'/g;
' Dockerfile.template > "Dockerfile"
