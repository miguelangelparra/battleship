package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Salvo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;

  private int turn = 0 ;

  @ElementCollection
  @Column(name = "salvoLocation")
  private Set<String> salvoLocations;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "gamePlayer_id")
  private GamePlayer gamePlayer;

  public Salvo() {
  }

  public Salvo(int turn, Set<String> salvoLocations, GamePlayer gamePlayer) {
    this.turn = turn;
    this.salvoLocations = salvoLocations;
    this.gamePlayer = gamePlayer;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getTurn() {
    return turn;
  }

  public Set<String> getSalvoLocations() {
    return salvoLocations;
  }

  public GamePlayer getGamePlayer() {
    return gamePlayer;
  }

  public void setGamePlayer(GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
  }

  public Map<String, Object> makeSalvoDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("turn", this.getTurn());
    dto.put("player", this.getGamePlayer().getPlayer().getId());
    dto.put("locations", this.getSalvoLocations());
    return dto;
  }
}
