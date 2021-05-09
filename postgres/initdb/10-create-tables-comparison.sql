CREATE TABLE component (
  id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL,
  url VARCHAR,
  version VARCHAR NOT NULL,
  license VARCHAR,
  copyright VARCHAR,
  row_version INTEGER DEFAULT 0,
  CONSTRAINT UC_comp UNIQUE (name, version)
);

CREATE TABLE sub_component (
  id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL,
  url VARCHAR,
  version VARCHAR NOT NULL,
  license VARCHAR,
  copyright VARCHAR,
  row_version INTEGER DEFAULT 0,
  CONSTRAINT UC_subcomp UNIQUE (name, version)
);

CREATE TABLE junction_table (
  comp_id INTEGER NOT NULL,
  subcomp_id INTEGER NOT NULL,
  row_version INTEGER DEFAULT 0,
  PRIMARY KEY(comp_id, subcomp_id),
  FOREIGN KEY(comp_id) REFERENCES component(id),
  FOREIGN KEY(subcomp_id) REFERENCES sub_component(id)
);

CREATE TABLE module (
  id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL,
  row_version INTEGER DEFAULT 0
);

CREATE TABLE module_component (
  module_id INTEGER,
  comp_id INTEGER,
  usage_type VARCHAR,
  attr_value1 VARCHAR,
  attr_value2 VARCHAR,
  attr_value3 VARCHAR,
  date DATE,
  comment_one VARCHAR,
  comment_two VARCHAR,
  row_version INTEGER DEFAULT 0,
  PRIMARY KEY(module_id, comp_id),
  FOREIGN KEY(module_id) REFERENCES module(id),
  FOREIGN KEY(comp_id) REFERENCES component(id)
);
