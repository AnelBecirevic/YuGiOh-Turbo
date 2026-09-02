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
-- Stores registered player accounts.
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
-- ============================================================

CREATE TABLE Card (

                      card_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                      name VARCHAR(100) NOT NULL UNIQUE,

                      card_type VARCHAR(20) NOT NULL,

                      description TEXT,

                      image_path VARCHAR(255),

                      CONSTRAINT chk_card_type
                          CHECK (card_type IN ('MONSTER', 'SPELL', 'TRAP'))

);


-- ============================================================
-- MONSTER CARD
-- Stores properties specific to Monster Cards.
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
-- Reserved for future Spell Card functionality.
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
-- Reserved for future Trap Card functionality.
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
-- Stores player-owned decks.
-- The contents are stored separately in DeckCard.
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
-- Represents all cards owned by a player.
-- One row represents one card type owned by one account.
-- quantity stores the number of copies owned.
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
-- Represents the cards contained inside a deck.
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
-- CARD CATALOGUE
-- Shared information for the currently implemented cards.
-- ============================================================

INSERT INTO Card
(card_id, name, card_type, description, image_path)
    OVERRIDING SYSTEM VALUE
VALUES

-- ============================================================
-- YUGI / PLAYER CARDS
-- ============================================================

(
    1,
    'Dark Magician',
    'MONSTER',
    'An ultimate wizard possessing exceptional attack and defense.',
    'images/dark_magician.png'
),

(
    2,
    'Gaia the Fierce Knight',
    'MONSTER',
    'A mounted knight who charges into battle with tremendous speed and power.',
    'images/gaia_the_fierce_knight.png'
),

(
    3,
    'Summoned Skull',
    'MONSTER',
    'A fiend that commands dark powers and possesses tremendous strength.',
    'images/summoned_skull.png'
),

(
    4,
    'Curse of Dragon',
    'MONSTER',
    'A wicked dragon that draws upon dark forces to unleash powerful attacks.',
    'images/curse_of_dragon.png'
),

(
    5,
    'Celtic Guardian',
    'MONSTER',
    'A sword-wielding elf known for confusing opponents with swift attacks.',
    'images/celtic_guardian.png'
),

(
    6,
    'Feral Imp',
    'MONSTER',
    'A mischievous fiend that lurks in darkness and waits for a chance to strike.',
    'images/feral_imp.png'
),

(
    7,
    'Giant Soldier of Stone',
    'MONSTER',
    'A gigantic warrior formed from stone whose strength can shake the earth.',
    'images/giant_soldier_of_stone.png'
),

(
    8,
    'Mystical Elf',
    'MONSTER',
    'A gentle elf whose magical power provides her with exceptional defense.',
    'images/mystical_elf.png'
),

(
    9,
    'Beaver Warrior',
    'MONSTER',
    'A skilled forest warrior whose small size hides surprising fighting ability.',
    'images/beaver_warrior.png'
),

(
    10,
    'Blackland Fire Dragon',
    'MONSTER',
    'A dragon that dwells in darkness and attacks its enemies with fierce power.',
    'images/blackland_fire_dragon.png'
),

(
    11,
    'Neo the Magic Swordsman',
    'MONSTER',
    'A dimensional traveler skilled in sorcery, swordsmanship and martial arts.',
    'images/neo_the_magic_swordsman.png'
),

(
    12,
    'Silver Fang',
    'MONSTER',
    'A beautiful silver-furred wolf that becomes vicious when drawn into battle.',
    'images/silver_fang.png'
),

(
    13,
    'Gazelle the King of Mythical Beasts',
    'MONSTER',
    'A mythical beast whose incredible speed makes it difficult for enemies to follow.',
    'images/gazelle_the_king_of_mythical_beasts.png'
),

(
    14,
    'Mammoth Graveyard',
    'MONSTER',
    'A mammoth that fiercely protects the resting place of its ancient herd.',
    'images/mammoth_graveyard.png'
),


-- ============================================================
-- KAIBA / OPPONENT CARDS
-- ============================================================

(
    15,
    'Blue-Eyes White Dragon',
    'MONSTER',
    'A legendary dragon possessing overwhelming power and incredible destructive force.',
    'images/blue-eyes_white_dragon.png'
),

(
    16,
    'Judge Man',
    'MONSTER',
    'A powerful warrior who swings a massive club and refuses to surrender.',
    'images/judge_man.png'
),

(
    17,
    'Swordstalker',
    'MONSTER',
    'A fearsome warrior formed from the vengeful spirits of those lost in battle.',
    'images/swordstalker.png'
),

(
    18,
    'Saggi the Dark Clown',
    'MONSTER',
    'A mysterious clown whose strange movements make his attacks difficult to predict.',
    'images/saggi_the_dark_clown.png'
),

