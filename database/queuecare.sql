-- QueueCare: skema dan data seed (akun demo, bisnis, layanan, jadwal, antrean demo).
-- Password disimpan sebagai hash BCrypt, bukan teks asli.
--
-- Cara impor ke database kosong:
--   mysql -u root -e "CREATE DATABASE queuecare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
--   mysql -u root queuecare < database/queuecare.sql
--
-- Data antrean pada berkas ini memakai tanggal seed terakhir. Untuk data demo
-- yang selalu memakai hari ini, pakai scripts/reset-demo.sh lalu restart aplikasi.
--
-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: localhost    Database: queuecare
-- ------------------------------------------------------
-- Server version	8.0.30

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
-- Current Database: `queuecare`
--



--
-- Table structure for table `business_schedules`
--

DROP TABLE IF EXISTS `business_schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `business_schedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `close_time` time(6) DEFAULT NULL,
  `is_closed` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `day_of_week` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `open_time` time(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `business_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqyp96y068v6prdk3qqcd02io` (`business_id`),
  CONSTRAINT `FKqyp96y068v6prdk3qqcd02io` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `business_schedules`
--

LOCK TABLES `business_schedules` WRITE;
/*!40000 ALTER TABLE `business_schedules` DISABLE KEYS */;
INSERT INTO `business_schedules` VALUES (88,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.841695','SENIN','09:00:00.000000','2026-11-15 07:46:54.841695',20),(89,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.847362','SELASA','09:00:00.000000','2026-11-15 07:46:54.847362',20),(90,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.851372','RABU','09:00:00.000000','2026-11-15 07:46:54.851372',20),(91,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.855444','KAMIS','09:00:00.000000','2026-11-15 07:46:54.855444',20),(92,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.859037','JUMAT','09:00:00.000000','2026-11-15 07:46:54.859037',20),(93,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.862618','SABTU','09:00:00.000000','2026-11-15 07:46:54.862618',20),(94,NULL,_binary '','2026-11-15 07:46:54.865002','MINGGU',NULL,'2026-11-15 07:46:54.865002',20),(95,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.867685','SENIN','09:00:00.000000','2026-11-15 07:46:54.867685',21),(96,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.872451','SELASA','09:00:00.000000','2026-11-15 07:46:54.872451',21),(97,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.877207','RABU','09:00:00.000000','2026-11-15 07:46:54.877207',21),(98,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.881217','KAMIS','09:00:00.000000','2026-11-15 07:46:54.881217',21),(99,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.883711','JUMAT','09:00:00.000000','2026-11-15 07:46:54.883711',21),(100,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.887251','SABTU','09:00:00.000000','2026-11-15 07:46:54.887251',21),(101,NULL,_binary '','2026-11-15 07:46:54.890282','MINGGU',NULL,'2026-11-15 07:46:54.890282',21),(102,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.894017','SENIN','09:00:00.000000','2026-11-15 07:46:54.894017',22),(103,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.897532','SELASA','09:00:00.000000','2026-11-15 07:46:54.897532',22),(104,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.899532','RABU','09:00:00.000000','2026-11-15 07:46:54.899532',22),(105,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.904018','KAMIS','09:00:00.000000','2026-11-15 07:46:54.904018',22),(106,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.908457','JUMAT','09:00:00.000000','2026-11-15 07:46:54.908457',22),(107,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.911928','SABTU','09:00:00.000000','2026-11-15 07:46:54.911928',22),(108,NULL,_binary '','2026-11-15 07:46:54.916586','MINGGU',NULL,'2026-11-15 07:46:54.916586',22),(109,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.919955','SENIN','09:00:00.000000','2026-11-15 07:46:54.919955',23),(110,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.924282','SELASA','09:00:00.000000','2026-11-15 07:46:54.924282',23),(111,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.927562','RABU','09:00:00.000000','2026-11-15 07:46:54.927562',23),(112,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.930754','KAMIS','09:00:00.000000','2026-11-15 07:46:54.930754',23),(113,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.933786','JUMAT','09:00:00.000000','2026-11-15 07:46:54.933786',23),(114,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.937325','SABTU','09:00:00.000000','2026-11-15 07:46:54.937325',23),(115,NULL,_binary '','2026-11-15 07:46:54.942162','MINGGU',NULL,'2026-11-15 07:46:54.942162',23),(116,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.945557','SENIN','09:00:00.000000','2026-11-15 07:46:54.945557',24),(117,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.947794','SELASA','09:00:00.000000','2026-11-15 07:46:54.947794',24),(118,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.950805','RABU','09:00:00.000000','2026-11-15 07:46:54.950805',24),(119,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.954229','KAMIS','09:00:00.000000','2026-11-15 07:46:54.954229',24),(120,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.958173','JUMAT','09:00:00.000000','2026-11-15 07:46:54.958173',24),(121,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.961183','SABTU','09:00:00.000000','2026-11-15 07:46:54.961183',24),(122,NULL,_binary '','2026-11-15 07:46:54.964003','MINGGU',NULL,'2026-11-15 07:46:54.964003',24),(123,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.966343','SENIN','09:00:00.000000','2026-11-15 07:46:54.966343',25),(124,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.969865','SELASA','09:00:00.000000','2026-11-15 07:46:54.969865',25),(125,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.974353','RABU','09:00:00.000000','2026-11-15 07:46:54.974353',25),(126,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.977255','KAMIS','09:00:00.000000','2026-11-15 07:46:54.977255',25),(127,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.980356','JUMAT','09:00:00.000000','2026-11-15 07:46:54.980356',25),(128,'17:00:00.000000',_binary '\0','2026-11-15 07:46:54.982483','SABTU','09:00:00.000000','2026-11-15 07:46:54.982483',25),(129,NULL,_binary '','2026-11-15 07:46:54.985869','MINGGU',NULL,'2026-11-15 07:46:54.985869',25);
/*!40000 ALTER TABLE `business_schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `businesses`
--

DROP TABLE IF EXISTS `businesses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `businesses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active_staff_count` int NOT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `max_capacity` int NOT NULL,
  `name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE','PENDING') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `category_id` bigint NOT NULL,
  `owner_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6ut8ysr3ho6adei9cgi099opu` (`category_id`),
  KEY `FKdh1y7wew1fqwy531d5ojohod5` (`owner_id`),
  CONSTRAINT `FK6ut8ysr3ho6adei9cgi099opu` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `FKdh1y7wew1fqwy531d5ojohod5` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `businesses`
--

LOCK TABLES `businesses` WRITE;
/*!40000 ALTER TABLE `businesses` DISABLE KEYS */;
INSERT INTO `businesses` VALUES (20,2,'Jl. Kemangi','2026-11-15 07:46:54.698387','Potong rambut pria dan anak, cuci rambut, serta treatment rambut. (Data dummy)',6,'Barbershop Senja','0812-0000-0001','ACTIVE','2026-11-15 07:46:54.698387',1,2),(21,1,'Jl. Melati','2026-11-15 07:46:54.737127','Layanan cukur klasik dengan harga bersahabat. (Data dummy)',4,'Pangkas Rambut Griya','0812-0000-0002','INACTIVE','2026-11-15 07:46:54.737127',1,2),(22,3,'Jl. Kenanga','2026-11-15 07:46:54.742891','Salon lengkap: gunting, catok, coloring, dan perawatan rambut. (Data dummy)',8,'Salon Lestari','0812-0000-0003','ACTIVE','2026-11-15 07:46:54.742891',2,2),(23,4,'Jl. Anggrek','2026-11-15 07:46:54.747407','Perawatan rambut wanita dan tata rias pengantin. (Data dummy)',10,'Salon Aurora','0812-0000-0004','ACTIVE','2026-11-15 07:46:54.747407',2,10),(24,2,'Jl. Cempaka','2026-11-15 07:46:54.752179','Perbaikan laptop, ganti SSD, bersih-bersih keyboard, dan instal ulang. (Data dummy)',5,'Servis Laptop Jaya','0812-0000-0005','ACTIVE','2026-11-15 07:46:54.752179',3,10),(25,3,'Jl. Flamboyan','2026-11-15 07:46:54.756691','Konsultasi kulit dan perawatan wajah dasar. (Data dummy)',7,'Klinik Cantik Sehat','0812-0000-0006','PENDING','2026-11-15 07:46:54.756691',4,10);
/*!40000 ALTER TABLE `businesses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'2026-11-15 07:07:09.098154','Barbershop','2026-11-15 07:07:09.098154'),(2,'2026-11-15 07:07:09.107352','Salon','2026-11-15 07:07:09.107352'),(3,'2026-11-15 07:07:09.114879','Servis Laptop','2026-11-15 07:07:09.114879'),(4,'2026-11-15 07:07:09.121881','Klinik Kecantikan','2026-11-15 07:07:09.121881');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `queues`
--

DROP TABLE IF EXISTS `queues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `queues` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `called_at` datetime(6) DEFAULT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `queue_date` date NOT NULL,
  `queue_number` int NOT NULL,
  `started_at` datetime(6) DEFAULT NULL,
  `status` enum('CALLED','CANCELLED','COMPLETED','IN_SERVICE','NO_SHOW','WAITING') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `business_id` bigint NOT NULL,
  `reservation_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_queues_nomor` (`business_id`,`queue_date`,`queue_number`),
  UNIQUE KEY `UKfuwn8ehyhatsw6do1vhubn3kf` (`reservation_id`),
  CONSTRAINT `FK85h7hkx79t39no0wtylp8mpic` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`id`),
  CONSTRAINT `FKo2nkfttxoqprsyuchigoah6c7` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=488 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `queues`
--

LOCK TABLES `queues` WRITE;
/*!40000 ALTER TABLE `queues` DISABLE KEYS */;
INSERT INTO `queues` VALUES (479,NULL,NULL,'2026-11-15 12:50:16.737024','2026-11-16',1,NULL,'WAITING',20,482),(480,'2026-11-15 12:50:16.769777',NULL,'2026-11-15 12:50:16.758925','2026-11-16',2,NULL,'CALLED',20,483),(481,'2026-11-15 12:50:16.801359',NULL,'2026-11-15 12:50:16.790153','2026-11-16',3,'2026-11-15 12:50:16.806869','IN_SERVICE',20,484),(482,'2026-11-15 12:50:16.837063','2026-11-15 12:50:16.846837','2026-11-15 12:50:16.827729','2026-11-16',4,'2026-11-15 12:50:16.838385','COMPLETED',20,485),(483,'2026-11-15 12:50:16.877867',NULL,'2026-11-15 12:50:16.870299','2026-11-16',5,NULL,'NO_SHOW',20,486),(484,NULL,NULL,'2026-11-15 12:50:16.896933','2026-11-16',6,NULL,'CANCELLED',20,487),(485,NULL,NULL,'2026-11-15 12:50:16.926951','2026-11-16',7,NULL,'WAITING',20,488),(486,NULL,NULL,'2026-11-15 12:50:16.946840','2026-11-17',1,NULL,'WAITING',20,489),(487,'2026-11-15 12:50:16.971565','2026-11-15 12:50:16.984255','2026-11-15 12:50:16.962403','2026-11-17',1,'2026-11-15 12:50:16.976722','COMPLETED',22,490);
/*!40000 ALTER TABLE `queues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `reservation_code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `reservation_date` date NOT NULL,
  `reservation_time` time(6) NOT NULL,
  `status` enum('CANCELLED','COMPLETED','CONFIRMED','PENDING') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `business_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `service_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6g1uj544xgjuyhj6kjh6pka6l` (`reservation_code`),
  KEY `FKm0uxj8bk8y5x8t9pnsj7qoni2` (`business_id`),
  KEY `FKcmkyuub3ieebwbnrvh5710ply` (`customer_id`),
  KEY `FK3ww618ll729ubr3pb5krt9d3h` (`service_id`),
  CONSTRAINT `FK3ww618ll729ubr3pb5krt9d3h` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
  CONSTRAINT `FKcmkyuub3ieebwbnrvh5710ply` FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKm0uxj8bk8y5x8t9pnsj7qoni2` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=491 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES (482,'2026-11-15 12:50:16.706768','Antrean (Data dummy)','QC-20261116-0001','2026-11-16','10:00:00.000000','CONFIRMED','2026-11-15 12:50:16.706768',20,23,42),(483,'2026-11-15 12:50:16.756869','Antrean (Data dummy)','QC-20261116-0002','2026-11-16','10:00:00.000000','CONFIRMED','2026-11-15 12:50:16.756869',20,24,42),(484,'2026-11-15 12:50:16.790153','Antrean (Data dummy)','QC-20261116-0003','2026-11-16','10:00:00.000000','CONFIRMED','2026-11-15 12:50:16.790153',20,23,42),(485,'2026-11-15 12:50:16.824705','Antrean (Data dummy)','QC-20261116-0004','2026-11-16','10:00:00.000000','COMPLETED','2026-11-15 12:50:16.846837',20,24,42),(486,'2026-11-15 12:50:16.866725','Antrean (Data dummy)','QC-20261116-0005','2026-11-16','10:00:00.000000','CANCELLED','2026-11-15 12:50:16.886795',20,23,42),(487,'2026-11-15 12:50:16.896933','Antrean (Data dummy)','QC-20261116-0006','2026-11-16','10:00:00.000000','CANCELLED','2026-11-15 12:50:16.909631',20,24,42),(488,'2026-11-15 12:50:16.926951','Antrean (Data dummy)','QC-20261116-0007','2026-11-16','10:00:00.000000','CONFIRMED','2026-11-15 12:50:16.926951',20,24,42),(489,'2026-11-15 12:50:16.944799','Antrean (Data dummy)','QC-20261117-0001','2026-11-17','10:00:00.000000','CONFIRMED','2026-11-15 12:50:16.944799',20,23,42),(490,'2026-11-15 12:50:16.958853','Antrean (Data dummy)','QC-20261117-0002','2026-11-17','10:00:00.000000','COMPLETED','2026-11-15 12:50:16.984255',22,24,47);
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `services`
--

DROP TABLE IF EXISTS `services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `services` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `estimated_duration_minutes` int NOT NULL,
  `name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `business_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKryfh22ccvq43d77rj8d6nfrk8` (`business_id`),
  CONSTRAINT `FKryfh22ccvq43d77rj8d6nfrk8` FOREIGN KEY (`business_id`) REFERENCES `businesses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `services`
--

LOCK TABLES `services` WRITE;
/*!40000 ALTER TABLE `services` DISABLE KEYS */;
INSERT INTO `services` VALUES (42,_binary '','2026-11-15 07:46:54.761709','Potong rambut sesuai keinginan.',30,'Cukur rambut',45000.00,'2026-11-15 07:46:54.761709',20),(43,_binary '','2026-11-15 07:46:54.768220','Potong rambut dan cuci rambut.',45,'Cukur + cuci',65000.00,'2026-11-15 07:46:54.768220',20),(44,_binary '\0','2026-11-15 07:46:54.773924','Potong rambut untuk anak.',25,'Cukur anak',35000.00,'2026-11-15 07:46:54.773924',20),(45,_binary '','2026-11-15 07:46:54.777456','Potong rambut model klasik.',30,'Cukur klasik',40000.00,'2026-11-15 07:46:54.777456',21),(46,_binary '','2026-11-15 07:46:54.781343','Potong rambut dan rapikan jenggot.',40,'Cukur + cukur jenggot',55000.00,'2026-11-15 07:46:54.781343',21),(47,_binary '','2026-11-15 07:46:54.785057','Gunting dan styling dasar.',45,'Gunting rambut',70000.00,'2026-11-15 07:46:54.785057',22),(48,_binary '','2026-11-15 07:46:54.790188','Catok lurus atau keriting.',60,'Catok',90000.00,'2026-11-15 07:46:54.790188',22),(49,_binary '','2026-11-15 07:46:54.794369','Pewarnaan rambut satu warna.',120,'Coloring',250000.00,'2026-11-15 07:46:54.794369',22),(50,_binary '','2026-11-15 07:46:54.798017','Hair mask dan serum.',50,'Perawatan rambut',120000.00,'2026-11-15 07:46:54.798017',22),(51,_binary '','2026-11-15 07:46:54.801730','Gunting dan styling acara.',60,'Gunting + styling',110000.00,'2026-11-15 07:46:54.801730',23),(52,_binary '','2026-11-15 07:46:54.806987','Tata rias lengkap pengantin.',180,'Tata rias pengantin',850000.00,'2026-11-15 07:46:54.806987',23),(53,_binary '\0','2026-11-15 07:46:54.811726','Facial dasar 60 menit.',60,'Perawatan facial',150000.00,'2026-11-15 07:46:54.811726',23),(54,_binary '','2026-11-15 07:46:54.814554','Diagnosis dan perbaikan komponen.',90,'Perbaikan hardware',150000.00,'2026-11-15 07:46:54.814554',24),(55,_binary '','2026-11-15 07:46:54.817957','Pemasangan SSD dan pindah data.',60,'Ganti SSD',200000.00,'2026-11-15 07:46:54.817957',24),(56,_binary '','2026-11-15 07:46:54.822130','Instal ulang sistem operasi.',75,'Instal ulang',180000.00,'2026-11-15 07:46:54.822130',24),(57,_binary '','2026-11-15 07:46:54.825644','Bongkar dan bersihkan keyboard.',30,'Bersih keyboard',90000.00,'2026-11-15 07:46:54.825644',24),(58,_binary '','2026-11-15 07:46:54.829153','Konsultasi kondisi kulit wajah.',30,'Konsultasi kulit',120000.00,'2026-11-15 07:46:54.829153',25),(59,_binary '','2026-11-15 07:46:54.832416','Facial pembersihan 60 menit.',60,'Facial dasar',220000.00,'2026-11-15 07:46:54.832416',25),(60,_binary '','2026-11-15 07:46:54.836356','Peeling untuk sel kulit mati.',45,'Peeling ringan',300000.00,'2026-11-15 07:46:54.836356',25);
/*!40000 ALTER TABLE `services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('ADMIN','CUSTOMER','OWNER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=261 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-11-14 20:22:07.632408','admin@queuecare.id','Admin QueueCare','$2a$10$ZN0glryZ5hM0BOQE482oEuSyz.LG1.e.8mGbDtPa/jZOfwkkCCn3y','ADMIN','2026-11-14 20:22:07.632408',1),(2,'2026-11-14 20:22:07.792609','owner@queuecare.id','Owner Demo','$2a$10$UTL3LeRL879Ric9giPPDOuiv.7eBlYAkagayeB.hunK4p2fyMGY.G','OWNER','2026-11-15 12:51:26.159089',1),(10,'2026-11-15 07:07:09.027285','owner2@queuecare.id','Owner Dua','$2a$10$7Y02ADQbSgz.gI7Ri.8Ed.0OZtRH.uAI/q7IwsoTxp4WFvm9T48lS','OWNER','2026-11-15 07:07:09.027285',1),(23,'2026-11-15 10:09:58.419404','dummy1@queuecare.id','Dummy Satu (Data dummy)','$2a$10$3alighWtcS/7c50vavMyLeH6jgpEDQBYG8i/pVkPAOsYBwDajOyI2','CUSTOMER','2026-11-15 10:09:58.419404',1),(24,'2026-11-15 10:09:58.562928','dummy2@queuecare.id','Dummy Dua (Data dummy)','$2a$10$FMvm7Bndya9St75o2qI9bOTyXe9QtPrXSNGzCyHalI4PeUJ51.p5K','CUSTOMER','2026-11-15 10:09:58.562928',1),(109,'2026-11-15 11:54:47.000000','customer@queuecare.id','Customer Demo','$2a$10$3alighWtcS/7c50vavMyLeH6jgpEDQBYG8i/pVkPAOsYBwDajOyI2','CUSTOMER','2026-11-15 12:51:29.469107',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'queuecare'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-15 12:56:26
