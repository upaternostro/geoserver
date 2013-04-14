package it.phoops.geoserver.ols.routing.pgrouting;

import it.phoops.geoserver.ols.OLSAbstractServiceProvider;
import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.routing.RoutingServiceProvider;
import it.phoops.geoserver.ols.routing.pgrouting.component.PgRoutingTab;
import it.phoops.geoserver.ols.routing.pgrouting.component.PgRoutingTabFactory;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.AbstractLocationType;
import net.opengis.www.xls.DetermineRouteRequestType;
import net.opengis.www.xls.DetermineRouteResponseType;
import net.opengis.www.xls.PositionType;
import net.opengis.www.xls.RoutePlan;
import net.opengis.www.xls.WayPointList;
import net.opengis.www.xls.WayPointType;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.config.ServiceInfo;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureIterator;
import org.geotools.jdbc.JDBCDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class PgRoutingServiceProvider extends OLSAbstractServiceProvider implements RoutingServiceProvider {
    //Properties Name
    private static final String  PN_ENDPOINT_ADDRESS = "OLS.serviceProvider.geocoding.pgrouting.service.endpointAddress";
    private static final String  PN_PORT_NUMBER = "OLS.serviceProvider.geocoding.pgrouting.service.portNumber";
    private static final String  PN_DATABASE = "OLS.serviceProvider.geocoding.pgrouting.service.database";
    private static final String  PN_USER = "OLS.serviceProvider.geocoding.pgrouting.service.user";
    private static final String  PN_PASSWORD = "OLS.serviceProvider.geocoding.pgrouting.service.password";
    private static final String  PN_ACTIVE_SERVICE = "OLS.serviceProvider.service.active";
    private static final String  PN_PGROUTING_ALGORITHM ="OLS.serviceProvider.geocoding.pgrouting.service.shortest.algorithm";
    
    private String      descriptionKey;
    private Properties  properties = new Properties();
    
    public String getEndpointAddress() {
        return properties.getProperty(PN_ENDPOINT_ADDRESS);
    }

    public void setEndpointAddress(String endpointAddress) {
        properties.setProperty(PN_ENDPOINT_ADDRESS, endpointAddress);
    }
    
    public String getPortNumber() {
        return properties.getProperty(PN_PORT_NUMBER);
    }

    public void setPortNumber(String portNumber) {
        properties.setProperty(PN_PORT_NUMBER, portNumber);
    }
    
    public String getDatabase() {
        return properties.getProperty(PN_DATABASE);
    }

    public void setDatabase(String database) {
        properties.setProperty(PN_DATABASE, database);
    }
    
    public String getUser() {
        return properties.getProperty(PN_USER);
    }

    public void setUser(String user) {
        properties.setProperty(PN_USER, user);
    }
    
    public String getPassword() {
        return properties.getProperty(PN_PASSWORD);
    }

    public void setPassword(String password) {
        properties.setProperty(PN_PASSWORD, password);
    }
    
    public String getAlgorithm() {
        return properties.getProperty(PN_PGROUTING_ALGORITHM);
    }

    public void setAlgorithm(String algorithm) {
        properties.setProperty(PN_PGROUTING_ALGORITHM, algorithm);
    }
    
    public String getActive(){
        return properties.getProperty(PN_ACTIVE_SERVICE);
    }
    
    public void setActive(String activeService){
        properties.setProperty(PN_ACTIVE_SERVICE, activeService);
    }
    
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }
    
    public void setDescriptionKey(String description) {
        this.descriptionKey = description;
    }
    
    @Override
    public Properties getProperties() {
        return properties;
    }
    
    @Override
    public OLSService getServiceType() {
        return OLSService.ROUTING_NAVIGATION;
    }
    @Override
    public ITab getTab() {
        IModel<String> title = new ResourceModel("pgRouting", "pgRouting");
        return PgRoutingTabFactory.getPgRoutingTabFactory().getPgRoutingTab(title);
    }
    @Override
    public void handleServiceChange(ServiceInfo service,
            List<String> propertyNames, List<Object> oldValues,
            List<Object> newValues) {
        String host = ((PgRoutingTab)getTab()).getHostPgRouting();
        if(host == null)
            host = "";
        String port = ((PgRoutingTab)getTab()).getPortPgRouting();
        if(port == null)
            port = "";
        String db = ((PgRoutingTab)getTab()).getDbPgRouting();
        if(db == null)
            db = "";
        String user = ((PgRoutingTab)getTab()).getUserPgRouting();
        if(user == null)
            user = "";
        String psw = ((PgRoutingTab)getTab()).getPswPgRouting();
        if(psw == null)
            psw = "";
        String active = ((PgRoutingTab)getTab()).getActivePgRouting();
        
        String algorithm = ((PgRoutingTab)getTab()).getSelectedAlgorithm().getCode();
        
        
        setActive(active);
        setEndpointAddress(host);
        setPortNumber(port);
        setDatabase(db);
        setUser(user);
        setPassword(psw);
        setAlgorithm(algorithm);
        
    }
    
    @Override
    public boolean isServiceActive() {
        return Boolean.parseBoolean(this.getActive());
    }
    @Override
    public void setPropertiesTab(ITab pgRoutingTab) {
        ((PgRoutingTab)pgRoutingTab).setActivePgRouting(this.getActive());
        ((PgRoutingTab)pgRoutingTab).setHostPgRouting(this.getEndpointAddress());
        ((PgRoutingTab)pgRoutingTab).setPortPgRouting(this.getPortNumber());
        ((PgRoutingTab)pgRoutingTab).setDbPgRouting(this.getDatabase());
        ((PgRoutingTab)pgRoutingTab).setUserPgRouting(this.getUser());
        ((PgRoutingTab)pgRoutingTab).setPswPgRouting(this.getPassword());
        Algorithm algorithm = Algorithm.get(this.getAlgorithm());
        ((PgRoutingTab)pgRoutingTab).setCodeAlgorithmSelected(Integer.parseInt(algorithm.getCode()));
    }

    @Override
    public JAXBElement<DetermineRouteResponseType> geocode(
            DetermineRouteRequestType input) throws OLSException {
        // Parse request parameters
        RoutePlan routePlan = input.getRoutePlan();
        
        if (routePlan == null) {
            throw new OLSException("Route plan is missing");
        }
        
        WayPointList                                    wpList = routePlan.getWayPointList();
        WayPointType                                    startPoint = wpList.getStartPoint();
        JAXBElement<? extends AbstractLocationType>    startLocation = startPoint.getLocation();
        
        if (!(startLocation.getValue() instanceof PositionType)) {
            throw new OLSException("Unsupported start point location");
        }
        
        PositionType                                    startPosition = (PositionType)startLocation.getValue();
        WayPointType                                    endPoint = wpList.getEndPoint();
        JAXBElement<? extends AbstractLocationType>    endLocation = endPoint.getLocation();
        
        if (!(endLocation.getValue() instanceof PositionType)) {
            throw new OLSException("Unsupported end point location");
        }

        PositionType            endPosition = (PositionType)endLocation.getValue();
        Map<String, Object>     params = new HashMap<String, Object>();
        
        params.put("dbtype",    "postgis");
        params.put("host",      getEndpointAddress());
        params.put("port",      new Integer(getPortNumber()));
        params.put("database",  getDatabase());
        params.put("user",      getUser());
        params.put("passwd",    getPassword());
        
        DataStore               pgDatastore = null;
        Connection              connection = null;
        CallableStatement       statement = null;
        ResultSet               rs = null;
        
        try {
            pgDatastore = DataStoreFinder.getDataStore(params); // org.geotools.jdbc.JDBCDataStore
            
//            select AddGeometryColumn('nodes', 'the_geom', 4326, 'POINT', 2);
//            update nodes set the_geom=ST_SetSRID(ST_MakePoint(lon, lat), 4326);
//            CREATE INDEX idx_nodes_geom ON nodes USING GIST ( the_geom );
//            VACUUM ANALYZE nodes (the_geom);
            
            SimpleFeatureSource nodes = pgDatastore.getFeatureSource("nodes"); // @@@ FIXME
            Feature             startNode = findNearestNode(nodes, startPosition);
            Feature             endNode = findNearestNode(nodes, endPosition);
            String              startId = startNode.getIdentifier().getID();
            String              endId = endNode.getIdentifier().getID();
            
            connection = ((JDBCDataStore)pgDatastore).getConnection(Transaction.AUTO_COMMIT);
            // shortest_path -- SELECT id, source, target, cost FROM edge_table
            // shortest_path_astar -- SELECT id, source, target, cost, x1, y1, x2, y2 FROM edge_table
            statement = connection.prepareCall("{call shortest_path(?, ?, ?, ?, ?)}");
            statement.setString(1, "SELECT gid as id, source, target, length as cost, reverse_cost FROM ways ");                                         // SQL
            statement.setInt(2, Integer.parseInt(startId.substring(6)));        // source id
            statement.setInt(3, Integer.parseInt(endId.substring(6)));          // target id
            statement.setBoolean(4, true);                                      // directed
            statement.setBoolean(5, true);                                      // has reverse cost
            rs = statement.executeQuery();
            
            int         v;
            int         e;
            double      c;
            
            while (rs.next()) {
                v = rs.getInt(1);
                e = rs.getInt(2);
                c = rs.getDouble(3);
            }
        } catch (IOException e) {
            throw new OLSException("Error accessing pgDatastore", e);
        } catch (SQLException e) {
            throw new OLSException("Error calling pgRouting", e);
        } finally {
            if (pgDatastore != null) {
                if (rs != null) {
                    ((JDBCDataStore)pgDatastore).closeSafe(rs);
                }
                
                if (statement != null) {
                    ((JDBCDataStore)pgDatastore).closeSafe(statement);
                }
                
                if (connection != null) {
                    ((JDBCDataStore)pgDatastore).closeSafe(connection);
                }
                
                pgDatastore.dispose();
            }
        }
        
        return null;
    }
    
    private Feature findNearestNode(SimpleFeatureSource nodes, PositionType position) throws IOException
    {
        List<Double>                    ordinates = position.getPoint().getPos().getValues();
        Double                          y = ordinates.get(0);
        Double                          x = ordinates.get(1);
        GeometryFactory                 gf = new GeometryFactory();
        Geometry                        point = gf.createPoint(new Coordinate(x.doubleValue(), y.doubleValue()));
        double                          radius = 0.0000001; // degrees
        double                          distance = 1000.0;
        FilterFactory                   ff = CommonFactoryFinder.getFilterFactory();
        FeatureType                     schema = nodes.getSchema();
        GeometryDescriptor              gd = schema.getGeometryDescriptor();
        String                          geometryPropertyName = gd.getLocalName();
        CoordinateReferenceSystem       targetCRS = gd.getCoordinateReferenceSystem();
        Filter                          filter;
        SimpleFeatureCollection         sfc;
        
        do {
            filter = ff.bbox(geometryPropertyName, x - radius, y - radius, x + radius, y + radius, targetCRS.toString());
            sfc = nodes.getFeatures(filter);
            radius *= 2.0;
        } while (sfc.isEmpty() && radius < distance);
        
        FeatureIterator fi = sfc.features();
        Feature         node;
        Geometry        nodeGeometry;
        double          currentDistance;
        Feature         nearestNode = null;
        
        while (fi.hasNext()) {
            node = fi.next();
            
            nodeGeometry = (Geometry)node.getDefaultGeometryProperty().getValue();
            currentDistance = nodeGeometry.distance(point);
            
            if (currentDistance < distance) {
                distance = currentDistance;
                nearestNode = node;
            }
        }
        
        fi.close();
        
        return nearestNode;
    }
}
