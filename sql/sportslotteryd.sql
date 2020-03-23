/*
 Navicat Premium Data Transfer

 Source Server         : share
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : sportslotteryd

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 23/03/2020 16:26:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for game
-- ----------------------------
DROP TABLE IF EXISTS `game`;
CREATE TABLE `game`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `matchs` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联赛',
  `home` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主队',
  `homenum` int(11) NULL DEFAULT NULL COMMENT '主队名次',
  `guest` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '客队',
  `guestnum` int(11) NULL DEFAULT NULL COMMENT '客队名次',
  `result` int(11) NULL DEFAULT NULL COMMENT '胜2 平1 负0',
  `allwin` double(11, 2) NULL DEFAULT NULL COMMENT '百家胜',
  `allflat` double(11, 2) NULL DEFAULT NULL COMMENT '百家平',
  `alldefeat` double(11, 2) NULL DEFAULT NULL COMMENT '百家负',
  `win` double(11, 2) NULL DEFAULT NULL COMMENT '胜',
  `flat` double(11, 2) NULL DEFAULT NULL COMMENT '平',
  `defeat` double(11, 2) NULL DEFAULT NULL COMMENT '负',
  `letwin` double(11, 2) NULL DEFAULT NULL COMMENT '让胜',
  `letflat` double(11, 2) NULL DEFAULT NULL COMMENT '让平',
  `letdefeat` double(11, 2) NULL DEFAULT NULL COMMENT '让负',
  `appwin` double(11, 2) NULL DEFAULT NULL COMMENT 'app预测胜',
  `appflat` double(11, 2) NULL DEFAULT NULL COMMENT 'app预测平',
  `appdefeat` double(11, 2) NULL DEFAULT NULL COMMENT 'app预测负',
  `homescore` int(11) NULL DEFAULT NULL COMMENT '主队进球数',
  `guestscore` int(11) NULL DEFAULT NULL COMMENT '客队进球数',
  `time` datetime(0) NULL DEFAULT NULL COMMENT 'yyyy-MM',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for investlog
-- ----------------------------
DROP TABLE IF EXISTS `investlog`;
CREATE TABLE `investlog`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `money` int(11) NULL DEFAULT NULL COMMENT '投资金额',
  `multiple` double(11, 2) NULL DEFAULT NULL COMMENT '倍数',
  `income` int(11) NULL DEFAULT NULL COMMENT '收入',
  `time` datetime(0) NULL DEFAULT NULL COMMENT 'yyyy-MM',
  `gameid` int(11) NULL DEFAULT NULL COMMENT '关联gameid',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `matchs` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联赛',
  `home` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '球队名',
  `guest` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对手名',
  `result` int(11) NULL DEFAULT NULL COMMENT '胜2 平1 负0',
  `score` int(11) NULL DEFAULT NULL COMMENT '进球数',
  `possession` double(11, 2) NULL DEFAULT NULL COMMENT '控球率',
  `shoot` int(11) NULL DEFAULT NULL COMMENT '射门数',
  `shoottrue` int(11) NULL DEFAULT NULL COMMENT '射正',
  `time` datetime(0) NULL DEFAULT NULL COMMENT 'yyyy-MM',
  `gameid` int(11) NULL DEFAULT NULL COMMENT '关联gameid',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
