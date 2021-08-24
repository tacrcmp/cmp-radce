-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema cmpradce
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cmpradce
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cmpradce` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;

USE `cmpradce` ;

-- -----------------------------------------------------
-- Table `cmpradce`.`Category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cmpradce`.`Category` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cmpradce`.`Document`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cmpradce`.`Document` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `text` TEXT NOT NULL,
  `source` VARCHAR(45) NOT NULL,
  `category_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Document_1_idx` (`category_id` ASC) VISIBLE,
  CONSTRAINT `fk_Document_1`
    FOREIGN KEY (`category_id`)
    REFERENCES `cmpradce`.`Category` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cmpradce`.`ngram`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cmpradce`.`ngram` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ngram` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cmpradce`.`category_profile_item`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cmpradce`.`category_profile_item` (
  `category_id` INT NOT NULL,
  `ngram_id` INT NOT NULL,
  `position` INT NOT NULL,
  PRIMARY KEY (`category_id`, `ngram_id`),
  INDEX `fk_category_profile_item_2_idx` (`ngram_id` ASC) VISIBLE,
  CONSTRAINT `fk_category_profile_item_1`
    FOREIGN KEY (`category_id`)
    REFERENCES `cmpradce`.`Category` (`id`),
  CONSTRAINT `fk_category_profile_item_2`
    FOREIGN KEY (`ngram_id`)
    REFERENCES `cmpradce`.`ngram` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cmpradce`.`keyword`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cmpradce`.`keyword` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `word` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cmpradce`.`keyword_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cmpradce`.`keyword_category` (
  `category_id` INT NOT NULL,
  `keyWord_id` INT NOT NULL,
  PRIMARY KEY (`category_id`, `keyWord_id`),
  INDEX `fk_keyword_category_1_idx` (`keyWord_id` ASC) VISIBLE,
  CONSTRAINT `fk_keyword_category_1`
    FOREIGN KEY (`keyWord_id`)
    REFERENCES `cmpradce`.`keyword` (`id`),
  CONSTRAINT `fk_keyword_category_2`
    FOREIGN KEY (`category_id`)
    REFERENCES `cmpradce`.`Category` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `cmpradce`.`lastsync`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cmpradce`.`lastsync` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `lastSync` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
