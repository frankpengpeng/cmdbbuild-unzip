Copyright Tecnoteca Srl 2005-2019



This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License 
along with this program.
If not, see <http://www.gnu.org/licenses/agpl.html>.



Project website: http://www.cmdbuild.org/en/

Maintainer: Tecnoteca - http://www.tecnoteca.com/en/

The interactive user interfaces in modified source and object code 
versions of this program must display Appropriate Legal Notices, as 
required under Section 5 of the GNU General Public License version 3.
In accordance with Section 7(b) of the GNU General Public License 
version 3, these Appropriate Legal Notices must retain the display 
of the "CMDBuild" logo.
The Logo "CMDBuild" must be a clickable link that leads directly 
to the Internet URL http://www.cmdbuild.org/



Hardware requirements:

* server-class computer (modern architecture)
* 8 GB of RAM (16 GB for application with full functionalities eg. DMS,
map services, BIM services)
* 120 GB of available hard disk space for each CMDBuild instance


Software requirements: 

* any OS able to handle the following applications (linux recommended)
* PostgreSQL from 9.5 to 10.7
* PostGIS 2.4 or 2.5 (optional)
* Apache Tomcat 8.5 (8.5.34 recommended)
* JDK 1.8
* Any DMS that supports the CMIS protocol (optional)


Included libraries:

* jdbc library for DB connection
* jasperreports libraries for report generation
* shark libraries for the workflow engine
* CMIS DMS client
* Ext JS libraries for user interface
* Server and client components for map making feature
* Server and client components for BIM viewer


Additional software that you may find useful (not included):

* JasperSoft Studio for custom report design
* Together Workflow Editor for custom workflow design
* OCS Inventory as automatic inventory software


Deployment instructions:

Before deployment make sure you have satisfied all the requirements above (expecially
the postgres database and java environment).

CMDBuild can be deployed inside tomcat as any regular war-packaged webapp. 

Database configuration: it is recommended to create a configuration directory under `<tomcat>/conf/<webapp_name>` 
(the name of config directory must match the name of webapp deployment). You
will find an example of db configuration inside `<webapp>/WEB-INF/conf/database.conf_example`. 
You must copy this to `database.conf` (either in config dir `<tomcat>/conf/<webapp_name>` or `<webapp>/WEB-INF/conf/`) and
edit it according to your database params.

A supported databse has to be loaded before running the application, in the downloaded files there are different database
dumps you can use:
 * emtpy.dump.xz: a database dump with a basic structure and some minimal informations like a default admin account;
 * demo.dump.xz: a database dump with a basic structure and some more informations added in the system, like example users;

Guided setup: if you want to perform a clean installation you may use the graphic wizard (either run the cmdbuild.sh
installer or run `java -jar cmdbuild.war` directly from the war). By following the guided setup the database dump will be 
automatically loaded based on the dump you choose in the configuration phase.

Available languages:
* English
* Arabic
* Brazilian Portuguese
* Chinese
* Croatian
* Czech
* Danish
* Dutch
* European Portuguese
* French
* German
* Greek
* Hungarian
* Italian
* Japanese
* Korean
* Norwegian
* Persian
* Russian
* Serbian (Latin)
* Serbian (Cyrillic)
* Slovak
* Slovenian
* Spanish
* Turkish
* Ukrainian
* Vietnamese

External contributors: 

* Arabic translation by Fahad Senan
* Brazilian Portuguese translation by T4HD 
* Chinese translation by Liansheng Yang 
* Croatian translation by Tomislav Perić 
* Czech translation by Jiří Langr 
* Danish translation by Ali Araghi
* Dutch translation by Eric van Rheenen
* European Portuguese translation by T4HD 
* Greek translation by Andreas Mavredakis
* Hungarian translation by Márton Natkó 
* Japanese translation by Satoru Funai
* Korean translation by Kyungik An
* Norwegian translation by Audun Wangen
* Persian translation by Ali Araghi
* Russian translation by Pavel Kraynyukhov
* Serbian (Latin and Cyrillic script) translation by Miroslav Zaric
* Slovak translation by Igor Kurty
* Slovenian translation by Tine Cus
* Turkish translation by Veysel Burak
* Ukrainian translation by Artem Yanchuk
* Vietnamese translation by Trong Nguyen



Below is a list of the publicly available software and resources used
by and distributed with CMDBuild, along with the licensing terms.

* Ace (Ajax.org Cloud9 Editor) (https://ace.c9.io) is
 released under the BSD License:
   https://raw.githubusercontent.com/ajaxorg/ace/master/LICENSE

* FullCalendar (https://fullcalendar.io) is released under the MIT License:
   https://github.com/fullcalendar/fullcalendar/blob/master/LICENSE.txt
