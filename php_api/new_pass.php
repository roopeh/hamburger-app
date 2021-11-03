<?php
    require_once 'config.php';

    $connect = mysqli_connect("$host", "$user", "$pass", "$database", "$port");
    if (!$connect)
        echo "Failed to connect to MySQL: ".$mysqli_connect_error();

    $user_id = mysqli_real_escape_string($connect, $_POST["user_id"]);
    $password = mysqli_real_escape_string($connect, $_POST["password"]);

    $response = array();

    $response["result"] = false;
    $response["query_status"] = "";

    if (mysqli_query($connect, "UPDATE users SET password='$password' WHERE id='$user_id'"))
    {
        $response["result"] = true;
    }
    else
    {
        $response["query_status"] = mysqli_error($connect);
    }

    echo json_encode($response);
    mysqli_close($connect);
?>
