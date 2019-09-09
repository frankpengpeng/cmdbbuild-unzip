## CMDBuild 3.1.0 (released 2019-07-10)

### NEW FEATURES:

* Csv/xls[x] import/export (manual or scheduled)
* Configurable sso integration module ( _services/custom-login_ )
* Improved security, support for _PBKDF2_ password protection
* Improved workflow upgrade
* Improved cli utils
* Added gis tematic mapping
* Restws and core test additions
* Possibility of displaying the attached files as an additional fieldset of the data card
* Possibility of displaying the relationships of a domain as an additional fielset of the data card
* Extension of the “Show if” feature as “View rules”
* New feature "Auto value" for fields
* Synchronization between the filter applied to the list of data cards of a class and the corresponding elements displayed on the map
* New custom components that can be used in context menus
* Possibility of uploading the customer's logo which is placed alongside the CMDBuild one
* Added Danish localization
* Added Norwegian localization

### BUGFIX:

* Recovery of the process startup task in the Task Manager
* Report are now translated in menu
* Added uploadcustompagedir command to upload a custom page via rest command
* Reserved domain relations are no longer being shown
* Added configuration to hide save button in workflow
* Added user sorting
* Added card ws distinct option
* Added system config to set relation limit
* Added detail parameter to menu ws
* Geo attribute deletion is no longer generating errors
* Added new feature to center a map when no items are selected
* Added print view function
* Removed unused sorters
* Removed screen flashes in login form
* Added ws to obtain a boundary box for attribute values
* Multiple filter/roles no longer generates a server error
* Added basic fulltext filter for geo style rules
* Added auto redirect to url on login if redirect config is not empty
* Forced usage of subclass instead of superclass model to create cards in reference combo popup
* Attachment widget various fixes
* Added possibility of resize of textareas in attributes administration
* SimpleClass.BeginDate is now reserved
* Processing of int values in numeric xls columns is no longer broken
* Various minor fixes for BIM administration
* Fixed skipunknowncolumns template processing related issues
* Apache poi has been upgraded
* PresetFromCard has been implemented
* Fixed LockNotAcquired error
* Button to remove filters in administration module is no longer broken
* Fixed filter processing related issues
* Gis icon preview now automatically refreshes when updated
* Minor fixes for Icon management
* Group and permission default filters are now saved
* Email widget serialization for rest v2 is no longer broken
* Allow more then one row expanded in history grid
* Default filter on Class are now saved
* Stop queue button is now disabled when queue is not enabled
* Ip type attributes are no longer forced to IPV4
* Multilevel and cql filter have been removed from bim layers
* Gisnavigationtree and bimnavigationtree now have active toggle button
* Added endpoint to obtain class domains
* Added geostyle rules put endpoint
* Fixed query builder related issues
* Changed filter format in rest v2 class attribute response
* Fixed errors in process instances rest v2
* Added configurable strong password algorythm
* Fixed permission of subclasses
* Litteral `{` is now supported in email template
* Fixed widget serialization for rest v2
* Added tenant column hidden in grids
* Fixed relation attributes related issues
* Fixed start activity related issues
* Uniformed tenant label to other labels
* Added geo style rules application without saving on db
* Fixed occasional errors on add form mode in permission page
* Lookup attributes in card relation ws are now handled
* Upload report is no longer broken
* Fixed rest v2 process instances response
* Class -> Domains -> Form are no longer missing 'View' tool button
* Fixed domain errors on new form
* Fixed advanced filter related bugs
* Added system configuration for default email account
* The form of navigation tree is now in view mode after edit
* Domain grids now hide 'M/D, Label M/D, Inline' columns
* Doubleclick on geoattribute grid no longer breaks ui
* Gis icon upload now refreshes the grid
* Input fields no longer make Firefox crash
* Added ws to query distinct values from a class
* LinkCards output is no longer null
* Auto skip email with no TO address (set directly to status `skipped`)
* It is now possible to create/edit workflow widgets in classes edit form
* Domain button from process tab is no longer broken
* Date filter no longer looses the date values
* Process history is no longer missing data
* Load activity in process instance is now specified
* Option to create a default menu is no longer missing
* Fixed calendar related issues
* Process instance history is no longer missing data
* Detail window in administration module is no longer staying open when changing context
* Fixed between filter related issues
* Menu list in administration module no longer shows Code instead of description
* Fixed icon related errors in administration module
* Improved class/process save/upload
* Reports no longer remain stuck in loading
* Nav tree with multiple nodes for the same class are now handled
* Multiple selection flags are now correctly updated
* Wf is now allowed to start with no-db performer
* Added full request log tracking
* Fixed csv stream processing related issues
* Fixed advanced filters with boolean attributes
* Removed popup error after logout
* Reduced system configuration changes processing time
* Combo for class/process/... now shows original description
* Sort option on permission page is no longer broken
* Reserved classes are no longer shown in grant ws
* Card filter for geo value query are now handled
* Localization page now ignores processes if workflow is not active
* Fixed class and process icon related issues
* Fixed geo attribute and gis icons related issues
* Menu shows again source folders in edit/add
* Tooltip no longer overlaps with custom validator
* Added support for function filter
* Now map reads initial configuration ZoomMaz, ZoomMin, InitialZoom, Longitudine, latitudine
* Mousewheel has been disabled on numeric fields
* Fixed hidden relations related issues
* Existing locales of class attribute description are no longer blank
* Process advancement no longer remains in loading
* Migration script now handles unique indexes
* Fixed class creation related issues
* Added system configuration endpoint
* Improved performance of attribute reorder
* Added missing SOAP functions
* Added cli tool to check dump
* Fixed master/detail tab simple class related issues
* Fixed fkdomain ws filter
* Added `isMasterDetail` filtrable attribute
* Added print schema
* Added, on db restore, option to freeze (not expire) existing sessions
* Added backup config files before update
* Attribute filters in report parameters are now handled
* Cmis selection is now forced in DMS settings
* Grid over window size in now scrollable
* Added card print for cards with `"` in attribute label
* Added new wd migration (from shark via db)
* Permission flags in relations are now handled
* Added view card print
* Added wf api for update/advance process
* Card access (tenant) is now checked for relation update/delete permissions
* Default for groups in filters is now saved
* Foreign keys are no longer shown as integer
* Fixed domain active field related issues
* Menus in administration navigation are now sorted
* Added geoserver file handling
* Added uptime to restws cli status report
* Added lookups migration with description for code
* Card display is no longer stuck on loading
* Select all in link card is now possible
* Added email template ws v2
* Fixed typeAhead related issues
* Added attachment category description translation
* Processes description is now translated
* Added client api to get remote lookup from type and code
* Added withCard() method to wf email api
* Added download zip CustomPages
* Modified date serialization on custom form widget
* Menu with already open popups now correctly closes
* Implemented newMail wf api method
* Removed unnecessary store filters
* Fixed custom form widget related issues
* Added missing file/dir error in job import
* Wf card api now returns boolean values
* Added default for showInGrid and writable properties in custom form widget
* Added user config for preferred office suite
* Added defaultSearchFilter in Reference popup instead of variable in viewmodel
* Added process stoppableByUser field in response
* Fixes various issues related to header auth
* Added navigation rule for views in menu after render event
* Implemented findAttributeFor for local wf api
* Fixed parallel gate wf processing related issues
* Clear model attributes in CustomForm widget panel
* Added edit possibility of card from linkCards widget
* Prevent special characters in dynamic model fields
* Added configuration to send always all fields to the server in PUT requests for cards and instances
* Fixed eval sql query related issues
* Functions with non-lowecase names are now handled
* Added add attachments to card via cmdb api
* Added case insensitive processing of `TYPE: function` comment value
* Fixed custom page component processing related issues
* Cleanup of user friendly messages for common import errors
* Fixed order grid details problems
* Fixed import of ipvAny attribute related issues
* Fixed editing relations related errors
* Added properties on grant model
* Email.delay param is now handled in seconds and not milliseconds
* Cm filter keys are now case-insensitive
* Req id is now included in popup messages
* Domain functions minor refactoring
* Added configuration parameter for column offset in import template
* Fixed ws v2 description translation related issues
* Default class filter are now correctly saved
* Added timezone in user config preferences
* Grant eventbus refactoring
* Added custompages active filed
* Card attribute reference fields now accept empty strings
* Fixed support for startsWith and endsWith in strings for IE11
* Added creation of fictitious menu


## CMDBuild 3.0.0 (released 2019-04-12)

### NEW FEATURES:
* Complete rewriting of the user interface, both for the Data Management and the Administration Module, with new layout and new functionalities.
* Complete refactoring of the server code.


## Previous versions

For the CHANGELOG versions up to 2.5.1 view http://www.cmdbuild.org/en/download/changelog-old
