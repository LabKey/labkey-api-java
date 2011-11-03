===========
LabKey Python Client API
===========

The LabKey package is designed to simplify querying and manipulating data in a LabKey Server. Its APIs are modeled after the LabKey Server 
JavaScript APIs of the same names.


Installation
===========

The LabKey package, currently, can only be installed via source. To install the package

1. Download the distribution from http://www.labkey.com/download-labkey-server

2. The package distribution will be named similar to LabKey-0.19.zip, where 

    - 0.19 is the python package number

3. Unzip the downloaded package and execute::
    
    python setup.py install 
    


Setup Prerequisite
-------------

In order to the use the LabKey Python Client API, you will need to specify your login credentials in a credential file. The package assumes that this file will be located either 

1. ``$HOME/.labkeycredentials.txt``

2. It location will be specified in the ``LABKEY_CREDENTIALS`` environment variable. 


The labkeycredentials file must be in the following format. (3 separate lines)::
    machine https://hosted.labkey.com
    login labkeypython@gmail.com
    password python

where 

- machine: is the URL of your LabKey Server

- login: is the email address to be used to login to the LabKey Server

- password: is the password associated with the login

A sample ``labkeycredentials`` file has been shipped with the source package and named ``.labkeycredentials.sample``


Compatibility
===========
Tested against 

- Python 2.6.x, 2.7.x

- LabKey Server v11.1 and later


Free, Hosted Test Server
===========

The sample login information provided above for the hosted.labkey.com server provides you with read access only.  This level of access is sufficient for only selectRows and executeSql. To test the other APIs on this server, please contact info@labkey.com.  LabKey Software will happily provide you with a private, free project on this server to use for testing.

Further details at http://www.labkey.com/hosted/labkey-hosted


Help and Contributing
=========

The `LabKey Python Client API`_ and the LabKey Server are maintained by the LabKey Software Foundation. If you have any questions or need support, please use the `LabKey Server support forum`_. 


Documentation 
-------------

- `LabKey Python API`_

- `Setup and configuration for the LabKey Python API`_

- `Using the LabKey Python API_`

- `Documentation for all the LabKey client APIs`_

.. _`LabKey Python API`: https://www.labkey.org/wiki/home/Documentation/page.view?name=python
.. _`Setup and configuration of the LabKey Python API`: https://www.labkey.org/wiki/home/Documentation/page.view?name=pythonSetup
.. _`Using the LabKey Python API`: https://www.labkey.org/wiki/home/Documentation/page.view?name=pythonUsing
.. _`Documentation for all the LabKey client APIs`: https://www.labkey.org/wiki/home/Documentation/page.view?name=viewAPIs
.. _`LabKey Python Client API`: https://www.labkey.org/wiki/home/Documentation/page.view?name=python
.. _`LabKey Server support forum`: https://www.labkey.org/announcements/home/Server/Forum/list.view?


Working with the Source Code
=========

1. Download the source_ from the `LabKey Software Foundation subversion repository`_

2. Make your changes

3. Bump the version number in the ``setup.py`` file 

4. Update the NEWS.txt file explaining the changes that were made 

You can create a new source distribution by running::
    
    python setup.py sdist 

This will create a new source distribution in the dist subdirectory.

.. _`LabKey Software Foundation subversion repository`: https://www.labkey.org/wiki/home/Documentation/page.view?name=svn
.. _source: https://hedgehog.fhcrc.org/tor/stedi/trunk/remoteapi/python



Contributors 
=========

- Elizabeth Nelson (eknelson@labkey.com)

    - Outreach Director at LabKey Software 

- Brian Connolly (brian@labkey.com)

    - Consultant at LabKey Software


Thanks also too
=========


