//Dispara funcion de solicitud de datos al servidor
$(function () {
    loadData();
});

//Barcos
var tipos = [{
    tipo: "Aircraft",
    cantidad: 5,
    orientation: false,
},
{
    tipo: "Battleship",
    cantidad: 4,
    orientation: false
},
{
    tipo: "Submarine",
    cantidad: 3,
    orientation: false
},
{
    tipo: "Destroyer",
    cantidad: 3,
    orientation: false
},
{
    tipo: "Patrol",
    cantidad: 2,
    orientation: false
}
]
//Variables de logica
var arrLocation = [];
var arrAuxLocation = []
var bufferOrientation = false
var bufferTipoCant
var bufferNumConservado
var bufferNumCambiante
var bufferTipo
var salvoesCant = 0

//Creacion de elementos:
var barco = document.querySelectorAll(".barco")
var barcoHall = document.querySelectorAll(".barcoHall")
var dates = document.querySelectorAll('*[id^="B_"]');
var salvoHall = document.querySelectorAll('*[id^="S_"]');

//Configura barcos
barco.forEach((a) => {
    a.setAttribute("draggable", "true");
    a.setAttribute("ondragstart", "drag(event)")
})
//Configura casillero del Hall inicial de barcos
barcoHall.forEach((a) => {
    a.setAttribute("ondrop", "drop(event)")
    a.setAttribute("ondragover", "allowDrop(event)")
})
//Configura casilleros del Drag and Drop
dates.forEach(s => {
    s.setAttribute("ondrop", "drop(event)");
    s.setAttribute("ondragover", "allowDrop(event)")
    s.setAttribute("ondragleave", "dragLeave(event)")
})
//Configura casilleros de los Halls de Salvoes
salvoHall.forEach(sh => sh.setAttribute("onclick", "toLocateSalvo(this)"))

