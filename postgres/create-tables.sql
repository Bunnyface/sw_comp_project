CREATE TABLE "Component" (
  "id" int PRIMARY KEY,
  "Name" varchar,
  "URL" varchar,
  "Version" varchar,
  "License" varchar,
  "Copyright" varchar
);

CREATE TABLE "Module" (
  "id" int PRIMARY KEY,
  "Name" varchar
);

CREATE TABLE "ModuleComponent" (
  "Module_id" int,
  "Component_id" int,
  "UsageType" varchar,
  "Date" Date,
  "TextOne" varchar,
  "TextTwo" varchar,
  "TextThree" varchar,
  "CommentOne" varchar,
  "CommentTwo" varchar,
  PRIMARY KEY ("Module_id", "Component_id")
);

ALTER TABLE "ModuleComponent" ADD FOREIGN KEY ("Module_id") REFERENCES "Module" ("id");

ALTER TABLE "ModuleComponent" ADD FOREIGN KEY ("Component_id") REFERENCES "Component" ("id");

