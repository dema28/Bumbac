-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: yarn_store_local
-- ------------------------------------------------------
-- Server version	8.0.43

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

--
-- Current Database: `yarn_store_local`
--

/*!40000 DROP DATABASE IF EXISTS `yarn_store_local`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `yarn_store_local` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `yarn_store_local`;

--
-- Table structure for table `attributes`
--

DROP TABLE IF EXISTS `attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attributes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `slug` varchar(128) NOT NULL,
  `data_type` enum('TEXT','NUMBER','BOOLEAN','ENUM') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attributes`
--

LOCK TABLES `attributes` WRITE;
/*!40000 ALTER TABLE `attributes` DISABLE KEYS */;
INSERT INTO `attributes` VALUES (1,'Length','length','NUMBER'),(2,'Weight','weight','NUMBER'),(3,'Needle Size','needle-size','TEXT'),(4,'Composition','composition','TEXT'),(5,'Thickness','thickness','ENUM'),(6,'Texture','texture','ENUM'),(7,'Machine Washable','machine-washable','BOOLEAN'),(8,'Hypoallergenic','hypoallergenic','BOOLEAN');
/*!40000 ALTER TABLE `attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brands`
--

DROP TABLE IF EXISTS `brands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brands` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `country` varchar(100) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brands`
--

LOCK TABLES `brands` WRITE;
/*!40000 ALTER TABLE `brands` DISABLE KEYS */;
INSERT INTO `brands` VALUES (1,'Alize',NULL,NULL),(2,'YarnArt',NULL,NULL),(3,'Himalaya',NULL,NULL),(4,'Etrofil',NULL,NULL),(5,'Madame Tricote',NULL,NULL);
/*!40000 ALTER TABLE `brands` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `cart_id` bigint NOT NULL,
  `colorId` bigint NOT NULL,
  `quantity` int unsigned NOT NULL,
  `added_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`cart_id`,`colorId`),
  KEY `idx_cart_items_color` (`colorId`),
  KEY `idx_cart_item_cart_id` (`cart_id`),
  KEY `idx_cart_item_color_id` (`colorId`),
  CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`colorId`) REFERENCES `colors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (2101,1,2,'2025-09-06 06:40:12','2025-09-06 06:40:12.000000'),(2101,2,1,'2025-09-06 06:40:12','2025-09-06 06:40:12.000000'),(2102,2,3,'2025-09-06 06:40:12','2025-09-06 06:40:12.000000'),(2103,3,1,'2025-09-06 06:40:12','2025-09-06 06:40:12.000000'),(2103,4,2,'2025-09-06 06:40:12','2025-09-06 06:40:12.000000'),(2104,5,1,'2025-09-06 06:40:12','2025-09-06 06:40:12.000000');
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `idx_cart_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (2101,101,'2025-09-06 06:40:12','2025-09-06 06:40:12'),(2102,102,'2025-09-06 06:40:12','2025-09-06 06:40:12'),(2103,103,'2025-09-06 06:40:12','2025-09-06 06:40:12'),(2104,1,'2025-09-06 06:40:12','2025-09-06 08:08:48');
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `slug` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `slug` (`slug`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Acrylic','acrylic'),(2,'Wool','wool'),(3,'Cotton','cotton'),(4,'Baby','baby'),(5,'Chunky','chunky');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collections`
--

DROP TABLE IF EXISTS `collections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collections` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `brand_id` bigint NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `brand_id` (`brand_id`),
  CONSTRAINT `collections_ibfk_1` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collections`
--

LOCK TABLES `collections` WRITE;
/*!40000 ALTER TABLE `collections` DISABLE KEYS */;
INSERT INTO `collections` VALUES (1,1,'Winter 2025',NULL),(2,2,'Baby Soft',NULL),(3,3,'Ombre Series',NULL),(4,4,'Multicolor Line',NULL);
/*!40000 ALTER TABLE `collections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `color_prices`
--

DROP TABLE IF EXISTS `color_prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `color_prices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `color_id` bigint NOT NULL,
  `price_czk` decimal(12,2) NOT NULL,
  `valid_from` date NOT NULL,
  `valid_to` date DEFAULT '9999-12-31',
  PRIMARY KEY (`id`),
  KEY `color_id` (`color_id`),
  CONSTRAINT `color_prices_ibfk_1` FOREIGN KEY (`color_id`) REFERENCES `colors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `color_prices`
--

LOCK TABLES `color_prices` WRITE;
/*!40000 ALTER TABLE `color_prices` DISABLE KEYS */;
INSERT INTO `color_prices` VALUES (2,2,140.00,'2025-08-01','9999-12-31'),(3,3,160.00,'2025-08-01','9999-12-31'),(4,4,180.00,'2025-08-01','9999-12-31'),(5,5,200.00,'2025-08-01','9999-12-31');
/*!40000 ALTER TABLE `color_prices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `colors`
--

DROP TABLE IF EXISTS `colors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `colors` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `yarn_id` bigint NOT NULL,
  `color_code` varchar(32) NOT NULL,
  `color_name` varchar(128) NOT NULL,
  `sku` varchar(100) NOT NULL,
  `barcode` varchar(100) DEFAULT NULL,
  `hex_value` varchar(255) DEFAULT NULL,
  `stock_quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_color_code` (`yarn_id`,`color_code`),
  KEY `idx_color_yarn_id` (`yarn_id`),
  KEY `idx_color_code` (`color_code`),
  KEY `idx_color_sku` (`sku`),
  KEY `idx_colors_color_name` (`color_name`),
  KEY `idx_colors_hex_value` (`hex_value`),
  KEY `idx_colors_yarn_id` (`yarn_id`),
  KEY `idx_colors_price` (`price`),
  KEY `idx_colors_stock_quantity` (`stock_quantity`),
  CONSTRAINT `colors_ibfk_1` FOREIGN KEY (`yarn_id`) REFERENCES `yarns` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `colors`
--

LOCK TABLES `colors` WRITE;
/*!40000 ALTER TABLE `colors` DISABLE KEYS */;
INSERT INTO `colors` VALUES (1,1,'C001','White','SKU-1',NULL,NULL,0,5.99,NULL,NULL),(2,1,'C002','Black','SKU-2',NULL,NULL,0,6.99,NULL,NULL),(3,2,'C001','Red','SKU-3',NULL,NULL,0,7.99,NULL,NULL),(4,4,'C001','Grey','SKU-4',NULL,NULL,100,8.99,NULL,NULL),(5,5,'C001','Blue','SKU-5',NULL,NULL,100,9.99,NULL,NULL),(7,1,'C003','Grey','YARN-1-GREY',NULL,'#808080',92,5.99,NULL,NULL),(8,2,'C002','Navy Blue','YARN-2-NAVY',NULL,'#000080',67,6.99,NULL,NULL),(9,2,'C003','Pink','YARN-2-PINK',NULL,'#FFC0CB',78,6.99,NULL,NULL),(10,3,'C002','Baby Blue','YARN-3-BABY-BLUE',NULL,'#ADD8E6',45,7.99,NULL,NULL),(11,4,'C002','Charcoal','YARN-4-CHARCOAL',NULL,'#36454F',23,8.99,NULL,NULL),(12,5,'C002','Royal Blue','YARN-5-ROYAL',NULL,'#4169E1',56,9.99,NULL,NULL);
/*!40000 ALTER TABLE `colors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `colors_v_legacy`
--

DROP TABLE IF EXISTS `colors_v_legacy`;
/*!50001 DROP VIEW IF EXISTS `colors_v_legacy`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `colors_v_legacy` AS SELECT 
 1 AS `id`,
 1 AS `yarn_id`,
 1 AS `color_code`,
 1 AS `color_name`,
 1 AS `sku`,
 1 AS `barcode`,
 1 AS `hex_value`,
 1 AS `stock_quantity`,
 1 AS `image_url`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `contact_messages`
--

DROP TABLE IF EXISTS `contact_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `name` varchar(100) NOT NULL,
  `read_at` datetime(6) DEFAULT NULL,
  `subject` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_contact_email` (`email`),
  KEY `idx_contact_subject` (`subject`),
  KEY `idx_contact_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=305 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact_messages`
--

LOCK TABLES `contact_messages` WRITE;
/*!40000 ALTER TABLE `contact_messages` DISABLE KEYS */;
INSERT INTO `contact_messages` VALUES (301,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000','ivan@domain.com','/messages/301.txt',_binary '\0','Ivan Ivanov',NULL,'Shipping Question'),(302,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000','maria@domain.com','/messages/302.txt',_binary '\0','Maria Popescu',NULL,'Payment Issue'),(303,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000','john@domain.com','/messages/303.txt',_binary '','John Doe','2025-09-06 06:40:11.000000','General Feedback'),(304,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000','ana@domain.com','/messages/304.txt',_binary '\0','Ana Smirnova',NULL,'Вопрос о доставке');
/*!40000 ALTER TABLE `contact_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discount_rules`
--

DROP TABLE IF EXISTS `discount_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discount_rules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` text,
  `type` enum('PERCENTAGE','FIXED_AMOUNT','FREE_SHIPPING') NOT NULL,
  `value` decimal(12,2) NOT NULL DEFAULT '0.00',
  `max_uses` int DEFAULT NULL,
  `max_uses_per_user` int DEFAULT NULL,
  `min_order_total_czk` decimal(12,2) DEFAULT NULL,
  `valid_from` date DEFAULT NULL,
  `valid_to` date DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `created_by` (`created_by`),
  KEY `idx_discount_rules_is_active` (`is_active`),
  KEY `idx_discount_rules_valid_from` (`valid_from`),
  CONSTRAINT `discount_rules_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discount_rules`
--

LOCK TABLES `discount_rules` WRITE;
/*!40000 ALTER TABLE `discount_rules` DISABLE KEYS */;
/*!40000 ALTER TABLE `discount_rules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','baseline schema','SQL','V1__baseline_schema.sql',543426850,'denis','2025-09-06 06:40:11',3861,1),(2,'2','baseline seed data','SQL','V2__baseline_seed_data.sql',-82974405,'denis','2025-09-06 06:40:11',73,1),(3,'3','test seed data','SQL','V3__test_seed_data.sql',-1332723257,'denis','2025-09-06 06:40:11',201,1),(4,'4','test users and orders','SQL','V4__test_users_and_orders.sql',-902212890,'denis','2025-09-06 06:40:11',158,1),(5,'5','newsletter and contact','SQL','V5__newsletter_and_contact.sql',1852203544,'denis','2025-09-06 06:40:11',59,1),(6,'6','patterns and translations','SQL','V6__patterns_and_translations.sql',1131332162,'denis','2025-09-06 06:40:12',104,1),(7,'7','carts and addresses','SQL','V7__carts_and_addresses.sql',-890012309,'denis','2025-09-06 06:40:12',71,1),(8,'8','returns and statuses','SQL','V8__returns_and_statuses.sql',-1361849065,'denis','2025-09-06 06:40:12',117,1),(9,'9','attributes and colors','SQL','V9__attributes_and_colors.sql',-1453902941,'denis','2025-09-06 06:40:12',114,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `guest_tokens`
--

DROP TABLE IF EXISTS `guest_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guest_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `token` char(36) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` timestamp NULL DEFAULT ((now() + interval 30 day)),
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `cart_id` (`cart_id`),
  CONSTRAINT `guest_tokens_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guest_tokens`
--

LOCK TABLES `guest_tokens` WRITE;
/*!40000 ALTER TABLE `guest_tokens` DISABLE KEYS */;
INSERT INTO `guest_tokens` VALUES (1,2104,'550e8400-e29b-41d4-a716-446655440000','2025-09-06 06:40:12','2025-10-06 07:09:15');
/*!40000 ALTER TABLE `guest_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media_assets`
--

DROP TABLE IF EXISTS `media_assets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media_assets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `entity_type` enum('YARN','COLOR','PATTERN','BRAND') NOT NULL,
  `entity_id` bigint NOT NULL,
  `variant` enum('ORIGINAL','L','M','S','XS') NOT NULL,
  `format` enum('JPEG','WEBP','AVIF','PNG','PDF','MP4','ZIP') NOT NULL,
  `url` varchar(255) NOT NULL,
  `width_px` smallint DEFAULT NULL,
  `height_px` smallint DEFAULT NULL,
  `size_bytes` int DEFAULT NULL,
  `alt_text` varchar(255) DEFAULT NULL,
  `sort_order` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_entity_variant` (`entity_type`,`entity_id`,`variant`,`format`),
  KEY `idx_entity` (`entity_type`,`entity_id`,`sort_order`,`variant`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media_assets`
--

LOCK TABLES `media_assets` WRITE;
/*!40000 ALTER TABLE `media_assets` DISABLE KEYS */;
INSERT INTO `media_assets` VALUES (1,'YARN',1,'ORIGINAL','JPEG','/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-8027-.jpg',NULL,NULL,NULL,NULL,0,'2025-09-06 06:40:11',NULL),(2,'YARN',2,'ORIGINAL','JPEG','/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-7914-.jpg',NULL,NULL,NULL,NULL,0,'2025-09-06 06:40:11',NULL),(3,'YARN',3,'ORIGINAL','JPEG','/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-7804-2.jpg',NULL,NULL,NULL,NULL,0,'2025-09-06 06:40:11',NULL),(4,'YARN',4,'ORIGINAL','JPEG','/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-7786-2.jpg',NULL,NULL,NULL,NULL,0,'2025-09-06 06:40:11',NULL),(5,'YARN',5,'ORIGINAL','JPEG','/media/yarn/superlana-maxi-batik/SUPERLANA MAXI BATÄ°K-7785-2.jpg',NULL,NULL,NULL,NULL,0,'2025-09-06 06:40:11',NULL),(101,'PATTERN',1001,'ORIGINAL','PDF','/media/patterns/pattern-001-scarf.pdf',NULL,NULL,NULL,'Схема шарфа для начинающих',1,'2025-09-06 06:40:12',NULL),(102,'PATTERN',1002,'ORIGINAL','PDF','/media/patterns/pattern-002-baby-hat.pdf',NULL,NULL,NULL,'Схема детской шапочки',1,'2025-09-06 06:40:12',NULL),(103,'PATTERN',1003,'ORIGINAL','PDF','/media/patterns/pattern-003-chunky-sweater.pdf',NULL,NULL,NULL,'Схема свитера из толстой пряжи',1,'2025-09-06 06:40:12',NULL),(104,'PATTERN',1004,'ORIGINAL','JPEG','/media/patterns/pattern-004-mittens-preview.jpg',NULL,NULL,NULL,'Превью схемы варежек',1,'2025-09-06 06:40:12',NULL),(105,'PATTERN',1005,'ORIGINAL','PDF','/media/patterns/pattern-005-dishcloth.pdf',NULL,NULL,NULL,'Схема универсальной мочалки',1,'2025-09-06 06:40:12',NULL);
/*!40000 ALTER TABLE `media_assets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `newsletter_subscribers`
--

DROP TABLE IF EXISTS `newsletter_subscribers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `newsletter_subscribers` (
  `user_id` bigint DEFAULT NULL,
  `subscribed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `confirmation_token` varchar(255) DEFAULT NULL,
  `confirmed` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `unsubscribed` bit(1) DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `newsletter_subscribers`
--

LOCK TABLES `newsletter_subscribers` WRITE;
/*!40000 ALTER TABLE `newsletter_subscribers` DISABLE KEYS */;
INSERT INTO `newsletter_subscribers` VALUES (NULL,'2025-09-06 06:40:11','token-201-abc123',_binary '','user1@bumbac.md',_binary '\0',201),(NULL,'2025-09-06 06:40:11','token-202-def456',_binary '','user2@bumbac.md',_binary '\0',202),(101,'2025-09-06 06:40:11','token-203-ghi789',_binary '\0','testuser1@bumbac.md',_binary '\0',203),(NULL,'2025-09-06 06:40:11','token-204-jkl012',_binary '','user4@bumbac.md',_binary '',204);
/*!40000 ALTER TABLE `newsletter_subscribers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_discounts`
--

DROP TABLE IF EXISTS `order_discounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_discounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `rule_id` bigint NOT NULL,
  `amount_czk` decimal(12,2) NOT NULL,
  `applied_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `rule_id` (`rule_id`),
  CONSTRAINT `order_discounts_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `order_discounts_ibfk_2` FOREIGN KEY (`rule_id`) REFERENCES `discount_rules` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_discounts`
--

LOCK TABLES `order_discounts` WRITE;
/*!40000 ALTER TABLE `order_discounts` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_discounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `order_id` bigint NOT NULL,
  `color_id` bigint NOT NULL,
  `quantity` int unsigned NOT NULL,
  `price` double NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `total_price` decimal(12,2) NOT NULL,
  `unit_price` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_items_color` (`color_id`),
  KEY `fk_order_items_order` (`order_id`),
  KEY `idx_order_item_order_id` (`order_id`),
  KEY `idx_order_item_color_id` (`color_id`),
  CONSTRAINT `fk_order_items_color` FOREIGN KEY (`color_id`) REFERENCES `colors` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (101,1,2,0,13,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000',NULL,11.98,5.99),(102,2,1,0,14,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000',NULL,6.99,6.99),(103,3,3,0,15,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000',NULL,23.97,7.99);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status_history`
--

DROP TABLE IF EXISTS `order_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_status_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `from_status_id` bigint DEFAULT NULL,
  `to_status_id` bigint NOT NULL,
  `changed_by` bigint DEFAULT NULL,
  `changed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `from_status_id` (`from_status_id`),
  KEY `to_status_id` (`to_status_id`),
  KEY `changed_by` (`changed_by`),
  CONSTRAINT `order_status_history_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `order_status_history_ibfk_2` FOREIGN KEY (`from_status_id`) REFERENCES `order_statuses` (`id`) ON DELETE SET NULL,
  CONSTRAINT `order_status_history_ibfk_3` FOREIGN KEY (`to_status_id`) REFERENCES `order_statuses` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `order_status_history_ibfk_4` FOREIGN KEY (`changed_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status_history`
--

LOCK TABLES `order_status_history` WRITE;
/*!40000 ALTER TABLE `order_status_history` DISABLE KEYS */;
INSERT INTO `order_status_history` VALUES (1001,101,NULL,1,101,'2025-09-01 06:40:12'),(1002,101,1,2,NULL,'2025-09-03 06:40:12'),(1003,101,2,3,NULL,'2025-09-05 06:40:12'),(1004,102,NULL,1,102,'2025-09-02 06:40:12'),(1005,102,1,2,NULL,'2025-09-04 06:40:12'),(1006,103,NULL,1,103,'2025-08-31 06:40:12');
/*!40000 ALTER TABLE `order_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_statuses`
--

DROP TABLE IF EXISTS `order_statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_statuses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL,
  `name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_statuses`
--

LOCK TABLES `order_statuses` WRITE;
/*!40000 ALTER TABLE `order_statuses` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `shipping_address_id` bigint DEFAULT NULL,
  `billing_address_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `total_amount` decimal(12,2) NOT NULL,
  `delivered_at` datetime(6) DEFAULT NULL,
  `status` enum('NEW','PAID','SHIPPED','DELIVERED','CANCELLED','RETURNED') NOT NULL,
  `status_id` bigint NOT NULL,
  `comment` varchar(1000) DEFAULT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `preferred_delivery_date` varchar(10) DEFAULT NULL,
  `shipping_address` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `shipping_address_id` (`shipping_address_id`),
  KEY `fk_orders_billing_address` (`billing_address_id`),
  KEY `idx_orders_status_date` (`created_at`),
  KEY `idx_order_user_id` (`user_id`),
  KEY `idx_order_status` (`status`),
  KEY `idx_order_created_at` (`created_at`),
  KEY `idx_orders_status_id` (`status_id`),
  CONSTRAINT `fk_orders_billing_address` FOREIGN KEY (`billing_address_id`) REFERENCES `shipping_addresses` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`shipping_address_id`) REFERENCES `shipping_addresses` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (101,101,NULL,NULL,'2025-09-06 06:40:11','2025-09-06 08:08:48',20.99,NULL,'NEW',1,NULL,NULL,NULL,NULL),(102,102,NULL,NULL,'2025-09-06 06:40:11','2025-09-06 08:08:48',21.99,NULL,'NEW',1,NULL,NULL,NULL,NULL),(103,103,NULL,NULL,'2025-09-06 06:40:11','2025-09-06 08:08:48',22.99,NULL,'NEW',1,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pattern_translations`
--

DROP TABLE IF EXISTS `pattern_translations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pattern_translations` (
  `pattern_id` bigint NOT NULL,
  `locale` varchar(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text,
  PRIMARY KEY (`pattern_id`,`locale`),
  CONSTRAINT `pattern_translations_ibfk_1` FOREIGN KEY (`pattern_id`) REFERENCES `patterns` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pattern_translations`
--

LOCK TABLES `pattern_translations` WRITE;
/*!40000 ALTER TABLE `pattern_translations` DISABLE KEYS */;
INSERT INTO `pattern_translations` VALUES (1001,'en','Beginner Scarf Pattern','A simple scarf pattern perfect for beginners'),(1001,'ro','Model de fular pentru începători','Un model simplu de fular perfect pentru începători'),(1001,'ru','Схема шарфа для начинающих','Простая схема шарфа, идеальная для новичков'),(1002,'en','Cotton Baby Hat','Cute baby hat made with cotton yarn'),(1002,'ro','Căciulă de bebeluș din bumbac','Căciulă drăguță pentru bebeluși din fire de bumbac'),(1002,'ru','Детская шапочка из хлопка','Милая детская шапочка из хлопковой пряжи'),(1003,'en','Advanced Chunky Sweater','Complex sweater pattern for experienced knitters'),(1003,'ro','Pulover complex din fire groase','Model complex de pulover pentru tricotoarele experimentate'),(1003,'ru','Сложный свитер из толстой пряжи','Сложная схема свитера для опытных вязальщиц'),(1004,'en','Simple Mittens','Basic mitten pattern for cold weather'),(1004,'ro','Mănuși simple','Model de bază de mănuși pentru vremea rece'),(1004,'ru','Простые варежки','Базовая схема варежек для холодной погоды'),(1005,'en','Universal Dishcloth','Multi-purpose dishcloth pattern'),(1005,'ro','Cârpă universală','Model multifuncțional de cârpă de bucătărie'),(1005,'ru','Универсальная мочалка','Многофункциональная схема мочалки');
/*!40000 ALTER TABLE `pattern_translations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patterns`
--

DROP TABLE IF EXISTS `patterns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patterns` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `yarn_id` bigint DEFAULT NULL,
  `pdf_url` varchar(255) DEFAULT NULL,
  `difficulty` enum('BEGINNER','INTERMEDIATE','ADVANCED') DEFAULT 'BEGINNER',
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `yarn_id` (`yarn_id`),
  CONSTRAINT `patterns_ibfk_1` FOREIGN KEY (`yarn_id`) REFERENCES `yarns` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1006 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patterns`
--

LOCK TABLES `patterns` WRITE;
/*!40000 ALTER TABLE `patterns` DISABLE KEYS */;
INSERT INTO `patterns` VALUES (1001,'PATTERN-001',1,'/media/patterns/pattern_1.pdf','BEGINNER','2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000'),(1002,'PATTERN-002',2,'/media/patterns/pattern_2.pdf','INTERMEDIATE','2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000'),(1003,'PATTERN-003',3,'/media/patterns/pattern_3.pdf','ADVANCED','2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000'),(1004,'PATTERN-004',4,'/media/patterns/pattern_4.pdf','BEGINNER','2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000'),(1005,'PATTERN-005',NULL,'/media/patterns/pattern_5.pdf','INTERMEDIATE','2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000');
/*!40000 ALTER TABLE `patterns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_status_history`
--

DROP TABLE IF EXISTS `payment_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_status_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint NOT NULL,
  `from_status_id` bigint DEFAULT NULL,
  `to_status_id` bigint NOT NULL,
  `changed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `payment_id` (`payment_id`),
  KEY `from_status_id` (`from_status_id`),
  KEY `to_status_id` (`to_status_id`),
  CONSTRAINT `payment_status_history_ibfk_1` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `payment_status_history_ibfk_2` FOREIGN KEY (`from_status_id`) REFERENCES `payment_statuses` (`id`) ON DELETE SET NULL,
  CONSTRAINT `payment_status_history_ibfk_3` FOREIGN KEY (`to_status_id`) REFERENCES `payment_statuses` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=2004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_status_history`
--

LOCK TABLES `payment_status_history` WRITE;
/*!40000 ALTER TABLE `payment_status_history` DISABLE KEYS */;
INSERT INTO `payment_status_history` VALUES (2001,101,1,2,'2025-09-03 06:40:12'),(2002,102,1,2,'2025-09-04 06:40:12'),(2003,103,1,2,'2025-09-05 06:40:12');
/*!40000 ALTER TABLE `payment_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_statuses`
--

DROP TABLE IF EXISTS `payment_statuses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_statuses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL,
  `name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_statuses`
--

LOCK TABLES `payment_statuses` WRITE;
/*!40000 ALTER TABLE `payment_statuses` DISABLE KEYS */;
INSERT INTO `payment_statuses` VALUES (1,'PENDING','Pending'),(2,'PAID','Paid'),(3,'FAILED','Failed'),(4,'REFUNDED','Refunded');
/*!40000 ALTER TABLE `payment_statuses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `status_id` bigint NOT NULL,
  `provider` varchar(64) NOT NULL,
  `provider_tx_id` varchar(128) DEFAULT NULL,
  `paid_at` timestamp NULL DEFAULT NULL,
  `amount_mdl` decimal(12,2) NOT NULL,
  `amount_usd` decimal(12,2) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `currency` varchar(3) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `exchange_rate` decimal(10,6) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `idx_payments_status_date` (`status_id`,`paid_at`),
  KEY `idx_payment_order_id` (`order_id`),
  KEY `idx_payment_status_id` (`status_id`),
  KEY `idx_payment_provider` (`provider`),
  KEY `idx_payment_created_at` (`created_at`),
  KEY `idx_payments_amount_mdl` (`amount_mdl`),
  KEY `idx_payments_amount_usd` (`amount_usd`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`status_id`) REFERENCES `payment_statuses` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (101,101,2,'stripe',NULL,NULL,20.99,1.10,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000','MDL',NULL,NULL,'card'),(102,102,2,'stripe',NULL,NULL,21.99,1.15,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000','MDL',NULL,NULL,'card'),(103,103,2,'stripe',NULL,NULL,22.99,1.20,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000','MDL',NULL,NULL,'card');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry` datetime(6) DEFAULT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_r4k4edos30bx9neoq81mdvwph` (`token`),
  KEY `FKjtx87i0jvq2svedphegvdwcuy` (`user_id`),
  CONSTRAINT `FKjtx87i0jvq2svedphegvdwcuy` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_token`
--

LOCK TABLES `refresh_token` WRITE;
/*!40000 ALTER TABLE `refresh_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `refresh_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `return_items`
--

DROP TABLE IF EXISTS `return_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `return_items` (
  `return_id` bigint NOT NULL,
  `color_id` bigint NOT NULL,
  `quantity` int unsigned NOT NULL,
  `reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`return_id`,`color_id`),
  KEY `color_id` (`color_id`),
  CONSTRAINT `return_items_ibfk_1` FOREIGN KEY (`return_id`) REFERENCES `returns` (`id`) ON DELETE CASCADE,
  CONSTRAINT `return_items_ibfk_2` FOREIGN KEY (`color_id`) REFERENCES `colors` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `return_items`
--

LOCK TABLES `return_items` WRITE;
/*!40000 ALTER TABLE `return_items` DISABLE KEYS */;
INSERT INTO `return_items` VALUES (501,1,1,'Color fading after wash'),(502,2,1,'Wrong color delivered'),(503,3,2,'Thickness not as described'),(503,4,1,'Poor quality material');
/*!40000 ALTER TABLE `return_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `return_status_history`
--

DROP TABLE IF EXISTS `return_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `return_status_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `changed_at` datetime(6) DEFAULT NULL,
  `changed_by` varchar(255) DEFAULT NULL,
  `new_status` enum('REQUESTED','APPROVED','RECEIVED','REFUNDED','REJECTED') DEFAULT NULL,
  `old_status` enum('REQUESTED','APPROVED','RECEIVED','REFUNDED','REJECTED') DEFAULT NULL,
  `return_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `return_status_history`
--

LOCK TABLES `return_status_history` WRITE;
/*!40000 ALTER TABLE `return_status_history` DISABLE KEYS */;
INSERT INTO `return_status_history` VALUES (1,'2025-09-04 06:40:12.000000','system','REQUESTED','REQUESTED',501),(2,'2025-09-05 06:40:12.000000','admin@bumbac.md','APPROVED','REQUESTED',502),(3,'2025-09-06 03:40:12.000000','admin@bumbac.md','APPROVED','REQUESTED',503),(4,'2025-09-06 05:40:12.000000','system','RECEIVED','APPROVED',503);
/*!40000 ALTER TABLE `return_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `returns`
--

DROP TABLE IF EXISTS `returns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `returns` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `status` enum('REQUESTED','APPROVED','RECEIVED','REFUNDED','REJECTED') DEFAULT 'REQUESTED',
  `refund_amount_czk` decimal(38,2) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `refund_amount_mdl` decimal(12,2) DEFAULT NULL,
  `refund_amount_usd` decimal(12,2) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `reason` varchar(1000) DEFAULT NULL,
  `refund_date` datetime(6) DEFAULT NULL,
  `return_date` datetime(6) DEFAULT NULL,
  `return_method` varchar(50) DEFAULT NULL,
  `tracking_number` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `idx_return_order_id` (`order_id`),
  KEY `idx_return_status` (`status`),
  KEY `idx_return_created_at` (`created_at`),
  CONSTRAINT `returns_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=504 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `returns`
--

LOCK TABLES `returns` WRITE;
/*!40000 ALTER TABLE `returns` DISABLE KEYS */;
INSERT INTO `returns` VALUES (501,101,'REQUESTED',NULL,'2025-09-06 06:40:12','2025-09-06 06:40:12',11.98,0.63,'The yarn had visible defects and color inconsistencies','Defective item',NULL,NULL,NULL,NULL),(502,102,'APPROVED',NULL,'2025-09-06 06:40:12','2025-09-06 06:40:12',6.99,0.37,'Customer received different color than ordered','Wrong color',NULL,NULL,NULL,NULL),(503,103,'RECEIVED',NULL,'2025-09-06 06:40:12','2025-09-06 06:40:12',23.97,1.26,'Yarn thickness was different than expected','Size mismatch',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `returns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `code` varchar(32) NOT NULL,
  `name` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'USER','User'),(2,'ADMIN','Administrator');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipment_status_history`
--

DROP TABLE IF EXISTS `shipment_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipment_status_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shipment_id` bigint NOT NULL,
  `from_status` enum('CREATED','SHIPPED','IN_TRANSIT','DELIVERED','LOST','RETURNED') DEFAULT NULL,
  `to_status` enum('CREATED','SHIPPED','IN_TRANSIT','DELIVERED','LOST','RETURNED') NOT NULL,
  `changed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `changed_by` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `shipment_id` (`shipment_id`),
  CONSTRAINT `shipment_status_history_ibfk_1` FOREIGN KEY (`shipment_id`) REFERENCES `shipments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3005 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipment_status_history`
--

LOCK TABLES `shipment_status_history` WRITE;
/*!40000 ALTER TABLE `shipment_status_history` DISABLE KEYS */;
INSERT INTO `shipment_status_history` VALUES (3001,101,'CREATED','SHIPPED','2025-09-05 06:40:12','system',NULL),(3002,102,'CREATED','SHIPPED','2025-09-04 06:40:12','system',NULL),(3003,103,'CREATED','SHIPPED','2025-09-05 06:40:12','system',NULL),(3004,101,'SHIPPED','IN_TRANSIT','2025-09-05 18:40:12','courier',NULL);
/*!40000 ALTER TABLE `shipment_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipments`
--

DROP TABLE IF EXISTS `shipments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `method_id` bigint NOT NULL,
  `tracking_no` varchar(128) DEFAULT NULL,
  `status` enum('CREATED','SHIPPED','IN_TRANSIT','DELIVERED','LOST','RETURNED') DEFAULT 'CREATED',
  `shipped_at` timestamp NULL DEFAULT NULL,
  `delivered_at` timestamp NULL DEFAULT NULL,
  `tracking_number` varchar(255) DEFAULT NULL,
  `shipping_method_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `method_id` (`method_id`),
  KEY `FKoqjrj8m21hs3csp6gt4h74a0k` (`shipping_method_id`),
  CONSTRAINT `FKoqjrj8m21hs3csp6gt4h74a0k` FOREIGN KEY (`shipping_method_id`) REFERENCES `shipping_methods` (`id`),
  CONSTRAINT `shipments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `shipments_ibfk_2` FOREIGN KEY (`method_id`) REFERENCES `shipping_methods` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipments`
--

LOCK TABLES `shipments` WRITE;
/*!40000 ALTER TABLE `shipments` DISABLE KEYS */;
INSERT INTO `shipments` VALUES (101,101,1,'TRACK-1001','SHIPPED','2025-09-06 06:40:11',NULL,NULL,NULL),(102,102,2,'TRACK-1002','SHIPPED','2025-09-06 06:40:11',NULL,NULL,NULL),(103,103,1,'TRACK-1003','SHIPPED','2025-09-06 06:40:11',NULL,NULL,NULL);
/*!40000 ALTER TABLE `shipments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_addresses`
--

DROP TABLE IF EXISTS `shipping_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `address_type` enum('SHIPPING','BILLING') NOT NULL DEFAULT 'SHIPPING',
  `recipient` varchar(255) NOT NULL,
  `street_1` varchar(255) NOT NULL,
  `street_2` varchar(255) DEFAULT NULL,
  `city` varchar(100) NOT NULL,
  `region` varchar(100) DEFAULT NULL,
  `postal_code` varchar(32) NOT NULL,
  `country` char(2) NOT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `shipping_addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=306 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_addresses`
--

LOCK TABLES `shipping_addresses` WRITE;
/*!40000 ALTER TABLE `shipping_addresses` DISABLE KEYS */;
INSERT INTO `shipping_addresses` VALUES (301,101,'SHIPPING','Denis Novicov','Stefan cel Mare 123',NULL,'Chisinau','Chisinau','MD-2001','MD','+37360000001','2025-09-06 06:40:12'),(302,102,'SHIPPING','Maria Popescu','Ion Mihalache 45','Ap. 12','Bucharest','Bucharest','010181','RO','+40212345678','2025-09-06 06:40:12'),(303,103,'BILLING','Ivan Ivanov','Tverskaya 7',NULL,'Moscow','Moscow','101000','RU','+79161234567','2025-09-06 06:40:12'),(304,101,'BILLING','Denis Novicov','Stefan cel Mare 123',NULL,'Chisinau','Chisinau','MD-2001','MD','+37360000001','2025-09-06 06:40:12'),(305,102,'SHIPPING','Maria Popescu','Calea Victoriei 200',NULL,'Bucharest','Bucharest','010026','RO','+40212345679','2025-09-06 06:40:12');
/*!40000 ALTER TABLE `shipping_addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipping_methods`
--

DROP TABLE IF EXISTS `shipping_methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipping_methods` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `carrier` varchar(64) NOT NULL,
  `service` varchar(64) NOT NULL,
  `base_price_czk` decimal(12,2) NOT NULL,
  `delivery_days_min` smallint DEFAULT NULL,
  `delivery_days_max` smallint DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `estimated_time` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_carrier_service` (`carrier`,`service`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipping_methods`
--

LOCK TABLES `shipping_methods` WRITE;
/*!40000 ALTER TABLE `shipping_methods` DISABLE KEYS */;
INSERT INTO `shipping_methods` VALUES (1,'Post','Standard',5.00,3,7,1,NULL,'Обычная почта',NULL),(2,'Courier','Express',15.00,1,2,1,NULL,'Курьерская доставка',NULL);
/*!40000 ALTER TABLE `shipping_methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_favorites`
--

DROP TABLE IF EXISTS `user_favorites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_favorites` (
  `user_id` bigint NOT NULL,
  `color_id` bigint NOT NULL,
  `added_at` datetime(6) DEFAULT NULL,
  `yarn_id` bigint DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `notes` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`color_id`),
  UNIQUE KEY `idx_user_favorite_user_yarn` (`user_id`,`yarn_id`),
  KEY `color_id` (`color_id`),
  KEY `FKbvf4vi0x7d4b6iins1r07m1pm` (`yarn_id`),
  KEY `idx_user_favorite_user_id` (`user_id`),
  KEY `idx_user_favorite_yarn_id` (`yarn_id`),
  KEY `idx_user_favorite_added_at` (`added_at`),
  CONSTRAINT `FKbvf4vi0x7d4b6iins1r07m1pm` FOREIGN KEY (`yarn_id`) REFERENCES `yarns` (`id`),
  CONSTRAINT `user_favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_favorites_ibfk_2` FOREIGN KEY (`color_id`) REFERENCES `colors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_favorites`
--

LOCK TABLES `user_favorites` WRITE;
/*!40000 ALTER TABLE `user_favorites` DISABLE KEYS */;
INSERT INTO `user_favorites` VALUES (101,1,'2025-09-06 06:40:11.000000',1,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000',NULL),(102,2,'2025-09-06 06:40:11.000000',2,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000',NULL),(103,3,'2025-09-06 06:40:11.000000',3,'2025-09-06 06:40:11.000000','2025-09-06 06:40:11.000000',NULL);
/*!40000 ALTER TABLE `user_favorites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (54,1),(55,1),(101,1),(102,1),(103,1),(53,2);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `password_algo` enum('BCRYPT','ARGON2') NOT NULL DEFAULT 'BCRYPT',
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `uk_users_phone` (`phone`),
  KEY `idx_users_email` (`email`),
  KEY `idx_users_phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'test-fix-user@bumbac.local','dummy','BCRYPT',NULL,NULL,NULL,'2025-09-06 08:08:48','2025-09-06 08:08:48'),(53,'admin@bumbac.md','$2a$10$IvZWQkHElPFejFRpGBx4ee.WljOEVeRHaHJBlAPxAWtnrxlGZJkH6','BCRYPT','Super','Admin','+37360000000','2025-09-03 18:05:32','2025-09-03 18:05:32'),(54,'test@bumbac.md','$2a$10$U6/fqv.TJTeWD1uY5SFe4eAqdw4vE9edzr5MIu4IvBE6Af1AZR7Q5u','BCRYPT','Test','User','+37361111111','2025-09-03 18:05:32','2025-09-03 18:05:32'),(55,'testuserme@bumbac.md','$2a$10$q6x39S0xcklQrH0tRFRzLOnl7K4MPx4HOEuWYPZnFVJdxEiMBdV6W','BCRYPT','Test','Me','+37362222222','2025-09-03 18:11:13','2025-09-03 18:11:13'),(101,'testuser1@bumbac.md','$2a$10$dZTVh6O.Kx5YYjJUfHzOsuLWJxE9nTRh8uC4HnJ3QnUaYqAJ7V5Ca','BCRYPT','Test','User1','+37360000101','2025-09-06 06:40:11','2025-09-06 06:40:11'),(102,'testuser2@bumbac.md','$2a$10$dZTVh6O.Kx5YYjJUfHzOsuLWJxE9nTRh8uC4HnJ3QnUaYqAJ7V5Ca','BCRYPT','Test','User2','+37360000102','2025-09-06 06:40:11','2025-09-06 06:40:11'),(103,'testuser3@bumbac.md','$2a$10$dZTVh6O.Kx5YYjJUfHzOsuLWJxE9nTRh8uC4HnJ3QnUaYqAJ7V5Ca','BCRYPT','Test','User3','+37360000103','2025-09-06 06:40:11','2025-09-06 06:40:11');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yarn_attribute_values`
--

DROP TABLE IF EXISTS `yarn_attribute_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `yarn_attribute_values` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `yarn_id` bigint NOT NULL,
  `attribute_id` bigint NOT NULL,
  `value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_yarn_attribute` (`yarn_id`,`attribute_id`),
  KEY `idx_yarn_attr_yarn_id` (`yarn_id`),
  KEY `idx_yarn_attr_attr_id` (`attribute_id`),
  KEY `idx_yarn_attribute_values_attribute_id` (`attribute_id`),
  CONSTRAINT `fk_yav_attribute` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_yav_yarn` FOREIGN KEY (`yarn_id`) REFERENCES `yarns` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yarn_attribute_values`
--

LOCK TABLES `yarn_attribute_values` WRITE;
/*!40000 ALTER TABLE `yarn_attribute_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `yarn_attribute_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yarn_attribute_values__bak_20250906`
--

DROP TABLE IF EXISTS `yarn_attribute_values__bak_20250906`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `yarn_attribute_values__bak_20250906` (
  `yarn_id` bigint NOT NULL,
  `attr_id` bigint NOT NULL,
  `value_text` varchar(255) DEFAULT NULL,
  `value_number` decimal(12,4) DEFAULT NULL,
  `value_bool` tinyint(1) DEFAULT NULL,
  `value_enum` varchar(128) DEFAULT NULL,
  `attribute_id` bigint NOT NULL,
  PRIMARY KEY (`yarn_id`,`attr_id`),
  KEY `attr_id` (`attr_id`),
  KEY `idx_yarn_attribute_values_yarn_id` (`yarn_id`),
  KEY `idx_yarn_attribute_values_attribute_id` (`attribute_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yarn_attribute_values__bak_20250906`
--

LOCK TABLES `yarn_attribute_values__bak_20250906` WRITE;
/*!40000 ALTER TABLE `yarn_attribute_values__bak_20250906` DISABLE KEYS */;
INSERT INTO `yarn_attribute_values__bak_20250906` VALUES (1,1,NULL,400.0000,NULL,NULL,0),(1,2,NULL,100.0000,NULL,NULL,0),(1,3,'4-5 mm',NULL,NULL,NULL,0),(1,4,'100% Wool',NULL,NULL,NULL,0),(1,5,NULL,NULL,NULL,'Medium',0),(1,6,NULL,NULL,NULL,'Smooth',0),(1,7,NULL,NULL,0,NULL,0),(1,8,NULL,NULL,1,NULL,0),(2,1,NULL,350.0000,NULL,NULL,0),(2,2,NULL,100.0000,NULL,NULL,0),(2,3,'3.5-4 mm',NULL,NULL,NULL,0),(2,4,'100% Cotton',NULL,NULL,NULL,0),(2,5,NULL,NULL,NULL,'Fine',0),(2,6,NULL,NULL,NULL,'Soft',0),(2,7,NULL,NULL,1,NULL,0),(2,8,NULL,NULL,1,NULL,0),(3,1,NULL,200.0000,NULL,NULL,0),(3,2,NULL,50.0000,NULL,NULL,0),(3,3,'2.5-3 mm',NULL,NULL,NULL,0),(3,4,'100% Baby Acrylic',NULL,NULL,NULL,0),(3,5,NULL,NULL,NULL,'Extra Fine',0),(3,6,NULL,NULL,NULL,'Ultra Soft',0),(3,7,NULL,NULL,1,NULL,0),(3,8,NULL,NULL,1,NULL,0),(4,1,NULL,120.0000,NULL,NULL,0),(4,2,NULL,150.0000,NULL,NULL,0),(4,3,'8-10 mm',NULL,NULL,NULL,0),(4,4,'80% Wool, 20% Acrylic',NULL,NULL,NULL,0),(4,5,NULL,NULL,NULL,'Chunky',0),(4,6,NULL,NULL,NULL,'Textured',0),(4,7,NULL,NULL,0,NULL,0),(4,8,NULL,NULL,0,NULL,0),(5,1,NULL,380.0000,NULL,NULL,0),(5,2,NULL,100.0000,NULL,NULL,0),(5,3,'4 mm',NULL,NULL,NULL,0),(5,4,'100% Acrylic',NULL,NULL,NULL,0),(5,5,NULL,NULL,NULL,'Medium',0),(5,6,NULL,NULL,NULL,'Smooth',0),(5,7,NULL,NULL,1,NULL,0),(5,8,NULL,NULL,1,NULL,0);
/*!40000 ALTER TABLE `yarn_attribute_values__bak_20250906` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yarn_category`
--

DROP TABLE IF EXISTS `yarn_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `yarn_category` (
  `yarn_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`yarn_id`,`category_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `yarn_category_ibfk_1` FOREIGN KEY (`yarn_id`) REFERENCES `yarns` (`id`) ON DELETE CASCADE,
  CONSTRAINT `yarn_category_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yarn_category`
--

LOCK TABLES `yarn_category` WRITE;
/*!40000 ALTER TABLE `yarn_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `yarn_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yarn_prices`
--

DROP TABLE IF EXISTS `yarn_prices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `yarn_prices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `yarn_id` bigint NOT NULL,
  `price_czk` decimal(12,2) NOT NULL,
  `valid_from` date NOT NULL,
  `valid_to` date DEFAULT '9999-12-31',
  PRIMARY KEY (`id`),
  KEY `yarn_id` (`yarn_id`),
  CONSTRAINT `yarn_prices_ibfk_1` FOREIGN KEY (`yarn_id`) REFERENCES `yarns` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yarn_prices`
--

LOCK TABLES `yarn_prices` WRITE;
/*!40000 ALTER TABLE `yarn_prices` DISABLE KEYS */;
/*!40000 ALTER TABLE `yarn_prices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yarn_translations`
--

DROP TABLE IF EXISTS `yarn_translations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `yarn_translations` (
  `yarn_id` bigint NOT NULL,
  `locale` varchar(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text,
  PRIMARY KEY (`yarn_id`,`locale`),
  CONSTRAINT `yarn_translations_ibfk_1` FOREIGN KEY (`yarn_id`) REFERENCES `yarns` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yarn_translations`
--

LOCK TABLES `yarn_translations` WRITE;
/*!40000 ALTER TABLE `yarn_translations` DISABLE KEYS */;
INSERT INTO `yarn_translations` VALUES (1,'en','Test Yarn 1','English description for yarn 1'),(1,'ro','Fir de test 1','Descriere în română pentru firul 1'),(1,'ru','Тестовая пряжа 1','Описание на русском для пряжи 1'),(2,'en','Test Yarn 2','English description for yarn 2'),(2,'ro','Fir de test 2','Descriere în română pentru firul 2'),(2,'ru','Тестовая пряжа 2','Описание на русском для пряжи 2'),(3,'en','Test Yarn 3','English description for yarn 3'),(3,'ro','Fir de test 3','Descriere în română pentru firul 3'),(3,'ru','Тестовая пряжа 3','Описание на русском для пряжи 3'),(4,'en','Test Yarn 4','English description for yarn 4'),(4,'ro','Fir de test 4','Descriere în română pentru firul 4'),(4,'ru','Тестовая пряжа 4','Описание на русском для пряжи 4'),(5,'en','Test Yarn 5','English description for yarn 5'),(5,'ro','Fir de test 5','Descriere în română pentru firul 5'),(5,'ru','Тестовая пряжа 5','Описание на русском для пряжи 5');
/*!40000 ALTER TABLE `yarn_translations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `yarns`
--

DROP TABLE IF EXISTS `yarns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `yarns` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `collection_id` bigint NOT NULL,
  `code` varchar(64) NOT NULL,
  `slug` varchar(128) NOT NULL,
  `weight_grams` decimal(8,2) DEFAULT NULL,
  `length_meters` decimal(8,2) DEFAULT NULL,
  `needle_size_mm` varchar(32) DEFAULT NULL,
  `composition` varchar(255) DEFAULT NULL,
  `texture` varchar(100) DEFAULT NULL,
  `thickness` varchar(50) DEFAULT NULL,
  `features` text,
  `created_at` datetime(6) DEFAULT NULL,
  `length` double DEFAULT NULL,
  `material` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `pricemdl` double DEFAULT NULL,
  `priceusd` double DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `brand_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_yarns_code_collection` (`code`,`collection_id`),
  KEY `collection_id` (`collection_id`),
  KEY `fk_yarn_brand` (`brand_id`),
  KEY `fk_yarn_category` (`category_id`),
  KEY `idx_yarn_name` (`name`),
  KEY `idx_yarn_material` (`material`),
  KEY `idx_yarn_brand_id` (`brand_id`),
  KEY `idx_yarn_category_id` (`category_id`),
  KEY `idx_yarn_collection_id` (`collection_id`),
  CONSTRAINT `fk_yarn_brand` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`),
  CONSTRAINT `fk_yarn_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `yarns_ibfk_1` FOREIGN KEY (`collection_id`) REFERENCES `collections` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `yarns`
--

LOCK TABLES `yarns` WRITE;
/*!40000 ALTER TABLE `yarns` DISABLE KEYS */;
INSERT INTO `yarns` VALUES (1,2,'TEST-001','test-yarn-1',100.00,400.00,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'Wool',NULL,5.99,0.25,NULL,2,2,NULL),(2,3,'TEST-002','test-yarn-2',100.00,350.00,NULL,NULL,NULL,NULL,NULL,'2025-09-06 06:40:11.000000',NULL,'Cotton',NULL,6.5,0.34,NULL,3,3,'2025-09-06 06:40:11.000000'),(3,4,'TEST-003','test-yarn-3',50.00,200.00,NULL,NULL,NULL,NULL,NULL,'2025-09-06 06:40:11.000000',NULL,'Baby Yarn',NULL,7.5,0.39,NULL,4,4,'2025-09-06 06:40:11.000000'),(4,1,'TEST-004','test-yarn-4',150.00,120.00,NULL,NULL,NULL,NULL,NULL,'2025-09-06 06:40:11.000000',NULL,'Chunky Wool',NULL,8.5,0.44,NULL,5,5,'2025-09-06 06:40:11.000000'),(5,2,'TEST-005','test-yarn-5',100.00,380.00,NULL,NULL,NULL,NULL,NULL,'2025-09-06 06:40:11.000000',NULL,'Acrylic',NULL,9.5,0.49,NULL,1,1,'2025-09-06 06:40:11.000000');
/*!40000 ALTER TABLE `yarns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'yarn_store_local'
--

--
-- Current Database: `yarn_store_local`
--

USE `yarn_store_local`;

--
-- Final view structure for view `colors_v_legacy`
--

/*!50001 DROP VIEW IF EXISTS `colors_v_legacy`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `colors_v_legacy` AS select `c`.`id` AS `id`,`c`.`yarn_id` AS `yarn_id`,`c`.`color_code` AS `color_code`,`c`.`color_name` AS `color_name`,`c`.`sku` AS `sku`,`c`.`barcode` AS `barcode`,`c`.`hex_value` AS `hex_value`,`c`.`stock_quantity` AS `stock_quantity`,(select `m`.`url` from `media_assets` `m` where ((`m`.`entity_type` = 'COLOR') and (`m`.`entity_id` = `c`.`id`) and (`m`.`variant` = 'S')) order by `m`.`sort_order` limit 1) AS `image_url` from `colors` `c` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-06  8:38:25