(
    19,
    'La Jinn the Mystical Genie of the Lamp',
    'MONSTER',
    'A powerful genie bound to a lamp and compelled to serve its master.',
    'images/la_jinn_the_mystical_genie_of_the_lamp.png'
),

(
    20,
    'Hitotsu-Me Giant',
    'MONSTER',
    'A one-eyed giant that overwhelms enemies through sheer physical strength.',
    'images/hitotsu-me_giant.png'
),

(
    21,
    'Claw Reacher',
    'MONSTER',
    'A sinister creature armed with enormous claws used to seize its opponents.',
    'images/claw_reacher.png'
),

(
    22,
    'Armored Zombie',
    'MONSTER',
    'An undead warrior protected by armor that continues fighting despite its decaying body.',
    'images/armored_zombie.png'
),

(
    23,
    'Battle Steer',
    'MONSTER',
    'A powerful beast-warrior whose enormous arms can crush even solid stone.',
    'images/battle_steer.png'
),

(
    24,
    'Mystic Horseman',
    'MONSTER',
    'A creature that is half man and half horse and is renowned for exceptional speed.',
    'images/mystic_horseman.png'
),

(
    25,
    'Ryu-Kishin Powered',
    'MONSTER',
    'A gargoyle empowered by darkness whose sharp talons make it a dangerous opponent.',
    'images/ryu-kishin_powered.png'
),

(
    26,
    'Aqua Madoor',
    'MONSTER',
    'A water magician capable of conjuring powerful magical barriers against enemies.',
    'images/aqua_madoor.png'
),

(
    27,
    'Mystic Clown',
    'MONSTER',
    'A crazed and powerful creature whose relentless assault is difficult to stop.',
    'images/mystic_clown.png'
),

(
    28,
    'Vorse Raider',
    'MONSTER',
    'A vicious beast-warrior known for his brutality and heavily scarred battle axe.',
    'images/vorse_raider.png'
);


-- ============================================================
-- MONSTER CARD STATISTICS
-- ============================================================

INSERT INTO MonsterCard
(card_id, attribute, monster_type, level, attack, defense)
VALUES

-- Yugi / Player
(1,  'DARK',  'Spellcaster',    7, 2500, 2100),
(2,  'EARTH', 'Warrior',        7, 2300, 2100),
(3,  'DARK',  'Fiend',          6, 2500, 1200),
(4,  'DARK',  'Dragon',         5, 2000, 1500),
(5,  'EARTH', 'Warrior',        4, 1400, 1200),
(6,  'DARK',  'Fiend',          4, 1300, 1400),
(7,  'EARTH', 'Rock',           3, 1300, 2000),
(8,  'LIGHT', 'Spellcaster',    4,  800, 2000),
(9,  'EARTH', 'Beast-Warrior',  4, 1200, 1500),
(10, 'DARK',  'Dragon',         4, 1500,  800),
(11, 'LIGHT', 'Spellcaster',    4, 1700, 1000),
(12, 'EARTH', 'Beast',          3, 1200,  800),
(13, 'EARTH', 'Beast',          4, 1500, 1200),
(14, 'EARTH', 'Dinosaur',       3, 1200,  800),

-- Kaiba / Opponent
(15, 'LIGHT', 'Dragon',         8, 3000, 2500),
(16, 'EARTH', 'Warrior',        6, 2200, 1500),
(17, 'DARK',  'Warrior',        6, 2000, 1600),
(18, 'DARK',  'Spellcaster',    3,  600, 1500),
(19, 'DARK',  'Fiend',          4, 1800, 1000),
(20, 'EARTH', 'Beast-Warrior',  4, 1200, 1000),
(21, 'DARK',  'Fiend',          3, 1000,  800),
(22, 'DARK',  'Zombie',         3, 1500,    0),
(23, 'EARTH', 'Beast-Warrior',  5, 1800, 1300),
(24, 'EARTH', 'Beast',          4, 1300, 1550),
(25, 'DARK',  'Fiend',          4, 1600, 1200),
(26, 'WATER', 'Spellcaster',    4, 1200, 2000),
(27, 'DARK',  'Fiend',          4, 1500, 1000),
(28, 'DARK',  'Beast-Warrior',  4, 1900, 1200);


-- ============================================================
-- RESET CARD IDENTITY COUNTER
--
-- Card IDs 1-28 were inserted explicitly above.
-- The next automatically generated Card ID must therefore be 29.
-- ============================================================

ALTER TABLE Card
    ALTER COLUMN card_id RESTART WITH 29;


-- ============================================================
-- END OF SCHEMA
-- ============================================================