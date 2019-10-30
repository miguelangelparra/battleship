package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Score {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;

  private Date joinGame;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "player_id")
  private Player player;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "game_id")
  private Game game;

  private long score;

  public Score() {
  }

  public Score(Game game, Player player, long score) {
    this.game = game;
    this.player = player;
    this.score = score;

  }

  ;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public long getScore() {
    return score;
  }

  public Map<String, Object> makeScoreDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("player", this.getPlayer().makePlayerDTO());
    dto.put("score", this.getScore());
    return dto;
  }

}
