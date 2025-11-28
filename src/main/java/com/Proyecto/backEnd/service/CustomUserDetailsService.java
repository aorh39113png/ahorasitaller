package com.Proyecto.backEnd.service;
import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Proyecto.backEnd.model.UsuariosModel;
import com.Proyecto.backEnd.repository.UsuariosRepo;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	private final UsuariosRepo usuariosRepo;

	public CustomUserDetailsService(UsuariosRepo usuariosRepo) {
		this.usuariosRepo = usuariosRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
		UsuariosModel usuario = usuariosRepo.findByLogin(login)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + login));

		return User.builder()
				.username(usuario.getLogin())
				.password(usuario.getPassword())   
				.authorities(Collections.emptyList())
				.build();
	}
	
	
}