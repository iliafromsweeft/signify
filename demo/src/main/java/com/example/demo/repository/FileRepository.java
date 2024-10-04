package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<com.example.demo.entity.FileEntity, Long> {

    Optional<com.example.demo.entity.FileEntity> findById(Long id);

}
