/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var wsUri = "ws://" + document.location.host + document.location.pathname + "tileendpoint";
var websocket = new WebSocket(wsUri);

websocket.onerror = function(evt) { onError(evt) ;};

function onError(evt) {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

websocket.onerror = function(evt) { onError(evt) ;};

websocket.onmessage = function(evt) { onMessage(evt); };

function sendTile(json) {
    console.log("sending text: " + json);
    websocket.send(json);
}
                
function onMessage(evt) {
    console.log("received: " + evt.data);
    recieveTile(evt.data);
    
}

function onError(evt) {
    console.log(evt.data);
    
};

// For testing purposes
websocket.onopen = function(evt) { onOpen(evt); };

function writeToScreen(message) {
    output.innerHTML += message + "<br>";
}


function onOpen() {
    writeToScreen("Connected to " + wsUri);
}
// End test functions

