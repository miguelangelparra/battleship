package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.Instant;
import java.util.*;


@SpringBootApplication
public class SalvoApplication {

  public static void main(String[] args) {
    SpringApplication.run(SalvoApplication.class, args);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Bean
  public CommandLineRunner initData(GameRepository gameRepository, PlayerRepository playerRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
    return (args) -> {

      Player player1 = new Player("Jack", "Bauer", "j.bauer", "j.bauer@ctu.gov", passwordEncoder.encode("24"));
      Player player2 = new Player("Chloe", "O'Brian", "C.Brian", "chloe@ctu.gov", passwordEncoder.encode("42"));
      Player player3 = new Player("Kim", "Bauer", "K.Bauer", "a.Bauer@ctu.gov", passwordEncoder.encode("kb"));
      Player player4 = new Player("Tonny", "Almeida", "AlmeidaTon", "almeida@gmail.com", passwordEncoder.encode("mole"));
      Player player5 = new Player("Miguelangel", "Parra", "MigParra", "miguel@gmail.com", passwordEncoder.encode("1234"));
      playerRepository.saveAll(Arrays.asList(player1, player2, player3, player4, player5));

      Date date = new Date();
      Instant date1 = date.toInstant().plusSeconds(3600);
      Instant date2 = date.toInstant().plusSeconds(7200);
      Instant date3 = date.toInstant().plusSeconds(9000);
      Instant date4 = date.toInstant().plusSeconds(10800);
      Instant date5 = date.toInstant().plusSeconds(12600);
      Instant date6 = date.toInstant().plusSeconds(14400);
      Instant date7 = date.toInstant().plusSeconds(16200);
      Instant date8 = date.toInstant().plusSeconds(19000);
      Instant date9 = date.toInstant().plusSeconds(20000);

      Game game1 = new Game();
      Game game2 = new Game(Date.from((date1)));
      Game game3 = new Game(Date.from((date2)));
      Game game4 = new Game(Date.from((date3)));
      Game game5 = new Game(Date.from((date4)));
      Game game6 = new Game(Date.from((date5)));
      Game game7 = new Game(Date.from((date6)));
      Game game8 = new Game(Date.from((date7)));
      Game game9 = new Game(Date.from((date9)));
      gameRepository.saveAll(Arrays.asList(game1, game2, game3, game4, game5, game6, game7, game8, game9));

      GamePlayer gamePlayer1 = new GamePlayer(date1, player1, game1);
      GamePlayer gamePlayer2 = new GamePlayer(date2, player2, game1);
      GamePlayer gamePlayer3 = new GamePlayer(date3, player1, game2);
      GamePlayer gamePlayer4 = new GamePlayer(date4, player2, game2);
      GamePlayer gamePlayer5 = new GamePlayer(date5, player2, game3);
      GamePlayer gamePlayer6 = new GamePlayer(date6, player4, game3);
      GamePlayer gamePlayer7 = new GamePlayer(date7, player2, game4);
      GamePlayer gamePlayer8 = new GamePlayer(date8, player1, game4);
      GamePlayer gamePlayer9 = new GamePlayer(date9, player4, game5);
      GamePlayer gamePlayer10 = new GamePlayer(date5, player1, game5);
      GamePlayer gamePlayer11 = new GamePlayer(date3, player3, game6);
      GamePlayer gamePlayer12 = new GamePlayer(date8, player4, game7);
      GamePlayer gamePlayer13 = new GamePlayer(date9, player3, game8);
      GamePlayer gamePlayer14 = new GamePlayer(date1, player4, game8);
      GamePlayer gamePlayer15 = new GamePlayer(date4, player5, game9);
      gamePlayerRepository.saveAll(Arrays.asList(gamePlayer1, gamePlayer2, gamePlayer3, gamePlayer4, gamePlayer5, gamePlayer6, gamePlayer7, gamePlayer8, gamePlayer9, gamePlayer10, gamePlayer11, gamePlayer12, gamePlayer13, gamePlayer14, gamePlayer15));


      String carrier = "Carrier";
      String battleship = "Battleship";
      String submarine = "Submarine";
      String destroyer = "Destroyer";
      String patrolBoat = "Patrol Boat";
      Ship ship1 = new Ship(gamePlayer1, destroyer, Arrays.asList("72", "73", "74"));
      Ship ship2 = new Ship(gamePlayer1, submarine, Arrays.asList("41", "51", "61"));
      Ship ship3 = new Ship(gamePlayer1, patrolBoat, Arrays.asList("14", "15"));
      Ship ship4 = new Ship(gamePlayer2, destroyer, Arrays.asList("25", "35", "45"));
      Ship ship5 = new Ship(gamePlayer2, patrolBoat, Arrays.asList("51", "52"));
      Ship ship6 = new Ship(gamePlayer3, destroyer, Arrays.asList("15", "25", "35"));
      Ship ship7 = new Ship(gamePlayer3, patrolBoat, Arrays.asList("26", "27"));
      Ship ship8 = new Ship(gamePlayer4, submarine, Arrays.asList("02", "03", "04"));
      Ship ship9 = new Ship(gamePlayer4, patrolBoat, Arrays.asList("66", "76"));
      Ship ship10 = new Ship(gamePlayer6, destroyer, Arrays.asList("15", "25", "35"));
      Ship ship11 = new Ship(gamePlayer6, patrolBoat, Arrays.asList("26", "27"));
      Ship ship12 = new Ship(gamePlayer5, submarine, Arrays.asList("02", "03", "04"));
      Ship ship13 = new Ship(gamePlayer5, patrolBoat, Arrays.asList("66", "76"));
      Ship ship14 = new Ship(gamePlayer7, destroyer, Arrays.asList("15", "25", "35"));
      Ship ship15 = new Ship(gamePlayer7, patrolBoat, Arrays.asList("26", "27"));
      Ship ship16 = new Ship(gamePlayer8, submarine, Arrays.asList("02", "03", "04"));
      Ship ship17 = new Ship(gamePlayer8, patrolBoat, Arrays.asList("66", "76"));
      Ship ship18 = new Ship(gamePlayer9, destroyer, Arrays.asList("15", "25", "35"));
      Ship ship19 = new Ship(gamePlayer9, patrolBoat, Arrays.asList("26", "27"));
      Ship ship20 = new Ship(gamePlayer10, submarine, Arrays.asList("02", "03", "04"));
      Ship ship21 = new Ship(gamePlayer10, patrolBoat, Arrays.asList("66", "76"));
      Ship ship22 = new Ship(gamePlayer11, destroyer, Arrays.asList("15", "25", "35"));
      Ship ship23 = new Ship(gamePlayer12, patrolBoat, Arrays.asList("26", "27"));
      Ship ship24 = new Ship(gamePlayer13, destroyer, Arrays.asList("15", "25", "35"));
      Ship ship25 = new Ship(gamePlayer14, patrolBoat, Arrays.asList("26", "27"));
      shipRepository.saveAll(Arrays.asList(ship1, ship2, ship3, ship4, ship5, ship6, ship7, ship8, ship9, ship10, ship11, ship12, ship13, ship14, ship15, ship16, ship17, ship18, ship19, ship20, ship21, ship22, ship23, ship24, ship25));

      Set<String> sL1 = new HashSet<>(Arrays.asList("35", "25", "51"));
      Set<String> sL2 = new HashSet<>(Arrays.asList("14", "15", "16"));
      Set<String> sL3 = new HashSet<>(Arrays.asList("45", "52"));
      Set<String> sL4 = new HashSet<>(Arrays.asList("41", "63", "02"));

      Salvo salvo1 = new Salvo(1, sL1, gamePlayer1);
      Salvo salvo2 = new Salvo(2, sL3, gamePlayer1);
      Salvo salvo3 = new Salvo(1, sL2, gamePlayer2);
      Salvo salvo4 = new Salvo(2, sL4, gamePlayer2);
      Salvo salvo5 = new Salvo(1, sL1, gamePlayer3);
      Salvo salvo6 = new Salvo(2, sL3, gamePlayer3);
      Salvo salvo7 = new Salvo(1, sL2, gamePlayer4);
      Salvo salvo8 = new Salvo(2, sL4, gamePlayer4);
      salvoRepository.saveAll(Arrays.asList(salvo1, salvo2, salvo3, salvo4,salvo5, salvo6, salvo7, salvo8));


      Score score1 = new Score(game1, player1, 2);
      Score score2 = new Score(game1, player2, 0);
      Score score3 = new Score(game2, player2, 1);
      Score score4 = new Score(game2, player1, 1);
      Score score5 = new Score(game3, player2, 2);
      Score score6 = new Score(game3, player4, 0);
      scoreRepository.saveAll(Arrays.asList(score1, score2, score3, score4, score5, score6));
    };
  }
}


@EnableWebSecurity
@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

  @Autowired
  PlayerRepository playerRepository;

  @Override
  public void init(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(name -> {
      Player player = playerRepository.findByEmail(name);
      if (player != null) {
        return new User(player.getEmail(), player.getPassword(),
          AuthorityUtils.createAuthorityList("USER"));
      } else {
        throw new UsernameNotFoundException("Unknown user: " + name);
      }
    });
  }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
      .antMatchers("/api/game_view/**").hasAuthority("USER")
      .antMatchers("/web/**").permitAll()
      .antMatchers("/api/**").permitAll()
      .antMatchers("/rest/**").permitAll();
//Anyrequest().permitAll

    http.formLogin()
      .usernameParameter("name")
      .passwordParameter("password")
      .loginPage("/api/login");

    http.logout().logoutUrl("/api/logout");


    // turn off checking for CSRF tokens
    http.csrf().disable();
    // if user is not authenticated, just send an authentication failure response
    http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
    // if login is successful, just clear the flags asking for authentication
    http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));
    // if login fails, just send an authentication failure response
    http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));
    // if logout is successful, just send a success response
    http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
  }

  private void clearAuthenticationAttributes(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
  }
}

