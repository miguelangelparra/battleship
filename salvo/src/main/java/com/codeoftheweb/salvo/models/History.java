package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")

    // @JoinColumn(name = "gamePlayer_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;


  private int turn;
  private String shipType;
  private boolean hint;
  private boolean sink ;

   public History(){};

    public History(int turn, GamePlayer gamePlayer, String shipType , Boolean hint, Set<Ship> shipsOponent) {
        this.turn = turn;
        this.game=gamePlayer.getGame();
       this.gamePlayer = gamePlayer;
        this.shipType = shipType;
        this.hint = hint;
        this.sink = isSink(shipsOponent);
    }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


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



  public boolean isSink(Set<Ship> shipsOponent){

    //for (Ship ship : gamePlayer.getShips()) {
    for (Ship ship : shipsOponent) {

      if(ship.getTypeShip() == this.getShipType()){
          ship.addDamage();
          System.out.println("el barco fue golpeado");
          this.sink=ship.isSink();
          return  ship.isSink();
        }
      }
   return false;

  }



  public Map<String, Object> makeHistoryDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("player", this.gamePlayer.getId());
        dto.put("ship", this.shipType);
        dto.put("hint",this.hint);
        dto.put("sink",this.sink);
        return dto;
    }
}
