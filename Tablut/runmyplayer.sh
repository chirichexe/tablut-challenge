#!/bin/bash

# Script per l'esecuzione del giocatore Team Lucani
# Utilizzo: ./runmyplayer.sh <role> <timeout> <server-ip>

ROLE=$1
TIMEOUT=$2
IP=$3

# Impostazione valori di default
if [ -z "$ROLE" ]; then
    echo "Errore: specificare il ruolo (WHITE o BLACK)"
    echo "Usage: ./runmyplayer.sh <ROLE> [TIMEOUT] [IP]"
    exit 1
fi

if [ -z "$TIMEOUT" ]; then
    TIMEOUT=60
fi

if [ -z "$IP" ]; then
    IP="localhost"
fi

echo "Avvio Player Lucani - Ruolo: $ROLE, Timeout: $TIMEOUT, Server: $IP"

# Esecuzione del client
java -cp "lib/*:build" it.unibo.ai.didattica.competition.tablut.client.TablutLucaniClient "$ROLE" "$TIMEOUT" "$IP"
