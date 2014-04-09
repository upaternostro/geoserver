GeoServer Open Location Server implementation
=============================================

**Sponsor**
Comune di Prato

**Abstract:** this document describes the approach used to implement a OGC OpenLS server into GeoServer.
The module is designed as a GeoServer plugin and may use various backends for geocoding,
reverse geocoding and routing.

**Keywords:** GeoServer, OpenLS, geocoding, reverse geocoding, routing, SOLR, Open Trip Planner, 
Open Street Map

Overview
========
Scope
-----
This document describes the implementation of an OpenLS as a GeoServer module.
This document assumes that you are familiar with GeoServer's deploy and configuration methods, with
Git and Maven. Moreover, this document will not explain how to deploy SOLR or Open Trip Planner, nor
how to install pgRouting in your PostgreSQL server.
References
==========
Open Geospatial Consortium OpenLS v1.2.0 http://www.opengeospatial.org/standards/ols

Definitions
===========
OGC: Open Geospatial Consortium
OpenLS: Open Location Service
OLS: see OpenLS
OTP: Open Trip Planner
Design consideration
====================
The project aims to deliver an OpenLS server that supplies geocoding, reverse geocoding and routing
functionalities as per the OGC OpenLS v1.2.0 standard.
The implementation consists of a series of GeoServer plugins that:
* enable the OpenLS functionality on GeoServer
* enable the various backends
At the moment of this writing, the supported backends are:

Functionality     | Backends
----------------- | --------
Geocoding         | SOLR
                  | RFC 591
Reverse geocoding | SOLR
Routing           | OTP
                  | pgRouting

Please note that, while the master plugin adds to GeoServer the OpenLS framework, additional plugins
are needed to handle the backends interactions.
Project availability
====================
The project has been developed as a series of community plugins to the GeoServer develop (master)
branch.
The source code for the project resides on GitHub at the URL https://github.com/phoops/geoserver
How to build
============
Follow GeoServer standard instruction, but clone the following git repository:
    https://github.com/phoops/geoserver.git
Remember to enable the ols profile when building:
    mvn -P ols ...
How to run
==========
Deploy the compiled GeoServer WAR archive to your application server as usual.
How to configure
================
After logging into GeoServer administration, you will notice a new service between the GeoServer's
ones. The new service is named OLS

Clicking on the OLS service will bring up the service configuration page, that is analogous to the
native GeoServer services configuration pages:

At the bottom of the page you can select which OpenLS service you want to configure, choosing
between the following list:
* Geocoding
* Reverse geocoding
* Routing & Navigation

Choosing a particular service will bring up the configuration pages for the backend plugins bound to
that service.
Please note that is mandatory to activate a backend for each service.
Back end configuration
======================
Geocoding
---------
###RFC 59
To configure RFC 59 geocoder, you must supply the RFC 59 web service endpoint, the time out and the
algorithm to use. Please refer to http://mappe.regione.toscana.it/ for further information.
###SOLR
To configure the SOLR-based geocoder, just the endpoint of the SOLR server is needed. The SOLR must
index data as described in the paragraph “Loading data into SOLR”.
Reverse geocoding
-----------------
###SOLR
To configure the SOLR-based reverse geocoder, just the endpoint of the SOLR server is needed.
The SOLR must index data as described in the paragraph “Loading data into SOLR”.
Routing & Navigation
--------------------
###OTP
To configure the Open Trip Planner router, you will need to supply the OTP URL (where REST web
services are available). The OTP server must contain data as described in the paragraph “Loading
data into OTP”.
Moreover, you must configure the output strings for the service:
* Navigation Relative Info: used if OTP outputs a relative direction in the walk step. GeoServer will
substitute this patterns:
 - {0}: relative direction
 - {1}: street name
 - {2}: distance
* Navigation Info: used if OTP outputs an absolute direction in the walk step and the street name is
not empty.  GeoServer will substitute this patterns:
 - {0}: direction
 - {1}: distance
 - {2}: street name
* Navigation Info Short: used if OTP outputs an absolute direction in the walk step and the street
name is empty.  GeoServer will substitute this patterns:
 - {0}: direction
 - {1}: distance

