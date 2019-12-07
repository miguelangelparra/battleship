package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping("/leaderBoard")
    public List<Map<String, Object>> leaderBoard() {
        return playerRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Player::getTotalScore).reversed())
                .map(Player::makePlayerScoreDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/game/{idGame}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> toJoinGame(Authentication authentication, @PathVariable long idGame) {
        Game game = gameRepository.findById(idGame);

        if (authentication == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if (game == null) {
            return new ResponseEntity<Map<String, Object>>(makeMap("error", "No Such Game"), HttpStatus.FORBIDDEN);
        }

        if (game.getGamePlayers().size() == 2) {
            return new ResponseEntity<Map<String, Object>>(makeMap("error", "Game is Full"), HttpStatus.FORBIDDEN);
        }

        GamePlayer gamePlayer = new GamePlayer(Instant.now(), playerRepository.findByEmail(authentication.getName()), game);
        gamePlayerRepository.save(gamePlayer);

        return new ResponseEntity<Map<String, Object>>(makeMap("gpid", gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping("/game_view/{idGamePlayer}")
    public ResponseEntity<Map<String, Object>> game(@PathVariable Long idGamePlayer, Authentication authentication) {

        if (gamePlayerRepository.findById(idGamePlayer).get().getPlayer().getEmail() != authentication.getName()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(gamePlayerRepository.findById(idGamePlayer).get().getGame().toCalculateStateGame(gamePlayerRepository.findById(idGamePlayer).get()) == 5 ){

            List <Score> score=  gamePlayerRepository.findById(idGamePlayer).get().getGame().toGetScore();
           score.forEach(sco->{scoreRepository.save(sco);
               gamePlayerRepository.findById(idGamePlayer).get().getGame().setStatus(6);
           });
        }

        return new ResponseEntity<>((gameViewDTO(gamePlayerRepository.findById(idGamePlayer).get())), HttpStatus.ACCEPTED);
    }

    private Map<String, Object> gameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("status", gamePlayer
                .getGame()
                .toCalculateStateGame(gamePlayer));
        dto.put("id", gamePlayer
                .getId());
        dto.put("created", gamePlayer
                .getJoinGame());
        dto.put("winner",gamePlayer.getGame().getWinnerGP());
        dto.put("gameplayers", gamePlayer
                .getGame()
                .getGamePlayers()
                .stream()
                .sorted(Comparator.comparing(GamePlayer::getId))
                .map(GamePlayer::makeGamePlayerDTO));
        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(Ship::makeShipDTO)
                .collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers()
                .stream()
                .map(gp -> gp.getSalves()
                        .stream()
                        .map(Salvo::makeSalvoDTO)));
        dto.put("hitOnOpponent",gamePlayer.getGame().getGamePlayers().stream().map(gp->gp.getShips().stream().map(Ship::makeShipHitDTO).collect(Collectors.toList())));
        return dto;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}


      /*  dto.put("damage",gamePlayer
                .getGame()
                .getGamePlayers()
                .stream()
                .map(gp->gp
                        .getShips()
                        .stream()
                        .map(Ship::makeShipPublicDTO)));*/

             /* dto.put("history", gamePlayer
                .getGame()
                .getHistories()
                .stream()
                .sorted(Comparator.comparing(History::getTurn)
                        .reversed())
                .map(History::makeHistoryDTO));*/