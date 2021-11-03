/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` tinyint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier',
  `type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'Normal (0) or meal (1)',
  `name` varchar(50) NOT NULL DEFAULT '0' COMMENT 'Product name',
  `price` double(255,2) unsigned NOT NULL DEFAULT '0.00' COMMENT 'Price',
  `koostumus` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Koostumus',
  `ravinto` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Ravinto',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Product data';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,0,'Juustohampurilainen',1.50,'Hampurilainen juustolla.','Ravintosisältö...'),(2,0,'Pekonihampurilainen',2.50,'Hampurilainen pekonilla.','Ravintosisältö...'),(3,0,'Megahampurilainen',4.40,'Hampurilainen kahdella pihvillä, makkaralla, salaatilla ja juustolla.','Ravintosisältö...'),(4,0,'Tuplajuustohampurilainen',2.70,'Hampurilainen kahdella pihvillä ja kahdella juustolla.','Ravintosisältö...'),(5,0,'Kanahampurilainen',3.30,'Hampurilainen kanapihvillä.','Ravintosisältö...'),(6,1,'Juustoateria',5.80,'Juustohampurilainen valitsemmillasi lisukkeilla.','Ravintosisältö...'),(7,1,'Pekoniateria',6.60,'Pekonihampurilainen valitsemmillasi lisukkeilla.','Ravintosisältö...'),(8,1,'Iso Pekoniateria',7.70,'Pekonihampurilainen isommalla sämpylällä jossa reilusti pekonia. Valitsemmillasi lisukkeilla.','Ravintosisältö...'),(9,1,'Mega-ateria',9.00,'Megahampurilainen valitsemmillasi lisukkeilla.','Ravintosisältö...'),(10,1,'Tuplajuustoateria',6.40,'Tuplajuustohampurilainen valitsemmillasi lisukkeilla.','Ravintosisältö...'),(11,1,'Iso Tuplajuustoateria',7.50,'Tuplajuustohampurilainen isommalla sämpylällä ja isommilla pihveillä. Valitsemmillasi lisukkeilla.','Ravintosisältö...'),(12,1,'Kana-ateria',7.20,'Kanahampurilainen valitsemmillasi lisukkeilla.','Ravintosisältö...');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `restaurants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurants` (
  `id` tinyint unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier',
  `name` varchar(50) NOT NULL DEFAULT '0' COMMENT 'Restaurant name',
  `address` varchar(50) NOT NULL DEFAULT '0' COMMENT 'Address',
  `city` varchar(50) NOT NULL DEFAULT '0' COMMENT 'City',
  `phone_number` varchar(50) NOT NULL DEFAULT '0' COMMENT 'Phone number',
  `dates_sun` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT 'Sunday hours',
  `dates_mon` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT 'Monday hours',
  `dates_tue` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT 'Tuesday hours',
  `dates_wed` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT 'Wednesday hours',
  `dates_thu` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT 'Thursday hours',
  `dates_fri` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT 'Friday hours',
  `dates_sat` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT 'Saturday hours',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Restaurant data';
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `restaurants` WRITE;
/*!40000 ALTER TABLE `restaurants` DISABLE KEYS */;
INSERT INTO `restaurants` VALUES (1,'Restaurant Linnanmaa 24h','Yliopistonkatu 9','Oulu','040-1234567','0024','0024','0024','0024','0024','0024','0024'),(2,'Restaurant Kaukovainio','Kotkantie 1','Oulu','040-1234567','1216','0818','0818','0818','0818','0818','-1'),(3,'Restaurant Kontinkangas','Kiviharjuntie 4','Oulu','040-1234567','-1','1220','-1','1220','-1','1220','1222'),(4,'Restaurant Oulainen','Kuntotie 2','Oulainen','040-1234567','-1','0917','0917','0917','0917','0917','1016');
/*!40000 ALTER TABLE `restaurants` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `user_coupons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_coupons` (
  `user_id` int unsigned NOT NULL DEFAULT '0' COMMENT 'The user id',
  `coupon_type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'Coupon type id',
  `expiry_date` bigint unsigned NOT NULL DEFAULT '0' COMMENT 'Expiry date'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='User coupons';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `user_order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_order_items` (
  `order_id` int unsigned NOT NULL DEFAULT '0' COMMENT 'Id of order',
  `owner_id` int unsigned NOT NULL DEFAULT '0' COMMENT 'Id of owner',
  `product_id` tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'Id of product',
  `price` double(255,2) unsigned NOT NULL DEFAULT '0.00' COMMENT 'Price',
  `meal_drink` tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'Meal drink',
  `large_drink` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0 or 1',
  `meal_extra` tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'Meal extra',
  `large_extra` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0 or 1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='User order items';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `user_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_orders` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique id for order',
  `owner_id` int unsigned NOT NULL DEFAULT '0' COMMENT 'User id',
  `order_date` bigint unsigned NOT NULL DEFAULT '0' COMMENT 'Order date',
  `pickup_date` bigint unsigned NOT NULL DEFAULT '0' COMMENT 'Pickup date',
  `restaurant_id` smallint unsigned NOT NULL DEFAULT '0' COMMENT 'Id of restaurant',
  `paid_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT 'Paid status (0 or 1)',
  `original_price` double(255,2) unsigned NOT NULL DEFAULT '0.00' COMMENT 'Original price',
  `discount_price` double(255,2) unsigned NOT NULL DEFAULT '0.00' COMMENT 'Discount price',
  `total_price` double(255,2) unsigned NOT NULL DEFAULT '0.00' COMMENT 'Total price',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='User orders';
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'Unique identifier',
  `username` varchar(50) NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Encrypted password',
  `first_login` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '1 if user has not ever logon',
  `first_name` varchar(50) NOT NULL COMMENT 'First name',
  `last_name` varchar(50) NOT NULL COMMENT 'Last name',
  `email` varchar(50) NOT NULL COMMENT 'Email',
  `phone_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'Phone number',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='User data';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
