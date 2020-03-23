/*
 Navicat Premium Data Transfer

 Source Server         : share
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : share

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 23/03/2020 14:46:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for shares
-- ----------------------------
DROP TABLE IF EXISTS `shares`;
CREATE TABLE `shares`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '股票名字',
  `nowprice` double(255, 2) NULL DEFAULT NULL COMMENT '当前价格',
  `number` int(20) NULL DEFAULT NULL COMMENT '成交的股票数 一百股为一手',
  `total` int(20) NULL DEFAULT NULL COMMENT '成交金额 万为单位',
  `happentime` datetime(0) NULL DEFAULT NULL COMMENT '发生时间',
  `oneday` double(255, 2) NULL DEFAULT NULL COMMENT '3点以后收盘价也就是k线',
  `fiveday` double(255, 2) NULL DEFAULT NULL,
  `fiveslope` double(255, 2) NULL DEFAULT NULL COMMENT '今天五日和第一个五日的斜率',
  `tenday` double(255, 2) NULL DEFAULT NULL,
  `tenslope` double(255, 2) NULL DEFAULT NULL,
  `twentyday` double(255, 2) NULL DEFAULT NULL,
  `twentyslope` double(255, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1451 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
