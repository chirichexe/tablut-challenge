# Tablut Challenge - Team Lucani
## Davide Chirichella, Gabriele Doti

Progetto per la competizione Tablut Challenge 2025/2026.

## Struttura del Progetto

Il sistema estende il framework fornito e implementa un agente in Java con un'architettura modulare.

- **`it.unibo.ai.didattica.competition.tablut.algorithm`**: Contiene l'engine di ricerca (Alpha-Beta, Iterative Deepening).
- **`it.unibo.ai.didattica.competition.tablut.heuristic`**: Contiene le implementazioni delle diverse euristiche (Materiale, Mobilità, Sicurezza Re, ecc.).
- **`Tablut/src/.../client/TablutLucaniClient.java`**: Punto di ingresso del client (Team Lucani).
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

### 3. Avvio dell'Agente (Team Lucani)
```bash
ant -f Tablut/build.xml lucaniwhite
```
*(Oppure `lucaniblack` a seconda del ruolo).*

## Implementazione AI Corrente

L'agente utilizza un approccio allo stato dell'arte per giochi avversariali:
- **Algoritmo**: Alpha-Beta Pruning con **Killer Move Heuristic** e **Move Ordering**.
- **Strategia**: Iterative Deepening per la gestione del tempo.
- **Euristiche Avanzate**:
  - `Rhombus`: Formazione a rombo per il blocco strategico dei Neri.
  - `BestPositions`: Controllo delle caselle chiave in apertura per i Bianchi.
  - `KingEscape/Safety`: Protezione dinamica del Re.
  - `Weighted Material`: Valutazione differenziata del materiale tra Bianchi e Neri.

## Vincoli Tecnici
- Timeout: 60 secondi per mossa.
- Regole: Ashton Tablut (9x9).
- Log: Output ottimizzato per evitare saturazione disco in ambiente di test.
