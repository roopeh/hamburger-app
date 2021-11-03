<?php
    require_once 'config.php';

    $connect = mysqli_connect("$host", "$user", "$pass", "$database", "$port");
    if (!$connect)
        echo "Failed to connect to MySQL: ".$mysqli_connect_error();

    $owner_id = mysqli_real_escape_string($connect, $_POST["owner_id"]);
    $order_date = mysqli_real_escape_string($connect, $_POST["order_date"]);
    $pickup_date = mysqli_real_escape_string($connect, $_POST["pickup_date"]);
    $restaurant_id = mysqli_real_escape_string($connect, $_POST["restaurant_id"]);
    $paid_status = mysqli_real_escape_string($connect, $_POST["paid_status"]);
    $original_price = mysqli_real_escape_string($connect, $_POST["original_price"]);
    $discount_price = mysqli_real_escape_string($connect, $_POST["discount_price"]);
    $total_price = mysqli_real_escape_string($connect, $_POST["total_price"]);
    $items_json = json_decode($_POST["order_items"], true);

    $order_query_string = "INSERT INTO user_orders (`owner_id`, `order_date`, `pickup_date`, `restaurant_id`, `paid_status`, `original_price`, `discount_price`, `total_price`) VALUES (";
    $order_query_string .= "'".$owner_id."', ";
    $order_query_string .= "'".$order_date."', ";
    $order_query_string .= "'".$pickup_date."', ";
    $order_query_string .= "'".$restaurant_id."', ";
    $order_query_string .= "'".$paid_status."', ";
    $order_query_string .= "'".$original_price."', ";
    $order_query_string .= "'".$discount_price."', ";
    $order_query_string .= "'".$total_price."');";

    $response = array();

    $response["result"] = false;
    $response["query_status"] = "";
    $response["order_id"] = 0;

    // Create order first
    if (mysqli_query($connect, $order_query_string))
    {
        $response["result"] = true;
        $order_id = mysqli_insert_id($connect);
        $response["order_id"] = $order_id;

        // Insert order items
        $items_query_string = "INSERT INTO user_order_items VALUES ";
        foreach ($items_json as $val)
        {
            $items_query_string .= "('".$order_id."', '".$val["owner_id"]."', '".$val["product_id"]."', '".$val["price"]."', '".$val["meal_drink"]."', '".$val["large_drink"]."', '".$val["meal_extra"]."', '".$val["large_extra"]."'), ";
        }

        // Remove last 2 characters and replace them with semicolon
        $items_query_string = substr($items_query_string, 0, -2).";";

        if (!mysqli_query($connect, $items_query_string))
        {
            $response["result"] = false;
            $response["query_status"] = mysqli_error($connect);
        }
    }
    else
    {
        $response["query_status"] = mysqli_error($connect);
    }

    echo json_encode($response);
    mysqli_close($connect);
?>
