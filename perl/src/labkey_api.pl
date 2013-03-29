#!/usr/bin/perl

#this script serves as a simple test of the API

#require 'Labkey/Query/lib/Labkey/Query.pm';
use Labkey::Query;
use Data::Dumper;

# Create and configure a UserAgent once for multiple requests
#use LWP::UserAgent;
#my $ua = new LWP::UserAgent;

my $results = Labkey::Query::selectRows(
	-baseUrl => 'http://localhost:8080/labkey/',
	-containerPath => 'home/',
	-schemaName => 'issues',
	-queryName => 'issues',
	-schemaName => 'core',
	-queryName => 'Users',
	-maxRows => 2,
	#-sort => '-userid',
	-debug => 1,
	-loginAsGuest => 1,
	#-timeout => 0.01,
	#-useragent => $ua
	);
#print Dumper($results);

#it seems guests cannot run executeSql
#my $sql = Labkey::Query::executeSql(
#	-baseUrl => 'https://labkey.org/',
#	-containerPath => 'home/Documentation/',
#	-schemaName => 'issues',
#	-sql => 'SELECT max(i.issueid) as id FROM issues.issues i',
#	-debug => 1,
#	-loginAsGuest => 1,
#	);
#	
#print Dumper($sql);		
