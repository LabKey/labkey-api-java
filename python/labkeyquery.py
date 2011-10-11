# LabKey Query Python Module

"""
############################################################################
SUMMARY:  
This module provides functions for interacting with data on a LabKey Server.

DESCRIPTION:
This module is designed to simplify querying and manipulating data in LabKey Server.  
Its APIs are modeled after the LabKey Server JavaScript APIs of the same names. 

SETUP PREREQUISITE: 
After the module is installed, you will need to create a labkeycredentials.txt files 
in the folder where you are running the python script.  To determine that directory,
you use the following two lines within python:

import os
os.getcwd()

Sample content for labkeycredentials file (3 separate lines):
machine https://hosted.labkey.com
login labkeypython@gmail.com
password python

This setup has been optimized for simplicity.  As an alternative, you can store the 
credentials file elsewhere and adjust the path for this file.

FREE, HOSTED TEST SERVER:

The sample login information provided above for the hosted.labkey.com server provides 
you with read access only.  This level of access is sufficient for only selectRows and 
executeSql. To test the other APIs on this server, please contact info@labkey.com.  
LabKey Software will happily provide you with a private, free project on this server 
to use for testing.

Further details: http://www.labkey.com/hosted/labkey-hosted

USAGE:

import labkeyquery
myresults = labkeyquery.selectRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'study',
    queryName = 'Physical Exam')
    
ASSUMPTION:
  
You have placed the labkeycredentials.txt file in the folder where you are
running python.  This file provides authentication credentials.
       
SEE ALSO:
Documentation for the LabKey client APIs:
https://www.labkey.org/wiki/home/Documentation/page.view?name=viewAPIs

Setup and configuration for the LabKey Python API:
https://www.labkey.org/wiki/home/Documentation/page.view?name=python

Support questions should be directed to the LabKey forum:
https://www.labkey.org/announcements/home/Server/Forum/list.view?

COMPATIBILITY:
Tested against Python 2.7 and LabKey Server v11.1.

VERSION:
LabKey Server Python API v15

AUTHOR:
Elizabeth K. Nelson

COPYRIGHT:
Copyright (c) 2011 LabKey Software
Licensed under the Apache License, Version 2.0: 
http://www.apache.org/licenses/LICENSE-2.0

############################################################################
"""

import os
import sys
import json
import urllib2
import urllib
import pprint

def selectRows(baseUrl, containerPath, schemaName, queryName, viewName=None,
    filterArray=None, columns=None, maxRows=None, sort=None, offset=None,
    containerFilter=None, debug=False):
    
    """
############################################################################
selectRows()

selectRows() can be used to query data from LabKey server

The following are the minimum required params:
		
myresults = labkeyquery.selectRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'study',
    queryName = 'Physical Exam')
    
The following are optional parameters:

	viewName => 'view1',
	filterArray => [
        ['ParticipantID', 'eq', 249318596], 
        ['Pulse', 'gt', '0'] 
	], 
	maxRows = 10,	#the max number of rows returned
	sort = 'ColumnA,ColumnB',	#sort order used for this query
	offset = 100,	#the offset used when running the query
	columns = 'ColumnA,ColumnB',  #A comma-delimited list of column names to include in the results.
	containerFilter = 'currentAndSubfolders',
	debug = True	#will result in a more verbose output

----------------------------------------------------------------------------
Test code:

import labkeyquery
reload(labkeyquery)
myresults = labkeyquery.selectRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'study',
    queryName = 'Physical Exam',
    maxRows = 2,
    filterArray = [
        ['ParticipantID', 'eq', 249318596], 
        ['Pulse', 'gt', '0']], 
    debug = True)    

import labkeyquery
reload(labkeyquery)
myresults = labkeyquery.selectRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'lists',
    queryName = 'Lab Machines', 
    debug = True)    
    
# Alternative baseUrl:
#     baseUrl = 'http://localhost:8080/labkey',   
    
############################################################################
    """

    # Build the URL for querying LabKey Server
    myurl = baseUrl.rstrip('/') +\
        "/query/" +\
        urllib2.quote(containerPath.strip('/')) +\
        "/getQuery.api?schemaName=" + urllib2.quote(schemaName) +\
        "&query.queryName=" + urllib2.quote(queryName)       
    if viewName!=None: myurl += "&query.viewName=" + urllib2.quote(viewName)
    if filterArray!=None:
        for filter_row in filterArray:
            myurl += "&query." + str(filter_row[0]) + "~" + str(filter_row[1]) + "=" + str(filter_row[2])
    if columns!=None: myurl += "&query.columns=" + urllib2.quote(columns)
    if maxRows!=None: myurl += "&query.maxRows=" + str(maxRows)
    if sort!=None: myurl += "&query.sort=" + urllib2.quote(sort)
    if offset!=None: myurl += "&query.offset=" + str(offset)
    if containerFilter!=None: myurl += "&query.containerFilter=" + urllib2.quote(containerFilter)    
    
    # Get authenticated to send URL requests
    opener =_create_opener()
    
    # Use the opener to fetch a URL request
    myrequest = urllib2.Request(myurl)
    try:
        response = opener.open(myrequest)
    except urllib2.HTTPError, e:
        print e.code
        print e.read()
        return
    data= response.read()

    # Decode the JSON into a Python "dictionary" - an associative array
    data_dict = json.loads(data, object_hook=_decode_dict)

    if debug:
        _print_debug_info(data_dict, myurl)
        
    return(data_dict)
    
