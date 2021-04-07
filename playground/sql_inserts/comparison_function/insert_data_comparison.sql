INSERT INTO "Module"(
	id, "Name")
	VALUES (1, 'TestModule'),
			(2, 'DemoModule');
  
INSERT INTO "Component"(
	id, "Name", "URL", "Version", "License", "Copyright")
	VALUES (1, 'TestComponent', 'testurl', 'Version 1', 'TestLicence', 'TestCopyright'),
		   (2, 'DemoComponent', 'testurl2', 'Version 1', 'TestLicence', 'TestCopyright' );

INSERT INTO "SubComponent"(
	id, "Name", "URL", "Version", "License", "Copyright")
	VALUES (1, 'TestSubComponent', 'testurl', 'Version 1', 'TestLicence', 'TestCopyright'),
		   (2, 'DemoSubComponent', 'testurl2', 'Version 1', 'TestLicence', 'TestCopyright' );

INSERT INTO "ModuleComponent"(
	"Module_id", "Component_id", "UsageType", "Date", "ModCompAttrValue1", "ModCompAttrValue2", "ModCompAttrValue3", "CommentOne", "CommentTwo")
	VALUES (1, 1, 'For fun', '2021-01-01', 'Text One', 'Text Two', 'Text Three', 'Comment One', 'Comment two'),
			(1,2, 'Also for fun','2021-01-01','Text One 1', 'Text 2', 'Text 3', 'Comment 1', 'Comment 2');

INSERT INTO "CompSubComp" (
	"comp_id", "subcomp_id")
	VALUES(1,2), (2,1);