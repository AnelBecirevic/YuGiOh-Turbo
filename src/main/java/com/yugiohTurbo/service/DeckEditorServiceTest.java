package com.yugiohTurbo.service;

import com.yugiohTurbo.repository.DeckRepository;
import com.yugiohTurbo.repository.TrunkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeckEditorServiceTest {

    private DeckRepository deckRepository;
    private TrunkRepository trunkRepository;

    private DeckEditorService deckEditorService;

    @BeforeEach
    void setUp() {

        deckRepository =
                Mockito.mock(DeckRepository.class);

        trunkRepository =
                Mockito.mock(TrunkRepository.class);

        deckEditorService =
                new DeckEditorService(
                        deckRepository,
                        trunkRepository
                );
    }

    @Test
    void addCardAddsCardWhenPlayerOwnsAvailableCopy() {

        Integer accountId = 1;
        Integer deckId = 1;
        Integer cardId = 5;

        when(
                deckRepository.findDeckIdByAccountId(
                        accountId
                )
        ).thenReturn(deckId);

        when(
                deckRepository.getDeckSize(
                        deckId
                )
        ).thenReturn(39);

        when(
                trunkRepository.getCardQuantity(
                        accountId,
                        cardId
                )
        ).thenReturn(3);

        when(
                deckRepository.getCardQuantity(
                        deckId,
                        cardId
                )
        ).thenReturn(2);

        deckEditorService.addCard(
                accountId,
                cardId
        );

        verify(
                deckRepository
        ).incrementCard(
                deckId,
                cardId
        );
    }

    @Test
    void addCardDoesNotAddCardWhenDeckAlreadyContainsFortyCards() {

        Integer accountId = 1;
        Integer deckId = 1;
        Integer cardId = 5;

        when(
                deckRepository.findDeckIdByAccountId(
                        accountId
                )
        ).thenReturn(deckId);

        when(
                deckRepository.getDeckSize(
                        deckId
                )
        ).thenReturn(40);

        deckEditorService.addCard(
                accountId,
                cardId
        );

        verify(
                deckRepository,
                never()
        ).incrementCard(
                deckId,
                cardId
        );
    }

    @Test
    void addCardDoesNotAddMoreCopiesThanPlayerOwns() {

        Integer accountId = 1;
        Integer deckId = 1;
        Integer cardId = 5;

        when(
                deckRepository.findDeckIdByAccountId(
                        accountId
                )
        ).thenReturn(deckId);

        when(
                deckRepository.getDeckSize(
                        deckId
                )
        ).thenReturn(30);

        when(
                trunkRepository.getCardQuantity(
                        accountId,
                        cardId
                )
        ).thenReturn(3);

        when(
                deckRepository.getCardQuantity(
                        deckId,
                        cardId
                )
        ).thenReturn(3);

        deckEditorService.addCard(
                accountId,
                cardId
        );

        verify(
                deckRepository,
                never()
        ).incrementCard(
                deckId,
                cardId
        );
    }

    @Test
    void addCardDoesNothingWhenAccountHasNoDeck() {

        Integer accountId = 1;
        Integer cardId = 5;

        when(
                deckRepository.findDeckIdByAccountId(
                        accountId
                )
        ).thenReturn(null);

        deckEditorService.addCard(
                accountId,
                cardId
        );

        verify(
                deckRepository,
                never()
        ).incrementCard(
                Mockito.anyInt(),
                Mockito.anyInt()
        );
    }

    @Test
    void removeCardRemovesExistingCardFromDeck() {

        Integer accountId = 1;
        Integer deckId = 1;
        Integer cardId = 5;

        when(
                deckRepository.findDeckIdByAccountId(
                        accountId
                )
        ).thenReturn(deckId);

        when(
                deckRepository.getCardQuantity(
                        deckId,
                        cardId
                )
        ).thenReturn(2);

        deckEditorService.removeCard(
                accountId,
                cardId
        );

        verify(
                deckRepository
        ).decrementCard(
                deckId,
                cardId
        );
    }

    @Test
    void removeCardDoesNotRemoveCardWhenQuantityIsZero() {

        Integer accountId = 1;
        Integer deckId = 1;
        Integer cardId = 5;

        when(
                deckRepository.findDeckIdByAccountId(
                        accountId
                )
        ).thenReturn(deckId);

        when(
                deckRepository.getCardQuantity(
                        deckId,
                        cardId
                )
        ).thenReturn(0);

        deckEditorService.removeCard(
                accountId,
                cardId
        );

        verify(
                deckRepository,
                never()
        ).decrementCard(
                deckId,
                cardId
        );
    }

    @Test
    void removeCardDoesNothingWhenAccountHasNoDeck() {

        Integer accountId = 1;
        Integer cardId = 5;

        when(
                deckRepository.findDeckIdByAccountId(
                        accountId
                )
        ).thenReturn(null);

        deckEditorService.removeCard(
                accountId,
                cardId
        );

        verify(
                deckRepository,
                never()
        ).decrementCard(
                Mockito.anyInt(),
                Mockito.anyInt()
        );
    }
}