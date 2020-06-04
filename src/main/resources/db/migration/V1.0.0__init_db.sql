--
-- Table structure for table `book`
--
DROP TABLE IF EXISTS `publisher`;
CREATE TABLE `publisher`
(
    `id`          int          NOT NULL AUTO_INCREMENT,
    `name`        varchar(255) NOT NULL,
    `description` text         NULL,
    `create_by`   int          NULL,
    `create_at`   timestamp    NULL,
    `update_by`   int          NULL,
    `update_at`   timestamp    NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

--
-- Table structure for table `book`
--
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`
(
    `id`           int          NOT NULL AUTO_INCREMENT,
    `name`         varchar(255) NOT NULL,
    `description`  text         NULL,
    `publisher_id` int          NOT NULL,
    `create_by`    int          NULL,
    `create_at`    timestamp    NULL,
    `update_by`    int          NULL,
    `update_at`    timestamp    NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_book_w_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `publisher` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

--
-- Table structure for table `author`
--
DROP TABLE IF EXISTS `author`;
CREATE TABLE `author`
(
    `id`        int          NOT NULL AUTO_INCREMENT,
    `name`      varchar(255) NOT NULL,
    `dob`       date         NULL,
    `create_by` int          NULL,
    `create_at` timestamp    NULL,
    `update_by` int          NULL,
    `update_at` timestamp    NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

--
-- Table structure for table `author_book`
--
DROP TABLE IF EXISTS `author_book`;
CREATE TABLE `author_book`
(
    `author_id` int NOT NULL,
    `book_id`   int NOT NULL,
    PRIMARY KEY (`author_id`, `book_id`),
    CONSTRAINT `fk_author_book_w_author` FOREIGN KEY (`author_id`) REFERENCES `author` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT `fk_author_book_w_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;