INSERT INTO TB_USERS (user_id, full_name, email, password_hash, role, is_verified,create_at, update_at)
VALUES('3a648cdb-3cec-44dd-b48f-d4b445389861', 'DEFAULT ADMIN', 'admin@application.com', '$2a$10$/AD/MJJ/tA0QzCZYT2.Jd.vSAIWhcOTlNcol94m1ClHEEGViCMw1q', 'ADMIN', true, '2025-12-11T10:55:28.321795', '2025-12-11T10:55:28.321795');

INSERT INTO TB_VALIDATION_CODES (validation_code_id, code, user_id, code_type, confirmed_at, expires_at, create_at)
VALUES ('2ffdb532-0481-4ca0-99b2-0dff8f5e8a73', '123456', '3a648cdb-3cec-44dd-b48f-d4b445389861', 'EMAIL_VERIFICATION', '2025-12-11T10:57:28.321795', '2025-12-11T11:00:28.321795', '2025-12-11T10:55:28.321795')