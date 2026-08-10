-- ============================================================
-- Yu-Gi-Oh! Turbo Database Schema
-- PostgreSQL
-- ============================================================


-- ============================================================
-- DROP EXISTING TABLES
-- Child tables must be dropped before parent tables
-- because of foreign key relationships.
-- ============================================================

DROP TABLE IF EXISTS DeckCard;
DROP TABLE IF EXISTS Trunk;
DROP TABLE IF EXISTS Deck;

DROP TABLE IF EXISTS MonsterCard;
DROP TABLE IF EXISTS SpellCard;
DROP TABLE IF EXISTS TrapCard;

DROP TABLE IF EXISTS Card;
DROP TABLE IF EXISTS Account;


-- ============================================================
-- ACCOUNT
-- Stores registered user accounts.
-- ============================================================

CREATE TABLE Account (

                         account_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                         username VARCHAR(30) NOT NULL UNIQUE,

                         password_hash VARCHAR(255) NOT NULL,

                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);


-- ============================================================
-- CARD
-- Stores information shared by every card type.
-- card_type determines whether the card is a
-- MONSTER, SPELL or TRAP.
-- ============================================================

CREATE TABLE Card (

                      card_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                      name VARCHAR(100) NOT NULL,

                      card_type VARCHAR(20) NOT NULL,

                      description TEXT,

                      image_path VARCHAR(255),

                      CONSTRAINT chk_card_type
                          CHECK (card_type IN ('MONSTER', 'SPELL', 'TRAP'))

);


-- ============================================================
-- MONSTER CARD
-- Stores information specific to monster cards.
--
-- card_id is both:
--   Primary Key
--   Foreign Key -> Card
--
-- This creates a one-to-one relationship between
-- a Card and its MonsterCard information.
-- ============================================================

CREATE TABLE MonsterCard (

                             card_id INT PRIMARY KEY,

                             attribute VARCHAR(20) NOT NULL,

                             monster_type VARCHAR(30) NOT NULL,

                             level INT NOT NULL,

                             attack INT NOT NULL,

                             defense INT NOT NULL,

                             CONSTRAINT fk_monstercard_card
                                 FOREIGN KEY (card_id)
                                     REFERENCES Card(card_id)
                                     ON DELETE CASCADE,

                             CONSTRAINT chk_monster_level
                                 CHECK (level > 0),

                             CONSTRAINT chk_monster_attack
                                 CHECK (attack >= 0),

                             CONSTRAINT chk_monster_defense
                                 CHECK (defense >= 0)

);


-- ============================================================
-- SPELL CARD
-- Stores information specific to Spell Cards.
--
-- Examples of spell_type later could include:
-- NORMAL
-- QUICK_PLAY
-- CONTINUOUS
-- EQUIP
-- FIELD
-- RITUAL
-- ============================================================

CREATE TABLE SpellCard (

                           card_id INT PRIMARY KEY,

                           spell_type VARCHAR(30) NOT NULL,

                           CONSTRAINT fk_spellcard_card
                               FOREIGN KEY (card_id)
                                   REFERENCES Card(card_id)
                                   ON DELETE CASCADE

);


-- ============================================================
-- TRAP CARD
-- Stores information specific to Trap Cards.
--
-- Examples of trap_type later could include:
-- NORMAL
-- CONTINUOUS
-- COUNTER
-- ============================================================

CREATE TABLE TrapCard (

                          card_id INT PRIMARY KEY,

                          trap_type VARCHAR(30) NOT NULL,

                          CONSTRAINT fk_trapcard_card
                              FOREIGN KEY (card_id)
                                  REFERENCES Card(card_id)
                                  ON DELETE CASCADE

);


-- ============================================================
-- DECK
-- Stores information about a player's decks.
--
-- The actual cards inside the deck are stored
-- separately in DeckCard.
-- ============================================================

CREATE TABLE Deck (

                      deck_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                      account_id INT NOT NULL,

                      deck_name VARCHAR(50) NOT NULL,

                      CONSTRAINT fk_deck_account
                          FOREIGN KEY (account_id)
                              REFERENCES Account(account_id)
                              ON DELETE CASCADE

);


-- ============================================================
-- TRUNK
-- Represents the player's entire card collection.
--
-- A player should only have one row for each card.
-- quantity records how many copies they own.
-- ============================================================

