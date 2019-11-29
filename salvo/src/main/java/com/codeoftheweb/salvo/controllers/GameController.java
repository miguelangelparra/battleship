package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/games")
    public Map<String, Object> getInfoPlayer(Authentication authentication) {

        Map<String, Object> dto = new LinkedHashMap<>();

        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            dto.put("player", playerRepository
                    .findByEmail(authentication.getName())
                    .makePlayerDTO());
        }

        dto.put("games", infoGame());
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> toCreateGame(Authentication authentication) {

        if (authentication == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } else {
            Game game = new Game();
            gameRepository.save(game);

            GamePlayer gamePlayer = new GamePlayer(Instant.now(), playerRepository.findByEmail(authentication.getName()), game);
            gamePlayerRepository.save(gamePlayer);

            Map<String, Object> gpid = new LinkedHashMap<>();

            gpid.put("gpid", gamePlayer.getId());

            return new ResponseEntity<>(gpid, HttpStatus.CREATED);
        }
    }

    @RequestMapping("/games/{idGame}/players")
    public List<Map<String, Object>> getPlayersInGame(@PathVariable long idGame) {
        return gameRepository
                .findById(idGame)
                .getGamePlayersList();
    }

    public List<Map<String, Object>> infoGame() {
        return gameRepository
                .findAll()
                .stream().sorted(Comparator.comparing(Game::getDateGame).reversed())
                .map(game -> game.getGameDTO())
                .collect(Collectors.toList());
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    public Map<String, Object> getGameDTO(Game game) {

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", game.getId());
        dto.put("created", game.getDateGame().getTime());
        dto.put("gamePlayers", game.getGamePlayersList().stream().sorted());
        dto.put("score", game.getScoresList());
        //  dto.put("histories", this.getHistories());

        return dto;
}}