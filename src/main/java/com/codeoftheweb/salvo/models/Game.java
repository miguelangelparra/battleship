package com.codeoftheweb.salvo.models;

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

    //Enlaza con tabla gamePleayer en columna "game"
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers;

    //Enlaza con tabla score en columna "game"
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private List<Score> scores;

    //Enlaza con tabla histories en columna "game"
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<History> histories;

    private int status ;

    private int winnerGP =-1;


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

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Set<History> getHistories() {
        return histories;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getWinnerGP() {
        return winnerGP;
    }

    //Calcula el estado de juego
    public int toCalculateStateGame(GamePlayer gp) {
System.out.println("estado del juego" + getStatus());
        if (getStatus() != 5) {

            //Transforma el set en array para poder procesar los datos
            GamePlayer[] myArray = new GamePlayer[this.getGamePlayers().size()];
            this.getGamePlayers().toArray(myArray);

//Busca al oponente
            GamePlayer oponent = new GamePlayer();
            for (GamePlayer gamePlayer : this.getGamePlayers()) {
                if (gp.getId() != gamePlayer.getId()) {
                    oponent = gamePlayer;
                }
            }

            //Comprueba si el otro jugador ya termino su turno
            if (this.getGamePlayers().size() == 2 && (gp.getSalves().size() > oponent.getSalves().size())) {
                this.setStatus(2);
            } else if ((gp.getShips().size() != 0)
                    && (gp.toCalculateAllShipSunk() || oponent.toCalculateAllShipSunk())
                    && (oponent.getSalves().size() == gp.getSalves().size())
                    &&(status < 4) ) {
                //Comprueba si alguno de los jugadores tiene todos los barcos hundidos, juego terminado
                this.setStatus(4);
                if(gp.isScoreSaved() == false && status!=6){
                    this.toCreateScore(gp, oponent);
                  //  this.setStatus(5);
                }
            }
            //Comprueba si hay otro jugador
            else if (this.getGamePlayers().size() < 2) {
                this.setStatus(0);
            }  //Comprueba si los jugadores colocaron los barcos
            else if (gp.getShips().size() == 0 || oponent.getShips().size() == 0) {
                this.setStatus(1);
            }
           else if (status<3){//Responde si aun no se han hundido los barcos, colocar salvoes.
                this.setStatus(3);
            }
        }

        return this.getStatus();
    }

    public void toCreateScore(GamePlayer playerGP, GamePlayer opponetGP) {
      Game game = playerGP.getGame();
              Player player = playerGP.getPlayer();
              Player opponent=opponetGP.getPlayer();
        Score score1  ;
        Score score2 ;
        if (playerGP.isAllShipSunk() && opponetGP.isAllShipSunk()) {
            score1 = new Score(game, player, 1);
            score2 = new Score(game, opponent, 1);
            winnerGP=0;
        } else if (playerGP.isAllShipSunk()) {
            score1 = new Score(game, player, 0);
            score2 = new Score(game, opponent, 2);
            winnerGP= Math.toIntExact(opponetGP.getId());
        } else {
            score1 = new Score(game, player, 2);
            score2 = new Score(game, opponent, 0);
            winnerGP= Math.toIntExact(playerGP.getId());
        }
        //System.out.println("create score fue ejecutado por : " + playerGP.getId());
        this.scores.add(score1);
        this.scores.add(score2);
        playerGP.setScoreSaved(true);
        opponetGP.setScoreSaved(true);

        if (scores.size() == 2){
            this.setStatus(5);
        }
    }

    public List<Score> toGetScore(){
        return this.scores;
    }

    public Map<String, Object> getGameDTO() {

        Map<String, Object> dto = new LinkedHashMap<>();

        dto.put("id", this.getId());
        dto.put("winner",this.getWinnerGP());
        dto.put("created", this.getDateGame().getTime());
        dto.put("gamePlayers", this.getGamePlayersList());
        dto.put("score", this.getScoresList());
      //  dto.put("histories", this.getHistories());

        return dto;
    }

   public List<Map<String, Object>> getScoresList() {
        return this.scores.stream()
                .map(Score::makeScoreDTO)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getGamePlayersList() {
        return this.gamePlayers.stream()
                .map(GamePlayer::makeGamePlayerDTO)
                .collect(Collectors.toList());
    }
 /* @JsonIgnore
  public List<Map<String, Object>> getHistoriesList() {
    return this.histories.stream()
            .map(history -> history.makeHistoryDTO())
            .collect(Collectors.toList());
  }*/

}
