$(function(){
    $("#uploadForm").submit(upload);
});

function upload() {
    var host = $("#host").val();
    $.ajax({
        //host的格式为 bucketname.endpoint
        url: host,
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function(res) {
            // if(res) {
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function(data) {
                        data = $.parseJSON(data);
                        if(data.code == 200) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            // } else {
            //     alert("上传失败!");
            // }
        }
    });
    return false;
}