At last, the Locale to output data must be configured.
###pgRouting
To configure the pgRouting router, you will need to supply the parameters to connect to the
database, the algorithm to use, the names for the nodes and edges tables and the queries required by
pgRouting to extract data in directed and undirected cases.
The PostgreSQL server must contain data as described in the paragraph “Loading data into pgRouting”.
Moreover, you must configure the output strings for the service:
* Navigation Relative Info: used if pgRouting outputs a relative direction in the walk step. GeoServer
will substitute this patterns:
 - {0}: street name
 - {1}: distance
* Navigation Info: used if pgRouting outputs an absolute direction in the walk step and the street
name is not empty.  GeoServer will substitute this patterns:
 - {0}: direction
 - {1}: distance
 - {2}: street name
* Navigation Info Short: used if pgRouting outputs an absolute direction in the walk step and the
street name is empty.  GeoServer will substitute this patterns:
 - {0}: direction
 - {1}: distance
At last, the Locale to output data must be configured.

How to load data
================
Please note that you have to download Open Street Map data from
http://wiki.openstreetmap.org/wiki/Planet.osm
Loading data into SOLR
----------------------
SOLR must be configured to have a specific core for the Geocoding/Reverse geocoding service. You
will need to modify the SOLR configuration (solr.xml) to add:

    <?xml version="1.0" encoding="UTF-8" ?>
    <solr persistent="true">
      <cores defaultCoreName="12I_CPO_TP" adminPath="/admin/cores" zkClientTimeout="${zkClientTimeout:15000}" host="${host:}" hostPort="${jetty.port:}" hostContext="${hostContext:}">
        <core schema="schema.xml" loadOnStartup="true" instanceDir="12I_CPO_TP/" transient="false" name="12I_CPO_TP" config="solrconfig.xml" dataDir="data"/>
      </cores>
    </solr>

