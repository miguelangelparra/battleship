package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class Game {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;
  private Date dateGame;

  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  List<GamePlayer> gamePlayers;

  @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
  List<Score> scores;

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

  public List<GamePlayer> getGamePlayers() {
    return gamePlayers;
  }

  public Map<String, Object> getGameDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", this.getId());
    dto.put("created", this.getDateGame().getTime());
    dto.put("gamePlayers", this.getGamePlayersList());
    dto.put("score", this.getScoresList());
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
}
