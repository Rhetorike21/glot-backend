-- <User> -- (password : abcd1234)
insert into user (id, type, account_id, name, email, mobile, phone, password, marketing_agreement, subscription_id,
                  active, last_logged_in_at,
                  created_time,
                  modified_time)
values (1, 'personal', 'test01personal', '테스트용 개인 사용자', 'test@personal.com', '01012345678', '05312345678',
        '{bcrypt}$2a$10$NC5hlrD/BhjMXZtE2ZH.8.Hanx4WLggyOy4I1sY9ZhSE5i8qXXFPO',
        true, null, true, '2023-10-13 09:00:00', '2023-10-13 09:00:00', '2023-10-13 09:00:00'),
       (2, 'organization', 'test02org', '테스트용 기관 사용자', 'test@organization.com', '01056781234',
        '05356781234', '{bcrypt}$2a$10$NC5hlrD/BhjMXZtE2ZH.8.Hanx4WLggyOy4I1sY9ZhSE5i8qXXFPO',
        true, null, true, '2023-10-13 09:00:00', '2023-10-13 09:00:00', '2023-10-13 09:00:00');

insert into user_roles (user_id, roles)
values (1, 'ROLE_USER'),
       (1, 'ROLE_PERSONAL'),
       (2, 'ROLE_USER'),
       (2, 'ROLE_ORG');

insert into personal (id)
values (1);

insert into organization (id, organization_name)
values (2, '한국고등학교');


INSERT INTO plan (plan_period, id, price, discounted_price, type, name)
VALUES ('MONTH', 1, 130, 130, 'basic', 'GLOT 베이직'),
       ('YEAR', 2, 130, 130, 'basic', 'GLOT 베이직'),
       ('MONTH', 3, 130, 120,  'enterprise', 'GLOT 엔터프라이즈'),
       ('YEAR', 4, 130, 110, 'enterprise', 'GLOT 엔터프라이즈'),
       ('DAY', 5, 100, 100, 'basic', '베이직 요금제 분당 결제 (테스트)');
