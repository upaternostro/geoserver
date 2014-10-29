package it.phoops.geoserver.ols.util;

import it.phoops.geoserver.ols.OLSException;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class SRSTransformer
{
    private static final GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    
    public static Coordinate transform(double x, double y, CoordinateReferenceSystem sourceCRS, String targetSRSName) throws OLSException
    {
        try {
            CoordinateReferenceSystem   destCrs = CRS.decode(targetSRSName);
            MathTransform               transform = CRS.findMathTransform(sourceCRS, destCrs);
            Coordinate                  coord = new Coordinate(x, y);
            Point                       sourcePoint = geometryFactory.createPoint(coord);
            Point                       destinationPoint = (Point)JTS.transform(sourcePoint, transform);
            
            return destinationPoint.getCoordinate();
        } catch (NoSuchAuthorityCodeException e) {
            throw new OLSException("Unknown authority in SRS", e);
        } catch (FactoryException e) {
            throw new OLSException("Factory exception converting SRS", e);
        } catch (TransformException e) {
            throw new OLSException("Error transforming geometry", e);
        }
    }
    
    public static Coordinate transform(double x, double y, String sourceSRSName, CoordinateReferenceSystem targetCRS) throws OLSException
    {
        try {
            CoordinateReferenceSystem   sourceCRS = CRS.decode(sourceSRSName);
            MathTransform               transform = CRS.findMathTransform(sourceCRS, targetCRS);
            Coordinate                  coord = new Coordinate(x, y);
            Point                       sourcePoint = geometryFactory.createPoint(coord);
            Point                       destinationPoint = (Point)JTS.transform(sourcePoint, transform);
            
            return destinationPoint.getCoordinate();
        } catch (NoSuchAuthorityCodeException e) {
            throw new OLSException("Unknown authority in SRS", e);
        } catch (FactoryException e) {
            throw new OLSException("Factory exception converting SRS", e);
        } catch (TransformException e) {
            throw new OLSException("Error transforming geometry", e);
        }
    }
}
