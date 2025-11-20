# Architecture Diagrams

This document provides comprehensive architecture diagrams for the AI-Powered RPG Game project, including both the main implementation and solutions folder.

## Table of Contents
1. [High-Level System Architecture](#high-level-system-architecture)
2. [Design Patterns Overview](#design-patterns-overview)
3. [Class Relationships Diagram](#class-relationships-diagram)
4. [Component Interaction Flow](#component-interaction-flow)
5. [Player Strategy Pattern Detail](#player-strategy-pattern-detail)
6. [Command Pattern Detail](#command-pattern-detail)
7. [Spring AI Integration](#spring-ai-integration)

---

## High-Level System Architecture

```mermaid
graph TB
    subgraph "Application Layer"
        GA[GameApplication<br/>Spring Boot Entry Point]
        GAS[GameApplicationSolution<br/>Complete Implementation]
    end
    
    subgraph "Controller Layer"
        GC[GameController<br/>Game Orchestration]
        GCS[GameControllerSolution<br/>Complete Implementation]
    end
    
    subgraph "Player Layer"
        P[Player Interface<br/>Strategy Pattern]
        HP[HumanPlayer]
        RBP[RuleBasedPlayer]
        LLMP[LLMPlayer]
        LLMPS[LLMPlayerSolution]
    end
    
    subgraph "Model Layer"
        C[Character<br/>Builder Pattern]
        CS[CharacterStats]
        CT[CharacterType]
        GS[GameState]
    end
    
    subgraph "Command Layer"
        GCmd[GameCommand Interface]
        AC[AttackCommand]
        HC[HealCommand]
        CI[CommandInvoker<br/>Command History]
    end
    
    subgraph "Strategy Layer"
        AS[AttackStrategy Interface]
        DS[DefenseStrategy Interface]
        MAS[MeleeAttackStrategy]
        RgAS[RangedAttackStrategy]
        MgAS[MagicAttackStrategy]
        SDS[StandardDefenseStrategy]
        HADS[HeavyArmorDefenseStrategy]
    end
    
    subgraph "Factory Layer"
        CF[CharacterFactory<br/>Factory Pattern]
    end
    
    subgraph "Template Layer"
        BS[BattleSequence<br/>Template Method]
        SBS[StandardBattleSequence]
        PAS[PowerAttackSequence]
    end
    
    subgraph "Configuration Layer"
        CCC[ChatClientConfig<br/>Spring Configuration]
        OAI[OpenAI ChatClient]
        ANT[Anthropic ChatClient]
    end
    
    subgraph "External Services"
        OpenAI[OpenAI API<br/>GPT-5]
        Anthropic[Anthropic API<br/>Claude Sonnet 4.5]
    end
    
    GA --> GC
    GAS --> GCS
    GC --> P
    GCS --> P
    GC --> CI
    GCS --> CI
    
    P --> HP
    P --> RBP
    P --> LLMP
    P --> LLMPS
    
    LLMP --> OAI
    LLMPS --> OAI
    LLMP --> ANT
    LLMPS --> ANT
    
    P --> GCmd
    GCmd --> AC
    GCmd --> HC
    CI --> GCmd
    
    GC --> C
    GCS --> C
    C --> AS
    C --> DS
    AS --> MAS
    AS --> RgAS
    AS --> MgAS
    DS --> SDS
    DS --> HADS
    
    CF --> C
    GA --> CF
    GAS --> CF
    
    C --> CS
    C --> CT
    
    OAI --> OpenAI
    ANT --> Anthropic
    CCC --> OAI
    CCC --> ANT
    
    style GA fill:#e1f5ff
    style GAS fill:#c8e6c9
    style GC fill:#e1f5ff
    style GCS fill:#c8e6c9
    style LLMP fill:#e1f5ff
    style LLMPS fill:#c8e6c9
```

---

## Design Patterns Overview

```mermaid
graph LR
    subgraph "Strategy Pattern"
        SP[Player Interface]
        SP1[HumanPlayer]
        SP2[RuleBasedPlayer]
        SP3[LLMPlayer]
        SP4[AttackStrategy]
        SP5[DefenseStrategy]
    end
    
    subgraph "Command Pattern"
        CP[GameCommand Interface]
        CP1[AttackCommand]
        CP2[HealCommand]
        CP3[CommandInvoker]
    end
    
    subgraph "Factory Pattern"
        FP[CharacterFactory]
        FP1[createWarrior]
        FP2[createMage]
        FP3[createArcher]
        FP4[createRogue]
    end
    
    subgraph "Builder Pattern"
        BP[Character.Builder]
        BP1[name]
        BP2[type]
        BP3[stats]
        BP4[attackStrategy]
        BP5[defenseStrategy]
        BP6[build]
    end
    
    subgraph "Template Method Pattern"
        TP[BattleSequence]
        TP1[executeTurn]
        TP2[StandardBattleSequence]
        TP3[PowerAttackSequence]
    end
    
    subgraph "Facade Pattern"
        FCP[GameController]
    end
    
    subgraph "Mediator Pattern"
        MP[GameController]
    end
    
    subgraph "Adapter Pattern"
        AP[LLMPlayer]
        AP1[LLM Response]
        AP2[GameCommand]
    end
    
    SP --> SP1
    SP --> SP2
    SP --> SP3
    SP --> SP4
    SP --> SP5
    
    CP --> CP1
    CP --> CP2
    CP3 --> CP
    
    FP --> FP1
    FP --> FP2
    FP --> FP3
    FP --> FP4
    
    BP --> BP1
    BP --> BP2
    BP --> BP3
    BP --> BP4
    BP --> BP5
    BP --> BP6
    
    TP --> TP1
    TP --> TP2
    TP --> TP3
    
    AP --> AP1
    AP --> AP2
    
    style SP fill:#ffccbc
    style CP fill:#c5e1a5
    style FP fill:#b3e5fc
    style BP fill:#f8bbd0
    style TP fill:#d1c4e9
    style FCP fill:#fff9c4
    style MP fill:#fff9c4
    style AP fill:#ffccbc
```

---

## Class Relationships Diagram

```mermaid
classDiagram
    class GameApplication {
        +main(String[] args)
        +run(ChatClient...)
        -createTeamConfiguration()
    }
    
    class GameController {
        -List~Character~ team1
        -List~Character~ team2
        -Map~Character,Player~ playerMap
        -CommandInvoker invoker
        -GameState gameState
        +playGame()
        -processTurn()
        -isGameOver()
    }
    
    class Player {
        <<interface>>
        +decideAction(Character, List, List, GameState) GameCommand
    }
    
    class HumanPlayer {
        -Scanner scanner
        +decideAction() GameCommand
    }
    
    class RuleBasedPlayer {
        +decideAction() GameCommand
    }
    
    class LLMPlayer {
        -ChatClient chatClient
        -String modelName
        +decideAction() GameCommand
        -buildPrompt() String
        -findCharacterByName() Character
    }
    
    class Character {
        -String name
        -CharacterType type
        -CharacterStats stats
        -AttackStrategy attackStrategy
        -DefenseStrategy defenseStrategy
        +attack(Character) int
        +defend(int) int
        +takeDamage(int)
        +heal(int)
        +Builder builder()
    }
    
    class CharacterBuilder {
        -String name
        -CharacterType type
        -CharacterStats stats
        -AttackStrategy attackStrategy
        -DefenseStrategy defenseStrategy
        +name(String) Builder
        +type(CharacterType) Builder
        +stats(CharacterStats) Builder
        +attackStrategy(AttackStrategy) Builder
        +defenseStrategy(DefenseStrategy) Builder
        +build() Character
    }
    
    class CharacterFactory {
        +createWarrior(String) Character
        +createMage(String) Character
        +createArcher(String) Character
        +createRogue(String) Character
        +createCharacter(String, CharacterType) Character
    }
    
    class GameCommand {
        <<interface>>
        +execute()
        +undo()
        +getDescription() String
    }
    
    class AttackCommand {
        -Character attacker
        -Character target
        -int actualHealthLost
        +execute()
        +undo()
    }
    
    class HealCommand {
        -Character target
        -int amount
        -int actualHealingDone
        +execute()
        +undo()
    }
    
    class CommandInvoker {
        -Stack~GameCommand~ commandHistory
        +executeCommand(GameCommand)
        +undoLastCommand()
        +getCommandHistory() List
    }
    
    class AttackStrategy {
        <<interface>>
        +calculateDamage(Character, Character) int
    }
    
    class DefenseStrategy {
        <<interface>>
        +calculateDamageReduction(Character, int) int
    }
    
    class CharacterStats {
        +int health
        +int maxHealth
        +int attackPower
        +int defense
        +int mana
        +int maxMana
        +withHealth(int) CharacterStats
        +withMana(int) CharacterStats
    }
    
    class GameState {
        +int turnNumber
        +int roundNumber
        +boolean canUndo
        +int commandHistorySize
        +nextTurn() GameState
        +nextRound() GameState
    }
    
    class ChatClientConfig {
        +openAiChatClient(OpenAiChatModel) ChatClient
        +anthropicChatClient(AnthropicChatModel) ChatClient
    }
    
    GameApplication --> GameController
    GameApplication --> CharacterFactory
    GameController --> Player
    GameController --> Character
    GameController --> CommandInvoker
    GameController --> GameState
    
    Player <|.. HumanPlayer
    Player <|.. RuleBasedPlayer
    Player <|.. LLMPlayer
    
    Player --> GameCommand
    GameCommand <|.. AttackCommand
    GameCommand <|.. HealCommand
    
    CommandInvoker --> GameCommand
    
    Character --> CharacterStats
    Character --> AttackStrategy
    Character --> DefenseStrategy
    Character --> CharacterBuilder
    
    CharacterFactory --> Character
    CharacterFactory --> AttackStrategy
    CharacterFactory --> DefenseStrategy
    
    AttackCommand --> Character
    HealCommand --> Character
    
    LLMPlayer --> ChatClientConfig
```

---

## Component Interaction Flow

```mermaid
sequenceDiagram
    participant App as GameApplication
    participant GC as GameController
    participant P as Player
    participant LLM as LLMPlayer
    participant CC as ChatClient
    participant API as LLM API
    participant Cmd as GameCommand
    participant CI as CommandInvoker
    participant Char as Character
    participant AS as AttackStrategy
    
    App->>GC: Create GameController(teams, players)
    App->>GC: playGame()
    
    loop Game Loop
        GC->>P: decideAction(character, allies, enemies, state)
        
        alt Human Player
            P->>P: Read from console
        else Rule-Based Player
            P->>P: Apply if-then rules
        else LLM Player
            LLM->>LLM: buildPrompt()
            LLM->>CC: prompt().user(prompt).call()
            CC->>API: Send prompt
            API-->>CC: JSON response
            CC-->>LLM: Decision object
            LLM->>LLM: Convert Decision to GameCommand
        end
        
        P-->>GC: GameCommand
        
        GC->>CI: executeCommand(command)
        CI->>Cmd: execute()
        
        alt Attack Command
            Cmd->>Char: attack(target)
            Char->>AS: calculateDamage(attacker, target)
            AS-->>Char: damage amount
            Char->>Char: target.takeDamage(damage)
        else Heal Command
            Cmd->>Char: heal(amount)
        end
        
        CI->>CI: Push command to history
        CI-->>GC: Command executed
        
        GC->>GC: Update GameState
        GC->>GC: Check win condition
    end
    
    GC->>App: Game complete
```

---

## Player Strategy Pattern Detail

```mermaid
graph TB
    subgraph "Strategy Interface"
        PI[Player Interface<br/>decideAction]
    end
    
    subgraph "Concrete Strategies"
        HP[HumanPlayer<br/>Console Input]
        RBP[RuleBasedPlayer<br/>If-Then Rules]
        LLMP[LLMPlayer<br/>LLM-Based Decisions]
        LLMPS[LLMPlayerSolution<br/>Complete Implementation]
    end
    
    subgraph "Rule-Based Logic"
        R1[Rule 1: Self HP < 30%<br/>→ Heal Self]
        R2[Rule 2: Ally HP < 20%<br/>→ Heal Weakest Ally]
        R3[Rule 3: Otherwise<br/>→ Attack Weakest Enemy]
    end
    
    subgraph "LLM Decision Process"
        P1[Build Prompt<br/>Character Status<br/>Allies Status<br/>Enemies Status<br/>Available Actions]
        P2[Call LLM API<br/>ChatClient.prompt]
        P3[Parse JSON Response<br/>Decision Record]
        P4[Convert to GameCommand<br/>AttackCommand or HealCommand]
    end
    
    subgraph "Output"
        GC[GameCommand<br/>AttackCommand or HealCommand]
    end
    
    PI --> HP
    PI --> RBP
    PI --> LLMP
    PI --> LLMPS
    
    RBP --> R1
    RBP --> R2
    RBP --> R3
    
    LLMP --> P1
    LLMPS --> P1
    P1 --> P2
    P2 --> P3
    P3 --> P4
    
    HP --> GC
    R1 --> GC
    R2 --> GC
    R3 --> GC
    P4 --> GC
    
    style PI fill:#ffccbc
    style HP fill:#e1f5ff
    style RBP fill:#e1f5ff
    style LLMP fill:#e1f5ff
    style LLMPS fill:#c8e6c9
    style GC fill:#c5e1a5
```

---

## Command Pattern Detail

```mermaid
graph TB
    subgraph CI_Group["Command Interface"]
        GCI[GameCommand Interface<br/>execute<br/>undo<br/>getDescription]
    end
    
    subgraph CC_Group["Concrete Commands"]
        AC[AttackCommand<br/>attacker: Character<br/>target: Character<br/>actualHealthLost: int]
        HC[HealCommand<br/>target: Character<br/>amount: int<br/>actualHealingDone: int]
    end
    
    subgraph INV_Group["Command Invoker"]
        CI[CommandInvoker<br/>commandHistory: Stack]
        CH[Command History<br/>Maintains execution order]
    end
    
    subgraph EXEC_Group["Execution Flow"]
        E1[1. Execute Command]
        E2[2. Store State for Undo]
        E3[3. Apply Changes to Character]
        E4[4. Push to History]
    end
    
    subgraph UNDO_Group["Undo Flow"]
        U1[1. Pop from History]
        U2[2. Call undo on Command]
        U3[3. Restore Previous State]
    end
    
    GCI --> AC
    GCI --> HC
    
    CI --> CH
    CI --> E1
    
    E1 --> E2
    E2 --> E3
    E3 --> E4
    E4 --> CH
    
    CH --> U1
    U1 --> U2
    U2 --> U3
    
    AC --> E1
    HC --> E1
    AC --> U2
    HC --> U2
    
    style GCI fill:#ffccbc
    style AC fill:#c5e1a5
    style HC fill:#c5e1a5
    style CI fill:#b3e5fc
    style CH fill:#fff9c4
```

---

## Spring AI Integration

```mermaid
graph TB
    subgraph SB_Group["Spring Boot Application"]
        SB[Spring Boot<br/>SpringBootApplication]
    end
    
    subgraph CONFIG_Group["Configuration Layer"]
        CCC[ChatClientConfig<br/>Configuration]
        OAI_B[openAiChatClient Bean]
        ANT_B[anthropicChatClient Bean]
    end
    
    subgraph AI_Group["Spring AI Framework"]
        OAI_M[OpenAiChatModel<br/>Auto-configured]
        ANT_M[AnthropicChatModel<br/>Auto-configured]
        CC[ChatClient<br/>Abstraction Layer]
    end
    
    subgraph PLAYER_Group["LLM Player Implementation"]
        LLMP[LLMPlayer<br/>Student Implementation]
        LLMPS[LLMPlayerSolution<br/>Complete Solution]
    end
    
    subgraph PROMPT_Group["Prompt Engineering"]
        PE[buildPrompt Method<br/>Character Status<br/>Allies Status<br/>Enemies Status<br/>Available Actions<br/>Strategic Guidance<br/>JSON Format Spec]
    end
    
    subgraph RESPONSE_Group["Response Processing"]
        RP[Parse JSON Response<br/>Decision Record<br/>action: String<br/>target: String<br/>reasoning: String]
        CONVERT[Convert to GameCommand<br/>AttackCommand or HealCommand]
    end
    
    subgraph API_Group["External APIs"]
        OAI_API[OpenAI API<br/>GPT-5]
        ANT_API[Anthropic API<br/>Claude Sonnet 4.5]
    end
    
    SB --> CCC
    CCC --> OAI_B
    CCC --> ANT_B
    
    OAI_B --> OAI_M
    ANT_B --> ANT_M
    
    OAI_M --> CC
    ANT_M --> CC
    
    LLMP --> CC
    LLMPS --> CC
    
    LLMP --> PE
    LLMPS --> PE
    
    PE --> CC
    CC --> OAI_API
    CC --> ANT_API
    
    OAI_API -.->|Response| CC
    ANT_API -.->|Response| CC
    
    CC --> RP
    RP --> CONVERT
    
    style SB fill:#e1f5ff
    style CCC fill:#c8e6c9
    style CC fill:#b3e5fc
    style LLMP fill:#e1f5ff
    style LLMPS fill:#c8e6c9
    style PE fill:#fff9c4
    style RP fill:#ffccbc
```

---

## Solutions Folder Architecture

```mermaid
graph TB
    subgraph "Solutions Package"
        GAS[GameApplicationSolution<br/>Complete Spring Boot App]
        GCS[GameControllerSolution<br/>Complete Game Loop]
        LLMPS[LLMPlayerSolution<br/>Complete LLM Integration]
    end
    
    subgraph "Main Implementation"
        GA[GameApplication<br/>Student TODO]
        GC[GameController<br/>Student TODO]
        LLMP[LLMPlayer<br/>Student TODO]
    end
    
    subgraph "Shared Components"
        P[Player Interface]
        C[Character]
        CF[CharacterFactory]
        CI[CommandInvoker]
        GCmd[GameCommand]
    end
    
    GAS -.->|Reference| GA
    GCS -.->|Reference| GC
    LLMPS -.->|Reference| LLMP
    
    GAS --> GCS
    GAS --> CF
    GAS --> P
    
    GCS --> P
    GCS --> CI
    GCS --> C
    
    LLMPS --> P
    LLMPS --> GCmd
    
    GA --> GC
    GC --> P
    LLMP --> P
    
    style GAS fill:#c8e6c9
    style GCS fill:#c8e6c9
    style LLMPS fill:#c8e6c9
    style GA fill:#ffcdd2
    style GC fill:#ffcdd2
    style LLMP fill:#ffcdd2
```

---

## Data Flow Diagram

```mermaid
flowchart TD
    Start([Game Starts]) --> Init[Initialize Teams<br/>CharacterFactory]
    Init --> Setup[Map Characters to Players<br/>HumanPlayer, RuleBasedPlayer, LLMPlayer]
    Setup --> Loop[Game Loop Begins]
    
    Loop --> Check{Game Over?}
    Check -->|Yes| End([Game Ends])
    Check -->|No| Turn[Process Character Turn]
    
    Turn --> GetPlayer[Get Player for Character]
    GetPlayer --> Decide{Player Type?}
    
    Decide -->|Human| HumanInput[Read Console Input]
    Decide -->|Rule-Based| Rules[Apply If-Then Rules]
    Decide -->|LLM| LLMProcess[Build Prompt → Call API → Parse Response]
    
    HumanInput --> CreateCmd[Create GameCommand]
    Rules --> CreateCmd
    LLMProcess --> CreateCmd
    
    CreateCmd --> Execute[CommandInvoker.executeCommand]
    Execute --> Apply[Apply Changes to Characters]
    Apply --> Update[Update GameState]
    Update --> Next[Next Character/Team]
    Next --> Loop
    
    style Start fill:#c8e6c9
    style End fill:#ffcdd2
    style LLMProcess fill:#fff9c4
    style Execute fill:#b3e5fc
```

---

## Package Structure

```
edu.trincoll.game
├── GameApplication.java (Main Entry Point)
├── builder/ (Builder Pattern - if implemented)
├── command/
│   ├── GameCommand.java (Interface)
│   ├── AttackCommand.java
│   ├── HealCommand.java
│   └── CommandInvoker.java
├── config/
│   └── ChatClientConfig.java (Spring Configuration)
├── controller/
│   └── GameController.java (Game Orchestration)
├── factory/
│   └── CharacterFactory.java (Factory Pattern)
├── model/
│   ├── Character.java (Builder Pattern)
│   ├── CharacterStats.java (Immutable Record)
│   └── CharacterType.java (Enum)
├── player/
│   ├── Player.java (Strategy Interface)
│   ├── HumanPlayer.java
│   ├── RuleBasedPlayer.java
│   ├── LLMPlayer.java
│   └── GameState.java (Immutable Record)
├── strategy/
│   ├── AttackStrategy.java (Functional Interface)
│   ├── DefenseStrategy.java (Functional Interface)
│   ├── MeleeAttackStrategy.java
│   ├── RangedAttackStrategy.java
│   ├── MagicAttackStrategy.java
│   ├── StandardDefenseStrategy.java
│   └── HeavyArmorDefenseStrategy.java
└── template/
    ├── BattleSequence.java (Template Method)
    ├── StandardBattleSequence.java
    └── PowerAttackSequence.java

edu.trincoll.solutions
├── GameApplicationSolution.java
├── GameControllerSolution.java
└── LLMPlayerSolution.java
```

---

## Design Patterns Summary

| Pattern | Location | Purpose |
|---------|----------|---------|
| **Strategy** | `player/Player.java` | Different decision-making algorithms (Human, Rule-based, LLM) |
| **Strategy** | `strategy/AttackStrategy.java` | Different attack calculation methods |
| **Strategy** | `strategy/DefenseStrategy.java` | Different defense calculation methods |
| **Command** | `command/GameCommand.java` | Encapsulate actions as objects, support undo |
| **Factory** | `factory/CharacterFactory.java` | Create pre-configured characters |
| **Builder** | `model/Character.Builder` | Construct complex Character objects |
| **Template Method** | `template/BattleSequence.java` | Define battle sequence skeleton |
| **Facade** | `controller/GameController.java` | Simplify complex game loop |
| **Mediator** | `controller/GameController.java` | Coordinate between components |
| **Adapter** | `player/LLMPlayer.java` | Adapt LLM responses to GameCommands |

---

## Notes

- **Main Implementation**: Files in `edu.trincoll.game` contain TODOs for students to implement
- **Solutions**: Files in `edu.trincoll.solutions` contain complete reference implementations
- **Spring AI**: Integration uses Spring AI's ChatClient abstraction for multiple LLM providers
- **Immutability**: `CharacterStats` and `GameState` use Java records for immutability
- **Functional Interfaces**: Strategy interfaces are functional, allowing lambda implementations

