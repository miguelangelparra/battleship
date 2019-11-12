package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.GamePlayer;
import com.codeoftheweb.salvo.models.History;
import com.codeoftheweb.salvo.models.Salvo;
import com.codeoftheweb.salvo.models.Ship;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.HistoryRepository;
import com.codeoftheweb.salvo.repositories.SalvoRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

//import javax.xml.ws.Response;
import java.lang.reflect.Array;
import java.util.*;

@RestController
@RequestMapping("/api")
public class SalvoController {

  @Autowired
  private GamePlayerRepository gamePlayerRepository;
  @Autowired
  private SalvoRepository salvoRepository;

  @Autowired
  private HistoryRepository historyRepository;

    @RequestMapping(path="/games/players/{gamePlayerId}/salvos")
  public Set<Salvo> getSalvoes(@PathVariable long gamePlayerId){
    return gamePlayerRepository.findById(gamePlayerId).get().getSalvoes();
  }
  @RequestMapping(path = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
  public ResponseEntity<Object> addSalvoes(@PathVariable long gamePlayerId, @RequestBody Set<String> salvoes , Authentication authentication) {

    System.out.println(salvoes);
    GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

    if (authentication == null
      || authentication.getName() == "Guest"
      || gamePlayer.getPlayer().getEmail() != authentication.getName()
    ) {
      return new ResponseEntity<>("You are not a User Authorized to place ships in this game", HttpStatus.UNAUTHORIZED);
    }
    if (salvoes.size() > 5) {
      return new ResponseEntity<>("Forbidden. You are trying send more than 5 salvoes.", HttpStatus.FORBIDDEN);
    }

    int turnOponente = 0;
    List<Ship> shipsOponente = new ArrayList<>();
    for (GamePlayer gp : gamePlayer.getGame().getGamePlayers()) {
      if (gp.getId() != gamePlayerId) {
        turnOponente = gp.getSalvoes().size();
        shipsOponente = gp.getShips();
      }
    }
    //   if( turnOponente +1 < gamePlayer.getSalvoes().size()) {
    if (turnOponente < gamePlayer.getSalvoes().size()) {
      return new ResponseEntity<>("You are trying to cheat", HttpStatus.NOT_ACCEPTABLE);
    }
    System.out.println(turnOponente);

    int turn = gamePlayer.getSalvoes().size();
    Salvo salvo = new Salvo(++turn, salvoes, gamePlayer);
    salvoRepository.save(salvo);
    System.out.println(turn);


    for (String a : salvo.getSalvoLocations()) {
      for (Ship sh : shipsOponente) {
        if (sh.getLocations().contains(a)) {
          History history = new History(turn, gamePlayer, sh.getTypeShip(), true);
          historyRepository.save(history);
          //   return  makeHistorialDTO(sl.getTurn(), sh.getTypeShip(), gp.getId());
        }
      }
    }
      return new ResponseEntity<>("Salvoes have been created", HttpStatus.CREATED);
    }
  }



/*
TO TEST SALVOES
* $.post({
      url: "/api/games/players/1/salvos",
      data: JSON.stringify(["A1", "A2", "A3","A21","A222"]),
      dataType: "text",
      contentType: "application/json"
  }).done(function(data){console.log(data)})
*
* */