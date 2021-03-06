var stompClient = null;
var roomId = 1;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#chatMessage").html("");
}

function connect() {
    var socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.send('/pub/chat', {}, JSON.stringify({'type':'ENTER', 'roomId' : roomId, 'username': 'userId'}));
        stompClient.subscribe('/sub/chat/'+roomId, function (greeting) {
            showMessage(JSON.parse(greeting.body));
        });
    });
}



function disconnect() {
    if (stompClient !== null) {
        stompClient.send('/pub/chat', {}, JSON.stringify({'type':'QUIT', 'roomId' : roomId, 'username': 'userId'}));
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    stompClient.send("/pub/chat", {},
        JSON.stringify({
            'type':'TALK',
            'roomId' : roomId,
            'username': $("#username").val(),
            'message' : $("#message").val()
        }));
}

function showMessage(chatMessage) {
    $("#chatMessage").append("<tr><td>" + "[" + chatMessage.username + "]" + chatMessage.message + "</td></tr>");
}

// function loadChat(chatMessage){
//     if(chatMessage.chat)
//     if(chatList != null) {
//         for(chat in chatList) {
//             $("#chatMessage").append(
//                 "<tr><td>" + "[" + chatMessage.username + "]" + chatMessage.message + "</td></tr>"
//             );
//         }
//     }
// }

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
});