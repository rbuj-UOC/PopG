#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_FILE="$ROOT_DIR/src/PopGUserInterface.java"
OUT_DIR="$ROOT_DIR/out"
JAR_FILE="$ROOT_DIR/PopG.jar"

if [[ ! -f "$SRC_FILE" ]]; then
  echo "Error: $SRC_FILE not found" >&2
  exit 1
fi

rm -f "$JAR_FILE"
mkdir -p "$OUT_DIR"

# Compile the Java source into the package directory structure.
javac -d "$OUT_DIR" "$SRC_FILE"

# Package the compiled classes into an executable JAR.
jar cfe "$JAR_FILE" popg.PopGUserInterface -C "$OUT_DIR" .

echo "Created $JAR_FILE"
