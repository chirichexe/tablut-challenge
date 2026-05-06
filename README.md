# Tablut Challenge - Team Lucani
## Davide Chirichella, Gabriele Doti

Progetto per la competizione Tablut Challenge 2025/2026.

## Struttura del Progetto

Il sistema estende il framework fornito e implementa un agente in Java con un'architettura modulare.

- **`it.unibo.ai.didattica.competition.tablut.algorithm`**: Contiene l'engine di ricerca (Alpha-Beta, Iterative Deepening).
- **`it.unibo.ai.didattica.competition.tablut.heuristic`**: Contiene le implementazioni delle diverse euristiche (Materiale, Mobilità, Sicurezza Re, ecc.).
- **`Tablut/src/.../client/TablutStudentClient.java`**: Punto di ingresso del client.
- **`Tablut/build.xml`**: Automazione con Ant per compilazione e testing.

## Istruzioni per l'Esecuzione

### 1. Compilazione
```bash
ant -f Tablut/build.xml compile
```

### 2. Avvio del Server
```bash
ant -f Tablut/build.xml gui-server
```

### 3. Avvio dell'Agente
```bash
java -cp "Tablut/lib/*:Tablut/build" it.unibo.ai.didattica.competition.tablut.client.TablutStudentClient WHITE 60 localhost
```

## Implementazione AI Corrente

L'agente utilizza un approccio allo stato dell'arte per giochi avversariali:
- **Algoritmo**: Alpha-Beta Pruning per ottimizzare lo spazio di ricerca.
- **Strategia**: Iterative Deepening per gestire dinamicamente il timeout (60s).
- **Modularità**: Le mosse sono generate da un `MoveGenerator` dedicato e valutate da un `Evaluator` che combina pesi dinamici.
- **Euristiche Implementate**:
  - `KingEscape`: Valuta le vie di fuga libere per il Re.
  - `KingSafety`: Protezione del Re tramite pedine bianche.
  - `Material`: Differenza tra pedine vive.
  - `Mobility`: Numero di mosse legali disponibili.
  - `OpenPaths`: Controllo delle linee strategiche sulla scacchiera.

## Vincoli Tecnici
- Timeout: 60 secondi per mossa.
- Regole: Ashton Tablut (9x9).
- Log: Output ottimizzato per evitare saturazione disco in ambiente di test.
