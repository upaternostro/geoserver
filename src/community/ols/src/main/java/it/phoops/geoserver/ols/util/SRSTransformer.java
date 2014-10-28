package it.phoops.geoserver.ols.util;

import it.phoops.geoserver.ols.OLSException;

import org.geotools.geometry.GeometryBuilder;
import org.geotools.referencing.CRS;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

public class SRSTransformer
{
    public static double[] transform(double x, double y, CoordinateReferenceSystem sourceCRS, String targetSRSName) throws OLSException
    {
        try {
            CoordinateReferenceSystem   destCrs = CRS.decode(targetSRSName);
            GeometryBuilder             builder = new GeometryBuilder(sourceCRS);
            Point                       sourcePoint = builder.createPoint(x, y);
            Point                       destinationPoint = (Point)sourcePoint.transform(destCrs);
            
            return destinationPoint.getDirectPosition().getCoordinate();
        } catch (NoSuchAuthorityCodeException e) {
            throw new OLSException("Unknown authority in SRS", e);
        } catch (FactoryException e) {
            throw new OLSException("Factory exception converting SRS", e);
        } catch (TransformException e) {
            throw new OLSException("Error transforming geometry", e);
        }
    }
    
    public static double[] transform(double x, double y, String sourceSRSName, CoordinateReferenceSystem targetCRS) throws OLSException
    {
        try {
            CoordinateReferenceSystem   sourceCRS = CRS.decode(sourceSRSName);
            GeometryBuilder             builder = new GeometryBuilder(sourceCRS);
            Point                       sourcePoint = builder.createPoint(x, y);
            Point                       destinationPoint = (Point)sourcePoint.transform(targetCRS);
            
            return destinationPoint.getDirectPosition().getCoordinate();
        } catch (NoSuchAuthorityCodeException e) {
            throw new OLSException("Unknown authority in SRS", e);
        } catch (FactoryException e) {
            throw new OLSException("Factory exception converting SRS", e);
        } catch (TransformException e) {
            throw new OLSException("Error transforming geometry", e);
        }
    }
}
