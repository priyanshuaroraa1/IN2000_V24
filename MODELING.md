# Modellering av funksjonalitet i PLASK
Dette dokumentet tar for seg noe av hovedfunksjonaliteten i appen PLASK.


## Funksjonelle krav
Utgangspunktet for denne modelleringen er noen av de viktigste funksjonelle kravene til appen. Disse kravene er utviklet fra de viktigste brukerhistoriene som ble identifisert under tidlig datainnsamling, og har dannet kjernen av funksjonalitet for appen. Disse kravene er ikke sortert etter noen prioritering, men har dannet utgangspunktet for to Use caser som dekker hovedfunksjonaliteten til appen.


### Viktigste funksjonelle krav
- Appen skal vise et kart over badeplasser i Norge.
- Brukeren skal kunne se aktive farevarsler for badeplassene.
- En badeplass sitt farenivå skal representeres med farge i Markøren i kartet
- Brukeren skal kunne se temperatur i vannet, bølgehøyde og -retning ved en badeplass.
- Brukeren skal kunne se været ved badeplassen nå.
- Brukeren skal kunne lagre favorittbadeplassene sine.
- Brukeren skal kunne se bilde av badeplassene
- Det skal være mulig å søke etter badeplasser både på navn, sted og område.

### Use case 
Fra kravene identifisert over, har vi identifisert to Use-caser som dekker flere av hovedfunksjonene i appen.

### Use case-diagram

### Sekvensdiagram

### Klassediagram

## Use case-diagram
Dette diagrammet viser ulike use caser i appen. 

```mermaid```
flowchart LR
%% Nodes

    A{User}
    subgraph System
    B(Legge til og fjerne favorittbadeplass)
    C(Søke etter badeplass)
    F(Finne badeplass i kartet)
    D(Se på favorittbadeplasser)
    E(Sjekke badevettreglene)
    G(Se badetemperatur)
    H(Se værmelding)
    I(Se bilde av badeplass)
    J(Se tilgjengelighetsinformasjon)
    end

%% Edge connections between nodes
    A --> C --> F
    A --> D -->F
    A --> E
    A --> F -- include --> G
    F -- include --> B
    F -- include --> H
    F -- include --> I
    F -- include --> J


%% Individual node styling. Try the visual editor toolbar for easier styling!
    style A color:#FFFFFF, stroke:#2962FF, fill:#2962FF
```

%% Individual node styling. Try the visual editor toolbar for easier styling!
    style A color:#FFFFFF, stroke:#2962FF, fill:#2962FF
    
%% You can add notes with two "%" signs in a row!

# Use-case 1 - Finne badeplass i Drøbak
Dette use-caset bekriver en bruker som er på dagstur til Drøbak, og ønsker å finne den badeplassen i Drøbak som frister mest å bade ved.

**Navn**: Finn badeplass i Drøbak
**Aktør**: Bruker
**Prebetingelser**: Internettilkobling
**Postbetingelser**: *Ingen*

### Hovedflyt
1. Åpne appen
2. Skrive "Drø" inn i søkefeltet
3. Klikke på "Badeparken i Drøbak"
4. Se på værvarsel og bilde
5. Klikke på en annen badeplass i nærheten
6. Se på værvarsel og bilde

### Alternativ flyt
4.1 Det er et farevarsel for Badeparken i Drøbak
4.2 Klikker på farvarselsymbolet
4.3 Ser at farlige værfohold gjør det lite attraktivt å bade. Velger å ikke bade i Drøbak på denne dagsturen

## Use case-diagram

```mermaid
flowchart TD
%% Nodes
    A(Start)
    B(Åpne appen)
    C(Skrive 'Drø' inn i søkefeltet)
    D(Klikke på en badeplass i søkemenyen)
    E(Se åpå værvarsel og bilde)
    F(Klikke på en annen badeplass i kartet)
    G(Klikke på farevarsel)
    H(Slutt)

%% Edge connections between nodes
    A --> B --> C --> D --> E 
    E --> F --> E
    E --> G
    G -- Velger å bade --> H
    G -- Velger å ikke bade --> H

```

## Sekvensdiagram

Dette sekvensdiagrammet beskriver samhandlingen mellom Bruker, UI og ViewModel, samt hvordan ViewModel henter data fra Repository og DataSource. I sekvensdiagrammet er det ikke spesifisert både LocationForecast og OceanForecast som Repository og DataSource, ettersom begge disse er implementert relativt likt. Sekvensdiagrammet inkluderer ikke prosesser som skjer ved oppstart av appen, som f.eks. kall på MetAlertRepository og -DataSource, men har som forutsetning at dette allerede er oppdatert i HomeState.

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant ViewModel
    participant Repository
    participant DataSource 

    loop Søke etter, og velge, badeplass
    loop Skrive i søkefeltet
    User->> UI: Skrive et symbol i søkefeltet
    UI  ->> ViewModel: Oppdatere HomeState
    ViewModel->>UI: UI observerer endringer i HomeState
    UI->>User: Tegner Composables på nytt med endringer
    end
    User->>UI: Klikke på en badeplass i søkeforslagene
    UI->>ViewModel: Kalle på onSearchbarSelectSwimspot()
    ViewModel->>UI: Oppdaterer SelectedSwimspot i HomeState
    ViewModel->>Repository: Etterspørre data fra repository
    Repository->>DataSource: Etterspørre data fra DataSource
    DataSource->>Repository: Returnere LocationForecast-objekt
    Repository->>ViewModel: Oppdatere LocationForecastUiState
    ViewModel->>UI: UI observerer endringer i LocationForecastUiState
    UI->>User: Tegner Composables på nytt
    end
    alt Farevarsel
    User->>UI: Klikker på farevarsel
    UI->>ViewModel: Oppdaterer HomeState
    ViewModel->>UI: UI observerer endringene i HomeState
    UI->>User: Tegner Composables på nytt
    end
```