def executeSql(baseUrl, containerPath, schemaName, sql, maxRows=None, sort=None, offset=None,  
    containerFilter=None, debug=False):
    
    """
############################################################################
executeSql()

executeSql() can be used to execute arbitrary SQL

The following are the minimum required params:

myresults = labkeyquery.executeSql(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'study',
    sql = 'SELECT * FROM "Physical Exam"')
		
The following are optional:

	maxRows = 10,	#the max number of rows returned
	sort = 'ColumnA,ColumnB',	#sort order used for this query
	offset = 100,	#the offset used when running the query
	containerFilter = 'currentAndSubfolders',
	debug = True	#will result in a more verbose output

----------------------------------------------------------------------------
Test Code:

import labkeyquery
reload(labkeyquery)
myresults = labkeyquery.executeSql(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'study',
    sql = 'SELECT "Physical Exam".ParticipantId, "Physical Exam".Pulse \
        FROM "Physical Exam" WHERE "Physical Exam".ParticipantId.ParticipantId=\'249318596\'',
    maxRows = 4,
    debug = True)

Reminder:  

In Python, if there are ' or \ characters in string arguments, the characters must 
be escaped as \' and \\  -- see the example above for \'
    
############################################################################
    """

    # Build the URL for querying LabKey Server      
    myurl = baseUrl.rstrip('/') +\
        "/query/" +\
        urllib2.quote(containerPath.strip('/')) +\
        "/executeSql.api?"
        
    myurldata_unencoded = {\
        'schemaName': schemaName,
        'sql': sql}
    myurldata = urllib.urlencode(myurldata_unencoded)
    
    if maxRows!=None: myurldata += "&query.maxRows=" + str(maxRows)
    if sort!=None: myurldata += "&query.sort=" + urllib2.quote(sort)
    if offset!=None: myurldata += "&query.offset=" + str(offset)
    if containerFilter!=None: myurldata += "&query.containerFilter=" + urllib2.quote(containerFilter)
         
    #Get authenticated to send URL requests
    opener =_create_opener()

    # Use the opener to fetch a URL request
    myrequest = urllib2.Request(myurl, myurldata)
    try:
        response = opener.open(myrequest)
    except urllib2.HTTPError, e:
        print e.code
        print e.read()
        return
    data= response.read()
    
    # Decode the JSON into a Python "dictionary" - an associative array
    data_dict = json.loads(data, object_hook=_decode_dict)

    if debug:
        _print_debug_info(data_dict, myurl, myurldata)
 
    return(data_dict)

