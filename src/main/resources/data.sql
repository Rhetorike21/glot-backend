-- <User> -- (password : abcd1234)
insert into user (id, type, account_id, name, email, mobile, phone, password, marketing_agreement, created_time,
                  modified_time)
values (1, 'personal', 'test01personal', '테스트용 개인 사용자', 'test@personal.com', '053-1234-5678', '010-1234-5678', '{bcrypt}$2a$10$NC5hlrD/BhjMXZtE2ZH.8.Hanx4WLggyOy4I1sY9ZhSE5i8qXXFPO',
        true, '2023-10-13 09:00:00', '2023-10-13 09:00:00'),
       (2, 'organization', 'test02org', '테스트용 기관 사용자', 'test@organization.com', '053-5678-1234',
        '010-5678-1234', '{bcrypt}$2a$10$NC5hlrD/BhjMXZtE2ZH.8.Hanx4WLggyOy4I1sY9ZhSE5i8qXXFPO',
        true, '2023-10-13 09:00:00', '2023-10-13 09:00:00');

insert into user_roles (user_id, roles)
values (1, 'USER');

insert into personal (id)
values (1);

insert into organization (id, organization_name)
values (2, '한국고등학교');