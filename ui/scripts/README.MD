### install or update dependency
```cd {cmdbuildRepositoryPath}/ui/scripts
npm install```

### to update languages
`npm run update-locales`

### to import old languages files
`npm run import-old-locales -- --dir={oldLocalePath}`

change {oldLocalePath} with your path like *"~/workspace/cmdbuild-25/cmdbuild/cmdbuild/src/main/webapp/translations"*

### to export languages as csv
`npm run export-csv -- --dir={csvDir}`

change {csvDir} with your path like *"./csv"*

### to import languages as csv
`npm run import-csv -- --dir={csvDir} --lang={lang}`

change {csvDir} with your path like *"./csv"* and {lang} with the language you are importing.
The script will try to import data from `Locales-{lang}.csv` and `LocalesAdministration-{lang}.csv`.