//当点击发布按钮时调publish
$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    //点击发布后隐藏弹出框
    $("#publishModal").modal("hide");
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
    //发送异步请求
    $.post(
        CONTEXT_PATH + "/discuss/add",
        {"title":title,"content":content},
        //回调函数
        function (data){
            data = $.parseJSON(data);
            //在弹出消息框中显示
            $("#modal-body").text(data.msg);
            $("#hintModal").modal("show");
            //2秒后自动隐藏提示框
            setTimeout(function () {
                $("#hintModal").modal("hide");
                //刷新页面
                if (data.code == 200) {
                    window.location.reload();
                }
            },2000)
        }
    )
}