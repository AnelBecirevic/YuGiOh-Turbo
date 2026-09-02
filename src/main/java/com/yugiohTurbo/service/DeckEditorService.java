package com.yugiohTurbo.service;

import com.yugiohTurbo.model.TrunkCard;
import com.yugiohTurbo.repository.DeckRepository;
import com.yugiohTurbo.repository.TrunkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeckEditorService {

    private static final int MAX_DECK_SIZE = 40;

    private final DeckRepository deckRepository;
    private final TrunkRepository trunkRepository;

    public DeckEditorService(
            DeckRepository deckRepository,
            TrunkRepository trunkRepository
    ) {
        this.deckRepository = deckRepository;
        this.trunkRepository = trunkRepository;
    }

    public Integer getDeckId(Integer accountId) {
        return deckRepository.findDeckIdByAccountId(accountId);
    }

    public List<TrunkCard> getTrunkCards(Integer accountId) {
        return trunkRepository.findAllByAccountId(accountId);
    }

    public List<TrunkCard> getDeckCards(Integer deckId) {
        return deckRepository.findCardsByDeckId(deckId);
    }

    public int getDeckSize(Integer deckId) {
        return deckRepository.getDeckSize(deckId);
    }

    @Transactional
    public void addCard(
            Integer accountId,
            Integer cardId
    ) {

        Integer deckId =
                deckRepository.findDeckIdByAccountId(accountId);

        if (deckId == null) {
            return;
        }

        int deckSize =
                deckRepository.getDeckSize(deckId);

        if (deckSize >= MAX_DECK_SIZE) {
            return;
        }

        Integer ownedQuantity =
                trunkRepository.getCardQuantity(
                        accountId,
                        cardId
                );

        Integer deckQuantity =
                deckRepository.getCardQuantity(
                        deckId,
                        cardId
                );

        if (ownedQuantity <= deckQuantity) {
            return;
        }

        deckRepository.incrementCard(
                deckId,
                cardId
        );
    }

    @Transactional
    public void removeCard(
            Integer accountId,
            Integer cardId
    ) {

        Integer deckId =
                deckRepository.findDeckIdByAccountId(accountId);

        if (deckId == null) {
            return;
        }

        Integer deckQuantity =
                deckRepository.getCardQuantity(
                        deckId,
                        cardId
                );

        if (deckQuantity <= 0) {
            return;
        }

        deckRepository.decrementCard(
                deckId,
                cardId
        );
    }
}