package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    @JsonIgnore
    private GamePlayer gamePlayer;


  private int turn;
  private String shipType;
  private boolean hint;

   public History(){};

    public History(int turn, GamePlayer gamePlayer, String shipType ,Boolean hint) {
        this.turn = turn;
        this.gamePlayer = gamePlayer;
        this.shipType = shipType;
        this.hint = hint;
    }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
  @JsonIgnore
  public GamePlayer getGamePlayer() {
    return gamePlayer;
  }

  public void setGamePlayer(GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
  }

  public int getTurn() {
    return turn;
  }

  public void setTurn(int turn) {
    this.turn = turn;
  }

  public String getShipType() {
    return shipType;
  }

  public void setShipType(String shipType) {
    this.shipType = shipType;
  }

  public boolean isHint() {
    return hint;
  }

  public void setHint(boolean hint) {
    this.hint = hint;
  }
  @JsonIgnore
  public Map<String, Object> makeHistoryDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
    dto.put("player", this.gamePlayer);
    dto.put("ship", this.shipType);
        dto.put("hint",this.hint);
        return dto;
    }
}
