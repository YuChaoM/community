$(function () {
    $("#uvBtn").click(getUV);
    $("#dauBtn").click(getDAU);
})

function getUV() {
    var start = $("#uvStart").val();
    var end = $("#uvEnd").val();
    $.post(
        CONTEXT_PATH + "/data/uv",
        {"start": start, "end": end},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                $("#uvRes").text(data.res);
            } else {
                alert(data.msg);
            }
        }
    )
}

function getDAU() {

    var start = $("#dauStart").val();
    var end = $("#dauEnd").val();
    $.post(
        CONTEXT_PATH + "/data/dau",
        {"start": start, "end": end},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                $("#dauRes").text(data.res);
            } else {
                alert(data.msg);
            }
        }
    )
}