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

  @JsonIgnore
  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  List<GamePlayer> gamePlayers;

  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  List<Score> scores;
/*
  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  Set<History> histories;
*/
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
  @JsonIgnore
  public List<GamePlayer> getGamePlayers() {
    return gamePlayers;
  }



  /*
  public void toCalculateStateGame(){

    for (GamePlayer gamePlayer : this.getGamePlayers()) {
      for (History history : histories) {
        if(gamePlayer == history.getGamePlayer());

      }

    }

    this.histories.stream().f;
  }
*/



  public Map<String, Object> getGameDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", this.getId());
    dto.put("created", this.getDateGame().getTime());
    dto.put("gamePlayers", this.getGamePlayersList());
    dto.put("score", this.getScoresList());
   // dto.put("histories",this.getHistoriesList());
    return dto;
  }
  @JsonIgnore
  public List<Map<String, Object>> getScoresList() {
    return this.scores.stream()
      .map(score -> score.makeScoreDTO())
      .collect(Collectors.toList());
  }
  @JsonIgnore
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
