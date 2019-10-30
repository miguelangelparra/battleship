package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
public class GamePlayer {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private Long id;

  private Instant joinGame;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "player_id")
  private Player player;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "game_id")
  private Game game;

  @JsonIgnore
  @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
  List<Ship> ships;

  @JsonIgnore
  @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
  private Set<Salvo> salvoes;

  public GamePlayer() {
  }

  public GamePlayer(Instant joinGame, Player player, Game game) {
    this.player = player;
    this.game = game;
    this.joinGame = joinGame;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Instant getJoinGame() {
    return joinGame;
  }

  @JsonIgnore
  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  @JsonIgnore
  public Game getGame() {
    return game;
  }

  @JsonIgnore
  public List<Ship> getShips() {
    return ships;
  }

  public void addShip(Ship ship) {
    ship.setGamePlayer(this);
    ships.add(ship);
  }

  public Set<Salvo> getSalvoes() {
    return salvoes;
  }

  public Map<String, Object> makeGamePlayerDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", this.getId());
    dto.put("player", this.getPlayer().makePlayerDTO());
    return dto;
  }

}
