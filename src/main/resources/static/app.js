var stompClient = null;

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
    // 연결 where our SockJS server waits for connections.
    var socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/sub/chats', function (greeting) {
            showMessage(JSON.parse(greeting.body).content);
        });
    });
    // 연결 성공 > 클라이언트는 /sub/chats에 구독함 그러면 서버가 인사말을 보냄
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    stompClient.send("/pub/chats", {}, JSON.stringify({'username': $("#username").val(), 'message': $("#message").val()}));
}  // /pub/chat로  사용자가 입력한 이름을 넣어 보낸다. 그럼 GreetingController.greeting()이 수신한다.


function showMessage(message) {
    $("#chatMessage").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
});