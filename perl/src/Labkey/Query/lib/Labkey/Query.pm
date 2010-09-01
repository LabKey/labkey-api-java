#!/usr/bin/perl

=head1 NAME

Labkey::Query

=head1 SYNOPSIS

	use Labkey::Query;
	my $results = Labkey::Query::selectRows(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => '/myFolder',
		-project => 'shared',
		-schemaName => 'lists',
		-queryName => 'mid_tags',
	);
		
=head1 ABSTRACT

For interacting with data in LabKey Server

=head1 DESCRIPTION

This module is designed to simplify querying and inserting data to and from LabKey Server.  It should more or less replicate the javascript APIs LABKEY.query.selectRows(), .updateRows() and .insertRows() 

After the module is installed, you will need to create a .netrc file in the home directory of the user
running the perl script.  Documentation on .netrc can be found here:
https://www.labkey.org/wiki/home/Documentation/page.view?name=netrc

=head1 SEE ALSO

The LabKey client APIs are described in great detail here:
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
use vars qw($VERSION);

our $VERSION = "0.04";



=head1 selectRows()

selectRows() can be used to query data from LabKey server
	
	my $results = Labkey::Query::selectRows(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => '/myFolder',
		-project => 'shared',
		-schemaName => 'lists',
		-queryName => 'mid_tags',
	);

	also supported:
	-viewName => 'view1',
	-filterArray => [
		['file_active', 'eq', 1], 
		['species', 'neq', 'zebra']
	], 
	-debug => 1,  #will result in a more verbose output
 
=cut

sub selectRows {

	my %args = @_;

	#allow baseUrl as environment variable
	$args{'-baseUrl'} ||= $ENV{LABKEY_URL};
	
	#sanity checking
	my @required = ( '-project', '-queryName', '-schemaName', '-baseUrl' );
	foreach (@required) {
		if ( !$args{$_} ) { croak("ERROR: Missing required param: $_") }
	}

	#if no machine supplied, extract domain from baseUrl 
	if (!$args{'-machine'}){
		my $url = URI->new($args{'-baseUrl'});
		$args{'-machine'} = $url->host;
	}

	my $lk_config = _readrc( $args{-machine} );

	my $ua = new LWP::UserAgent;
	$ua->agent("Perl API Client/1.0");

	my $url =
	    $args{'-baseUrl'} 
	  . "query/"
	  . $args{'-project'}
	  . ($args{'-containerPath'} ? $args{'-containerPath'} : '')
	  . "/getQuery.api?schemaName="
	  . $args{'-schemaName'}
	  . "&query.queryName="
	  . $args{'-queryName'};

	foreach ( @{ $args{-filterArray} } ) {
		$url .= "&query." . ( @{$_}[0] ) . "~" . @{$_}[1] . "=" . ( @{$_}[2] );
	}

	if ( $args{'-viewName'} ) {
		$url .= "&query.viewName=" . ( $args{'-viewName'} );
	}

	print $url if $args{-debug};

	#Fetch the actual data from the query
	my $request = HTTP::Request->new( "GET" => $url );
	$request->authorization_basic( $$lk_config{'login'}, $$lk_config{'password'} );
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

	my $insert = Labkey::Query::insertRows(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => '/myFolder',
		-project => 'home',
		-schemaName => 'lists',
		-queryName => 'backup',
		-rows => [{
			"JobName" => 'jobName', 
			"Status" => $status, 
			"Log" => $log, 
			"Date" => $date
		}],
	);
 
	also supported:
	-debug => 1,  #will result in a more verbose output 

=cut

sub insertRows {
	my %args = @_;

	#allow baseUrl as an environment variable
	$args{'-baseUrl'} ||= $ENV{LABKEY_URL};

	#sanity checking
	my @required = ( '-project', '-queryName', '-schemaName', '-baseUrl', '-rows' );
	foreach (@required) {
		if ( !$args{$_} ) { croak("ERROR: Missing required param: $_") }
	}	

	#if no machine supplied, extract domain from baseUrl 
	if (!$args{'-machine'}){
		my $url = URI->new($args{'-baseUrl'});
		$args{'-machine'} = $url->host;
	}
	
	my $lk_config = _readrc( $args{-machine} );

	my $ua = new LWP::UserAgent;
	$ua->agent("Perl API Client/1.0");

	my $url =
	    $args{'-baseUrl'} 
	  . "query/"
	  . $args{'-project'}
	  . ($args{'-containerPath'} ? $args{'-containerPath'} : '')
	  . "/insertRows.api";

	print $url if $args{-debug};

	my $data = {
		"schemaName" => $args{'-schemaName'},
		"queryName"  => $args{'-queryName'},
		"command"    => "insert",
		"rows"       => $args{'-rows'}
	};

	my $json_obj = JSON->new->utf8->encode($data);

	#insert the row
	my $req = new HTTP::Request;
	$req->method('POST');
	$req->url($url);
	$req->content_type('application/json');
	$req->content($json_obj);
	$req->authorization_basic( $$lk_config{'login'}, $$lk_config{'password'} );
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


=head1 updateRows()

updateRows() can be used to update records in a LabKey table

	my $update = Labkey::Query::updateRows(
		-baseUrl => 'http://labkey.com:8080/labkey/',
		-containerPath => '/myFolder',
		-project => 'home',
		-schemaName => 'lists',
		-queryName => 'backup',
		-rows => [{
			"JobName" => 'jobName', 
			"Status" => $status, 
			"Log" => $log, 
			"Date" => $date
		}],
	);
		
	also supported:
	-debug => 1,  #will result in a more verbose output
 
=cut

sub updateRows {
	my %args = @_;

	#allow baseUrl as environment variable
	$args{'-baseUrl'} ||= $ENV{LABKEY_URL};

	#sanity checking
	my @required = ( '-project', '-queryName', '-schemaName', '-baseUrl', '-rows' );
	foreach (@required) {
		if ( !$args{$_} ) { croak("ERROR: Missing required param: $_") }
	}

	#if no machine supplied, extract domain from baseUrl 
	if (!$args{'-machine'}){
		my $url = URI->new($args{'-baseUrl'});
		$args{'-machine'} = $url->host;
	}
	my $lk_config = _readrc( $args{-machine} );

	my $ua = new LWP::UserAgent;
	$ua->agent("Perl API Client/1.0");

	my $url =
	    $args{'-baseUrl'} 
	  . "query/"
	  . $args{'-project'}
	  . ($args{'-containerPath'} ? $args{'-containerPath'} : '')
	  . "/updateRows.api";

	print $url if $args{-debug};

	my $data = {
		"schemaName" => $args{'-schemaName'},
		"queryName"  => $args{'-queryName'},
		"command"    => "update",
		"rows"       => $args{'-rows'}
	};

	my $json_obj = JSON->new->utf8->encode($data);

	my $req = new HTTP::Request;
	$req->method('POST');
	$req->url($url);
	$req->content_type('application/json');
	$req->content($json_obj);
	$req->authorization_basic( $$lk_config{'login'}, $$lk_config{'password'} );
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

	croak("Unable to find entry for host: $host") unless $auth;
	croak("Missing password for host: $host") unless $auth->{password};
	croak("Missing login for host: $host") unless $auth->{login};

	return $auth;
}


1;

