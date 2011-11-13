#!/usr/bin/perl

#this script serves as a simple test of the API

require 'Labkey/Query/lib/Labkey/Query.pm';
use Data::Dumper;

my $results = Labkey::Query::selectRows(
	-baseUrl => 'https://labkey.org/',
	-containerPath => 'home/Developer/issues/',
	-schemaName => 'issues',
	-queryName => 'issues',
	-maxRows => 2,
	#-sort => '-userid',
	-debug => 1,
	-loginAsGuest => 1,	
	);
print Dumper($results);

my $sql = Labkey::Query::executeSql(
	-baseUrl => 'https://labkey.org/',
	-containerPath => 'home/Documentation/',
	-schemaName => 'issues',
	-sql => 'SELECT max(i.issueid) as id FROM issues.issues i',
	-debug => 1,
	-loginAsGuest => 1,
	);
	
print Dumper($sql);		