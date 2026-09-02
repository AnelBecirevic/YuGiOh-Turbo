package com.yugiohTurbo.controller;

import com.yugiohTurbo.model.TrunkCard;
import com.yugiohTurbo.service.DeckEditorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DeckEditorController {

    private final DeckEditorService deckEditorService;

    public DeckEditorController(
            DeckEditorService deckEditorService
    ) {
        this.deckEditorService = deckEditorService;
    }

    @GetMapping("/deck-editor")
    public String deckEditorPage(
            HttpSession session,
            Model model
    ) {

        Integer accountId =
                (Integer) session.getAttribute("accountId");

        if (accountId == null) {
            return "redirect:/";
        }

        Integer deckId =
                deckEditorService.getDeckId(accountId);

        if (deckId == null) {
            return "redirect:/menu";
        }

        List<TrunkCard> trunkCards =
                deckEditorService.getTrunkCards(accountId);

        List<TrunkCard> deckCards =
                deckEditorService.getDeckCards(deckId);

        model.addAttribute(
                "trunkCards",
                trunkCards
        );

        model.addAttribute(
                "deckCards",
                deckCards
        );

        model.addAttribute(
                "deckSize",
                deckEditorService.getDeckSize(deckId)
        );

        return "deck-editor";
    }

    @PostMapping("/deck-editor/add")
    public String addCard(
            @RequestParam Integer cardId,
            HttpSession session
    ) {

        Integer accountId =
                (Integer) session.getAttribute("accountId");

        if (accountId == null) {
            return "redirect:/";
        }

        deckEditorService.addCard(
                accountId,
                cardId
        );

        return "redirect:/deck-editor";
    }

    @PostMapping("/deck-editor/remove")
    public String removeCard(
            @RequestParam Integer cardId,
            HttpSession session
    ) {

        Integer accountId =
                (Integer) session.getAttribute("accountId");

        if (accountId == null) {
            return "redirect:/";
        }

        deckEditorService.removeCard(
                accountId,
                cardId
        );

        return "redirect:/deck-editor";
    }
}