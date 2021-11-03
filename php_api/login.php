<?php
    require_once 'config.php';

    $connect = mysqli_connect("$host", "$user", "$pass", "$database", "$port");
    if (!$connect)
        echo "Failed to connect to MySQL: ".$mysqli_connect_error();

    $username = mysqli_real_escape_string($connect, $_POST["username"]);
    $password = mysqli_real_escape_string($connect, $_POST["password"]);

    $response = array();
    $user_id = -1;
    $first_login = 0;

    $response["result"] = false;
    $response["userdata"] = array();

    $result = mysqli_query($connect, "SELECT * FROM users WHERE username='$username' AND password='$password'");
    if (mysqli_num_rows($result) > 0)
    {
        $response["result"] = true;
        $response["userdata"] = mysqli_fetch_all($result, MYSQLI_ASSOC);

        $user_id = $response["userdata"][0]["id"];
        $first_login = $response["userdata"][0]["first_login"];
    }

    if ($user_id != -1)
    {
        // Load user coupons
        $result = mysqli_query($connect, "SELECT * FROM user_coupons WHERE user_id='$user_id'");
        if (mysqli_num_rows($result) > 0)
        {
            $response["coupons"] = mysqli_fetch_all($result, MYSQLI_ASSOC);
        }

        // Load user orders
        $result = mysqli_query($connect, "SELECT * FROM user_orders WHERE owner_id='$user_id'");
        if (mysqli_num_rows($result) > 0)
        {
            $response["orders"] = mysqli_fetch_all($result, MYSQLI_ASSOC);
        }

        // Load user orders' items
        $result = mysqli_query($connect, "SELECT * FROM user_order_items WHERE owner_id='$user_id'");
        if (mysqli_num_rows($result) > 0)
        {
            $response["order-items"] = mysqli_fetch_all($result, MYSQLI_ASSOC);
        }

        // Remove first login status
        if ($first_login == 1)
        {
            mysqli_query($connect, "UPDATE users SET first_login='0' WHERE id='$user_id'");
        }
    }

    echo json_encode($response);
    mysqli_close($connect);
?>
