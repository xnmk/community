$(function () {
    $("#topBtn").click(setTop);
    $("#EssenceBtn").click(setEssence);
    $("#DeleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId, "postId": postId},
        function (data) {
            // 转化 json 对象
            if (!isJson(data)) {
                data = $.parseJSON(data);
            }
            if (data.code == 200) {
                $(btn).children("i").text(data.data.likeCount);
                $(btn).children("b").text(data.data.likeStatus == 1 ? "已赞" : "赞");
            } else {
                alert(data.msg);
            }
        }
    )
}

// 置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id":$("#postId").val()},
        function (data) {
            if (!isJson(data)) {
                data = $.parseJSON(data);
            }
            if (data.code == 200) {
                $("#topBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    )
}

// 加精
function setEssence() {
    $.post(
        CONTEXT_PATH + "/discuss/essence",
        {"id": $("#postId").val()},
        function (data) {
            if (!isJson(data)) {
                data = $.parseJSON(data);
            }
            if (data.code == 200) {
                $("#EssenceBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        }
    )
}

// 删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id": $("#postId").val()},
        function (data) {
            if (!isJson(data)) {
                data = $.parseJSON(data);
            }
            if (data.code == 200) {
                // $("#DeleteBtn").attr("disabled", "disabled");
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        }
    )
}

// 判断 obj 是否为 json 对象
function isJson(obj){
    var isjson = typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length;
    return isjson;
}