# Proposte di Miglioramento Tablut AI

Questo documento elenca le criticità prestazionali e algoritmiche identificate nell'attuale implementazione dell'AI e le relative soluzioni proposte.

## 1. Ottimizzazione Generazione Mosse (Critico)
L'attuale utilizzo di `game.checkMove(state.clone(), action)` all'interno del ciclo di ricerca è il principale collo di bottiglia.

*   **Problema:** `checkMove` esegue una clonazione profonda dello stato per ogni mossa, lancia eccezioni costose per controllare la validità e scrive log su disco per ogni nodo dell'albero.
*   **Soluzione:**
    *   Implementare un generatore di mosse leggero che non usi `checkMove`.
    *   Evitare la clonazione (`clone()`). Utilizzare la tecnica **Do-Undo**: applicare la mossa allo stato, scendere in ricorsione e poi annullare la mossa.
    *   Rimuovere ogni operazione di I/O (log) e gestione di eccezioni dal percorso critico della ricerca.

## 2. Gestione del Tempo e Iterative Deepening
L'attuale implementazione dell'Iterative Deepening non interrompe la ricerca in modo efficace se il timeout scade a metà di una profondità elevata.

*   **Problema:** La guardia del timeout è solo nel ciclo esterno. Se la profondità 4 richiede 10 minuti, il bot rimarrà bloccato nonostante il timeout di 60 secondi.
*   **Soluzione:**
    *   Passare il tempo di scadenza (`deadline`) a tutte le chiamate ricorsive di `alphaBeta`.
    *   Controllare periodicamente (es. ogni 1000 nodi) se il tempo è scaduto.
    *   Lanciare una `TimeOutException` per risalire l'albero e restituire il risultato della profondità precedente completata.

## 3. Transposition Table (Zobrist Hashing)
L'algoritmo attualmente rivaluta stati identici raggiunti tramite percorsi diversi.

*   **Soluzione:**
    *   Implementare lo **Zobrist Hashing** per mappare univocamente una configurazione della scacchiera a un valore a 64 bit.
    *   Utilizzare una **Transposition Table** (una cache basata su hash) per memorizzare lo score e la profondità di ricerca per gli stati già visitati.

## 4. Ottimizzazione delle Euristiche
Il calcolo delle euristiche tramite più oggetti e cicli indipendenti è inefficiente.

*   **Problema:** Ogni euristica (Material, KingSafety, ecc.) scorre la scacchiera separatamente.
*   **Soluzione:**
    *   Accorpare tutte le funzioni di valutazione in un unico metodo `evaluate`.
    *   Eseguire **un solo passaggio** sulla scacchiera (singolo ciclo annidato) per raccogliere tutti i dati necessari (posizione re, numero pedine, mobilità, ecc.).

## 5. Move Ordering (Ordinamento Mosse)
L'efficacia della potatura Alfa-Beta dipende drasticamente dall'ordine in cui vengono esplorate le mosse.

*   **Soluzione:**
    *   Migliorare l'ordinamento dando priorità alla mossa trovata come migliore nella profondità precedente (**Hash Move** o **Principal Variation**).
    *   Ordinare le restanti mosse: Catture > Killer Moves > Altre mosse.
