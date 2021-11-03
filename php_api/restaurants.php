<?php
    require_once 'config.php';

    $connect = mysqli_connect("$host", "$user", "$pass", "$database", "$port");
    if (!$connect)
        echo "Failed to connect to MySQL: ".$mysqli_connect_error();

    $response = array();

    $response["result"] = false;
    $response["restaurantdata"] = array();

    $result = mysqli_query($connect, "SELECT * FROM restaurants");
    if (mysqli_num_rows($result) > 0)
    {
        $response["result"] = true;
        $response["restaurant_data"] = mysqli_fetch_all($result, MYSQLI_ASSOC);
    }

    echo json_encode($response);
    mysqli_close($connect);
?>
