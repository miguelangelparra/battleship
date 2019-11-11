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

    private int turn;
    private String shipType;
   private Long player;
   private boolean hint;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private Game game;



   public History(){};

    public History(int turn, String shipType, Long player, Game game,Boolean hint) {
        this.turn = turn;
        this.shipType = shipType;
        this.player = player;
        this.game = game;
        this.hint = hint;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    @JsonIgnore
    public long getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
    @JsonIgnore
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
        hint = hint;
    }
@JsonIgnore
    public long getPlayer() {
        return player;
    }

    public void setPlayer(long player) {
        this.player = player;
    }
    @JsonIgnore
    public Map<String, Object> makeHistoryDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turn);
        dto.put("ship", this.shipType);
        dto.put("player", this.player);
        dto.put("hint",this.hint);
        return dto;
    }
}
