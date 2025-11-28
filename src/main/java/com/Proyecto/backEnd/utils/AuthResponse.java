package com.Proyecto.backEnd.utils;

import java.util.List;

import com.Proyecto.backEnd.model.RolesModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
	
	private String token;
    private String nombre;
    private List<RolesModel> roles;
    private String fecha;

    public AuthResponse(String token, String nombre, List<RolesModel> roles, String fecha) {
        this.token = token;
        this.nombre = nombre;
        this.roles = roles;
        this.fecha = fecha;
    }
	
}