def insertRows(baseUrl, containerPath, schemaName, queryName, rows, debug=False):
    
    """
############################################################################
insertRows()

insertRows() can be used to insert records into a LabKey table

The following are the minimum required params:
 
myresults = labkeyquery.insertRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'study',
    queryName = 'Physical Exam'
    rows = [{'DiastolicBloodPressure': 90,
        'Language': 'English',
        'ParticipantId': '249325718', #The combo of ParticipantId + date must be unique
        'Pregnancy': '0',
        'Pulse': 77,
        'Respirations': 13,
        'Signature': 0,
        'SystolicBloodPressure': 137,
        'Temp_C': 38,
        'Weight_kg': 111,
        'date': '21 May 2008 00:00:00'}])
		
The following are optional:

	debug = True	#will result in a more verbose output
    
----------------------------------------------------------------------------
Test Code:

import labkeyquery
reload(labkeyquery)
myresults = labkeyquery.insertRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'study',
    queryName = 'Physical Exam',
    rows = [{'DiastolicBloodPressure': 90,
        'Language': 'English',
        'ParticipantId': '249325728', #The combo of ParticipantId + date must be unique
        'Pregnancy': '0',
        'Pulse': 77,
        'Respirations': 13,
        'Signature': 0,
        'SystolicBloodPressure': 137,
        'Temp_C': 38,
        'Weight_kg': 111,
        'date': '21 May 2008 00:00:00'}],
    debug = True)

import labkeyquery
reload(labkeyquery)
myresults = labkeyquery.insertRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'lists',
    queryName = 'Lab Machines', 
    rows = [{'ContactPerson': 'Elizabeth',
        'InstrumentID': '7',  # The key for each inserted row must be unique
        'Name': 'HAL'}],
    debug = True)    
        
############################################################################
    """

    # Build the URL for querying LabKey Server      
    myurl = baseUrl.rstrip('/') +\
        "/query/" +\
        urllib2.quote(containerPath.strip('/')) +\
        "/insertRows.api?"
        
    mypostdata_unencoded = {\
        'schemaName': schemaName,
        'queryName': queryName,
        'rows': rows}    
    mypostdata = json.dumps(mypostdata_unencoded)
 
    #Get authenticated to send URL requests
    opener =_create_opener()
    
    # Use the opener to fetch a URL request
    myrequest = urllib2.Request(myurl, mypostdata, {'Content-Type': 'application/json'})
    try:
        response = opener.open(myrequest)
    except urllib2.HTTPError, e:
        print e.code
        print e.read()
        return
    data= response.read()

    # Decode the JSON into a Python "dictionary" - an associative array
    data_dict = json.loads(data, object_hook=_decode_dict)

    if debug:
        _print_debug_info(data_dict, myurl, mypostdata)
            
    return(data_dict)

def updateRows(baseUrl, containerPath, schemaName, queryName, rows, debug=False):
    
    """
############################################################################
updateRows()

updateRows() can be used to insert records into a LabKey table

The following are the minimum required params:
 
myresults = labkeyquery.updateRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'lists',
    queryName = 'Lab Machines', 
    rows = [{'InstrumentID': '10', #This is the key - it's required
        'Name': 'HAL'}])  #This is the update you wish to execute
		
The following are optional:

	debug = True	#will result in a more verbose output
    
----------------------------------------------------------------------------
Test Code:

import labkeyquery
reload(labkeyquery)
myresults = labkeyquery.updateRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'study',
    queryName = 'Physical Exam',
    rows = [{'ParticipantId': '249325717', #Not required
        'SystolicBloodPressure': 1390,     #This is the update
        'lsid': 'urn:lsid:labkey.com:Study.Data-173:5004.2.0080427E7.249325717'}], #Key    
    debug = True)

import labkeyquery
reload(labkeyquery)
myresults = labkeyquery.updateRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'lists',
    queryName = 'Lab Machines', 
    rows = [{'ContactPerson': 'Elizabeth',
        'InstrumentID': '10', #This is the key
        'Name': 'HAL'}],  #This is the update
    debug = True)    

############################################################################
    """

    # Build the URL for querying LabKey Server      
    myurl = baseUrl.rstrip('/') +\
        "/query/" +\
        urllib2.quote(containerPath.strip('/')) +\
        "/updateRows.api?"
        
    mypostdata_unencoded = {\
        'schemaName': schemaName,
        'queryName': queryName,
        'rows': rows}    
    mypostdata = json.dumps(mypostdata_unencoded)
 
    #Get authenticated to send URL requests
    opener =_create_opener()
    
    # Use the opener to fetch a URL request
    myrequest = urllib2.Request(myurl, mypostdata, {'Content-Type': 'application/json'})
    try:
        response = opener.open(myrequest)
    except urllib2.HTTPError, e:
        print e.code
        print e.read()
        return
    data= response.read()

    # Decode the JSON into a Python "dictionary" - an associative array
    data_dict = json.loads(data, object_hook=_decode_dict)

    if debug:
        _print_debug_info(data_dict, myurl, mypostdata)
            
    return(data_dict)

