package com.codeoftheweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    private String email;

    private String userName;

    private String name;

    private String lastName;

    private String password;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @JsonIgnore
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> scores;

    public Player() {
    }

    public Player(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Player(String name, String lastName, String userName, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return email + " " + userName;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getEmail());
        return dto;
    }

    public Map<String, Object> makePlayerScoreDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        Map<String, Object> score = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("email", this.getEmail());
        dto.put("scores", score);
        score.put("total", this.getTotalScore());
        score.put("won", this.getWinScore());
        score.put("lost", this.getLostScore());
        score.put("tied", this.getTiedScore());
        return dto;
    }

    public long getTotalScore() {
        return this.getWinScore() + this.getTiedScore();
    }

    public long getWinScore() {
        return this.getScores().stream()
                .filter(score -> score.getScore() == 2)
                .count();
    }

    public long getLostScore() {
        return this.getScores().stream()
                .filter(score -> score.getScore() == 0)
                .count();
    }

    public long getTiedScore() {
        return this.getScores().stream()
                .filter(score -> score.getScore() == 1)
                .count();
    }
}