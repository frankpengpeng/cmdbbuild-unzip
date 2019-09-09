-- lookup trigger

ALTER TABLE "LookUp" ALTER COLUMN "Index" SET DEFAULT -1;

CREATE TRIGGER "_cm3_lookup_trigger" AFTER INSERT OR UPDATE ON "LookUp" EXECUTE PROCEDURE _cm3_trigger_lookup();
