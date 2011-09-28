#!/usr/bin/perl

=head1 NAME

Labkey::Query

=head1 SYNOPSIS

	use Labkey::Query;
	my $results = Labkey::Query::selectRows(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => 'myFolder/',
		-schemaName => 'lists',
		-queryName => 'mid_tags',
	);
		
=head1 ABSTRACT

For interacting with data in LabKey Server

=head1 DESCRIPTION

This module is designed to simplify querying and manipulating data in LabKey Server.  It should more or less replicate the javascript APIs of the same names. 

After the module is installed, if you need to login with a specific user you 
will need to create a .netrc file in the home directory of the user
running the perl script.  Documentation on .netrc can be found here:
https://www.labkey.org/wiki/home/Documentation/page.view?name=netrc

In API versions 0.08 and later, you can specify the param '-loginAsGuest'
which will query the server without any credentials.  The server must permit 
guest to that folder for this to work though.

=head1 SEE ALSO

The LabKey client APIs are described in greater detail here:
https://www.labkey.org/wiki/home/Documentation/page.view?name=viewAPIs

Support questions should be directed to the LabKey forum:
https://www.labkey.org/announcements/home/Server/Forum/list.view?

=head1 AUTHOR 

Ben Bimber


=head1 COPYRIGHT
 
Copyright (c) 2010 Ben Bimber

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0

=cut

package Labkey::Query;

use strict;
use LWP::UserAgent;
use HTTP::Request;
use JSON;
use Data::Dumper;
use FileHandle;
use File::Spec;
use File::HomeDir;
use Carp;
use URI;

use vars qw($VERSION);

our $VERSION = "1.00";



=head1 selectRows()

selectRows() can be used to query data from LabKey server

The following are the minimum required params:
		
	my $results = Labkey::Query::selectRows(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => 'myFolder/',
		-schemaName => 'lists',
		-queryName => 'mid_tags',
	);

The following are optional:

	-viewName => 'view1',
	-filterArray => [
		['file_active', 'eq', 1], 
		['species', 'neq', 'zebra']
	], #allows filters to be applied to the query similar to the labkey Javascript API.
	-parameters => [
		['enddate', '2011/01/01'], 
		['totalDays', 15]
	], #allows parameters to be applied to the query similar to the labkey Javascript API.	
	-maxRows => 10	#the max number of rows returned
	-sort => 'ColumnA,ColumnB'	#sort order used for this query
	-offset => 100	#the offset used when running the query
	-columns => 'ColumnA,ColumnB'  #A comma-delimited list of column names to include in the results.
	-containerFilter => 'currentAndSubfolders'
	-debug => 1,	#will result in a more verbose output
	-loginAsGuest => #will not attempt to lookup credentials in netrc
	-requiredVersion => 9.1 #if 8.3 is selected, it will use Labkey's pre-9.1 format for returning the data.  9.1 is the default.  See documentation of LABKEY.Query.ExtendedSelectRowsResults for more detail here:
		https://www.labkey.org/download/clientapi_docs/javascript-api/symbols/LABKEY.Query.html
	
	
NOTE: 

- In version 1.0 and later of the perl API, the default result format is 9.1.  This is different from the LabKey JS, which defaults to the earlier format for legacy purposes.
- The environment variable 'LABKEY_URL' can be used instead of supplying a '-baseUrl' param 

=cut

