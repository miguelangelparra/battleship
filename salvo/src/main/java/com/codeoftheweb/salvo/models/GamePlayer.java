package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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

    /*@OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<History> histories;*/

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Ship> ships;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER)
    private Set<Salvo> salves;

    private boolean allShipSunk = false;

    private boolean scoreSaved = false;

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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Set<Salvo> getSalves() {
        return salves;
    }

    public boolean isAllShipSunk() {
        return allShipSunk;
    }

    public void setAllShipSunk(boolean allShipSunk) {
        this.allShipSunk = allShipSunk;
    }

    public boolean isScoreSaved() {
        return scoreSaved;
    }

    public void setScoreSaved(boolean scoreSaved) {
        this.scoreSaved = scoreSaved;
    }

    public boolean toCalculateAllShipSunk() {
        if (((getShips().stream().filter(sh -> sh.isSink() == true)).collect(Collectors.toList()).size() == getShips().size()) && getShips().size() != 0) {
            setAllShipSunk(true);
        }
        return isAllShipSunk();
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("player", this.getPlayer().makePlayerDTO());
         dto.put("damage",this.getShips().stream().map(Ship::makeShipPublicDTO));
        return dto;
    }

}

  /*public Set<History> getHistories() {
    return histories;
  }

  public void setHistories(Set<History> histories) {
    this.histories = histories;
  }*/
