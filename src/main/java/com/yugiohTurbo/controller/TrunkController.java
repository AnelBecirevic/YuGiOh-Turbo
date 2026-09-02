package com.yugiohTurbo.controller;

import com.yugiohTurbo.model.TrunkCard;
import com.yugiohTurbo.service.TrunkService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TrunkController {

    private final TrunkService trunkService;

    public TrunkController(TrunkService trunkService) {
        this.trunkService = trunkService;
    }

    @GetMapping("/trunk")
    public String trunkPage(
            HttpSession session,
            Model model
    ) {

        Integer accountId =
                (Integer) session.getAttribute("accountId");

        if (accountId == null) {
            return "redirect:/";
        }

        List<TrunkCard> cards =
                trunkService.getCards(accountId);

        model.addAttribute("cards", cards);
        model.addAttribute(
                "totalCards",
                trunkService.getTotalCardCount(cards)
        );

        return "trunk";
    }
}