package com.planner.spring_boot_planner.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "imagenes", schema = "public")
public class Imagen {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JsonIgnore
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@NotBlank
	@Size(max = 500)
	@Column(nullable = false, length = 500)
	private String url;

	@NotBlank
	@Size(max = 255)
	@Column(name = "public_id", nullable = false, length = 255, unique = true)
	private String publicId;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public Imagen() {
	}

	public Imagen(Usuario usuario, String url, String publicId) {
		this.usuario = usuario;
		this.url = url;
		this.publicId = publicId;
	}

	@PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
    public Long getId() {
        return id;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getPublicId() {
        return publicId;
    }
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

