/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import net.opengis.www.xls.DetermineRouteRequestType;
import net.opengis.www.xls.RequestHeaderType;
import net.opengis.www.xls.XLS;

import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.config.SecurityNamedServiceConfig;
import org.geoserver.security.filter.GeoServerAuthenticationFilter;
import org.geoserver.security.filter.GeoServerSecurityFilter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class OLSAuthenticationFilter extends GeoServerSecurityFilter implements GeoServerAuthenticationFilter {
    protected Logger logger = org.geotools.util.logging.Logging.getLogger("org.geoserver.filters");

    private AuthenticationDetailsSource<HttpServletRequest,?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private String roleName;

    @Override
    public void initializeFromConfig(SecurityNamedServiceConfig config) throws IOException {
        super.initializeFromConfig(config);
        
        OLSAuthenticationFilterConfig olsConfig = (OLSAuthenticationFilterConfig) config;
        
        roleName = olsConfig.getOlsRoleName();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequestWrapper       requestClone = new CachedHttpServletRequestWrapper((HttpServletRequestWrapper)request);

        try {
            JAXBContext                         jaxbContext = JAXBContext.newInstance(DetermineRouteRequestType.class);
            Unmarshaller                        unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<XLS>                    jaxbElement = unmarshaller.unmarshal(new StreamSource(requestClone.getInputStream()), XLS.class);
            XLS                                 input = jaxbElement.getValue();
            RequestHeaderType                   inputHeader = (RequestHeaderType) input.getHeader().getValue();
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(inputHeader.getClientName(), inputHeader.getClientPassword());
            
            authRequest.setDetails(authenticationDetailsSource.buildDetails(requestClone));
            
            GeoServerSecurityManager    gssm = getSecurityManager();
            Authentication              authResult = gssm.authenticate(authRequest);
            
            if (gssm.checkAuthenticationForRole(authResult, gssm.getActiveRoleService().getRoleByName(roleName))) {
                SecurityContextHolder.getContext().setAuthentication(authResult);
                
                chain.doFilter(requestClone, response);
            } else {
                SecurityContextHolder.clearContext();
                logger.log(Level.INFO, "OLS authorization failed");
                ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "No role found");
            }
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
            logger.log(Level.INFO, "OLS authentication failed");
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED, failed.getMessage());
        } catch (JAXBException e) {
            logger.log(Level.SEVERE, "JAXB error", e);
            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

    @Override
    public boolean applicableForHtml() {
        return false;
    }

    @Override
    public boolean applicableForServices() {
        return true;
    }

    class CachedHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final String body;

        public CachedHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);

            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                InputStream inputStream = request.getInputStream();

                if (inputStream != null) {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    char[] charBuffer = new char[128];
                    int bytesRead = -1;

                    while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                        stringBuilder.append(charBuffer, 0, bytesRead);
                    }
                } else {
                    stringBuilder.append("");
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Error reading the request body...", ex);
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, "Error closing bufferedReader...", ex);
                    }
                }
            }

            body = stringBuilder.toString();
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    body.getBytes());

            ServletInputStream inputStream = new ServletInputStream() {
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }
            };

            return inputStream;
        }
    }
}
