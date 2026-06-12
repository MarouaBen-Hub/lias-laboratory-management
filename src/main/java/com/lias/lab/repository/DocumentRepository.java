package com.lias.lab.repository;

import com.lias.lab.entity.Document;
import com.lias.lab.entity.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByEvenementIdOrderByDateUploadDesc(Long evenementId);

    List<Document> findByUploaderIdOrderByDateUploadDesc(Long uploaderId);

    List<Document> findByTypeOrderByDateUploadDesc(TypeDocument type);

    @Query("SELECT d FROM Document d WHERE d.nomOriginal LIKE %:keyword% OR d.description LIKE %:keyword%")
    List<Document> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT d FROM Document d WHERE d.evenement IS NULL ORDER BY d.dateUpload DESC")
    List<Document> findDocumentsGeneraux();

    List<Document> findTop10ByOrderByDateUploadDesc();
}