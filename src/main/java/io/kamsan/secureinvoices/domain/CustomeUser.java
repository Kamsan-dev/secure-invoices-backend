package io.kamsan.secureinvoices.domain;


import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import io.kamsan.secureinvoices.dtomapper.UserDTOMapper;
import io.kamsan.secureinvoices.dtos.UserDTO;
import io.kamsan.secureinvoices.entities.Role;
import io.kamsan.secureinvoices.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
public class CustomeUser implements UserDetails {
    private final User user;
    private final Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> authorities = stream(this.role.getPermission().split(","))
//            .map(String::trim) // Trim any extra spaces
//            .map(SimpleGrantedAuthority::new)
//            .collect(toList());
//
//        // Log the authorities
//        log.info("Mapped authorities: {}", authorities);
    	
//        return authorities;
        
    	return AuthorityUtils.commaSeparatedStringToAuthorityList(this.role.getPermission());

    }


    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.user.isNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.isEnabled();
    }
    
    public UserDTO getUser() {
    	return UserDTOMapper.fromUser(this.user, this.role);
    }
}