def deleteRows(baseUrl, containerPath, schemaName, queryName, rows, debug=False):
    
    """
############################################################################
deleteRows()

deleteRows() can be used to delete records into a LabKey table

The following are the minimum required params:
 
myresults = labkeyquery.deleteRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'lists',
    queryName = 'Lab Machines', 
    rows = [{'InstrumentID': '10'}])  #The key for each row is required.
		
The following are optional:

	debug = True	#will result in a more verbose output
    
----------------------------------------------------------------------------
Test Code:

import labkeyquery
reload(labkeyquery)
myresults = labkeyquery.deleteRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'study',
    queryName = 'Physical Exam',
    rows = [{'lsid': 'urn:lsid:labkey.com:Study.Data-290:5004.2.0080427E7.249325717'}],
    debug = True)

import labkeyquery
reload(labkeyquery)
myresults = labkeyquery.deleteRows(
    baseUrl = 'https://hosted.labkey.com',
    containerPath = 'PythonProject',
    schemaName = 'lists',
    queryName = 'Lab Machines', 
    rows = [{'InstrumentID': '10'}], #Make sure this exists
    debug = True)    
   
############################################################################
    """

    # Build the URL for querying LabKey Server      
    myurl = baseUrl.rstrip('/') +\
        "/query/" +\
        urllib2.quote(containerPath.strip('/')) +\
        "/deleteRows.api?"
        
    mypostdata_unencoded = {\
        'schemaName': schemaName,
        'queryName': queryName,
        'rows': rows}    
    mypostdata = json.dumps(mypostdata_unencoded)
 
    #Get authenticated to send URL requests
    opener =_create_opener()
    
    # Use the opener to fetch a URL request
    myrequest = urllib2.Request(myurl, mypostdata, {'Content-Type': 'application/json'})
    try:
        response = opener.open(myrequest)
    except urllib2.HTTPError, e:
        print e.code
        print e.read()
        return
    data= response.read()

    # Decode the JSON into a Python "dictionary" - an associative array
    data_dict = json.loads(data, object_hook=_decode_dict)

    if debug:
        _print_debug_info(data_dict, myurl, mypostdata)
        
    return(data_dict)

def _print_debug_info(data_dict, myurl, mydata=None):
    
    # Print the URL and any data used to query the server
    print myurl
    if mydata: 
        print mydata
    
    # Review the results
    pp = pprint.PrettyPrinter(4)
    pp.pprint(data_dict)
    type(data_dict)

    # Look at the dictionary's keys
    mykeys = data_dict.keys()	
    print mykeys

    # Look at the list of rows
    rowlist = data_dict['rows']
    # Look at the first row of data
    if rowlist: 
        pp.pprint(rowlist[0])

    return    
    
def _create_opener():	
    # Use a workaround because netrc functions require a .netrc file and windows only allows 
    # saving a _netrc file (not a .netrc) from the UI
    f = open('labkeycredentials.txt', 'r')
    mymachine = f.readline().strip().split(' ')[1]
    myusername = f.readline().strip().split(' ')[1]
    mypassword = f.readline().strip().split(' ')[1]
    f.close()
    
    # Create a password manager
    passmanager = urllib2.HTTPPasswordMgrWithDefaultRealm()

    # Add login info to the password manager
    passmanager.add_password(None, mymachine, myusername, mypassword)

    # Create the AuthHandler
    authhandler = urllib2.HTTPBasicAuthHandler(passmanager)

    # Create opener
    opener = urllib2.build_opener(authhandler)
    return opener
    
def _decode_list(lst):
    # Helper function for dealing with unicode
    # Adapted from Stack Overflow: 
    #   http://stackoverflow.com/questions/956867/how-to-get-string-objects-instead-unicode-ones-from-json-in-python
    #   Answer from Mike Brennan: http://stackoverflow.com/users/658138/mike-brennan
    #   Question from Brutus: http://stackoverflow.com/users/11666/brutus
    newlist = []
    for i in lst:
        if isinstance(i, unicode):
            i = i.encode('utf-8')
        elif isinstance(i, list):
            i = _decode_list(i)
        newlist.append(i)
    return newlist

def _decode_dict(dct):
    # Helper function for dealing with unicode
    # Adapted from Stack Overflow: 
    #   http://stackoverflow.com/questions/956867/how-to-get-string-objects-instead-unicode-ones-from-json-in-python
    #   Answer from Mike Brennan: http://stackoverflow.com/users/658138/mike-brennan
    #   Question from Brutus: http://stackoverflow.com/users/11666/brutus
    newdict = {}
    for k, v in dct.iteritems():
        if isinstance(k, unicode):
            k = k.encode('utf-8')
        if isinstance(v, unicode):
             v = v.encode('utf-8')
        elif isinstance(v, list):
            v = _decode_list(v)
        newdict[k] = v
    return newdict