//Movimiento Drag and Drop:
//toma barco
function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
    // toGetOrientation(ev.target.id)
    toGetTipoCant(ev.target.id)
    toSetTipo(ev.target.id)
}
//Barco deja casillero
function dragLeave(ev) {
    ev.preventDefault();
    $('.ship-piece').removeClass('ship-piece')
    toDrawShips(arrLocation)
}
//Barco se posiciona encima de casillero posible mientra draguea
function allowDrop(ev) {
    arrAuxLocation = []
    ev.preventDefault();
    toPosIniPosCam(ev.target.id, bufferTipo)
    toBuildArrAuxLocation()
    arrAuxLocation.forEach(function (shipLocation) {
        $('#B_' + shipLocation).addClass('ship-piece');
    })
}
//Barco es soltado
function drop(ev) {
    ev.preventDefault();
    $('.ship-piece').removeClass('ship-piece')
    toUpdatePosition(ev, "barco")
    console.log(ev)
}
//Logica:
//Actualiza Posicion de Barcos
function toUpdatePosition(ev, tipo) {
    if (toValidatePosition()) {
        if (tipo === "barco") {
            //Si la actualizacion fue ejecutada por el Drag
            var data = ev.dataTransfer.getData("text");
            var ida = document.getElementById(ev.target.id)
            ida.appendChild(document.getElementById(data));
            toFindRepetedShip(data)
        } else {
            //Si la actualizacion fue ejecutada por el boton
            arrAuxLocation = []
            toBuildArrAuxLocation()
            $('.ship-piece').removeClass('ship-piece')
            toFindRepetedShip(tipo)
        }
    }
    toDrawShips(arrLocation)
}
//Setea valor de tipo de barco en buffer
function toSetTipo(tipo) {
    bufferTipo = tipo
}
//Busca el valor de la orientacion
function toGetOrientation(tipo) {
    bufferOrientation = (tipos.find(e => e.tipo == tipo)).orientation
    return (tipos.find(e => e.tipo == tipo)).orientation
}
//Busca la cantidad de espacios ocupados segun tipo de barco 
function toGetTipoCant(tipo) {
    bufferTipoCant = (tipos.find(e => e.tipo == tipo)).cantidad
}
//Identifica que numero debe cambiar segun la orientacion
function toPosIniPosCam(positionIni, tipo) {
    arrConverPosicionIni = positionIni.toString().split("")
    if (toGetOrientation(tipo)) {
        bufferNumCambiante = arrConverPosicionIni[2];
        bufferNumConservado = arrConverPosicionIni[3];
    } else {
        bufferNumCambiante = arrConverPosicionIni[3];
        bufferNumConservado = arrConverPosicionIni[2];
    }
}
//Cambia la orientacion
function toChangeOrientation(ev) {
    var button = document.getElementById(ev.id)
    var ship = button.parentElement.id
    var shipPosition = button.parentElement.parentElement.id
    toGetTipoCant(ship)
    toSetTipo(ship)
    tipos = (tipos.map(function (e) {
        if (e.tipo == ship) {
            var modificado = {
                "tipo": e.tipo,
                "cantidad": e.cantidad,
                "orientation": !e.orientation
            }
            return modificado
        } else {
            return e
        }
    }))
    toGetOrientation(ship)
    toPosIniPosCam(shipPosition, ship)
    toUpdatePosition(shipPosition, ev.name)
    toDrawShips(arrLocation)
}
//Busca si el barco ya fue posicionado una vez
function toFindRepetedShip(tipo) {
    if (arrLocation.some((e) => e.type == tipo)) {
        arrLocation.find(e => e.type == tipo).locations = arrAuxLocation
        arrAuxLocation = []
        //repetido
    } else {
        arrLocation.push({
            "type": tipo,
            "locations": arrAuxLocation
        })
        arrAuxLocation = []
        //No repetido
    }
}
//Construye array auxiliar de posiciones
function toBuildArrAuxLocation() {
    for (let i = parseInt(bufferNumCambiante); i < parseInt(bufferNumCambiante) + bufferTipoCant; i++) {
        let posicionFinal;
        if (bufferOrientation) {
            posicionFinal = i + bufferNumConservado
        } else {
            posicionFinal = bufferNumConservado + i
        }
        arrAuxLocation.push(posicionFinal);
    }
}
//Valida si la posicion del barco es posible
function toValidatePosition() {
    if ((parseInt(bufferNumCambiante) + bufferTipoCant) > 10) {
        alert("Posicion Invalida: Se sale del limite del tablero")
        return false
    }
    arrAuxLocation = []
    toBuildArrAuxLocation()

    var valido = true
    var auxLocation = arrLocation.filter((ship) => {
        return bufferTipo != ship.type
    })

    auxLocation.forEach(function (ship) {
        ship.locations.forEach((location) => {
            for (let i = 0; i < arrAuxLocation.length; i++) {
                if (arrAuxLocation[i] == location) {
                    valido = false
                }
            }
        })
    })
    if (!valido) {
        alert("Posicion Invalida: Choca con otro barco")
    }
    return valido
}
//Salvoes:
//Posicionamiento de Salvoes
function toLocateSalvo(e) {
    console.log(e.id)
    console.log(e)
    if ($('#' + e.id).hasClass('salvo-piece')) {
        $('#' + e.id).removeClass('salvo-piece')
        salvoesCant--
    } else if (salvoesCant < 5 && !$('#' + e.id).hasClass('salvo-piece-finished')) {
        $('#' + e.id).addClass('salvo-piece')
        salvoesCant++
    }
}
//Dibujos:
//Dibuja en barcos
function toDrawShips(ships, salvoes, playerInfo) {
    ships.forEach(function (shipPiece) {
        shipPiece.locations.forEach(function (shipLocation) {
            if (playerInfo == undefined) {
                $('#B_' + shipLocation).addClass('ship-piece');
            } else {
                if (isHit(shipLocation, salvoes, playerInfo[0].id) != 0) {
                    $('#B_' + shipLocation).addClass('ship-piece-hited');
                    $('#B_' + shipLocation).text(isHit(shipLocation, salvoes, playerInfo[0].id));
                } else
                    $('#B_' + shipLocation).addClass('ship-piece');
            }
        });
    });
}
//Dibuja salvos
function toDrawSalvoes(salvoes, playerInfo) {
    salvoes.forEach(function (gp) {

        gp.forEach(salvo => {
            if (playerInfo[0].id === salvo.player) {
                salvo.locations.forEach(function (location) {
                    $('#S_' + location).addClass('salvo-piece-finished');
                });
            } else {
                salvo.locations.forEach(function (location) {
                    $('#B_' + location).addClass('salvo');
                });
            }
        })
    })
}
//Dibuja Barcos impactados
function isHit(shipLocation, salvoes, playerId) {
    var turn = 0;
    salvoes.forEach(function (gp) {
        gp.forEach(salvo => {
            if (salvo.player != playerId)
                salvo.locations.forEach(function (location) {
                    if (shipLocation === location)
                        turn = salvo.turn;
                });
        })
    });
    return turn;
}
//Dibuja Historial
function toDrawHistorial(history, gpId) {
    var historialOwn = document.getElementById("historialOwn")
    var historialOponent = document.getElementById("historialOponent")
    historialOwn.innerHTML = ""
    historialOponent.innerHTML = ""
    historyOwn = []
    historyOponent = []

    history.forEach(historial => {
        if (historial.player == gpId) {
                historyOwn.push(historial)
        }
        else {
            historyOponent.push(historial)
        }
        console.log(historyOwn)
    })


    historyOwn.forEach(historial => {
        if (historial.sink) {
            sink = "sink"
        } else { sink = "hited" }
        var tr = document.createElement("tr")
        tr.innerHTML = "<td>" + historial.turn + "</td>" + "<td>" + historial.ship + "</td>" + "<td>" + sink + "</td>" 
        historialOwn.appendChild(tr)
    })


    historyOponent.forEach(historial => {
        if (historial.sink) {
            sink = "sink"
        } else { sink = "hited" }
        var tr = document.createElement("tr")
        tr.innerHTML = "<td>" + historial.turn + "</td>" + "<td>" + historial.ship + "</td>" + "<td>" + sink + "</td>"
        historialOponent.appendChild(tr)
    })

}

