package com.lias.lab.config;

import com.lias.lab.entity.Membre;
import com.lias.lab.repository.MembreRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MembreRepository membreRepository;

    public CustomUserDetailsService(MembreRepository membreRepository) {
        this.membreRepository = membreRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Membre membre = membreRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Membre non trouvé: " + email));

        if (!membre.isActif() || membre.isCompteVerrouille()) {
            throw new UsernameNotFoundException("Compte inactif ou verrouillé");
        }

        return User.builder()
                .username(membre.getEmail())
                .password(membre.getPassword())
                .authorities(
                        membre.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                                .collect(Collectors.toList())
                )
                .build();
    }
}