package it.phoops.geoserver.ols.geocoding.rfc59;

import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import it.toscana.regione.normaws.MusumeServiceLocator;
import it.toscana.regione.normaws.MusumeSoapBindingStub;
import it.toscana.regione.normaws.RispostaNormalizzataType;

public class MusumeTest {
    private MusumeSoapBindingStub       binding;
    
    @Before
    public void setUp() throws Exception {
        binding = (MusumeSoapBindingStub) new MusumeServiceLocator().getMusume();
    }
    
    @Test
    public void testGeocodeStreet() throws Exception {
        RispostaNormalizzataType        rispostaNormalizzata = binding.richiesta("1", "Via Roma", "Scandicci", "FI", "50018", "1");
        assertNotNull(rispostaNormalizzata);
    }
}
