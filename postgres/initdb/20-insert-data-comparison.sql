INSERT INTO releases VALUES ('First', '0.1');
INSERT INTO releases VALUES ('Second', '0.2');
INSERT INTO releases VALUES ('Third', '0.3');

INSERT INTO componentTable VALUES ('Java', '1.2');
INSERT INTO componentTable VALUES ('Scala', '3.2');

INSERT INTO junctionTable VALUES ('First', 'Java');
INSERT INTO junctionTable VALUES ('First', 'Scala');
INSERT INTO junctionTable VALUES ('Second', 'Scala');
INSERT INTO junctionTable VALUES ('Third', 'Java');
