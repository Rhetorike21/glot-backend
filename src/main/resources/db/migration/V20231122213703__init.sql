# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 11.1.2-MariaDB)
# Database: glot_local
# Generation Time: 2023-11-22 06:12:50 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table order
# ------------------------------------------------------------

DROP TABLE IF EXISTS `order`;

CREATE TABLE `order` (
                         `first_ordered_date` date DEFAULT NULL,
                         `quantity` int(11) NOT NULL,
                         `created_time` datetime(6) DEFAULT NULL,
                         `modified_time` datetime(6) DEFAULT NULL,
                         `plan_id` bigint(20) DEFAULT NULL,
                         `supply_price` bigint(20) NOT NULL,
                         `total_price` bigint(20) NOT NULL,
                         `user_id` bigint(20) DEFAULT NULL,
                         `vat` bigint(20) NOT NULL,
                         `id` varchar(255) NOT NULL,
                         `status` enum('CANCELLED','EMPTY','FAILED','PAID','READY') DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `FKh7b96v0vj5vosrlws1srjsm6n` (`plan_id`),
                         KEY `FKcpl0mjoeqhxvgeeeq5piwpd3i` (`user_id`),
                         CONSTRAINT `FKcpl0mjoeqhxvgeeeq5piwpd3i` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                         CONSTRAINT `FKh7b96v0vj5vosrlws1srjsm6n` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



# Dump of table organization
# ------------------------------------------------------------

DROP TABLE IF EXISTS `organization`;

CREATE TABLE `organization` (
                                `id` bigint(20) NOT NULL,
                                `organization_name` varchar(50) DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                CONSTRAINT `FKnch8p5lb2ypubn4pdi4ikcacq` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `organization` WRITE;
/*!40000 ALTER TABLE `organization` DISABLE KEYS */;

INSERT INTO `organization` (`id`, `organization_name`)
VALUES
    (2,'한국고등학교');

/*!40000 ALTER TABLE `organization` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table organization_member
# ------------------------------------------------------------

DROP TABLE IF EXISTS `organization_member`;

CREATE TABLE `organization_member` (
                                       `id` bigint(20) NOT NULL,
                                       PRIMARY KEY (`id`),
                                       CONSTRAINT `FK44imgaav31nwjqg6i3bap21q8` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



# Dump of table personal
# ------------------------------------------------------------

DROP TABLE IF EXISTS `personal`;

CREATE TABLE `personal` (
                            `id` bigint(20) NOT NULL,
                            PRIMARY KEY (`id`),
                            CONSTRAINT `FK9408bnb73fqgq702k8svxsdam` FOREIGN KEY (`id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `personal` WRITE;
/*!40000 ALTER TABLE `personal` DISABLE KEYS */;

INSERT INTO `personal` (`id`)
VALUES
    (1);

/*!40000 ALTER TABLE `personal` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table plan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `plan`;

CREATE TABLE `plan` (
                        `discounted_price` bigint(20) NOT NULL,
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `price` bigint(20) NOT NULL,
                        `type` varchar(31) NOT NULL,
                        `name` varchar(255) DEFAULT NULL,
                        `plan_period` enum('DAY','MONTH','YEAR') DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `plan` WRITE;
/*!40000 ALTER TABLE `plan` DISABLE KEYS */;

INSERT INTO `plan` (`discounted_price`, `id`, `price`, `type`, `name`, `plan_period`)
VALUES
    (130,1,130,'basic','GLOT 베이직','MONTH'),
    (130,2,130,'basic','GLOT 베이직','YEAR'),
    (120,3,130,'enterprise','GLOT 엔터프라이즈','MONTH'),
    (110,4,130,'enterprise','GLOT 엔터프라이즈','YEAR');

/*!40000 ALTER TABLE `plan` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table subscription
# ------------------------------------------------------------

DROP TABLE IF EXISTS `subscription`;

CREATE TABLE `subscription` (
                                `continued` bit(1) NOT NULL,
                                `end_date` date DEFAULT NULL,
                                `start_date` date DEFAULT NULL,
                                `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                `name` varchar(255) DEFAULT NULL,
                                `order_id` varchar(255) DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `UK_c3k3xerxnijiank1k73901tt8` (`order_id`),
                                CONSTRAINT `FKpotf149mobup60kwplwcwyj66` FOREIGN KEY (`order_id`) REFERENCES `order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
                        `active` bit(1) NOT NULL,
                        `marketing_agreement` bit(1) NOT NULL,
                        `created_time` datetime(6) DEFAULT NULL,
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `last_logged_in_at` datetime(6) DEFAULT NULL,
                        `modified_time` datetime(6) DEFAULT NULL,
                        `subscription_id` bigint(20) DEFAULT NULL,
                        `type` varchar(31) NOT NULL,
                        `account_id` varchar(50) DEFAULT NULL,
                        `email` varchar(50) DEFAULT NULL,
                        `mobile` varchar(50) DEFAULT NULL,
                        `name` varchar(50) DEFAULT NULL,
                        `phone` varchar(50) DEFAULT NULL,
                        `language` enum('KOREAN') DEFAULT NULL,
                        `password` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `FKb3msv39y7d4obq9nnmey7okq9` (`subscription_id`),
                        CONSTRAINT `FKb3msv39y7d4obq9nnmey7okq9` FOREIGN KEY (`subscription_id`) REFERENCES `subscription` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO `user` (`active`, `marketing_agreement`, `created_time`, `id`, `last_logged_in_at`, `modified_time`, `subscription_id`, `type`, `account_id`, `email`, `mobile`, `name`, `phone`, `language`, `password`)
VALUES
    (b'1',b'1','2023-10-13 09:00:00.000000',1,'2023-10-13 09:00:00.000000','2023-10-13 09:00:00.000000',NULL,'personal','test01personal','test@personal.com','01012345678','테스트용 개인 사용자','05312345678',NULL,'{bcrypt}$2a$10$NC5hlrD/BhjMXZtE2ZH.8.Hanx4WLggyOy4I1sY9ZhSE5i8qXXFPO'),
    (b'1',b'1','2023-10-13 09:00:00.000000',2,'2023-10-13 09:00:00.000000','2023-10-13 09:00:00.000000',NULL,'organization','test02org','test@organization.com','01056781234','테스트용 기관 사용자','05356781234',NULL,'{bcrypt}$2a$10$NC5hlrD/BhjMXZtE2ZH.8.Hanx4WLggyOy4I1sY9ZhSE5i8qXXFPO');

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user_roles
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_roles`;

CREATE TABLE `user_roles` (
                              `user_id` bigint(20) NOT NULL,
                              `roles` varchar(255) DEFAULT NULL,
                              KEY `FK55itppkw3i07do3h7qoclqd4k` (`user_id`),
                              CONSTRAINT `FK55itppkw3i07do3h7qoclqd4k` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;

INSERT INTO `user_roles` (`user_id`, `roles`)
VALUES
    (1,'ROLE_USER'),
    (1,'ROLE_PERSONAL'),
    (2,'ROLE_USER'),
    (2,'ROLE_ORG');

/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user_writing_boards
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_writing_boards`;

CREATE TABLE `user_writing_boards` (
                                       `user_id` bigint(20) NOT NULL,
                                       `writing_boards_id` bigint(20) NOT NULL,
                                       UNIQUE KEY `UK_8nf2bdafs9wflcqfugyk6wy71` (`writing_boards_id`),
                                       KEY `FKhxtut6x71t1ug940401xs5pju` (`user_id`),
                                       CONSTRAINT `FKhxtut6x71t1ug940401xs5pju` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                                       CONSTRAINT `FKn72kxmusepfis1pa8ah9j2mbx` FOREIGN KEY (`writing_boards_id`) REFERENCES `writing_board` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



# Dump of table writing_board
# ------------------------------------------------------------

DROP TABLE IF EXISTS `writing_board`;

CREATE TABLE `writing_board` (
                                 `sequence` int(11) NOT NULL,
                                 `created_time` datetime(6) DEFAULT NULL,
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `modified_time` datetime(6) DEFAULT NULL,
                                 `user_id` bigint(20) DEFAULT NULL,
                                 `title` varchar(40) DEFAULT NULL,
                                 `content` varchar(10000) DEFAULT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `FKjpsre50qtlxvkjso3lh46jc7o` (`user_id`),
                                 CONSTRAINT `FKjpsre50qtlxvkjso3lh46jc7o` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
