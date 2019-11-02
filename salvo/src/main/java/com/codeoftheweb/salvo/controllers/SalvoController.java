package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Game;
import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Player;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

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

    @RequestMapping("/leaderBoard")
    public List<Map<String, Object>> leaderBoard() {
        return playerRepository.findAll()
          .stream()
          .map(player -> player.makePlayerScoreDTO())
          .collect(Collectors.toList());
    }

    @RequestMapping(path = "/game/{idGame}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> toJoinGame(Authentication authentication, @PathVariable long idGame) {
        Game game = gameRepository.findById(idGame);
        System.out.println(game);
        if (authentication == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        if (game == null) {
            return new ResponseEntity<Map<String,Object>>( makeMap("error","No Such Game"), HttpStatus.FORBIDDEN);
        }
        System.out.println(game.getGamePlayersList());
        if (game.getGamePlayers().size() == 2){
            return new ResponseEntity<Map<String,Object>>(makeMap("error","Game is Full"), HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = new GamePlayer(Instant.now(),playerRepository.findByEmail(authentication.getName()),game);
        gamePlayerRepository.save(gamePlayer);

        return new ResponseEntity<Map<String,Object>>(makeMap("gpid",gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping("/game_view/{idGamePlayer}")
    public ResponseEntity<Map<String, Object>> game(@PathVariable Long idGamePlayer, Authentication authentication) {
        System.out.println(authentication.getName());
        if (gamePlayerRepository.findById(idGamePlayer).get().getPlayer().getEmail() != authentication.getName()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>((gameViewDTO(gamePlayerRepository.findById(idGamePlayer).get())), HttpStatus.ACCEPTED);
    }

    private Map<String, Object> gameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", gamePlayer.getId());
        dto.put("created", gamePlayer.getJoinGame());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers()
                .stream()
                .map(gP -> gP.makeGamePlayerDTO()));

        dto.put("ships", gamePlayer.getShips()
                .stream()
                .map(sh -> sh.makeShipDTO()));

        dto.put("salvoes", gamePlayer.getSalvoes()
                .stream()
                .flatMap((gP -> gamePlayer.getSalvoes()
                        .stream()
                        .map(salvo -> salvo.makeSalvoDTO()))));
        return dto;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

}




// public List<Map<String,Object>> infoPlayersInGameDTO(Long id){
//   return repoGame.findById(id).getGamePlayersList();
//}

    /* private Map<String,Object> infoPlayerDTO(Player player){

       Codigo para encontrar una lista de juegos donde participa el jugador

       List<GamePlayer> gamePlayerForPlayer = repoGamePlayer.findAll()
                .stream()
                .filter(gamePlayer -> gamePlayer.getPlayer().getId() == player.getId()).collect(Collectors.toList());
        dto.put("gamesOwn",gamePlayerForPlayer);
        //dto.put("games",game(repoGamePlayer.findById((player.getId()))));
        return dto;
    }*/

//return repo.findAll().stream().map(game -> game.getId()).collect(Collectors.toList()); Para pedir solo el ID