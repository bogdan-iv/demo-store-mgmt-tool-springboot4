
INSERT INTO users (username, password, enabled) VALUES ('user', '$2a$10$eWCsj60sW/0GToigKXa42.1isOl.kpI9Kx6heIX3Mdi13719cpCIS', TRUE); -- Password: password
INSERT INTO users (username, password, enabled) VALUES ('admin', '$2a$10$u6xD73xkMJorEkzJUdrvI.5tY71.pkkhg63Q13WUnU2.HNbnn4.fO', TRUE); -- Password: adminpass

INSERT INTO authorities (username, authority) VALUES ('user', 'ROLE_USER');
INSERT INTO authorities (username, authority) VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO authorities (username, authority) VALUES ('admin', 'ROLE_USER');