sub selectRows {

	my %args = @_;	
	
	#allow baseUrl as environment variable
	$args{'-baseUrl'} ||= $ENV{LABKEY_URL};
	
	#sanity checking
	my @required = ( '-containerPath', '-queryName', '-schemaName', '-baseUrl' );
	foreach (@required) {
		if ( !$args{$_} ) { croak("ERROR: Missing required param: $_") }
	}

	my $url = URI->new(
		_normalizeSlash($args{'-baseUrl'}) 
	  . "query/"
	  . _normalizeSlash($args{'-containerPath'})
	  . "getQuery.api?"
  	);	
	
	#if no machine supplied, extract domain from baseUrl	
	if (!$args{'-machine'}){		
		$args{'-machine'} = $url->host;
	}

	my $lk_config;
	if(!$args{'-loginAsGuest'}){
		$lk_config = _readrc( $args{-machine} );
	}
	
	my %params = (
  		schemaName => $args{'-schemaName'},
  		"query.queryName" => $args{'-queryName'},
  		apiVersion => $args{'-requiredVersion'} || 9.1,
  	);

	foreach ( @{ $args{-filterArray} } ) {
		$params{"query." . @{$_}[0] . "~" . @{$_}[1]} = @{$_}[2] ;
	}

	foreach ( @{ $args{-parameters} } ) {
		$params{"query.param." . @{$_}[0]} = @{$_}[1];
	}
	
	foreach ('viewName', 'offset', 'sort', 'maxRows', 'columns', 'containerFilter'){
		if ( $args{'-'.$_} ) {
			$params{"query.".$_} = $args{'-'.$_};
		}		
	}	
	
	$url->query_form(%params);
		
	print $url."\n" if $args{-debug};

	#Fetch the actual data from the query
	my $request = HTTP::Request->new( "GET" => $url );
	if($lk_config){
		$request->authorization_basic( $$lk_config{'login'}, $$lk_config{'password'} );
	}
	my $ua = new LWP::UserAgent;
	$ua->agent("Perl API Client/1.0");
	my $response = $ua->request($request);

	# Simple error checking
	if ( $response->is_error ) {
		croak( $response->status_line );
	}

	my $json_obj = JSON->new->utf8->decode( $response->content )
	  || croak("ERROR: Unable to decode JSON.\n$url\n");

	return $json_obj;

}


=head1 insertRows()

insertRows() can be used to insert records into a LabKey table

The following are the minimum required params:

	my $insert = Labkey::Query::insertRows(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => 'myFolder/',
		-schemaName => 'lists',
		-queryName => 'backup',
		-rows => [{
			"JobName" => 'jobName', 
			"Status" => $status, 
			"Log" => $log, 
			"Date" => $date
		}],
	);
 
The following are optional:

	-debug => 1,  #will result in a more verbose output 
	-loginAsGuest => #will not attempt to lookup credentials in netrc

NOTE: The environment variable 'LABKEY_URL' can be used instead of supplying a '-baseUrl' param

=cut

sub insertRows {
	my %args = @_;

	#allow baseUrl as an environment variable
	$args{'-baseUrl'} ||= $ENV{LABKEY_URL};

	#sanity checking
	my @required = ( '-containerPath', '-queryName', '-schemaName', '-baseUrl', '-rows' );
	foreach (@required) {
		if ( !$args{$_} ) { croak("ERROR: Missing required param: $_") }
	}	

	#if no machine supplied, extract domain from baseUrl 
	if (!$args{'-machine'}){
		my $url = URI->new($args{'-baseUrl'});
		$args{'-machine'} = $url->host;
	}
	
	my $lk_config;
	if(!$args{'-loginAsGuest'}){
		$lk_config = _readrc( $args{-machine} );
	}

	my $url =
	    _normalizeSlash($args{'-baseUrl'}) 
	  . "query/"
	  . _normalizeSlash($args{'-containerPath'})
	  . "insertRows.api";

	print $url."\n" if $args{-debug};

	my $data = {
		"schemaName" => $args{'-schemaName'},
		"queryName"  => $args{'-queryName'},
		"rows"       => $args{'-rows'}
	};

	my $response = _postData($url, $data, $lk_config);
	return $response;

}


=head1 updateRows()

updateRows() can be used to update records in a LabKey table

The following are the minimum required params:

	my $update = Labkey::Query::updateRows(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => 'myFolder/',
		-schemaName => 'lists',
		-queryName => 'backup',
		-rows => [{
			"JobName" => 'jobName', 
			"Status" => $status, 
			"Log" => $log, 
			"Date" => $date
		}],
	);
		
The following are optional:

	-debug => 1,  #will result in a more verbose output
	-loginAsGuest => #will not attempt to lookup credentials in netrc

NOTE: The environment variable 'LABKEY_URL' can be used instead of supplying a '-baseUrl' param
	 
=cut

sub updateRows {
	my %args = @_;

	#allow baseUrl as environment variable
	$args{'-baseUrl'} ||= $ENV{LABKEY_URL};

	#sanity checking
	my @required = ( '-containerPath', '-queryName', '-schemaName', '-baseUrl', '-rows' );
	foreach (@required) {
		if ( !$args{$_} ) { croak("ERROR: Missing required param: $_") }
	}

	#if no machine supplied, extract domain from baseUrl 
	if (!$args{'-machine'}){
		my $url = URI->new($args{'-baseUrl'});
		$args{'-machine'} = $url->host;
	}
	my $lk_config;
	if(!$args{'-loginAsGuest'}){
		$lk_config = _readrc( $args{-machine} );
	}

	my $url =
	    _normalizeSlash($args{'-baseUrl'}) 
	  . "query/"
	  . _normalizeSlash($args{'-containerPath'})
	  . "updateRows.api";

	print $url."\n" if $args{-debug};

	my $data = {
		"schemaName" => $args{'-schemaName'},
		"queryName"  => $args{'-queryName'},
		"rows"       => $args{'-rows'}
	};

	my $response = _postData($url, $data, $lk_config);
	return $response;

}


