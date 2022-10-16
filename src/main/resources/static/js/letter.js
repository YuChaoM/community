$(function () {
    $("#sendBtn").click(send_letter);
    $(".deleteBtn").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");
    var toName = $("#recipient-name").val();
    var content = $("#message-text").val();
    $.post(
        CONTEXT_PATH + "/message/add",
        {"toName": toName, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 200) {
                $("#hintModal").text("发送成功!");
            } else {
                $("#hintModal").text(data.msg);
            }
            $("#hintModal").modal("show");

            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000);
        }
    );

}

function delete_msg() {
    // TODO 删除数据
    // $(this).parents(".media").remove();
    // var id = $("#letterId").val();
    var btn = this;
    var id = $(btn).prev().val();
    $.ajax({
            url: CONTEXT_PATH + "/message/delete",
            data: {"id": id},
            type: "DELETE",
            dataType: "JSON",
            success: function (data) {
                console.log(data)
                if (data.code == 200) {
                    $(btn).parents(".media").remove();
                } else {
                    alert(data.msg);
                }
            }
        }
    )
}