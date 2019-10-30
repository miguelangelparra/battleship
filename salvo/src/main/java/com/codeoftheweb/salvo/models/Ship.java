package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Entity
public class Ship {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
  @GenericGenerator(name = "native", strategy = "native")
  private long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "gamePlayer_id")
  private GamePlayer gamePlayer;

  @ElementCollection
  @Column(name = "locations")
  private List<String> locations = new ArrayList<>();
  private String type;

  public Ship() {
  }

  public Ship(GamePlayer gamePlayer, String typeShip, List<String> locations) {
    this.gamePlayer = gamePlayer;
    this.type = typeShip;
    this.locations = locations;
  }

  public long getId() {
    return id;
  }

  public GamePlayer getGamePlayer() {
    return gamePlayer;
  }

  public List<String> getLocations() {
    return locations;
  }

  public void setLocations(List<String> locations) {
    this.locations = locations;
  }

  public String getTypeShip() {
    return type;
  }

  public void setTypeShip(String typeShip) {
    this.type = typeShip;
  }

  public void setGamePlayer(GamePlayer gamePlayer) {
    this.gamePlayer = gamePlayer;
  }

  public Map<String, Object> makeShipDTO() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("type", this.type);
    dto.put("locations", this.locations);
    return dto;
  }

}
