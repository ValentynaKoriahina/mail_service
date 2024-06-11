package com.example.emailservice.repository;

import com.example.emailservice.model.Email;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends ElasticsearchRepository<Email, String> {
    List<Email> findByStatus(String status);
}
