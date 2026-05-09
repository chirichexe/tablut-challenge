#!/bin/bash

ROLE=$1
TIMEOUT=$2
IP=$3

if [ -z "$ROLE" ]; then
    ROLE="WHITE"
fi

if [ -z "$TIMEOUT" ]; then
    TIMEOUT=60
fi

if [ -z "$IP" ]; then
    IP="localhost"
fi

java -cp "lib/*:build" it.unibo.ai.didattica.competition.tablut.client.TablutLucaniClient "$ROLE" "$TIMEOUT" "$IP"
