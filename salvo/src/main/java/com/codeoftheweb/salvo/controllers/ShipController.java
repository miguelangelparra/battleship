package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ShipController {

  @Autowired
  private GamePlayerRepository gamePlayerRepository;
  @Autowired
  private ShipRepository shipRepository;

  @RequestMapping("/games/players/{gamePlayerId}/ships")
  public Set<Ship> getShipsFromIdPlayer(@PathVariable long gamePlayerId) {
    return gamePlayerRepository.findById(gamePlayerId).get().getShips();
  }

  @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
  public ResponseEntity<Object> addShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {
GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();
    if(authentication == null
      || authentication.getName() == "Guest"
      || gamePlayer.getPlayer().getEmail() != authentication.getName()
    ) {
      return new ResponseEntity<>("You are not a User Authorized to place ships in this game", HttpStatus.UNAUTHORIZED);
    }
    if (!gamePlayer.getShips().isEmpty()) {
      return new ResponseEntity<>("Forbidden. Your ships have already been placed .", HttpStatus.FORBIDDEN);
    }

    System.out.println(ships);

    ships.forEach(ship -> {
        System.out.println(ship.getTypeShip());
        ship.setGamePlayer(gamePlayer);
        shipRepository.save(ship);
      //gamePlayerRepository.findById(gamePlayerId).get().addShip(ship);
      }
    );
    return new ResponseEntity<>("Ships have been created", HttpStatus.CREATED);
  }

}