=head1 deleteRows()

deleteRows() can be used to delete records in a LabKey table

The following are the minimum required params:

	my $update = Labkey::Query::deleteRows(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => 'myFolder/',
		-schemaName => 'lists',
		-queryName => 'backup',
		-rows => [{
			"Key" => '12', 
		}],
	);
		
The following are optional:

	-debug => 1,  #will result in a more verbose output
	-loginAsGuest => #will not attempt to lookup credentials in netrc

NOTE: The environment variable 'LABKEY_URL' can be used instead of supplying a '-baseUrl' param
	 
=cut

sub deleteRows {
	my %args = @_;

	#allow baseUrl as environment variable
	$args{'-baseUrl'} ||= $ENV{LABKEY_URL};

	#sanity checking
	my @required = ( '-containerPath', '-queryName', '-schemaName', '-baseUrl', '-rows' );
	foreach (@required) {
		if ( !$args{$_} ) { croak("ERROR: Missing required param: $_") }
	}

	#if no machine supplied, extract domain from baseUrl 
	if (!$args{'-machine'}){
		my $url = URI->new($args{'-baseUrl'});
		$args{'-machine'} = $url->host;
	}
	my $lk_config;
	if(!$args{'-loginAsGuest'}){
		$lk_config = _readrc( $args{-machine} );
	}

	my $url =
	    _normalizeSlash($args{'-baseUrl'}) 
	  . "query/"
	  . _normalizeSlash($args{'-containerPath'})
	  . "deleteRows.api";

	print $url."\n" if $args{-debug};

	my $data = {
		"schemaName" => $args{'-schemaName'},
		"queryName"  => $args{'-queryName'},
		"rows"       => $args{'-rows'}
	};

	my $response = _postData($url, $data, $lk_config);
	return $response;

}


=head1 executeSql()

executeSql() can be used to execute arbitrary SQL

The following are the minimum required params:

	my $result = Labkey::Query::executeSql(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => 'myFolder/',
		-schemaName => 'study',
		-sql => 'select MyDataset.foo, MyDataset.bar from MyDataset',
	);
		
The following are optional:

	-maxRows => 10	#the max number of rows returned
	-sort => 'ColumnA,ColumnB'	#sort order used for this query
	-offset => 100	#the offset used when running the query
	-containerFilter => 'currentAndSubfolders'
	-debug => 1,  #will result in a more verbose output
	-loginAsGuest => #will not attempt to lookup credentials in netrc

NOTE: The environment variable 'LABKEY_URL' can be used instead of supplying a '-baseUrl' param
	 
=cut

sub executeSql {
	my %args = @_;

	#allow baseUrl as environment variable
	$args{'-baseUrl'} ||= $ENV{LABKEY_URL};

	#sanity checking
	my @required = ( '-containerPath', '-baseUrl', '-sql' );
	foreach (@required) {
		if ( !$args{$_} ) { croak("ERROR: Missing required param: $_") }
	}

	#if no machine supplied, extract domain from baseUrl 
	if (!$args{'-machine'}){
		my $url = URI->new($args{'-baseUrl'});
		$args{'-machine'} = $url->host;
	}
	my $lk_config;
	if(!$args{'-loginAsGuest'}){
		$lk_config = _readrc( $args{-machine} );
	}

	my $url =
	    _normalizeSlash($args{'-baseUrl'}) 
	  . "query/"
	  . _normalizeSlash($args{'-containerPath'})
	  . "executeSql.api?";

	print $url."\n" if $args{-debug};
	
	my $data = {
		"schemaName" => $args{'-schemaName'},
		"sql" => $args{'-sql'},			
	};
	
	foreach ('offset', 'sort', 'maxRows', 'containerFilter'){
		if ( $args{'-'.$_} ) {
			$$data{$_} = $args{'-'.$_};
		}		
	}
		
	print Dumper($data) if $args{-debug};
	
	my $response = _postData($url, $data, $lk_config);
	return $response;

}




