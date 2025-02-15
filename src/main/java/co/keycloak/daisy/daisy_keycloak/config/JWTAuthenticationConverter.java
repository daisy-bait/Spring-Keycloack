package co.keycloak.daisy.daisy_keycloak.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JWTAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    //Objeto que nos ayudará a extraer los roles del jwt y convertirlos en permisos dentro de nuestra aplicación
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.principal-attribute}")
    private String principalAttribute;

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = Stream
                .concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(),extractResourceRoles(jwt).stream())
                .toList();

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalName(jwt));
    }

    // Devolvemos una Colección de cualquier objeto que extienda de una GrantedAuthority
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        // Se utilizan Maps puesto que un JSON es un objeto de tipo Clave -> Valor, como los Maps
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (jwt.getClaim("resource_access") == null) {
            return List.of();
        }

        // Accedemos a los recursos provistos del cliente del realm
        resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess.get(resourceId) == null) {
            return List.of();
        }

        // Accedemos al recurso del cliente específico
        resource = (Map<String, Object>) resourceAccess.get(resourceId);

        if (resource.get("roles") == null) {
            return List.of();
        }

        // Accedemos a los roles que el cliente ha provisto para el usuario logueado
        resourceRoles = (Collection<String>) resource.get("roles");

        // Devolvemos la lista de Authorities que Spring admite para ser registrados en el core
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role)))
                .toList();
    };

    private String getPrincipalName(Jwt jwt) {
        // Definimos la constante de la clave "sub" que es una constante dentro del jwt, o su ID
        String claimName = JwtClaimNames.SUB;

        /*
         Si el nombre de la clave del principalAttribute (el que indica el nombre de usuario) viene vacío
         devolvemos el id del jwt (la constante "sub") en su lugar
         */
        if (principalAttribute != null) {
            claimName = principalAttribute;
        }

        return jwt.getClaim(claimName);
    }
}
