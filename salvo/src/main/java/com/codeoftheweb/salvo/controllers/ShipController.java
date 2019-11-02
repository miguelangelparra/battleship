package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ShipController {

  @Autowired
  private GamePlayerRepository gamePlayerRepository;
  @Autowired
  private ShipRepository shipRepository;

  @RequestMapping("/games/players/{gamePlayerId}/ships")
  public List<Ship> getShipsFromIdPlayer(@PathVariable long gamePlayerId) {
    return gamePlayerRepository.findById(gamePlayerId).get().getShips();
  }

  @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
  public ResponseEntity<Object> addShips(@PathVariable long gamePlayerId, @RequestBody List<Ship> ships, Authentication authentication) {

    if(authentication == null
      || authentication.getName() == "Guest"
      || gamePlayerRepository.findById(gamePlayerId).get().getPlayer().getEmail() != authentication.getName()
    ) {
      return new ResponseEntity<>("You are not a User Authorized to place ships in this game", HttpStatus.UNAUTHORIZED);
    }
    if (!gamePlayerRepository.findById(gamePlayerId).get().getShips().isEmpty()) {
      return new ResponseEntity<>("Forbidden. Your ships have already been placed .", HttpStatus.FORBIDDEN);
    }

    ships.forEach(ship -> {
        ship.setGamePlayer(gamePlayerRepository.findById(gamePlayerId).get());
        shipRepository.save(ship);
      gamePlayerRepository.findById(gamePlayerId).get().addShip(ship);
      }
    );
    return new ResponseEntity<>("Ships have been created", HttpStatus.CREATED);
  }

}
