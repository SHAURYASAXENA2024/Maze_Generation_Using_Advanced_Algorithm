#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out
mapfile -t SRCS < <(find . -name '*.java' -not -path './out/*' | sort)
javac -encoding UTF-8 -d out "${SRCS[@]}"
exec java -cp out main.main
