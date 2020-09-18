# Backy - GDrive backup utility
Backy is a backup tool that requires Java. It can be executed from cron task to be
executed in scheduled intervals without manual influence.

## Stages of Backy's work
### Stage 1 - Load and execute scripts
Backy loads all bash scripts from `scripts` folder and executes them.
These scripts are meant to copy all important files and stuff into `backups` folder
in Backy's workplace.

### Stage 2 - Backup files into zip
All files from `backups` folder are now zipped into `Backy-dd-MM-yyyy.zip` folder named by
current datetime.
GDrive file data for zip is prepared in this stage.

### Stage 3 - Upload backup zip into GDrive
Backy uploads backup zip to GDrive to target folder that can be configured in `config.json`.
Then Backy checks for maximum amount of backups kept in the folder (default 5) and if number
is exceeded it will remove older backups in order to save drive size not to run out of space.

**At this stage our backup is safe at GDrive**

### Stage 4 - Cleanup
At this final stage Backy removes all contents of `backups` folder and removes backup zip file.
Everything is cleared like before the work!

### Stage 5 - Discord webhook notification
This stage will just use Discord webhook url to push a notification about successful backup, nothing else.
Set webhook url in `config.json`

## The Setup
Backy main configuration is in `config.json` folder generated on first run.
There is also `logs.txt` file generated for logging runtime of Backy's work.

User must create `scripts`, `backups` and `credentials` folders himself.

`scripts` folder contains all bash scripts that will copy files into `backups` folder in order
to zip them and upload to GDrive

`backups` folders contains all files to zip and upload to GDrive

`credentials` is GDrive folder required to access Google Services
for credentials folder setup I refer you here https://developers.google.com/drive/api/v3/quickstart/java
and here https://o7planning.org/en/11889/manipulating-files-and-folders-on-google-drive-using-java

When these folders are prepared with all required files in `credentials` folder, you're ready.

---

To run Backy you can use `start.sh` provided or `java -jar Backy.jar` assuming you have compiled
version of this repository. Project uses maven so there shouldn't be problems compiling it