//Interaccion con servidor:
//Envia Barcos
function toAddShips() {
    if (arrLocation.length != 5) {
        alert("You didn´t place all yours ships")
    } else {
        $.post({
            url: '/api/games/players/' + toGetParameterByName('gp') + '/ships',
            data: JSON.stringify(arrLocation),
            dataType: "text",
            contentType: "application/json"
        })
            .done(function (data) {
                console.log("success");
                location.reload()
            })
            .fail(function (jqXHR, textStatus) {
                console.log(jqXHR.status)
            })
    }
}
//Envia Salvoes
function toAddSalvoes() {
    var salvoes = Array.from(document.getElementsByClassName("salvo-piece")).map(s => s.id.split("_")[1])
    console.log(salvoes)
    $.post({
        url: '/api/games/players/' + toGetParameterByName('gp') + '/salvos',
        data: JSON.stringify(salvoes),
        dataType: 'text',
        contentType: 'application/json'
    })
        .done(function (data) {
            console.log("Salvoes sent")
            location.reload()

        })
        .fail(function (jqXHR) {
            console.log(jqXHR.status)
        })
}
//Toma parametro de la url
function toGetParameterByName(name) {
    var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
};
//Realiza peticion de datos del juego
function loadData() {
    $.get('/api/game_view/' + toGetParameterByName('gp'))
        .done(function (data) {
            var playerInfo;
            console.log(data)
            if (data.gameplayers.length == 1) {
                playerInfo = [data.gameplayers[0].player]
            } else if (data.gameplayers[0].id == toGetParameterByName('gp')) {
                playerInfo = [data.gameplayers[0].player, data.gameplayers[1].player];
            } else
                playerInfo = [data.gameplayers[1].player, data.gameplayers[0].player];

            var statusGame = document.getElementById("statusGame")
            switch (data.status) {
                case 0:
                    statusGame.innerText = "Esperando Jugador"
                    break;
                case 1:
                    statusGame.innerText = "Esperando barcos del otro jugador"
                    break;
                case 2:
                    statusGame.innerText = "Esperando jugada de otro jugador"
                    document.getElementById("btnEnviarSalvoes").classList.remove("hidden")

                    break;
                case 3:
                    statusGame.innerText = "Coloque salvoes"
                    document.getElementById("btnEnviarSalvoes").classList.remove("hidden")
                    break;
                case 4:
                    statusGame.innerText = "Juego terminado"
                    break;
            }
            $('#userLogged').text('It´s time to win, ' + playerInfo[0].email)
            var player2 = playerInfo[1] != undefined ? playerInfo[1].email : ""
            $('#playerInfo').text(playerInfo[0].email + '(you) vs ' + player2);

            if (data.ships.length != 0) {
                let shipsHall = document.getElementById("shipsHall")
                shipsHall.style.display = "none"
            }
            toDrawShips(data.ships, data.salvoes, playerInfo)
            toDrawSalvoes(data.salvoes, playerInfo)
            toDrawHistorial(data.history, data.id)
        })
        .fail(function (jqXHR, textStatus) {
            alert("Failed: " + textStatus);
        });
};
//Realiza logout
function toLogOut() {
    $.post("/api/logout").done(function () {
        location.href = "/web/games.html"
    })
}

setInterval(() => {
    loadData()
}, 4000);


/*
//Realiza peticion de datos del juego
function loadStatus() {
    $.get('/api/game_view/' + toGetParameterByName('gp')+'/status')
        .done(function (data) {
            var statusGame= document.getElementById("statusGame")
            switch (data.status){
case 0:
statusGame.innerText= "Esperando Jugador"
break;
case 1:
statusGame.innerText= "Esperando barcos del otro jugador"
break;
case 2:
statusGame.innerText= "Esperando jugada de otro jugador"
document.getElementById("btnEnviarSalvoes").classList.remove("hidden")

break;
case 3:
statusGame.innerText= "Coloque salvoes"
document.getElementById("btnEnviarSalvoes").classList.remove("hidden")
break;
case 4:
statusGame.innerText= "Juego terminado"
break;
            }
        })
        .fail(function (jqXHR, textStatus) {
            alert("Failed: " + textStatus);
        })}*/