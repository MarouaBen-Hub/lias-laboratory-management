package com.lias.lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 2.1 Visiteurs : Accès libre sans authentification
                        // 2.1 Visiteurs : Accès libre sans authentification
                        .requestMatchers("/", "/css/**", "/js/**", "/presentation", "/equipes", "/evenements/public", "/publications", "/adhesion/soumettre").permitAll()

                        // 6. Gestion des adhésions (Uniquement le Directeur en mandat / Admin)
                        .requestMatchers("/adhesion/liste", "/adhesion/decider/**").hasAnyAuthority("DIRECTEUR", "ADMIN")

                        // 11. Rapport annuel & 15. Validation matériel
                        .requestMatchers("/rapport/generer", "/materiel/valider").hasAuthority("DIRECTEUR")

                        // 2.2.C Doctorants : Ajouter/Consulter publications, aucun accès aux modules internes (matériel, PV...)
                        .requestMatchers("/publications/ajouter", "/publications/mes-publications").hasAnyAuthority("DOCTORANT", "PERMANENT", "ASSOCIE")

                        // Modules Internes (Matériel, Discussions, PV, Conventions) : Interdit aux Doctorants, Retraités, Anciens
                        .requestMatchers("/materiel/demander", "/discussions/**", "/documents/upload", "/pv/**", "/conventions/**").hasAnyAuthority("PERMANENT", "ASSOCIE")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}