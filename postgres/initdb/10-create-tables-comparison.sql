CREATE TABLE "Component" (
  "id" int PRIMARY KEY,
  "Name" varchar,
  "URL" varchar,
  "Version" varchar,
  "License" varchar,
  "Copyright" varchar
);

CREATE TABLE "SubComponent" (
  "id" int PRIMARY KEY,
  "Name" varchar,
  "URL" varchar,
  "Version" varchar,
  "License" varchar,
  "Copyright" varchar
);

CREATE TABLE "CompSubComp" (
  "comp_id" int,
  "subcomp_id" int,
  PRIMARY KEY ("comp_id", "subcomp_id")
);

CREATE TABLE "Module" (
  "id" int PRIMARY KEY,
  "Name" varchar
);

CREATE TABLE "ModuleComponent" (
  "Module_id" int,
  "Component_id" int,
  "UsageType" varchar,
  "ModCompAttrValue1" varchar,
  "ModCompAttrValue2" varchar,
  "ModCompAttrValue3" varchar,
  "Date" Date,
  "CommentOne" varchar,
  "CommentTwo" varchar,
  PRIMARY KEY ("Module_id", "Component_id")
);


ALTER TABLE "CompSubComp" ADD FOREIGN KEY ("comp_id") REFERENCES "Component" ("id");

ALTER TABLE "CompSubComp" ADD FOREIGN KEY ("subcomp_id") REFERENCES "SubComponent" ("id");

ALTER TABLE "ModuleComponent" ADD FOREIGN KEY ("Module_id") REFERENCES "Module" ("id");

ALTER TABLE "ModuleComponent" ADD FOREIGN KEY ("Component_id") REFERENCES "Component" ("id");

