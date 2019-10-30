$(function() {
    loadData()
});

function updateViewGames(data) {
  var htmlList = data.map(function (game) {
      return  '<li class="list-group-item">' + new Date(game.created).toLocaleString() + ' | <br> ' + game.gamePlayers.map(function(element) { return element.player.email}).join(', ')  +'</li>';
  }).join('');
  document.getElementById("game-list").innerHTML = htmlList;
}

function updateViewLBoard(data) {
  var htmlList = data.map(function (score) {
      return  '<tr><td>' + score.email + '</td>'
              + '<td>' + score.scores.total + '</td>'
              + '<td>' + score.scores.won + '</td>'
              + '<td>' + score.scores.lost + '</td>'
              + '<td>' + score.scores.tied + '</td></tr>';
  }).join('');
  document.getElementById("leader-list").innerHTML = htmlList;
}

function loadData() {
  $.get("http://localhost:8080/api/games")
    .done(function(data) {
      updateViewGames(data);
    })
    .fail(function( jqXHR, textStatus ) {
      alert( "Failed: " + textStatus );
    });

  $.get("http://localhost:8080/api/leaderBoard")
    .done(function(data) {
      updateViewLBoard(data);
    })
    .fail(function( jqXHR, textStatus ) {
      alert( "Failed: " + textStatus );
    });
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
)*/
