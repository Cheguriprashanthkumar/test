#!/bin/bash
# Converts a MySQL dump to SQLite-compatible SQL

sed -e 's/`//g' \
    -e 's/ENGINE=[^ ]* / /g' \
    -e 's/AUTO_INCREMENT=[0-9]*\b//g' \
    -e 's/unsigned //g' \
    -e 's/DEFAULT CHARSET=[^ ]*//g' \
    -e 's/ COLLATE [^ ]*//g' \
    -e 's/COMMENT .*//g' \
    -e 's/ON UPDATE CURRENT_TIMESTAMP//g' \
    -e 's/\\r//g' \
    -e 's/LOCK TABLES .*;//g' \
    -e 's/UNLOCK TABLES;//g' \
    -e 's/_binary //g' \
    "$1"
