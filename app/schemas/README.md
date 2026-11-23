Room 2.7.1 exports the current version-2 schema here during Gradle compilation.

TaskBar 1.0 used `exportSchema=false` and did not publish its original v1 schema JSON.
The v1 -> v2 migration is tested by creating an authentic SQLite v1 database in
`RoomMigrationInstrumentedTest`, running the production migration, then opening
with Room v2 and asserting that task records remain intact.

After a successful build, commit the exported `kz.taskbar.app.data.TaskDatabase/2.json`.
