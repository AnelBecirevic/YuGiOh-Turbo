package com.yugiohTurbo.service;

import com.yugiohTurbo.repository.DeckRepository;
import com.yugiohTurbo.repository.TrunkRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerSetupService {

    private final TrunkRepository trunkRepository;
    private final DeckRepository deckRepository;

    public PlayerSetupService(
            TrunkRepository trunkRepository,
            DeckRepository deckRepository
    ) {

        this.trunkRepository = trunkRepository;
        this.deckRepository = deckRepository;
    }

    public void initializeNewPlayer(Integer accountId) {

        Integer deckId = deckRepository.createDeck(
                accountId,
                "Starter Deck"
        );

        // Cards 1-12: 3 copies each
        for (int cardId = 1; cardId <= 12; cardId++) {

            trunkRepository.addCard(
                    accountId,
                    cardId,
                    3
            );

            deckRepository.addCard(
                    deckId,
                    cardId,
                    3
            );
        }

        // Gazelle the King of Mythical Beasts
        trunkRepository.addCard(
                accountId,
                13,
                2
        );

        deckRepository.addCard(
                deckId,
                13,
                2
        );

        // Mammoth Graveyard
        trunkRepository.addCard(
                accountId,
                14,
                2
        );

        deckRepository.addCard(
                deckId,
                14,
                2
        );
    }
}