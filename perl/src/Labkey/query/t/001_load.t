# -*- perl -*-

# t/001_load.t - check module loading and create testing directory

use Test::More tests => 2;

BEGIN { use_ok( 'Labkey::query' ); }

my $object = Labkey::query->new ();
isa_ok ($object, 'Labkey::query');


