package com.codeoftheweb.salvo.models;

import com.codeoftheweb.salvo.repositories.ScoreRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;

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
//Enlaza con tabla gamePleayer en columna "game"
  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  private Set<GamePlayer> gamePlayers;
//Enlaza con tabla score en columna "game"
  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  private List<Score> scores;
//Enlaza con tabla histories en columna "game"
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

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  //Calcula el estado de juego
  public int toCalculateStateGame(GamePlayer gp){

    if (gameOver == true) {
    }else{
    //Transforma el set en array para poder procesar los datos
    GamePlayer[] myArray = new GamePlayer[this.getGamePlayers().size()];
    this.getGamePlayers().toArray(myArray);
//Busca al oponente
    GamePlayer oponent = new GamePlayer();
    for(GamePlayer gamePlayer: this.getGamePlayers()){
      if(gp.getId() != gamePlayer.getId()){
        oponent = gamePlayer;
      }
    }
      //Comprueba si el otro jugador ya termino su turno
      if(this.getGamePlayers().size()==2 && (gp.getSalves().size() > oponent.getSalves().size()) ){
        this.setStatus(2);
      }
      else if ((gp.getShips().size()!=0) && (gp.toCalculateAllShipSunk()== true || oponent.toCalculateAllShipSunk() == true))
      {
        //Comprueba si alguno de los jugadores tiene todos los barcos hundidos, juego terminado
        this.gameOver = true;
        this.setStatus(4);
toCreateScore(gp,oponent);
      }else{
//Responde si aun no se han hundido los barcos, colocar salvoes.
        this.setStatus(3);
      }

      //Comprueba si los jugadores colocaron los barcos
      if(gp.getShips().size()==0 || oponent.getShips().size()==0){
        this.setStatus(1);
      }

      //Comprueba si hay otro jugador
      if(this.getGamePlayers().size()<2){
      this.setStatus(0);
}


    }

return this.getStatus();
  }

  public void toCreateScore(GamePlayer gamePlayer, GamePlayer oponnet){
    Score score;
    if(gamePlayer.isAllShipSunk()==true && oponnet.isAllShipSunk()==true){
      score = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(),1);
    }else if ( gamePlayer.isAllShipSunk()==true){
      score = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(),0);
    }else{
       score = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(),2);
    }
   scores.add(score);
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