The instance directory must contain a subdirectory named “conf” where the following schema file
(schema.xml) must reside:

    <?xml version="1.0" encoding="UTF-8" ?>
    <schema name="example" version="1.5">
     <fields>
       <field name="id" type="string" indexed="true" stored="true" required="true" multiValued="false" />
       <field name="is_building" type="boolean" indexed="true" stored="true"/>
       <field name="name" type="text_general" indexed="true" stored="true"/>
       <field name="street_type" type="text_general" indexed="true" stored="true"/>
       <field name="street_name" type="text_general" indexed="true" stored="true"/>
       <field name="municipality" type="text_general" indexed="true" stored="true"/>
       <field name="country_subdivision" type="text_general" indexed="true" stored="true"/>
       <field name="building_number" type="text_general" indexed="true" stored="true"/>
       <field name="number" type="text_general" indexed="true" stored="true"/>
       <field name="number_extension" type="text_general" indexed="true" stored="true"/>
       <field name="centerline" type="location_rpt" indexed="true" stored="true"/>
       <field name="centroid" type="location_rpt" indexed="true" stored="true"/>
       <field name="bounding_box" type="location_rpt" indexed="true" stored="true"/>
       <field name="_version_" type="long" indexed="true" stored="true"/>   
     </fields>
     <uniqueKey>id</uniqueKey>
    
      <types>
        <fieldType name="string" class="solr.StrField" sortMissingLast="true" />
        <fieldType name="boolean" class="solr.BoolField" sortMissingLast="true"/>
        <fieldType name="int" class="solr.TrieIntField" precisionStep="0" positionIncrementGap="0"/>
        <fieldType name="float" class="solr.TrieFloatField" precisionStep="0" positionIncrementGap="0"/>
        <fieldType name="long" class="solr.TrieLongField" precisionStep="0" positionIncrementGap="0"/>
        <fieldType name="double" class="solr.TrieDoubleField" precisionStep="0" positionIncrementGap="0"/>
        <fieldType name="tint" class="solr.TrieIntField" precisionStep="8" positionIncrementGap="0"/>
        <fieldType name="tfloat" class="solr.TrieFloatField" precisionStep="8" positionIncrementGap="0"/>
        <fieldType name="tlong" class="solr.TrieLongField" precisionStep="8" positionIncrementGap="0"/>
        <fieldType name="tdouble" class="solr.TrieDoubleField" precisionStep="8" positionIncrementGap="0"/>
        <fieldType name="date" class="solr.TrieDateField" precisionStep="0" positionIncrementGap="0"/>
        <fieldType name="tdate" class="solr.TrieDateField" precisionStep="6" positionIncrementGap="0"/>
        <fieldtype name="binary" class="solr.BinaryField"/>
        <fieldType name="pint" class="solr.IntField"/>
        <fieldType name="plong" class="solr.LongField"/>
        <fieldType name="pfloat" class="solr.FloatField"/>
        <fieldType name="pdouble" class="solr.DoubleField"/>
        <fieldType name="pdate" class="solr.DateField" sortMissingLast="true"/>
        <fieldType name="random" class="solr.RandomSortField" indexed="true" />
        <fieldType name="text_ws" class="solr.TextField" positionIncrementGap="100">
          <analyzer>
            <tokenizer class="solr.WhitespaceTokenizerFactory"/>
          </analyzer>
        </fieldType>
        <fieldType name="text_general" class="solr.TextField" positionIncrementGap="100">
          <analyzer type="index">
            <tokenizer class="solr.StandardTokenizerFactory"/>
            <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" enablePositionIncrements="true" />
            <filter class="solr.LowerCaseFilterFactory"/>
          </analyzer>
          <analyzer type="query">
            <tokenizer class="solr.StandardTokenizerFactory"/>
            <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" enablePositionIncrements="true" />
            <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
            <filter class="solr.LowerCaseFilterFactory"/>
          </analyzer>
        </fieldType>
        <fieldType name="text_it" class="solr.TextField" positionIncrementGap="100">
          <analyzer type="index">
            <tokenizer class="solr.StandardTokenizerFactory"/>
            <filter class="solr.StopFilterFactory"
                    ignoreCase="true"
                    words="lang/stopwords_it.txt"
                    enablePositionIncrements="true"
                    />
            <filter class="solr.LowerCaseFilterFactory"/>
        <filter class="solr.EnglishPossessiveFilterFactory"/>
            <filter class="solr.KeywordMarkerFilterFactory" protected="protwords.txt"/>
            <filter class="solr.PorterStemFilterFactory"/>
          </analyzer>
          <analyzer type="query">
            <tokenizer class="solr.StandardTokenizerFactory"/>
            <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
            <filter class="solr.StopFilterFactory"
                    ignoreCase="true"
                    words="lang/stopwords_it.txt"
                    enablePositionIncrements="true"
                    />
            <filter class="solr.LowerCaseFilterFactory"/>
        <filter class="solr.EnglishPossessiveFilterFactory"/>
            <filter class="solr.KeywordMarkerFilterFactory" protected="protwords.txt"/>
            <filter class="solr.PorterStemFilterFactory"/>
          </analyzer>
        </fieldType>
        <fieldType name="text_it_splitting" class="solr.TextField" positionIncrementGap="100" autoGeneratePhraseQueries="true">
          <analyzer type="index">
            <tokenizer class="solr.WhitespaceTokenizerFactory"/>
            <filter class="solr.StopFilterFactory"
                    ignoreCase="true"
                    words="lang/stopwords_it.txt"
                    enablePositionIncrements="true"
                    />
            <filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="1" catenateWords="1" catenateNumbers="1" catenateAll="0" splitOnCaseChange="1"/>
            <filter class="solr.LowerCaseFilterFactory"/>
            <filter class="solr.KeywordMarkerFilterFactory" protected="protwords.txt"/>
            <filter class="solr.PorterStemFilterFactory"/>
          </analyzer>
          <analyzer type="query">
            <tokenizer class="solr.WhitespaceTokenizerFactory"/>
            <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
            <filter class="solr.StopFilterFactory"
                    ignoreCase="true"
                    words="lang/stopwords_it.txt"
                    enablePositionIncrements="true"
                    />
            <filter class="solr.WordDelimiterFilterFactory" generateWordParts="1" generateNumberParts="1" catenateWords="0" catenateNumbers="0" catenateAll="0" splitOnCaseChange="1"/>
            <filter class="solr.LowerCaseFilterFactory"/>
            <filter class="solr.KeywordMarkerFilterFactory" protected="protwords.txt"/>
            <filter class="solr.PorterStemFilterFactory"/>
          </analyzer>
        </fieldType>
        <fieldType name="text_it_splitting_tight" class="solr.TextField" positionIncrementGap="100" autoGeneratePhraseQueries="true">
          <analyzer>
            <tokenizer class="solr.WhitespaceTokenizerFactory"/>
            <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="false"/>
            <filter class="solr.StopFilterFactory" ignoreCase="true" words="lang/stopwords_it.txt"/>
            <filter class="solr.WordDelimiterFilterFactory" generateWordParts="0" generateNumberParts="0" catenateWords="1" catenateNumbers="1" catenateAll="0"/>
            <filter class="solr.LowerCaseFilterFactory"/>
            <filter class="solr.KeywordMarkerFilterFactory" protected="protwords.txt"/>
            <filter class="solr.EnglishMinimalStemFilterFactory"/>
            <filter class="solr.RemoveDuplicatesTokenFilterFactory"/>
          </analyzer>
        </fieldType>
        <fieldType name="text_general_rev" class="solr.TextField" positionIncrementGap="100">
          <analyzer type="index">
            <tokenizer class="solr.StandardTokenizerFactory"/>
            <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" enablePositionIncrements="true" />
            <filter class="solr.LowerCaseFilterFactory"/>
            <filter class="solr.ReversedWildcardFilterFactory" withOriginal="true"
               maxPosAsterisk="3" maxPosQuestion="2" maxFractionAsterisk="0.33"/>
          </analyzer>
          <analyzer type="query">
            <tokenizer class="solr.StandardTokenizerFactory"/>
            <filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt" ignoreCase="true" expand="true"/>
            <filter class="solr.StopFilterFactory" ignoreCase="true" words="stopwords.txt" enablePositionIncrements="true" />
            <filter class="solr.LowerCaseFilterFactory"/>
          </analyzer>
        </fieldType>
        <fieldType name="alphaOnlySort" class="solr.TextField" sortMissingLast="true" omitNorms="true">
          <analyzer>
            <tokenizer class="solr.KeywordTokenizerFactory"/>
            <filter class="solr.LowerCaseFilterFactory" />
            <filter class="solr.TrimFilterFactory" />
            <filter class="solr.PatternReplaceFilterFactory"
                    pattern="([^a-z])" replacement="" replace="all"
            />
          </analyzer>
        </fieldType>
        
        <fieldtype name="phonetic" stored="false" indexed="true" class="solr.TextField" >
          <analyzer>
            <tokenizer class="solr.StandardTokenizerFactory"/>
            <filter class="solr.DoubleMetaphoneFilterFactory" inject="false"/>
          </analyzer>
        </fieldtype>
    
        <fieldtype name="payloads" stored="false" indexed="true" class="solr.TextField" >
          <analyzer>
            <tokenizer class="solr.WhitespaceTokenizerFactory"/>
            <filter class="solr.DelimitedPayloadTokenFilterFactory" encoder="float"/>
          </analyzer>
        </fieldtype>
        <fieldType name="lowercase" class="solr.TextField" positionIncrementGap="100">
          <analyzer>
            <tokenizer class="solr.KeywordTokenizerFactory"/>
            <filter class="solr.LowerCaseFilterFactory" />
          </analyzer>
        </fieldType>
        <fieldType name="descendent_path" class="solr.TextField">
          <analyzer type="index">
        <tokenizer class="solr.PathHierarchyTokenizerFactory" delimiter="/" />
          </analyzer>
          <analyzer type="query">
        <tokenizer class="solr.KeywordTokenizerFactory" />
          </analyzer>
        </fieldType>
        <fieldType name="ancestor_path" class="solr.TextField">
          <analyzer type="index">
        <tokenizer class="solr.KeywordTokenizerFactory" />
          </analyzer>
          <analyzer type="query">
        <tokenizer class="solr.PathHierarchyTokenizerFactory" delimiter="/" />
          </analyzer>
        </fieldType> 
        <fieldtype name="ignored" stored="false" indexed="false" multiValued="true" class="solr.StrField" />
        <fieldType name="point" class="solr.PointType" dimension="2" subFieldSuffix="_d"/>
        <fieldType name="location" class="solr.LatLonType" subFieldSuffix="_coordinate"/>
     <fieldType name="location_rpt" class="solr.SpatialRecursivePrefixTreeFieldType"
                   spatialContextFactory="com.spatial4j.core.context.jts.JtsSpatialContextFactory"
            distErrPct="0.025" maxDistErr="0.000009" units="degrees" />
     </types>
    </schema>

