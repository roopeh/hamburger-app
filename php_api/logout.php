<?php
    require_once 'config.php';

    $connect = mysqli_connect("$host", "$user", "$pass", "$database", "$port");
    if (!$connect)
        echo "Failed to connect to MySQL: ".$mysqli_connect_error();

    $user_id = mysqli_real_escape_string($connect, $_POST["user_id"]);
    $coupon_amount = mysqli_real_escape_string($connect, $_POST["coupon_size"]);
    $coupons_json = json_decode($_POST["coupons"], true);

    $response = array();

    $response["result"] = false;
    $response["query_status"] = "";

    // Remove existing coupons from database
    $remove_coupons_query = "DELETE FROM user_coupons WHERE user_id='$user_id'";
    if (!mysqli_query($connect, $remove_coupons_query))
    {
        $response["query_status"] = mysqli_error($connect);
        echo json_encode($response);
        mysqli_close($connect);
        return;
    }

    // If user has no coupons, do nothing and close connection
    if ($coupon_amount == 0)
    {
        $response["result"] = true;
        echo json_encode($response);
        mysqli_close($connect);
        return;
    }

    $coupon_query_string = "INSERT INTO user_coupons VALUES ";
    foreach ($coupons_json as $coupon)
    {
        $coupon_query_string .= "('".$user_id."', '".$coupon["type"]."', '".$coupon["expiry_date"]."'), ";
    }

    // Remove last 2 characters and replace them with semicolon
    $coupon_query_string = substr($coupon_query_string, 0, -2).";";

    if (mysqli_query($connect, $coupon_query_string))
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
