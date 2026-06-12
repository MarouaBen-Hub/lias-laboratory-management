package com.lias.lab.config;

import com.lias.lab.entity.Membre;
import com.lias.lab.repository.MembreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final MembreRepository membreRepository;

    public boolean isCurrentUser(Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        String email = auth.getName();
        return membreRepository.findByEmail(email)
                .map(membre -> membre.getId().equals(userId))
                .orElse(false);
    }

    public Membre getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return membreRepository.findByEmail(auth.getName()).orElse(null);
    }
}
