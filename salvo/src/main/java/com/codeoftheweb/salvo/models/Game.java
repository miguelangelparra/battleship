package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Game {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;
  private Date dateGame;

  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  private Set<GamePlayer> gamePlayers;

  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  private List<Score> scores;

  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  Set<History> histories;

  private boolean gameOver = false;
  private int status ;

  public Game() {
    this.dateGame = new Date();
  }

  public Game(Date date) {
    this.dateGame = date;
  }

  public long getId() {
    return id;
  }

  public Date getDateGame() {
    return dateGame;
  }

  public Set<GamePlayer> getGamePlayers() {
    return gamePlayers;
  }

  public Set<History> getHistories() {
    return histories;
  }

//Calcula el estado de juego
  public int toCalculateStateGame(GamePlayer gp){

    //Transforma el set en array para poder procesar los datos
    GamePlayer[] myArray = new GamePlayer[this.getGamePlayers().size()];
    this.getGamePlayers().toArray(myArray);

    GamePlayer oponent = new GamePlayer();
    for(GamePlayer gamePlayer: this.getGamePlayers()){
      if(gp.getId() != gamePlayer.getId()){
        oponent = gamePlayer;
      }
    }
    //Comprueba si hay otro jugador
    if(this.getGamePlayers().size()<2){
  status=0;
  return status;
}
//Comprueba si el otro jugador ya termino su turno
    if(gp.getSalves().size() >= oponent.getSalves().size() && this.getGamePlayers().size()==2 ){
  status= 2;
}else{
  for (GamePlayer gamePlayer : this.getGamePlayers()) {
    //Comprueba si los jugadores colocaron los barcos
    if(gamePlayer.getShips().size()==0){
      status = 1;
    }
    //Comprueba si alguno de los jugadores tiene todos los barcos hundidos
    if (gamePlayer.toCalculateAllShipSunk() && gamePlayer.getSalves().size() != 0){
      gameOver = true;
      status =   4;}
    else{
      //Responde si aun no se han hundido los barcos.
      status = 3;
    }
  }
}
return status;
  }


  public Map<String, Object> getGameDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", this.getId());
    dto.put("created", this.getDateGame().getTime());
    dto.put("gamePlayers", this.getGamePlayersList());
    dto.put("score", this.getScoresList());
   // dto.put("histories",this.getHistoriesList());
    return dto;
  }
  public List<Map<String, Object>> getScoresList() {
    return this.scores.stream()
      .map(score -> score.makeScoreDTO())
      .collect(Collectors.toList());
  }
  public List<Map<String, Object>> getGamePlayersList() {
    return this.gamePlayers.stream()
      .map(GamePlayer -> GamePlayer.makeGamePlayerDTO())
      .collect(Collectors.toList());
  }
 /* @JsonIgnore
  public List<Map<String, Object>> getHistoriesList() {
    return this.histories.stream()
            .map(history -> history.makeHistoryDTO())
            .collect(Collectors.toList());
  }*/

}
