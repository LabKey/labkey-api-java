#export $PERL5LIB = '/home/fedora/workspace/LabKey-API/src/Labkey/Query/lib:$PERL5LIB';

require '/home/fedora/workspace/LabKey-API/src/Labkey/Query/lib/Labkey/Query.pm';
use Data::Dumper;

my $results = Labkey::Query::selectRows(
	-baseUrl => 'https://xnight.primate.wisc.edu:8443/labkey',
	-containerPath => 'wnprc/ehr/',
	-schemaName => 'core',
	-queryName => 'users',
	-maxRows => 2,
	-sort => '-userid',
	-debug => 1,
	);
print Dumper($results);

my $sql = Labkey::Query::executeSql(
	-baseUrl => 'https://xnight.primate.wisc.edu:8443/labkey',
	-containerPath => 'wnprc/ehr/',
	-schemaName => 'core',
	-sql => 'SELECT u.userid FROM core.users u',
	-maxRows => 2,
	-offset => 100,
	-debug => 1,
	);
	
print Dumper($sql);		