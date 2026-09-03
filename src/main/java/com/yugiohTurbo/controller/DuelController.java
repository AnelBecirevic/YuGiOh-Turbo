package com.yugiohTurbo.controller;

import com.yugiohTurbo.model.DuelMonster;
import com.yugiohTurbo.model.DuelState;
import com.yugiohTurbo.service.DuelService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DuelController {

    private static final String DUEL_SESSION_KEY =
            "duelState";

    private final DuelService duelService;

    public DuelController(
            DuelService duelService
    ) {
        this.duelService = duelService;
    }

    @GetMapping("/duel")
    public String duelPage(
            HttpSession session,
            Model model
    ) {

        Integer accountId =
                (Integer) session.getAttribute(
                        "accountId"
                );

        if (accountId == null) {
            return "redirect:/";
        }

        DuelState state =
                (DuelState) session.getAttribute(
                        DUEL_SESSION_KEY
                );

        model.addAttribute(
                "state",
                state
        );

        model.addAttribute(
                "deckSize",
                duelService.getPlayerDeckSize(
                        accountId
                )
        );

        return "duel";
    }

    @PostMapping("/duel/start")
    public String startDuel(
            HttpSession session,
            Model model
    ) {

        Integer accountId =
                (Integer) session.getAttribute(
                        "accountId"
                );

        if (accountId == null) {
            return "redirect:/";
        }

        DuelState state =
                duelService.startDuel(
                        accountId
                );

        if (state == null) {

            model.addAttribute(
                    "state",
                    null
            );

            model.addAttribute(
                    "deckSize",
                    duelService.getPlayerDeckSize(
                            accountId
                    )
            );

            model.addAttribute(
                    "error",
                    "Your deck must contain exactly 40 cards before starting a duel."
            );

            return "duel";
        }

        session.setAttribute(
                DUEL_SESSION_KEY,
                state
        );

        return "redirect:/duel";
    }

    @PostMapping("/duel/summon")
    public String summon(
            @RequestParam Integer cardId,
            @RequestParam String position,
            @RequestParam(required = false)
            List<Integer> tributeZones,
            HttpSession session
    ) {

        DuelState state =
                getDuelState(session);

        if (state != null) {

            DuelMonster.Position duelPosition =
                    DuelMonster.Position.valueOf(
                            position
                    );

            duelService.summonPlayerMonster(
                    state,
                    cardId,
                    duelPosition,
                    tributeZones
            );
        }

        return "redirect:/duel";
    }

    @PostMapping("/duel/battle-phase")
    public String enterBattlePhase(
            HttpSession session
    ) {

        DuelState state =
                getDuelState(session);

        if (state != null) {

            duelService.enterBattlePhase(
                    state
            );
        }

        return "redirect:/duel";
    }

    @PostMapping("/duel/main-phase-2")
    public String enterMainPhase2(
            HttpSession session
    ) {

        DuelState state =
                getDuelState(session);

        if (state != null) {

            duelService.enterMainPhase2(
                    state
            );
        }

        return "redirect:/duel";
    }

    @PostMapping("/duel/attack")
    public String attack(
            @RequestParam int attackerZone,
            @RequestParam(defaultValue = "-1")
            int targetZone,
            HttpSession session
    ) {

        DuelState state =
                getDuelState(session);

        if (state != null) {

            duelService.attack(
                    state,
                    attackerZone,
                    targetZone
            );
        }

        return "redirect:/duel";
    }

    @PostMapping("/duel/end-turn")
    public String endTurn(
            HttpSession session
    ) {

        DuelState state =
                getDuelState(session);

        if (state != null) {

            duelService.endPlayerTurn(
                    state
            );
        }

        return "redirect:/duel";
    }

    @PostMapping("/duel/leave")
    public String leaveDuel(
            HttpSession session
    ) {

        session.removeAttribute(
                DUEL_SESSION_KEY
        );

        return "redirect:/menu";
    }

    private DuelState getDuelState(
            HttpSession session
    ) {

        return (DuelState)
                session.getAttribute(
                        DUEL_SESSION_KEY
                );
    }
}