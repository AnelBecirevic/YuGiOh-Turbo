package com.yugiohTurbo.service;

import com.yugiohTurbo.model.TrunkCard;
import com.yugiohTurbo.repository.TrunkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrunkService {

    private final TrunkRepository trunkRepository;

    public TrunkService(TrunkRepository trunkRepository) {
        this.trunkRepository = trunkRepository;
    }

    public List<TrunkCard> getCards(Integer accountId) {
        return trunkRepository.findAllByAccountId(accountId);
    }

    public int getTotalCardCount(List<TrunkCard> cards) {

        return cards.stream()
                .mapToInt(TrunkCard::quantity)
                .sum();
    }
}