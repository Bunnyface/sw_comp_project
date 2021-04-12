INSERT INTO module(name) VALUES ('TestModule'), ('DemoModule');
  
INSERT INTO component(name, url, version, license, copyright)
	VALUES ('TestComponent', 'testurl', 'Version 1', 'TestLicence', 'TestCopyright'),
		   ('DemoComponent', 'testurl2', 'Version 1', 'TestLicence', 'TestCopyright' );

INSERT INTO sub_component(name, url, version, license, copyright)
	VALUES ('TestSubComponent', 'testurl', 'Version 1', 'TestLicence', 'TestCopyright'),
		   ('DemoSubComponent', 'testurl2', 'Version 1', 'TestLicence', 'TestCopyright');

INSERT INTO module_component(module_id, comp_id, usage_type, date, attr_value1, attr_value2, attr_value3, comment_one, comment_two)
	VALUES (1, 1, 'For fun', '2021-01-01', 'Text One', 'Text Two', 'Text Three', 'Comment One', 'Comment two'),
			(1, 2, 'Also for fun','2021-01-01','Text One 1', 'Text 2', 'Text 3', 'Comment 1', 'Comment 2');

INSERT INTO junction_table(comp_id, subcomp_id) VALUES(1, 2), (2, 1);