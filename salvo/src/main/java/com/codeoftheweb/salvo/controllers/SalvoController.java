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
    private GameRepository repoGame;

    @Autowired
    private GamePlayerRepository repoGamePlayer;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ShipRepository shipRepository;


    @RequestMapping("/games")
    public Map<String, Object> getInfoPlayer(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<>();

        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            dto.put("player", playerRepository.findByEmail(authentication.getName()).makePlayerDTO());

            List<GamePlayer> gamePlayerForPlayer = repoGamePlayer.findAll()
                    .stream()
                    .filter(gamePlayer -> gamePlayer.getPlayer().getEmail() == authentication.getName()).collect(Collectors.toList());
            dto.put("gamesOwn",gamePlayerForPlayer);
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
            repoGame.save(game);
            GamePlayer gamePlayer = new GamePlayer(Instant.now(), playerRepository.findByEmail(authentication.getName()), game);
            repoGamePlayer.save(gamePlayer);
            Map<String, Object> gpid = new LinkedHashMap<>();
            gpid.put("gpid", gamePlayer.getId());

            return new ResponseEntity<>(gpid, HttpStatus.CREATED);
        }
    }

    @RequestMapping(path = "/game/{idGame}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> toJoinGame(Authentication authentication, @PathVariable long idGame) {
        Game game = repoGame.findById(idGame);
        System.out.println(game);
        if (authentication == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        if (game == null) {
            return new ResponseEntity<Map<String,Object>>( makeMap("error","No Such Game"), HttpStatus.FORBIDDEN);
        }
        if (game.getGamePlayers().size() == 2){
            return new ResponseEntity<Map<String,Object>>(makeMap("error","Game is Full"), HttpStatus.FORBIDDEN);
        }
        GamePlayer gamePlayer = new GamePlayer(Instant.now(),playerRepository.findByEmail(authentication.getName()),game);
        repoGamePlayer.save(gamePlayer);

        return new ResponseEntity<Map<String,Object>>(makeMap("gpid",gamePlayer.getId()), HttpStatus.CREATED);
    }

    @RequestMapping("/games/{idGame}/players")
    public List<Map<String, Object>> getPlayersInGame(@PathVariable long idGame) {
        return repoGame.findById(idGame).getGamePlayersList();
    }


    @RequestMapping("/games/players/{gamePlayerId}/ships")
    public List<Ship> getPlayerssInGame(@PathVariable long gamePlayerId) {
        return repoGamePlayer.findById(gamePlayerId).get().getShips();
    }


    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Object> addShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {


        if(authentication == null
                || authentication.getName() == "Guest"
                || repoGamePlayer.findById(gamePlayerId).get().getPlayer().getEmail() != authentication.getName()
        ) {
            return new ResponseEntity<>("You are not a User Authorized to place ships in this game", HttpStatus.UNAUTHORIZED);
        }
        if (!repoGamePlayer.findById(gamePlayerId).get().getShips().isEmpty()) {
            return new ResponseEntity<>("Forbidden. Your ships have already been placed .", HttpStatus.FORBIDDEN);
        }

        ships.forEach(ship -> {
                    ship.setGamePlayer(repoGamePlayer.findById(gamePlayerId).get());
                    System.out.println(ship.getId());
                    System.out.println(ship.getLocations());
                    System.out.println(ship.getTypeShip());

                    shipRepository.save(ship);
                    repoGamePlayer.findById(gamePlayerId).get().addShip(ship);
                }
        );
        return new ResponseEntity<>("Ships have been created", HttpStatus.CREATED);
    }


    @RequestMapping("/leaderBoard")
    public List<Map<String, Object>> leaderBoard() {
        return playerRepository.findAll()
                .stream()
                .map(player -> player.makePlayerScoreDTO())
                .collect(Collectors.toList());
    }


    @RequestMapping("/game_view/{idGamePlayer}")
    public ResponseEntity<Map<String, Object>> game(@PathVariable Long idGamePlayer, Authentication authentication) {
        System.out.println(authentication.getName());
        if (repoGamePlayer.findById(idGamePlayer).get().getPlayer().getEmail() != authentication.getName()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>((gameViewDTO(repoGamePlayer.findById(idGamePlayer).get())), HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(
            @RequestParam String email, @RequestParam String password) {

        if (email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByEmail(email) != null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(email, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
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

    public List<Map<String, Object>> infoGame() {
        return repoGame.findAll().stream()
                .map(game -> game.getGameDTO())
                .collect(Collectors.toList());
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
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