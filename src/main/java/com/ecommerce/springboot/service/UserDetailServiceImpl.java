/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecommerce.springboot.service;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.ecommerce.springboot.model.UsuarioModel;
import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder bCrypt;

    @Autowired
    HttpSession session;

    private Logger log = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Este es el username");
        Optional<UsuarioModel> optionalUser = usuarioService.findByMail(username);
        if (optionalUser.isPresent()) {
            log.info("Esto es el id del usuario: {}", optionalUser.get().getId());
            session.setAttribute("idusuario", optionalUser.get().getId());
            UsuarioModel usuario = optionalUser.get();
            return User.builder().username(usuario.getNombre()).password(usuario.getPassword()).roles(usuario.getTipo()).build();
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }

}