CREATE TABLE Trunk (

                       trunk_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                       account_id INT NOT NULL,

                       card_id INT NOT NULL,

                       quantity INT NOT NULL DEFAULT 1,

                       CONSTRAINT fk_trunk_account
                           FOREIGN KEY (account_id)
                               REFERENCES Account(account_id)
                               ON DELETE CASCADE,

                       CONSTRAINT fk_trunk_card
                           FOREIGN KEY (card_id)
                               REFERENCES Card(card_id),

                       CONSTRAINT uq_trunk_account_card
                           UNIQUE (account_id, card_id),

                       CONSTRAINT chk_trunk_quantity
                           CHECK (quantity > 0)

);


-- ============================================================
-- DECK CARD
-- Represents the contents of a deck.
--
-- A deck only has one row per card.
-- quantity records how many copies of that card
-- are currently inside the deck.
-- ============================================================

CREATE TABLE DeckCard (

                          deckcard_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                          deck_id INT NOT NULL,

                          card_id INT NOT NULL,

                          quantity INT NOT NULL DEFAULT 1,

                          CONSTRAINT fk_deckcard_deck
                              FOREIGN KEY (deck_id)
                                  REFERENCES Deck(deck_id)
                                  ON DELETE CASCADE,

                          CONSTRAINT fk_deckcard_card
                              FOREIGN KEY (card_id)
                                  REFERENCES Card(card_id),

                          CONSTRAINT uq_deckcard_deck_card
                              UNIQUE (deck_id, card_id),

                          CONSTRAINT chk_deckcard_quantity
                              CHECK (quantity > 0)

);


-- ============================================================
-- DUMMY ACCOUNT DATA
--
-- These passwords are only placeholder test data.
-- Real accounts should contain properly hashed passwords.
-- ============================================================

INSERT INTO Account
(account_id, username, password_hash)
    OVERRIDING SYSTEM VALUE
VALUES
    (1, 'Yugi', 'password123'),
    (2, 'Kaiba', 'password123');


-- ============================================================
-- DUMMY CARD DATA
-- Shared card information.
-- ============================================================

INSERT INTO Card
(card_id, name, card_type, description, image_path)
    OVERRIDING SYSTEM VALUE
VALUES

    (
        1,
        'Dark Magician',
        'MONSTER',
        'The ultimate wizard in terms of attack and defense.',
        'images/dark_magician.png'
    ),

    (
        2,
        'Blue-Eyes White Dragon',
        'MONSTER',
        'This legendary dragon is a powerful engine of destruction.',
        'images/blue_eyes_white_dragon.png'
    ),

    (
        3,
        'Celtic Guardian',
        'MONSTER',
        'An elf who learned to wield a sword.',
        'images/celtic_guardian.png'
    ),

    (
        4,
        'Battle Ox',
        'MONSTER',
        'A monster with tremendous attack power.',
        'images/battle_ox.png'
    ),

    (
        5,
        'Hitotsu-Me Giant',
        'MONSTER',
        'A one-eyed giant that attacks with brute strength.',
        'images/hitotsu_me_giant.png'
    );


-- ============================================================
-- DUMMY MONSTER CARD DATA
-- Monster-specific properties.
-- ============================================================

INSERT INTO MonsterCard
(card_id, attribute, monster_type, level, attack, defense)
VALUES

    (1, 'DARK',  'Spellcaster', 7, 2500, 2100),

    (2, 'LIGHT', 'Dragon',      8, 3000, 2500),

    (3, 'EARTH', 'Warrior',     4, 1400, 1200),

    (4, 'EARTH', 'Beast-Warrior', 4, 1700, 1000),

    (5, 'EARTH', 'Beast-Warrior', 4, 1200, 1000);


-- ============================================================
-- DUMMY DECK
-- ============================================================

INSERT INTO Deck
(deck_id, account_id, deck_name)
    OVERRIDING SYSTEM VALUE
VALUES
    (1, 1, 'Yugi Starter Deck');


-- ============================================================
-- DUMMY TRUNK
-- Yugi's owned cards.
-- ============================================================

INSERT INTO Trunk
(trunk_id, account_id, card_id, quantity)
    OVERRIDING SYSTEM VALUE
VALUES

    (1, 1, 1, 1),

    (2, 1, 2, 1),

    (3, 1, 3, 3),

    (4, 1, 4, 2),

    (5, 1, 5, 2);


-- ============================================================
-- DUMMY DECK CONTENTS
-- ============================================================

INSERT INTO DeckCard
(deckcard_id, deck_id, card_id, quantity)
    OVERRIDING SYSTEM VALUE
VALUES

    (1, 1, 1, 1),

    (2, 1, 3, 2),

    (3, 1, 4, 2);


-- ============================================================
-- END OF SCHEMA
-- ============================================================