<?php
    require_once 'config.php';

    $connect = mysqli_connect("$host", "$user", "$pass", "$database", "$port");
    if (!$connect)
        echo "Failed to connect to MySQL: ".$mysqli_connect_error();

    $username = mysqli_real_escape_string($connect, $_POST["username"]);
    $password = mysqli_real_escape_string($connect, $_POST["password"]);
    $first_name = mysqli_real_escape_string($connect, $_POST["first_name"]);
    $last_name = mysqli_real_escape_string($connect, $_POST["last_name"]);
    $email = mysqli_real_escape_string($connect, $_POST["email"]);
    $phone = mysqli_real_escape_string($connect, $_POST["phone"]);

    $response = array();

    $response["result"] = false;
    $response["query_status"] = "";
    $response["status"] = 0;

    // Check for duplicate username, email or phone number
    $query_check_user = mysqli_query($connect, "SELECT username, email, phone_number FROM users WHERE username='$username' OR email='$email' OR phone_number='$phone'");
    if (mysqli_num_rows($query_check_user) > 0)
    {
        while ($row_user = mysqli_fetch_assoc($query_check_user))
        {
            if ($username == $row_user["username"])
            {
                $response["status"] = 1;
                break;
            }
            else if ($email == $row_user["email"])
            {
                $response["status"] = 2;
                break;
            }
            else if ($phone == $row_user["phone_number"])
            {
                $response["status"] = 3;
                break;
            }
        }
        echo json_encode($response);
        mysqli_close($connect);
        return;
    }

    $user_query_string = "INSERT INTO users (`username`, `password`, `first_name`, `last_name`, `email`, `phone_number`) VALUES (";
    $user_query_string .= "'".$username."', ";
    $user_query_string .= "'".$password."', ";
    $user_query_string .= "'".$first_name."', ";
    $user_query_string .= "'".$last_name."', ";
    $user_query_string .= "'".$email."', ";
    $user_query_string .= "'".$phone."');";

    if (mysqli_query($connect, $user_query_string))
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
