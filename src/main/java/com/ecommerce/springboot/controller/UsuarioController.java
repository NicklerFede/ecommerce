/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ecommerce.springboot.controller;

import com.ecommerce.springboot.model.OrdenModel;
import com.ecommerce.springboot.model.UsuarioModel;
import com.ecommerce.springboot.service.OrdenService;
import com.ecommerce.springboot.service.UsuarioService;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Nickler
 */
@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
    private final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private OrdenService ordenService;
    
    BCryptPasswordEncoder passEncode = new BCryptPasswordEncoder();
    
    @GetMapping("/registro")
    public String create() {
        return "usuario/registro";
    }
    
    @PostMapping("/save")
    public String save(UsuarioModel usuario) {
        
        logger.info("Usuario registro: {}", usuario);
        usuario.setTipo("USER");
        usuario.setPassword(passEncode.encode(usuario.getPassword()));
        usuarioService.save(usuario);
        
        return "redirect:/";
    }
    
    @GetMapping("/login")
    public String login() {
        return "usuario/login";
    }
    
    @GetMapping("/acceder")
    public String acceder(UsuarioModel usuario, HttpSession session) {
        logger.info("Accesos: {}", usuario);
       
        Optional<UsuarioModel> user = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString()));
        //logger.info("Usuario obtenido: {}", user.get());
        
        if (user.isPresent()) {
            session.setAttribute("idusuario", user.get().getId());
            if (user.get().getTipo().equals("ADMIN")) {
                return "redirect:/administrador";
            } else{
                return "redirect:/";
            }
        } else {
            logger.info("Usuario no existe");
        }
        
        return "redirect:/";
    }
    
    @GetMapping("/compras")
    public String obtenerCompras(HttpSession session, Model modelo){
        modelo.addAttribute("sesion", session.getAttribute("idusuario"));
        UsuarioModel usuario= usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
        List<OrdenModel> ordenes= ordenService.findByUsuario(usuario);
        
        modelo.addAttribute("ordenes", ordenes);
        
        return "usuario/compras";
    }
    
    @GetMapping("/detalle/{id}")
    public String detalleCompra(@PathVariable Integer id, HttpSession session, Model modelo){
        //session
        modelo.addAttribute("sesion", session.getAttribute("idusuario"));
        logger.info("Id de la orden: {}", id);
        Optional<OrdenModel> orden= ordenService.findById(id);
        
        modelo.addAttribute("detalles", orden.get().getDetalle());
        
        return "usuario/detallecompra";
    }
   
    @GetMapping("/cerrar")
    public String cerrarSesion(HttpSession session){
        session.removeAttribute("idusuario");
        
        return "redirect:/";
    }
}
