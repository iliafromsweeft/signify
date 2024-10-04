package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "files")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String name;

    @Column(name = "file_extension", nullable = false)
    private String extension;

    @Column(name = "file_path", nullable = false)
    private String path;

    @Override
    public String toString() {
        return "FileEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", extension='" + extension + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
