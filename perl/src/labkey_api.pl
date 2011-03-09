#export $PERL5LIB = '/home/fedora/workspace/LabKey-API/src/Labkey/Query/lib:$PERL5LIB';

require '/home/fedora/workspace/LabKey-API/src/Labkey/Query/lib/Labkey/Query.pm';
use Data::Dumper;

my $results = Labkey::Query::selectRows(
	-baseUrl => 'https://xnight.primate.wisc.edu:8443/labkey',
	-containerPath => 'wnprc/ehr/',
	-schemaName => 'study',
	-queryName => 'demographics',
	-maxRows => 2,
	-sort => '-id',
	-debug => 1,
	);
print Dumper($results);

my $sql = Labkey::Query::executeSql(
	-baseUrl => 'https://xnight.primate.wisc.edu:8443/labkey',
	-containerPath => 'wnprc/ehr/',
	-schemaName => 'study',
	-sql => 'SELECT d.id FROM study.demographics d',
	-maxRows => 2,
	-offset => 100,
	-debug => 1,
	);
	
print Dumper($sql);		