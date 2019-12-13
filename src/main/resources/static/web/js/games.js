$(function() {
  loadData();
});
var gameList = document.getElementById("game-list");
var dataRaw;

/*login*/
function toLogin() {
  var nameUsu = document.getElementById("inpEmail").value;
  var passwordUsu = document.getElementById("inpPassword").value;
  console.log("por aqui pase");
  loginFunc(nameUsu, passwordUsu);
}

function toLogUp() {
  let nameUsu = document.getElementById("inpEmail").value;
  let passwordUsu = document.getElementById("inpPassword").value;
  $.post("/api/players", {
    email: nameUsu,
    password: passwordUsu
  })
    .done(function(data) {
      loginFunc(nameUsu, passwordUsu);
      console.log(data);
      console.log(" Successed LogUP!");
    })
    .fail(function(jqXHR, textStatus) {
      //console.log(jqXHR.status)
      alert("Failed: " + jqXHR.responseText);
    });
}

function toLogOut() {
  $.post("/api/logout").done(function() {
    console.log("logged out");
    location.reload();
  });
}

function loginFunc(nameUsu, passwordUsu) {
  var body = {
    name: nameUsu,
    password: passwordUsu
  };

  fetch("/api/login", {
    method: "POST",
    body: new URLSearchParams(body),
    header: "Content-Type = application/x-www-form-urlencoded"
  })
    //  .then((resp) =>  resp.json())
    .then(function(data) {
      if (data.ok) {
        $.get("/web/games.html").done(() => {
          loadData();
          document
            .getElementById("btnLoginModal")
            .setAttribute("style", "display:none");
          document
            .getElementById("btnLogout")
            .setAttribute("style", "display:inline");
        });
      }
    });
}

//Botones de juego
function toReEntry(e) {
  let dataJoinGame = e.getAttribute("data-gameid");
  location.href = "/web/game.html?gp=" + dataJoinGame;
}

function toJoin(e) {
  $.post("/api/game/" + e.getAttribute("data-gameid") + "/players")
    .done(function(data) {
      location.href = "game.html?gp=" + data.gpid.valueOf();
    })
    .fail(function(jqXHR, textStatus) {
      console.log(jqXHR.responseText);
    });
}

function toCreateGame() {
  $.post("/api/games")
    .done(function(data) {
      location.href = "/web/game.html?gp=" + data.gpid.valueOf();
    })
    .fail(function(jqXHR, textStatus) {
      console.log(jqXHR.responseText);
    });
}

//Carga y actualizacion de datos
function loadData() {
  $.get("/api/games")
    .done(function(data) {
      updateViewGames(data);
    })
    .fail(function(jqXHR, textStatus) {
      //  alert( "Failed: " + textStatus );
    });

  $.get("/api/leaderBoard")
    .done(function(data) {
      updateViewLBoard(data);
    })
    .fail(function(jqXHR, textStatus) {
      alert("Failed: " + textStatus);
    });
}

function updateViewGames(data) {
  console.log(data);
  gameList.innerHTML = "";
  document.getElementById("MessageToLogin")

  if (data.player != "Guest") {
    let btnCreate = document.getElementById("btnCreate");
    btnCreate.style.display = "block";
  }

 /* var htmlList = data.games
  .map(function(game) {
    let fecha = new Date(game.created).toLocaleString()
    let gps= game.gamePlayers.sort((a,b)=>a.player.email-b.player.email)
    return (
"<li class='list-group-item'>" +
"<span>"+ fecha + "</span>"+
"<span>"+ gps[0].player.email + "VS" + (gps.length ==2? gps[1].player.email : '') +  "</span>"+ 
 game.gamePlayers.map(function(gamePlayer) {
  //game.gamePlayers.length == 2 &&
  if (gamePlayer.player.email == data.player.email) {
    toCreateBtn(gamePlayer.id, "Re-Entry", "toReEntry(this)");
    return; //idGamePlayer = gamePlayer.id
  } else if (
    game.gamePlayers.length == 1 &&
    gamePlayer.player.email != data.player.email
  ) {
    toCreateBtn(game.id, "Join!", "toJoin(this)");
    return;
  }
})+
"</li>"
    );
  })
  .join("");
document.getElementById("game-list").innerHTML = htmlList;
}

function toCreateBtn(id, message, func) {
 //if (data.player != "Guest") {
   /* let btnJoinGame = document.createElement("button");
    btnJoinGame.setAttribute("onclick", func);
    btnJoinGame.classList.add("btnJoinGame", "btn", "bg-warning");
    btnJoinGame.setAttribute("data-gameId", id);
    btnJoinGame.innerText = message;
    line.appendChild(btnJoinGame);
    let btn = ('<button onclick="' +func+ 'data-gameId="'+ id+ '">' + message+'</button>')
    return  btn
  //}
*/
/////////
  data.games.map(function(game) {

    let line = document.createElement("li");

    var spanUser = document.getElementById("spanUser");
    spanUser.textContent = data.player.email;

    line.classList.add("list-group-item");

    let textDate = document.createElement("h5");
    textDate.innerHTML = new Date(game.created).toLocaleString();

    let textPlayer = document.createElement("h6");
    textPlayer.innerHTML = game.gamePlayers
      .map(function(element) {
        return element.player.email;
      })
      .join(" VS ");

    line.appendChild(textDate);
    line.appendChild(textPlayer);


    function toCreateBtn(id, message, func) {
      if (data.player != "Guest") {
        let btnJoinGame = document.createElement("button");
        btnJoinGame.setAttribute("onclick", func);
        btnJoinGame.classList.add("btnJoinGame", "btn", "bg-warning");
        btnJoinGame.setAttribute("data-gameId", id);
        btnJoinGame.innerText = message;
        line.appendChild(btnJoinGame);
      }
    }
    game.gamePlayers.map(function(gamePlayer) {
      //game.gamePlayers.length == 2 &&
      if (gamePlayer.player.email == data.player.email) {
        toCreateBtn(gamePlayer.id, "Re-Entry", "toReEntry(this)");
        return; //idGamePlayer = gamePlayer.id
      } else if (
        game.gamePlayers.length == 1 &&
        gamePlayer.player.email != data.player.email
      ) {
        toCreateBtn(game.id, "Join!", "toJoin(this)");
        return;
      }
    });

    return gameList.appendChild(line);
  });
}

////////////

function updateViewLBoard(data) {
  var htmlList = data
    .map(function(score) {
      return (
        "<tr><td>" +
        score.email +
        "</td>" +
        "<td>" +
        score.scores.total +
        "</td>" +
        "<td>" +
        score.scores.won +
        "</td>" +
        "<td>" +
        score.scores.lost +
        "</td>" +
        "<td>" +
        score.scores.tied +
        "</td></tr>"
      );
    })
    .join("");
  document.getElementById("leader-list").innerHTML = htmlList;
}

setInterval(() => {
  loadData();
}, 4000);
