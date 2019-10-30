var app = new Vue({
    el: '#vm',
    data: {
        dataRaw: "inicio",
        message: 'Hello Vue!',
        arrLocations: [],
    },
    methods:
    {
        getParameterByName: function (name) {
            var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
            return match && decodeURIComponent(match[1].replace(/\+/g,' '));
        },

        loadData: function () {
            console.log(this.dataRaw)
            $.get('/api/game_view/' + this.getParameterByName('gp'))
                .done(function (d) {
                    this.dataRaw = d;
                    console.log(this.dataRaw.ships )
                })
                .fail(function (jqXHR, textStatus) {
                    alert("Failed: " + textStatus);
                });
        },

        toAddShips: function () {
            $.post({
                url: '/api/games/players/' + this.getParameterByName('gp') + '/ships',
                data: JSON.stringify(arrLocations),
                dataType: 'text',
                contentType: 'application/json'
            })
                .done(function (data) {
                    location.reload()
                })
                .fail(function (jqXHR, textStatus) {
                    console.log(jqXHR.status)
                })
        },

        toLogOut: function (){
            $.post("/api/logout")
                .done(function () {
                    location.href = "/web/games.html"
                })
        },
        isHit: function(shipLocation, salvoes, playerId) {
            var turn = 0;
            salvoes.forEach(function (salvo) {
                if (salvo.player != playerId)
                    salvo.locations.forEach(function (location) {
                        if (shipLocation === location)
                            turn = salvo.turn;
                    });
            });
            return turn;},

        toDrawShips:  function () {
            console.log(this.dataRaw + "bola")
            console.log(this.dataRaw.ships)
            this.dataRaw.ships.forEach(function (shipPiece) {
            shipPiece.locations.forEach(function (shipLocation) {
                if (isHit(shipLocation, data.salvoes, playerInfo[0].id) != 0) {
                    $('#B_' + shipLocation).addClass('ship-piece-hited');
                    $('#B_' + shipLocation).text(isHit(shipLocation, this.dataRaw.salvoes, playerInfo[0].id));
                } else
                    $('#B_' + shipLocation).addClass('ship-piece');
            });
        });
    },

    },

    computed:{
       
},

    created: function () {
        console.log("CAbeza")
        this.loadData()

    },
})