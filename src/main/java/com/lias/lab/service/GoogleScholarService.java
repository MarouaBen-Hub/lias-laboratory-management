package com.lias.lab.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lias.lab.entity.Membre;
import com.lias.lab.entity.Publication;
import com.lias.lab.entity.enums.TypePublication;
import com.lias.lab.repository.MembreRepository;
import com.lias.lab.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GoogleScholarService {

    @Value("${serpapi.key}")
    private String serpapiKey;

    private static final String SERPAPI_URL = "https://serpapi.com/search.json?engine=google_scholar&q={query}&api_key={apiKey}";

    private final PublicationRepository publicationRepository;
    private final MembreRepository membreRepository;
    private final RestTemplate restTemplate;

    public GoogleScholarService(PublicationRepository publicationRepository,
                                MembreRepository membreRepository) {
        this.publicationRepository = publicationRepository;
        this.membreRepository = membreRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Recherche les publications d'un membre par son ID
     */
    public List<Publication> rechercherPublicationsMembre(Long membreId) {
        Membre membre = membreRepository.findById(membreId).orElse(null);
        if (membre == null) return List.of();

        String query = membre.getNom() + " " + membre.getPrenom();
        return rechercherGoogleScholar(query, membre);
    }

    /**
     * Recherche générale sur Google Scholar
     */
    public List<Publication> rechercherGoogleScholar(String query, Membre auteur) {
        String url = SERPAPI_URL.replace("{query}", query.replace(" ", "+"))
                .replace("{apiKey}", serpapiKey);

        try {
            String response = restTemplate.getForObject(url, String.class);
            return parserResultats(response, auteur);
        } catch (Exception e) {
            System.err.println("Erreur SerpApi: " + e.getMessage());
            return List.of();
        }
    }

    private List<Publication> parserResultats(String jsonResponse, Membre auteur) {
        List<Publication> publications = new ArrayList<>();
        if (jsonResponse == null) return publications;

        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray results = jsonObject.getAsJsonArray("organic_results");

        if (results == null) return publications;

        for (int i = 0; i < results.size() && i < 10; i++) {
            JsonObject result = results.get(i).getAsJsonObject();

            Publication pub = new Publication();
            pub.setTitre(getString(result, "title"));
            pub.setResume(getString(result, "snippet"));
            pub.setUrl(getString(result, "link"));
            pub.setAuteurPrincipal(auteur);
            pub.setAnnee(extraireAnnee(getString(result, "publication_info")));
            pub.setType(TypePublication.ARTICLE);

            publications.add(pub);
        }

        return publicationRepository.saveAll(publications);
    }

    private String getString(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : "";
    }

    private Integer extraireAnnee(String info) {
        if (info == null || info.isEmpty()) return null;
        String[] parts = info.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.matches("\\d{4}")) {
                return Integer.parseInt(part);
            }
        }
        return null;
    }
}