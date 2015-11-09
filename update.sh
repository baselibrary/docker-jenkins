#!/bin/bash
set -e

cd "$(dirname "$(readlink -f "$BASH_SOURCE")")"

versions=( "$@" )
if [ ${#versions[@]} -eq 0 ]; then
	versions=( */ )
fi
versions=( "${versions[@]%/}" )


for version in "${versions[@]}"; do	
  fullVersion="$(curl -fsSL "http://pkg.jenkins-ci.org/debian-stable/binary/Packages.gz" | gunzip | awk -v pkgname="jenkins" -F ': ' '$1 == "Package" { pkg = $2 } pkg == pkgname && $1 == "Version" { print $2 }' | sort -rV | head -n1 )"
	(
		set -x
		cp docker-entrypoint.sh "$version/"
		sed '
		  s/%%JENKINS_MAJOR%%/'"$version"'/g;
		  s/%%JENKINS_VERSION%%/'"$fullVersion"'/g;
		' Dockerfile.template > "$version/Dockerfile"
	)
done
