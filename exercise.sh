#!/usr/bin/env bash

for f in ./input/*.json; do fn="$(basename "$f" .json)"; echo java -jar PopG.jar -n -p=print/${fn}.png input/${fn}.json; done | sh
