#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_FILE="$ROOT_DIR/src/PopGUserInterface.java"
OUT_DIR="$ROOT_DIR/out"

if [[ ! -f "$SRC_FILE" ]]; then
  echo "Error: $SRC_FILE not found" >&2
  exit 1
fi

mkdir -p "$OUT_DIR"

# Compile and place classes following the package structure.
javac -d "$OUT_DIR" "$SRC_FILE"

# Run the main class.
java -cp "$OUT_DIR" popg.PopGUserInterface
