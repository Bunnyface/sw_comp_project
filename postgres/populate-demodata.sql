INSERT INTO "Module"
    (
    id, "Name")
VALUES
    (1, 'TestModule');

INSERT INTO "Component"
    (
    id, "Name", "URL", "Version", "License", "Copyright")
VALUES
    (1, 'TestComponent', 'testurl', 'Version 1', 'TestLicence', 'TestCopyright'),
    (2, 'DemoComponent', 'testurl2', 'Version 1', 'TestLicence', 'TestCopyright' );


INSERT INTO "ModuleComponent"
    (
    "Module_id", "Component_id", "UsageType", "Date", "TextOne", "TextTwo", "TextThree", "CommentOne", "CommentTwo")
VALUES
    (1, 1, 'For fun', '2021-01-01', 'Text One', 'Text Two', 'Text Three', 'Comment One', 'Comment two'),
    (1, 2, 'Also for fun', '2021-01-01', 'Text One 1', 'Text 2', 'Text 3', 'Comment 1', 'Comment 2');