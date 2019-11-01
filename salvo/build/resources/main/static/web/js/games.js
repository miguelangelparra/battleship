$(function() {
    loadData()
});
var gameList = document.getElementById("game-list")
var dataRaw

function updateViewGames(data) {
    console.log(data)
    gameList.innerHTML = ""
    document.getElementById("MessageToLogin").setAttribute("style", "display:none");
    if (data.player != "Guest") {
        let btnCreate = document.getElementById("btnCreate")
        btnCreate.style.display = "block"
    }
    var htmlListGames = data.games.map(function(game) {

        let line = document.createElement("li")

        function toCreateBtn(id, message, func) {
            if (data.player != "Guest") {
                let btnJoinGame = document.createElement("button")
                btnJoinGame.setAttribute("onclick", func)
                btnJoinGame.classList.add('btnJoinGame', 'btn', 'bg-success')
                btnJoinGame.setAttribute("data-gameId", id)
                btnJoinGame.innerText = message
                line.appendChild(btnJoinGame)
            }
        }
        game.gamePlayers.map(function(gamePlayer) {

            if (game.gamePlayers.length == 2 && gamePlayer.player.email == data.player.email) {
                toCreateBtn(gamePlayer.id, "Re-Entry", "clickReEntry(this)")
                return //idGamePlayer = gamePlayer.id
            } else if (game.gamePlayers.length == 1) {
                toCreateBtn(game.id, "Join!", "clickJoin(this)")
                return
            }
        })

        var spanUser = document.getElementById("spanUser")
        spanUser.textContent = data.player.email

        line.classList.add('list-group-item')

        let textDate = document.createElement("h5")
        textDate.innerHTML = new Date(game.created).toLocaleString()

        let textPlayer = document.createElement("h6")
        textPlayer.innerHTML = game.gamePlayers.map(function(element) {
            return element.player.email
        }).join(' VS ');

        line.appendChild(textDate)
        line.appendChild(textPlayer)

        return gameList.appendChild(line)
    })
}

function updateViewLBoard(data) {
    var htmlList = data.map(function(score) {
        return '<tr><td>' + score.email + '</td>' +
            '<td>' + score.scores.total + '</td>' +
            '<td>' + score.scores.won + '</td>' +
            '<td>' + score.scores.lost + '</td>' +
            '<td>' + score.scores.tied + '</td></tr>';
    }).join('');
    document.getElementById("leader-list").innerHTML = htmlList;
}

function loadData() {
    $.get("http://localhost:8080/api/games")
        .done(function(data) {
            updateViewGames(data);
        })
        .fail(function(jqXHR, textStatus) {
            //  alert( "Failed: " + textStatus );
        });

    $.get("http://localhost:8080/api/leaderBoard")
        .done(function(data) {
            updateViewLBoard(data);
        })
        .fail(function(jqXHR, textStatus) {
            alert("Failed: " + textStatus);
        });
}

function clickReEntry(e) {
    let dataJoinGame = e.getAttribute("data-gameid")
    location.href = "/web/game.html?gp=" + dataJoinGame;
}

function clickJoin(e) {
    $.post("/api/game/" + e.getAttribute("data-gameid") + "/players").done(function(data) {
            location.href = "game.html?gp=" + data.gpid.valueOf()
        })
        .fail(function(jqXHR, textStatus) {
            console.log(jqXHR.responseText)
        })
}

function toCreateGame() {
    $.post("/api/games").done(function(data) {
        location.href = "/web/game.html?gp=" + data.gpid.valueOf()
    }).
    fail(function(jqXHR, textStatus) {
        console.log(jqXHR.responseText)
    })
}

/*formulario de login*/
function toLogin() {
    var nameUsu = document.getElementById("inpEmail").value
    var passwordUsu = document.getElementById("inpPassword").value
    console.log("por aqui pase")
    loginFunc(nameUsu, passwordUsu)

}

