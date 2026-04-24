# Tablut Challenge - Team GuerrieroSannita

Progetto per la competizione Tablut Challenge 2025/2026.

## Struttura del Progetto

Il sistema estende il framework fornito e implementa un agente in Java.

- Tablut/src/.../client/TablutStudentClient.java: Classe principale contenente il ciclo di gioco e la logica di ricerca.
- Tablut/Executables/run_student.sh: Script di utilità per test locali.
- Tablut/Executables/runmyplayer.sh: Script ufficiale per la sottomissione (puntamento a jar/classi finali).
- Tablut/build.xml: File Ant per compilazione e gestione server.

## Istruzioni per il Test

Eseguire i comandi in terminali separati.

1. Compilazione:
   ant -f Tablut/build.xml compile

2. Avvio Server:
   ant -f Tablut/build.xml server

3. Avvio Giocatore (Student):
   ./Tablut/Executables/run_student.sh WHITE 60 localhost

4. Avvio Avversario (Random):
   ant -f Tablut/build.xml randomblack

## Implementazione Logica AI

Sviluppare le seguenti componenti in TablutStudentClient.java:

1. Ricerca: Minimax con Alpha-Beta Pruning.
   - Utilizzare state.clone() per le simulazioni.
   - Utilizzare rules.checkMove(state, action) per validare le mosse.

2. Valutazione: Funzione di euristica per l'assegnazione di un punteggio agli stati.
   - Bianco: priorità alla fuga del Re e controllo dei percorsi verso le escape tiles.
   - Nero: priorità alla cattura e all'accerchiamento del Re.

3. Gestione Tempo: Iterative Deepening.
   - Monitorare System.currentTimeMillis() per interrompere la ricerca prima del timeout di 60 secondi.

## Vincoli Tecnici
- Timeout: 60 secondi per mossa (imposto dal server).
- Regole: Ashton Tablut (9x9).
- Risorse: Il processo deve essere autonomo e non richiedere connessione internet.
- Log: Limitare l'output su stdout per evitare la saturazione del disco nella VM.