# Use-case 2 - Lagre Huk som favoritt
Dette use-caset beskriver en bruker som elsker å bade ved Huk-badeplass, og derfor ønsker å lagre Huk som favoritt.

**Navn**: Lagre Huk som favoritt
**Aktør**: Bruker
**Prebetingelser**: Internettilkobling
**Postbetingelser**: Huk er lagt til i listen over favorittbadeplasser

### Hovedflyt

 1. Åpne appen
 2. Navigere til Huk i kartet
 3. Klikke på *Markøren* til Huk badeplass
 4. Klikke på *stjernen* ved siden av navnet til badeplassen
 5. Klikke på *Favoritter* i navigasjonsmenyen
 6. Ser at Huk er sagret som favoritt

### Alternativ flyt 1
2.1 Skriver "Huk" i søkefeltet
2.2 Klikker på Huk i søkemenyen.

### Alternativ flyt 2
4.1 Huk er allerede markert med stjerne


## Use case-diagram

```mermaid
flowchart  TD

%% Nodes
A(Start)
B(Åpne appen)
C(Navigere til Huk i kartet)
I(Skrive inn 'Huk' i søkefeltet)
J(Trykke på 'Huk' i søkemenyen)
D(Klikke på Markøren til Huk badeplass)
K(Se info om Huk badeplass)
E(Klikke på stjernen ved siden av navnet til badeplassen)
F(Klikke på Favoritter i navigasjonsmenyen)
G(Klikke på farevarsel)
H(Slutt)

%% Edge connections between nodes
A  -->  B  -->  C  -->  D  -->  K  -->  E  -->  F  -->  G
B  -->  I  -->  J  -->  K
K  -- Huk er allerede markert med stjerne -->
G  -- Huk er lagret som favoritt i listen -->  H
```


## Sekvensdiagram

På samme måte som tidligere, beskriver dette sekvensdiagrammet samhandlingen mellom Bruker, UI og ViewModel. I tillegg vises hvordan ViewModel henter data fra Repository, DataSource og Database. Implementasjonen av databasen er i dette diagrammet abstrahert bort, men i hovedsak kommuniserer ViewModel med et repository, som igjen kommuniserer med et DataAccessObject (DAO), som kommuniserer med databasen.

```mermaid
sequenceDiagram
    actor User
    participant UI
    participant ViewModel
    participant Repository
    participant DataSource 
    participant Database

    %% Navigere til Huk i kartet
    %% Klikke på Markøren til Huk badeplass
    %% Klikke på stjernen ved siden av navnet til badeplassen
    %% Klikke på Favoritter i navigasjonsmenyen
    %% Ser at Huk er sagret som favoritt

    loop Navigere i kartet
    User->>UI: Navigere i kartet
    UI->>UI: Oppdatere cameraPositionState
    UI->>User: Composables tegnes på nytt
    end

    loop Velge badeplass i kartet
    User->>UI: Klikke på en badeplass-markør i kartet
    UI->>ViewModel: Kalle på onSwimspotPinClick()
    ViewModel->>UI: Oppdaterer SelectedSwimspot i HomeState
    ViewModel->>Repository: Etterspørre data fra repository
    Repository->>DataSource: Etterspørre data fra DataSource
    DataSource->>Repository: Returnere LocationForecast-objekt
    Repository->>ViewModel: Oppdatere LocationForecastUiState
    ViewModel->>UI: UI observerer endringer i LocationForecastUiState
    UI->>User: Tegner Composables på nytt
    end

    User->>UI: Klikke på stjernen ved siden av navnet
    UI->>ViewModel: Kalle på onFavouriteClick()
    ViewModel->>Database: Oppdatere database
    Database->>ViewModel: ViewModelen observerer databasen som Flow
    ViewModel->>UI: Tegne Markør i kartet på nytt

    UI->>UI: Navigere til FavoritesScreen
    UI-->>ViewModel: Opprette instans av FavoritesViewModel (om nødvendig)
    ViewModel->>Database: Etterspørre favoriserte badeplasser
    Database->>ViewModel: Returnere liste av Swimspot-objekter
    ViewModel->>UI: UI observerer endringer i FavoritesState
    UI->>User: Tegner Composables.
```

