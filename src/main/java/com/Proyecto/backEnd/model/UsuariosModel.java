package com.Proyecto.backEnd.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="usuarios")
@Getter
@Setter
public class UsuariosModel {
	@Id
	String login;
	String password;
	int estado;
	
	@OneToOne
	@JoinColumn(name="codp")
	@JsonManagedReference
	private PersonalModel personal;
	
    @ManyToMany
    @JoinTable(
        name = "usurol",
        joinColumns = @JoinColumn(name = "login"),
        inverseJoinColumns = @JoinColumn(name = "codr")
    )
    @JsonIgnoreProperties("usuarios")
    List<RolesModel> roles;
}
