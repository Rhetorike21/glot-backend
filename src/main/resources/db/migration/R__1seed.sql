-- <IdentityVerification> --
# ${flyway:timestamp}
SET FOREIGN_KEY_CHECKS = 0;
-- *****************************************더미데이터-시작***************************************** --

INSERT INTO `plan` (`discounted_price`, `id`, `price`, `type`, `name`, `plan_period`)
VALUES
    (130, 1, 130, 'basic', 'GLOT 베이직', 'MONTH'),
    (130, 2, 130, 'basic', 'GLOT 베이직', 'YEAR'),
    (120, 3, 130, 'enterprise', 'GLOT 엔터프라이즈', 'MONTH'),
    (110, 4, 130, 'enterprise', 'GLOT 엔터프라이즈', 'YEAR');

-- *****************************************더미데이터-종료***************************************** --
SET FOREIGN_KEY_CHECKS = 1;
