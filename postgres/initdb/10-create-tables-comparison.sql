CREATE TABLE releases (
    name VARCHAR(100) PRIMARY KEY,
    version VARCHAR(100)
);

CREATE TABLE componentTable(
  cname VARCHAR(100) PRIMARY KEY,
  cver VARCHAR(100)
);

CREATE TABLE junctionTable(
  releasename VARCHAR(100),
  componentname VARCHAR(100),
  PRIMARY KEY (releasename, componentname)
);