# NOTE: this code adapted from Net::Netrc module.  I do not use netrc b/c it assumes a filename of .netrc, which is not PC compatible.
# If Net::Netrc is changed, should use that instead.
sub _readrc() {

	my $host = shift || 'default';

	#This should allow it to work on linux and newer windows:
	my $file = File::Spec->catfile( File::HomeDir::home(), '.netrc' );
	if ( !-e $file ) {
		$file = File::Spec->catfile( File::HomeDir::home(), '_netrc' );
	}

	my %netrc = ();
	my ( $login, $pass, $acct ) = ( undef, undef, undef );
	my $fh;
	local $_;

	$netrc{default} = undef;

	# OS/2 and Win32 do not handle stat in a way compatable with this check :-(
	unless ( $^O eq 'os2'
		|| $^O eq 'MSWin32'
		|| $^O eq 'MacOS'
		|| $^O =~ /^cygwin/ )
	{
		my @stat = stat($file);

		if (@stat) {
			if ( $stat[2] & 077 ) {
				carp "Bad permissions: $file";
				return;
			}
			if ( $stat[4] != $< ) {
				carp "Not owner: $file";
				return;
			}
		}
	}

	if ( $fh = FileHandle->new( $file, "r" ) ) {
		my ( $mach, $macdef, $tok, @tok ) = ( 0, 0 );

		while (<$fh>) {
			undef $macdef if /\A\n\Z/;

			if ($macdef) {
				push( @$macdef, $_ );
				next;
			}

			s/^\s*//;
			chomp;

			while ( length && s/^("((?:[^"]+|\\.)*)"|((?:[^\\\s]+|\\.)*))\s*// )
			{
				( my $tok = $+ ) =~ s/\\(.)/$1/g;
				push( @tok, $tok );
			}

		  TOKEN:
			while (@tok) {
				if ( $tok[0] eq "default" ) {
					shift(@tok);
					$mach = bless {};
					$netrc{default} = [$mach];

					next TOKEN;
				}

				last TOKEN
				  unless @tok > 1;

				$tok = shift(@tok);

				if ( $tok eq "machine" ) {
					my $host = shift @tok;
					$mach = { machine => $host };

					$netrc{$host} = []
					  unless exists( $netrc{$host} );
					push( @{ $netrc{$host} }, $mach );
				}
				elsif ( $tok =~ /^(login|password|account)$/ ) {
					next TOKEN unless $mach;
					my $value = shift @tok;

		  # Following line added by rmerrell to remove '/' escape char in .netrc
					$value =~ s/\/\\/\\/g;
					$mach->{$1} = $value;
				}
				elsif ( $tok eq "macdef" ) {
					next TOKEN unless $mach;
					my $value = shift @tok;
					$mach->{macdef} = {}
					  unless exists $mach->{macdef};
					$macdef = $mach->{machdef}{$value} = [];
				}
			}
		}
		$fh->close();
	}
	
	my $auth = $netrc{$host}[0];
	
	#if no machine is specified and there is only 1 machine in netrc, we use that one
	if (!$auth && length((keys %netrc))==1){
		$auth = $netrc{(keys %netrc)[0]}[0];	
	}	 

	warn("Unable to find entry for host: $host") unless $auth;
	warn("Missing password for host: $host") unless $auth->{password};
	warn("Missing login for host: $host") unless $auth->{login};

	return $auth;
}


sub _normalizeSlash(){
	my $containerPath = shift;
		
	$containerPath =~ s/^\///;
	$containerPath =~ s/\/$//;	
	$containerPath .= '/';
	return $containerPath;
}


sub _postData(){
	my ($url, $data, $lk_config) = @_;
	
	my $json_obj = JSON->new->utf8->encode($data);

	my $req = new HTTP::Request;
	$req->method('POST');
	$req->url($url);
	$req->content_type('application/json');
	$req->content($json_obj);
	$req->authorization_basic( $$lk_config{'login'}, $$lk_config{'password'} );
	my $ua = new LWP::UserAgent;
	$ua->agent("Perl API Client/1.0");
	my $response = $ua->request($req);

	# Simple error checking
	if ( $response->is_error ) {
		croak($response->status_line);
	}

	#print Dumper($response);
	$json_obj = JSON->new->utf8->decode( $response->content )
	  || croak("ERROR: Unable to decode JSON.\n$url\n");
	  
  	return $json_obj;	
}


1;

