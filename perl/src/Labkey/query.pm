#!/usr/bin/perl

=head1 NAME

PROGRAM  : labkey.pm

=head1 SYNOPSIS


=head1 DESCRIPTION

PURPOSE  : This module contains functions to simplify querying and inserting data to and from LabKey Server.  The functions more or less replicate LABKEY.query.selectRows() and .insertRows() 

=head1 AUTHOR 

Ben Bimber

=cut

package Labkey::query;

use strict;
use Config::Abstract::Ini;
use LWP::UserAgent; 
use HTTP::Request; 
use JSON;
use Data::Dumper;
use File::Spec;

=head1 selectRows()

	my $results = Labkey::query::selectRows(
		-containerPath => '/WNPRC_Units/Research_Services/Research_Computing',
		-project => 'WNPRC',
		-schemaName => 'lists',
		-queryName => 'mid_tags',
		);

	#also supported:
	-viewName => 'view1',
	-filterArray => [['file_active', 'eq', 1], ['species', 'neq', 'zebra']],
 
=cut

sub selectRows {
	#This should allow it to work on linux and newer windows: 	
	my $configFile = File::Spec->catfile($ENV{HOME}, '.lkpass');
	if (! -e $configFile){
		$configFile = File::Spec->catfile($ENV{USERPROFILE},'_lkpass');
	}
	
	my $settings = new Config::Abstract::Ini($configFile);
	my %lk_config = $settings->get_entry('lkconfig');
		die "ERROR: Unable to find .lkpass or file not properly formatted" unless %lk_config;
	
	my %args = @_;

	#sanity checking
	my @required = ('-project', '-queryName', '-schemaName');	
	foreach (@required){
		if (!$args{$_}){die "ERROR: Missing required param: $_"};		
	}

	my @ini_required = qw(baseURL login password);	
	foreach (@ini_required){
		if (!$lk_config{$_}){die "ERROR: INI file missing required param: $_"};		
	}
			
	my $ua = new LWP::UserAgent; 
	$ua->agent("Perl API Client/1.0");
	$args{'-containerPath'} ||= '';
			
	my $url = $lk_config{'baseURL'} . "query/" . $args{'-project'} . $args{'-containerPath'} . "/getQuery.api?schemaName=" . $args{'-schemaName'} . "&query.queryName=" . $args{'-queryName'};

	foreach (@{$args{-filterArray}}){
		$url .= "&query.".@{$_}[0]."~".@{$_}[1]."=".@{$_}[2];	
	}
	
	if ($args{'-viewName'}){
		$url .= "&query.viewName=".$args{'-viewName'};	
	}	
	print $url if $args{-debug};

	#Fetch the actual data from the query
	my $request = HTTP::Request->new("GET" => $url); 
	$request->authorization_basic($lk_config{'login'}, $lk_config{'password'}); 
	my $response = $ua->request($request);					

	# Simple error checking 
	if ($response->is_error){
		die $response->status_line;			
	}

	#print Dumper($response);
	my $json_obj = JSON->new->utf8->decode($response->content) || die "ERROR: Unable to decode JSON.\n$url\n";

	return $json_obj;	
	
}

=head1 insertRows()

	my $insert = Labkey::query::insertRows(
		-containerPath => '/WNPRC_Units/Research_Services/Research_Computing',
		-project => 'WNPRC',
		-schemaName => 'lists',
		-queryName => 'mid_tags',
		-rows => $rows,
		);
 
=cut

sub insertRows {	
	#This should allow it to work on linux and newer windows: 	
	my $configFile = File::Spec->catfile($ENV{HOME}, '.lkpass');
	if (! -e $configFile){
		$configFile = File::Spec->catfile($ENV{USERPROFILE},'_lkpass');
	}
	my $settings = new Config::Abstract::Ini($configFile);
	
	my %lk_config = $settings->get_entry('lkconfig');
		die "ERROR: Unable to find .lkpass or file not properly formatted" unless %lk_config;

	my %args = @_;

	#sanity checking
	my @required = ('-project', '-queryName', '-schemaName', '-rows');	
	foreach (@required){
		if (!$args{$_}){die "ERROR: Missing required param: $_"};		
	}

	my @ini_required = qw(baseURL login password);	
	foreach (@ini_required){
		if (!$lk_config{$_}){die "ERROR: INI file missing required param: $_"};		
	}
	
	$args{'-containerPath'} ||= '';
						
	my $ua = new LWP::UserAgent; 
	$ua->agent("Perl API Client/1.0");

	my $url = $lk_config{'baseURL'} . "query/" . $args{'-project'} . $args{'-containerPath'} . "/insertRows.api";
 	print $url if $args{-debug};
 	
	my $data = {
		"schemaName" => $args{'-schemaName'},
 		"queryName"=> $args{'-queryName'},
 		"command" => "insert",
 		"rows" => $args{'-rows'}
		};
		
	my $json_obj = JSON->new->utf8->encode($data);
	 
	#insert the row	
	my $req = new HTTP::Request;
	$req->method('POST');
	$req->url($url);
	$req->content_type('application/json');
	$req->content($json_obj);
						
	$req->authorization_basic($lk_config{'login'}, $lk_config{'password'}); 
	my $response = $ua->request($req);					

	# Simple error checking 
	if ($response->is_error){
		die $response->status_line;			
	}

	#print Dumper($response);
	$json_obj = JSON->new->utf8->decode($response->content) || die "ERROR: Unable to decode JSON.\n$url\n";	
	
}


1;