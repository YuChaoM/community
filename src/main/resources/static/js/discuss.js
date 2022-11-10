$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, userId, discussPostId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": userId, "discussPostId": discussPostId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : '赞');
            } else {
                alert(data.msg);
            }
        }
    )
}

function setTop() {
    var id = $("#postId").val();
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"postId": id},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                $("#topBtn").text(data.type == 1 ? '取消置顶' : '置顶');
            } else {
                alert(data.msg);
            }
        }
    )
}

function setWonderful() {
    var id = $("#postId").val();
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"postId": id},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                $("#wonderfulBtn").text(data.status == 1 ? '取消加精' : '加精');
            } else {
                alert(data.msg);
            }
        }
    )
}

function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"dosId": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                location.href = CONTEXT_PATH = "/index";
            } else {
                alert(data.msg);
            }
        }
    )
}