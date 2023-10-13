-- <User> --
insert into user (id, type, username, name, email, mobile, phone, password, marketing_agreement, created_time,
                  modified_time)
values (1, 'personal', 'test-personal', '테스트용 개인 사용자', 'test@personal.com', '053-1234-5678', '010-1234-5678', 'abc',
        true, '2023-10-13 09:00:00', '2023-10-13 09:00:00'),
       (2, 'organization', 'test-organization', '테스트용 기관 사용자', 'test@organization.com', '053-5678-1234',
        '010-5678-1234', 'abc',
        true, '2023-10-13 09:00:00', '2023-10-13 09:00:00');

insert into user_roles (user_id, roles)
values (1, 'USER');

insert into personal (id)
values (1);

insert into organization (id, organization_name)
values (2, '한국고등학교');