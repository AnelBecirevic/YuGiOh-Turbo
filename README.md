# Yu-Gi-Oh! Turbo

Yu-Gi-Oh! Turbo is a Java and Spring Boot web application developed as a learning project.

The project was created to apply concepts learned during my Java backend development course in a larger application. Its main focus is account management, relational database design, CRUD operations, deck and card collection management, and the integration of a PostgreSQL database with a Spring Boot application.

The application also includes a simplified browser-based Yu-Gi-Oh! duel prototype based on the original Yu-Gi-Oh! Power of Chaos games.

> **Note:** This is a non-commercial educational project. Yu-Gi-Oh! and related names, card artwork and assets belong to their respective copyright holders.

---

## Features

### Account Management

Users can:

- Register a new account
- Log in and log out
- Change their username
- Change their password
- Delete their account

Protected pages can only be accessed after authentication.

### Card Trunk

Each account has its own card collection, represented by the **Trunk**.

The Trunk stores:

- Cards owned by the player
- Quantity of each card
- Card information such as ATK, DEF, Level, Attribute and Monster Type
- Card artwork and descriptions

### Deck Editor

Players can manage their deck using cards from their Trunk.

The Deck Editor:

- Displays the player's available card collection
- Displays the current deck
- Allows cards to be added and removed
- Tracks card quantities
- Prevents the player from using more copies than they own
- Enforces a 40-card deck size for entering a duel

### Duel System

The project contains a simplified duel system against a Kaiba-inspired computer opponent.

Currently implemented mechanics include:

- 40-card decks
- Random deck shuffling
- Starting hands
- Card drawing
- Five Monster Zones
- Five Spell/Trap Zones prepared for future expansion
- Deck and Graveyard zones
- Life Points
- Turn and phase handling
- Normal Summoning
- Attack Position
- Defense Position
- Tribute Summoning
- Player-selected Tribute Monsters
- Battle calculation
- Direct attacks
- Graveyard handling
- Basic computer opponent logic
- Battle log
- Card information display

The duel system currently focuses on **Monster Cards only**. Spell and Trap Card support is planned as a possible future extension.

---

## Technologies

- Java
- Spring Boot
- Spring JDBC
- PostgreSQL
- Thymeleaf
- HTML
- CSS
- Maven
- Git
- GitHub

---

## Architecture

The application follows a layered structure to separate responsibilities.

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
PostgreSQL Database
```

### Controller Layer

Handles HTTP requests, page navigation and communication between the frontend and application logic.

### Service Layer

Contains application and business logic such as:

- Account operations
- Deck management
- Card quantity validation
- Duel rules
- Summoning
- Battle calculations
- Computer opponent decisions

### Repository Layer

Handles communication with PostgreSQL using Spring JDBC.

### Model Layer

Represents application data such as accounts, cards, decks and duel state.

---

## Database Design

PostgreSQL is used for persistent application data.

The main database entities are:

```text
Account
  |
  +---- Deck
  |       |
  |       +---- DeckCard ---- Card
  |
  +---- Trunk ------------ Card


Card
 |
 +---- MonsterCard
 |
 +---- SpellCard
 |
 +---- TrapCard
```

`Card` contains properties shared between all card types.

Card-specific information is separated into specialized tables such as `MonsterCard`, allowing the card model to be extended with additional card types without putting every possible property into a single table.

`DeckCard` and `Trunk` store quantities and create the relationships between accounts, decks and cards.

---

## Card Model

The project was designed with future card types in mind.

Instead of making the duel system exclusively dependent on monsters, a general duel card abstraction can represent different types of cards.

```text
DuelCard
   |
   +---- DuelMonsterCard
   |
   +---- Future Spell implementation
   |
   +---- Future Trap implementation
```

This allows future functionality to be added without redesigning the entire duel card model.

---

## Project Structure

A simplified overview:

```text
src/
├── main/
│   ├── java/
│   │   └── com/yugiohTurbo/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       └── service/
│   │
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   └── images/
│       │
│       ├── templates/
│       └── application.properties
│
└── test/
```

---

## What I Learned

One of the main goals of this project was moving beyond isolated exercises and combining different backend concepts in one application.

Important learning areas included:

- Designing a relational database schema
- Creating one-to-many and many-to-many style relationships
- Working with primary and foreign keys
- Connecting Java to PostgreSQL
- Writing SQL queries through Spring JDBC
- Implementing CRUD functionality
- Separating Controller, Service and Repository responsibilities
- Managing application state
- Implementing authentication and protected routes
- Designing backend logic around business rules
- Connecting backend functionality to a Thymeleaf frontend
- Using Git and GitHub throughout development

A particularly useful challenge was designing the relationship between the player's **Trunk** and **Deck**.

The Trunk represents the total number of copies a player owns, while the Deck represents how many of those copies are currently being used. The application therefore calculates how many copies remain available rather than physically moving cards between the two database structures.

---

## Development Process

The project was developed incrementally, with features separated into individual tasks and Git commits.

The general development process included:

1. Planning the application and database structure
2. Creating the PostgreSQL schema
3. Implementing account management
4. Connecting Spring Boot to PostgreSQL
5. Implementing the card collection and Trunk
6. Implementing the Deck Editor
7. Adding card artwork and frontend presentation
8. Implementing the simplified duel prototype
9. Testing and refining application behavior

GitHub Issues and commits were used to track development progress.

---

## AI Assistance

AI tools were used during development as a programming and learning assistant.

AI assistance was particularly significant during implementation of the duel system and was also used for:

- Discussing architecture and design decisions
- Debugging
- Reviewing code
- Generating implementation suggestions
- Explaining unfamiliar concepts
- Frontend/CSS experimentation

The database design, CRUD functionality, project requirements and application structure were developed as part of my coursework and learning process.

Where AI-generated or AI-assisted code was used, I reviewed and tested its integration with the existing application.

---

## Current Limitations

Yu-Gi-Oh! Turbo is a learning project and does not attempt to implement the complete Yu-Gi-Oh! ruleset.

Current limitations include:

- Only Monster Cards are playable
- Spell Cards are not yet implemented
- Trap Cards are not yet implemented
- No monster effects
- No Fusion, Ritual, Synchro, Xyz, Pendulum or Link mechanics
- Simplified computer opponent
- Simplified turn and battle logic
- No multiplayer
- No reward system after winning a duel
- Limited card pool

---

## Possible Future Improvements

Potential future additions include:

- Spell Cards
- Trap Cards
- Monster effects
- Improved opponent AI
- Duel rewards
- Additional cards
- Multiple editable decks
- More detailed turn phases
- Improved animations
- Sound effects
- Expanded testing
- Further UI styling inspired by classic Yu-Gi-Oh! games

---

## Running the Project

### Requirements

You will need:

- Java
- Maven
- PostgreSQL

### Database

Create a PostgreSQL database for the application and execute the provided database schema.

Configure the database connection in:

```text
src/main/resources/application.properties
```

Example configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/YOUR_DATABASE
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Do **not** commit real database passwords or other credentials to GitHub.

### Start the Application

Using Maven:

```bash
./mvnw spring-boot:run
```

Then open:

```text
http://localhost:8080
```

---

## Disclaimer

This project was created solely for educational and non-commercial purposes.

Yu-Gi-Oh! is the property of its respective rights holders. Card names, artwork and other Yu-Gi-Oh!-related assets used in this project are not owned by the developer.

The project is not affiliated with or endorsed by Konami.
