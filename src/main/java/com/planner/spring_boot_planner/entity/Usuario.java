package com.planner.spring_boot_planner.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "usuarios", schema = "public")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String apellidos;

	@NotBlank
    @Email
    @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;
	
    @NotBlank
	@JsonIgnore
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String password;
	
    @NotBlank
	@JsonIgnore
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String repitePassword;

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BloqueEstudio> bloquesEstudio = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioClase> horariosClases = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarea> tareas = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Examen> examenes = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nombre, String apellidos, String email, String password, String repitePassword) {
        this.nombre = nombre;
		this.apellidos = apellidos;
        this.email = email;
		this.password = password;
		this.repitePassword = repitePassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	@JsonIgnore
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@JsonIgnore
    public String getRepitePassword() {
		return repitePassword;
	}

	public void setRepitePassword(String repitePassword) {
		this.repitePassword = repitePassword;
	}

    public List<BloqueEstudio> getBloquesEstudio() {
        return bloquesEstudio;
    }

    public List<HorarioClase> getHorariosClases() {
        return horariosClases;
    }

    public List<Tarea> getTareas() {
        return tareas;
    }

    public List<Examen> getExamenes() {
        return examenes;
    }

    public void setBloquesEstudio(List<BloqueEstudio> bloquesEstudio) {
        this.bloquesEstudio = bloquesEstudio;
    }

    @Override
    public String toString() {
        return "Usuario {" +
                "id = " + id +
                ", nombre = " + nombre + 
                ", apellidos = " + apellidos + 
                ", email = '" + email + 
                "}";
    }

	@Override
	public String getUsername() {
		return email; // o el campo que uses como identificador de login
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList(); // O asigna roles si los tienes
	}
}
