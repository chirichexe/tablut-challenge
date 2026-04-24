#!/bin/bash

# run_student.sh <ROLE> <TIMEOUT> <IP>
ROLE=$1
TIMEOUT=$2
IP=$3

java -cp "Tablut/lib/*:Tablut/build" it.unibo.ai.didattica.competition.tablut.client.TablutStudentClient "$ROLE" "$TIMEOUT" "$IP"
