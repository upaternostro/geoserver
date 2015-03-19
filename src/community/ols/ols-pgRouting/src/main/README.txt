To load iter.net data into pgRouting:

shp2pgsql -s 3003 -i -I -S GIA_EL_STRADALE.shp > GIA_EL_STRADALE.sql
shp2pgsql -s 3003 -i -I -S GIA_ACCESSO.shp > GIA_ACCESSO.sql
shp2pgsql -s 3003 -i -I -S GIA_GIUNZIONE.shp > GIA_GIUNZIONE.sql
shp2pgsql -i -n GIA_TOPONIMO_STRADALE.dbf > GIA_TOPONIMO_STRADALE.sql
shp2pgsql -i -n GIA_NUMCIVICO.dbf > GIA_NUMCIVICO.sql

psql -h localhost -U routing -W -d prova -f GIA_EL_STRADALE.sql
psql -h localhost -U routing -W -d prova -f GIA_ACCESSO.sql 
psql -h localhost -U routing -W -d prova -f GIA_GIUNZIONE.sql
psql -h localhost -U routing -W -d prova -f GIA_TOPONIMO_STRADALE.sql
psql -h localhost -U routing -W -d prova -f GIA_NUMCIVICO.sql

create table in_edges as
select 
    e.gid as id, 
    s.gid as source, 
    t.gid as target,
    case
        when oneway = 'TF' or oneway = 'N' then 100000000
        else lng
    end as cost,
    case
        when oneway = 'FT' or oneway = 'N' then 100000000
        else lng
    end as reverse_cost, 
    ST_X(ST_PointN(ST_Transform(e.geom, 4326), 1)) as x1, 
    ST_Y(ST_PointN(ST_Transform(e.geom, 4326), 1)) as y1, 
    ST_X(ST_PointN(ST_Transform(e.geom, 4326), ST_NPoints(e.geom))) as x2,
    ST_Y(ST_PointN(ST_Transform(e.geom, 4326), ST_NPoints(e.geom))) as y2,
    case
        when cod_dug = 'SENZA' and den_uff = 'STRADA SENZA NOME' then ''
        when cod_dug = 'SENZA' then den_uff
        else cod_dug || ' ' || den_uff
    end as name,
    ST_Transform(e.geom, 4326) as the_geom
from gia_el_stradale e 
join gia_giunzione s on e.nod_ini = s.cod_gnz 
join gia_giunzione t on e.nod_fin = t.cod_gnz
join gia_toponimo_stradale ts on e.cod_top = ts.cod_top;

alter table in_edges add primary key(id);

create index idx_in_edges_source on in_edges(source);
create index idx_in_edges_target on in_edges(target);

CREATE INDEX in_edges_the_geom_gist ON in_edges USING gist (the_geom);


create table in_nodes as
select
    gid,
    ST_Transform(geom, 4326) as the_geom
from gia_giunzione; 

alter table in_nodes add primary key(gid);

CREATE INDEX in_nodes_the_geom_gist ON in_nodes USING gist (the_geom);


Node table: in_nodes
Edge table: in_edges
Edge query: SELECT id, source, target, cost, reverse_cost, x1, y1, x2, y2 FROM in_edges
