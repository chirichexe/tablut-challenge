# Tablut Challenge - Team Lucani
## Davide Chirichella, Gabriele Doti

Progetto per la competizione Tablut Challenge 2025/2026.

## Struttura del Progetto

Il sistema estende il framework fornito e implementa un agente in Java.

- Tablut/src/.../client/TablutStudentClient.java: Classe principale contenente il ciclo di gioco e la logica di ricerca.
- Tablut/Executables/run_student.sh: Script di utilità per test locali.
- Tablut/Executables/runmyplayer.sh: Script ufficiale per la sottomissione (puntamento a jar/classi finali).
- Tablut/build.xml: File Ant per compilazione e gestione server.

## Istruzioni per l'Esecuzione

Per testare l'agente con la nuova implementazione **Minimax**, segui questi passaggi da terminali separati (posizionandoti nella root del progetto):

### 1. Compilazione
Compila tutto il progetto usando Ant:
```bash
ant -f Tablut/build.xml compile
```

### 2. Avvio del Server
Lancia il server che gestisce la partita (usa `-g` per la GUI se disponibile nel tuo ambiente):
```bash
ant -f Tablut/build.xml gui-server
```

### 3. Avvio del tuo Agente (Minimax)
Lancia il client `TablutStudentClient` (che ora usa l'algoritmo Minimax):
```bash
java -cp "Tablut/lib/*:Tablut/build" it.unibo.ai.didattica.competition.tablut.client.TablutStudentClient WHITE 60 localhost
```
*Nota: Puoi cambiare `WHITE` con `BLACK` a seconda del ruolo desiderato.*

### 4. Avvio dell'Avversario
Lancia un giocatore casuale per testare la risposta del tuo agente:
```bash
ant -f Tablut/build.xml randomblack
```
*(Oppure `randomwhite` se il tuo agente è BLACK).*

## Implementazione AI Corrente

L'agente attualmente utilizza:
- **Algoritmo**: Minimax puro (senza Alpha-Beta pruning per ora).
- **Profondità**: Impostata a 2 livelli (modificabile in `TablutStudentClient.java`).
- **Euristica**: Mock basata sulla differenza di materiale (pedine bianche + re vs pedine nere).
- **Generatore di Mosse**: Integrato in `MinMaxTablut.java`, utilizza le regole ufficiali per validare ogni mossa.

## Vincoli Tecnici
- Timeout: 60 secondi per mossa (imposto dal server).
- Regole: Ashton Tablut (9x9).
- Risorse: Il processo deve essere autonomo e non richiedere connessione internet.
- Log: Limitare l'output su stdout per evitare la saturazione del disco nella VM.

## Sopprimere i log in esecuzione

Per ridurre il peso dei log durante i test:

1. Nascondi completamente output standard ed errori:
```bash
ant -f Tablut/build.xml server > /dev/null 2>&1
java -cp "Tablut/lib/*:Tablut/build" it.unibo.ai.didattica.competition.tablut.client.TablutStudentClient WHITE 60 localhost > /dev/null 2>&1
```
2. Se vuoi vedere solo gli errori:
```bash
ant -f Tablut/build.xml server > /dev/null
```
3. Il server salva anche file di log in `Tablut/logs/`; per pulirli:
```bash
rm -f Tablut/logs/*_systemLog.txt Tablut/logs/*_gameLog.txt
```