A temporary PostgreSQL database is used to prepare data that will be loaded into SOLR. The steps are:
* load Open Street Map data into PostgreSQL using osm2pgrouting;
* load the municipality border (in this example we are using Regione Toscana ones)
```
shp2pgsql -s 3003:4326 -c -I GIA_COMUNI.shp >  GIA_COMUNI.sql
psql -h localhost -U routing -W -d prova -f GIA_COMUNI.sql
```
* extract features from pgRouting ways with the following queries:
```
create table features as 
select 'OSM_' || coalesce(codcom, 'OUTSIDE') || '_' || osm_id id, 
    trim(replace(replace(replace(name, '&', '&amp;'), '>', '&gt;'), '<', '&lt;'))::character varying as name, 
    st_collect(the_geom) centerline, 
    st_pointonsurface(st_collect(the_geom)) centroid, 
    st_envelope(st_collect(the_geom)) bounding_box,
    coalesce(c.nome, 'UNKNOWN') municipality, CASE 
        WHEN codprov = '045' THEN 'MS'
        WHEN codprov = '046' THEN 'LU'
        WHEN codprov = '048' THEN 'FI'
        WHEN codprov = '047' THEN 'PT'
        WHEN codprov = '100' THEN 'PO'
        WHEN codprov = '051' THEN 'AR'
        WHEN codprov = '050' THEN 'PI'
        WHEN codprov = '052' THEN 'SI'
        WHEN codprov = '049' THEN 'LI'
        WHEN codprov = '053' THEN 'GR'
        ELSE 'XX'
    END country_subdivision
from ways w LEFT JOIN am_com_region_am_com c ON w.the_geom && c.geom and st_intersects(w.the_geom, c.geom) = 't'
where name is not null and name <> ''
group by id, name, municipality, country_subdivision;

create table dug (text character varying(50));
INSERT INTO dug (text) VALUES ('NUOVA');
INSERT INTO dug (text) VALUES ('STRADELLO');
INSERT INTO dug (text) VALUES ('RIPA');
INSERT INTO dug (text) VALUES ('CANTO');
INSERT INTO dug (text) VALUES ('SOBBORGO');
INSERT INTO dug (text) VALUES ('CORSO');
INSERT INTO dug (text) VALUES ('PONTE');
INSERT INTO dug (text) VALUES ('S.V.');
INSERT INTO dug (text) VALUES ('CHIASSO');
INSERT INTO dug (text) VALUES ('PIAZZETTA');
INSERT INTO dug (text) VALUES ('PIAZZALE');
INSERT INTO dug (text) VALUES ('SENZA');
INSERT INTO dug (text) VALUES ('LARGO');
INSERT INTO dug (text) VALUES ('VIOTTOLO');
INSERT INTO dug (text) VALUES ('VOLTA');
INSERT INTO dug (text) VALUES ('VIADOTTO');
INSERT INTO dug (text) VALUES ('ERTA');
INSERT INTO dug (text) VALUES ('VIUZZO');
INSERT INTO dug (text) VALUES ('VIA');
INSERT INTO dug (text) VALUES ('BORGO');
INSERT INTO dug (text) VALUES ('PIAZZA');
INSERT INTO dug (text) VALUES ('LOCALITA''');
INSERT INTO dug (text) VALUES ('RAMPA');
INSERT INTO dug (text) VALUES ('LUNGARNO');
INSERT INTO dug (text) VALUES ('VIALETTO');
INSERT INTO dug (text) VALUES ('VICOLO');
INSERT INTO dug (text) VALUES ('VIALE');
INSERT INTO dug (text) VALUES ('COSTA');
INSERT INTO dug (text) VALUES ('SALITA');
INSERT INTO dug (text) VALUES ('C.S.');
INSERT INTO dug (text) VALUES ('C.S.O.');
INSERT INTO dug (text) VALUES ('CAVALCAFERROVIA');
INSERT INTO dug (text) VALUES ('S.G.C.');
INSERT INTO dug (text) VALUES ('RACCORDO AUTOSTRADALE');
INSERT INTO dug (text) VALUES ('PARCO');
INSERT INTO dug (text) VALUES ('NUCLEO');
INSERT INTO dug (text) VALUES ('GALLERIA');
INSERT INTO dug (text) VALUES ('SDRUCCIOLO');
INSERT INTO dug (text) VALUES ('SENTIERO');
INSERT INTO dug (text) VALUES ('EX S.V.');
INSERT INTO dug (text) VALUES ('S.C.');
INSERT INTO dug (text) VALUES ('S.S.');
INSERT INTO dug (text) VALUES ('C.A.');
INSERT INTO dug (text) VALUES ('C.S.T.');
INSERT INTO dug (text) VALUES ('S.V');
INSERT INTO dug (text) VALUES ('CIRCONVALLAZIONE');
INSERT INTO dug (text) VALUES ('GIARDINO PUBBLICO');
INSERT INTO dug (text) VALUES ('STRETTO');
INSERT INTO dug (text) VALUES ('TRAVERSA VIA');
INSERT INTO dug (text) VALUES ('EX S.S.');
INSERT INTO dug (text) VALUES ('EX S.R.');
INSERT INTO dug (text) VALUES ('S.D.');
INSERT INTO dug (text) VALUES ('SCALE');
INSERT INTO dug (text) VALUES ('AUTOSTRADA');
INSERT INTO dug (text) VALUES ('PIAGGIA');
INSERT INTO dug (text) VALUES ('S.R.');
INSERT INTO dug (text) VALUES ('RACCORDO');
INSERT INTO dug (text) VALUES ('VILLAGGIO');
INSERT INTO dug (text) VALUES ('EX S.P.');
INSERT INTO dug (text) VALUES ('PORTA');
INSERT INTO dug (text) VALUES ('EX S.C.');
INSERT INTO dug (text) VALUES ('STRADA');
INSERT INTO dug (text) VALUES ('SPIAZZO');
INSERT INTO dug (text) VALUES ('N.A.');
INSERT INTO dug (text) VALUES ('C.S.R.');
INSERT INTO dug (text) VALUES ('TRAVERSA');
INSERT INTO dug (text) VALUES ('TANGENZIALE');
INSERT INTO dug (text) VALUES ('ARCO');
INSERT INTO dug (text) VALUES ('LOCALITA');
INSERT INTO dug (text) VALUES ('S.P.');
INSERT INTO dug (text) VALUES ('FRAZIONE');
INSERT INTO dug (text) VALUES ('PODERE');
INSERT INTO dug (text) VALUES ('PASSEGGIO');
INSERT INTO dug (text) VALUES ('SCALA');
INSERT INTO dug (text) VALUES ('STAZIONE');
INSERT INTO dug (text) VALUES ('PRATO');
INSERT INTO dug (text) VALUES ('PIAZZOLA');
INSERT INTO dug (text) VALUES ('PASSAGGIO');
INSERT INTO dug (text) VALUES ('CHIASSINO');
INSERT INTO dug (text) VALUES ('STRADA VICINALE');
INSERT INTO dug (text) VALUES ('VICINALE');
INSERT INTO dug (text) VALUES ('MULATTIERA');
INSERT INTO dug (text) VALUES ('CAVALCAVIA');
INSERT INTO dug (text) VALUES ('PASSERELLA');
INSERT INTO dug (text) VALUES ('PASSO');
INSERT INTO dug (text) VALUES ('PIAZZALETTO');
INSERT INTO dug (text) VALUES ('PRATELLO');
INSERT INTO dug (text) VALUES ('SCALO');
INSERT INTO dug (text) VALUES ('STRADONE');
INSERT INTO dug (text) VALUES ('SUPERSTRADA');
INSERT INTO dug (text) VALUES ('VARCO');
INSERT INTO dug (text) VALUES ('VIOTTOLA');
INSERT INTO dug (text) VALUES ('VIOTTOLONE');
INSERT INTO dug (text) VALUES ('CA');
INSERT INTO dug (text) VALUES ('LUNGO');
INSERT INTO dug (text) VALUES ('STRADA COMUNALE');
INSERT INTO dug (text) VALUES ('PASSEGGIATA');
INSERT INTO dug (text) VALUES ('LOGGE');
INSERT INTO dug (text) VALUES ('EMICICLO');
INSERT INTO dug (text) VALUES ('VIUCOLA');
INSERT INTO dug (text) VALUES ('SORTITA');
INSERT INTO dug (text) VALUES ('CORTE');
INSERT INTO dug (text) VALUES ('GIARDINO');
INSERT INTO dug (text) VALUES ('LOGGIATI');
INSERT INTO dug (text) VALUES ('ROTONDA');
INSERT INTO dug (text) VALUES ('VIUCCIOLO');
INSERT INTO dug (text) VALUES ('VIUZZA');
INSERT INTO dug (text) VALUES ('BORGATA');
INSERT INTO dug (text) VALUES ('LUNGOMARE');

alter table features add column street_type character varying(200);
alter table features add column street_name character varying(200);

CREATE OR REPLACE FUNCTION normalize()
    RETURNS void AS
$BODY$
DECLARE
    c CURSOR FOR select text from dug;
    t character varying(50);
BEGIN
    open c;
    loop
        fetch c into t;
        exit when NOT FOUND;

        update features set street_type = t, street_name = trim(substring(name, length(t) + 1)) where upper(name) like t || ' %';
    END LOOP;
    CLOSE c;
END;
$BODY$
LANGUAGE plpgsql;

select normalize();

create table features2 as 
select min(id) id, name, st_union(centerline) centerline, 
st_centroid(st_union(centerline)) centroid, st_envelope(st_collect(centerline)) bounding_box, 
municipality, country_subdivision, street_type, street_name 
from features 
group by name, municipality, country_subdivision, street_type, street_name;
```
* generate the SOLR dataset using the output of the following query:
```
select '<doc><field name="id">' || id || '</field><field name="is_building">false</field><field name="name">' || 
        name || '</field><field name="street_type">' || street_type || '</field><field name="street_name">' || 
        street_name || '</field><field name="municipality">' || municipality || '</field><field name="country_subdivision">' || 
        country_subdivision || '</field><field name="centerline">' || st_astext(centerline) || '</field><field name="centroid">' ||
        st_astext(centroid) || '</field><field name="bounding_box">' ||
        st_xmin(bounding_box) || ' ' || st_ymin(bounding_box) || ' ' || st_xmax(bounding_box) || ' ' || st_ymax(bounding_box) ||
    '</field></doc>' doc
from features2 where street_type is not null and street_name is not null
union all
select '<doc><field name="id">' || id || '</field><field name="is_building">false</field><field name="name">' || 
        name || '</field><field name="municipality">' || municipality || '</field><field name="country_subdivision">' || 
        country_subdivision || '</field><field name="centerline">' || st_astext(centerline) || '</field><field name="centroid">' ||
        st_astext(centroid) || '</field><field name="bounding_box">' ||
        st_xmin(bounding_box) || ' ' || st_ymin(bounding_box) || ' ' || st_xmax(bounding_box) || ' ' || st_ymax(bounding_box) ||
    '</field></doc>'
from features2 where street_type is null and street_name is null;
```
* modify the output of the previous query to remove psql header and add an XML tag <add>...</add>
that encloses everything;
* sent the data to SOLR using post.jar

Loading data into OTP
---------------------
Please follow the Open Trip Planner documentation to load Open Street Map data into OTP.
Loading data into pgRouting
---------------------------
Load Open Street Map data into PostgreSQL using osm2pgrouting. Once the process ends, add a primary
key to the table with the following command:

    alter table vertices_tmp add primary key(gid);

To use the loaded data, configure the pgRouting plugin with the following parameters:

Parameter                 | Value
------------------------- | ----------------------------------------------------------------------------------------
Node table name           | vertices_tmp
Edge table name           | ways
Directed Edge SQL query   | SELECT gid as id, source, target, length as cost, x1, y1, x2, y2, reverse_cost FROM ways
Undirected Edge SQL query | SELECT gid as id, source, target, length as cost, x1, y1, x2, y2 FROM ways