function toLogUp() {
    let nameUsu = document.getElementById("inpEmail").value
    let passwordUsu = document.getElementById("inpPassword").value
    $.post("/api/players", {
        "email": nameUsu,
        "password": passwordUsu
    }).done(function(data) {
        loginFunc(nameUsu, passwordUsu)
        console.log(data)
        console.log(" Successed LogUP!");
    }).fail(function(jqXHR, textStatus) {
        //console.log(jqXHR.status)
        alert("Failed: " + jqXHR.responseText);
    });
}

function toLogOut() {
    $.post("/api/logout").done(function() {
        console.log("logged out");
        location.reload();
    })
}

/*function loginFunc(nameUsu, passwordUsu) {

    var body = {
        "name": nameUsu,
        "password": passwordUsu
    }

    fetch('http://localhost:8080/api/login', {
            method: 'POST',
            body: new URLSearchParams(body),
            header: 'Content-Type = application/x-www-form-urlencoded'
        })
        //  .then((resp) =>  resp.json())
        .then(function(data) {
            console.log("login!")
            $.get("http://localhost:8080/web/games.html").done(() => {
                loadData();
                document.getElementById("inpEmail").setAttribute("style", "display:none")
                document.getElementById("inpPassword").setAttribute("style", "display:none")
                document.getElementById("btnLoginModal").setAttribute("style", "display:none")
                document.getElementById("btnLogUp").setAttribute("style", "display:none")
                document.getElementById("btnLogout").setAttribute("style", "display:inline")

            })


            //  location.reload()  })

        })
}*/

function loginFunc(nameUsu, passwordUsu) {

    $.ajax({
        type: 'POST',
        url: '/api/login',
        data: {
            name: nameUsu,
            password: passwordUsu
        },
        success: function() {
            console.log("login!")
            $.get("http://localhost:8080/web/games.html")

            loadData();
            document.getElementById("inpEmail").setAttribute("style", "display:none")
            document.getElementById("inpPassword").setAttribute("style", "display:none")
            document.getElementById("btnLoginModal").setAttribute("style", "display:none")
            document.getElementById("btnLogUp").setAttribute("style", "display:none")
            document.getElementById("btnLogout").setAttribute("style", "display:inline")
                //  location.reload()
        },
        error: function(data) {
            alert("No se ha podido obtener la información, compruebe sus datos de acceso")
            alert(data.responseJSON.error)
        }
    })

}



/*
Codigo anterior
$(function(){
    var listGames = $("ol")
    var games = $.getJSON({
        url:"http://localhost:8080/api/games",
        })
        .done(function (data)
            {
            data.map(function(data)
                {
                let date = new Date(data.created).toLocaleString()
                var arrGame = []
                arrGame.push(date)
                for (let i = 0; i < data.gamePlayers.length; i++){
                let email = data.gamePlayers[i].player.player
                arrGame.push(email)
                }
                listGames.append(
                '<li>' + arrGame + '</li>'
                )})
            }
            )}
)



*/
//line.innerHTML=new Date(game.created).toLocaleString()
//                   + ' | <br> '
//                 + game.gamePlayers.map(function(element) { return element.player.email}).join(', ')

/* '<li class="list-group-item">'
      + new Date(game.created).toLocaleString()
      + ' | <br> '
      + game.gamePlayers.map(function(element) { return element.player.email}).join(', ')
       + '</li>' + btnJoinGame;
  }).join('');
    document.getElementById("game-list").innerHTML = htmlListGames;

   var htmlListGamesOwn = data.gamesOwn.map(function (game) {
         return  '<li class="list-group-item">' + new Date(game.joinGame).toLocaleString() /*+ ' | <br> ' + game.map(function(element) { return element.player.email}).join(', ')  +'</li>';
     }).join('');*/
//document.getElementById("game-list-own").innerHTML = htmlListGamesOwn;
//   return document.getElementById("game-list").appendChild(line)