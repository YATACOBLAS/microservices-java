package org.blas.springcloud.msvc.usuarios.msvcusuarios.controllers;

import org.blas.springcloud.msvc.usuarios.msvcusuarios.models.entity.Usuario;
import org.blas.springcloud.msvc.usuarios.msvcusuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> listar(){
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
         Optional<Usuario> usuarioOptinal=service.porId(id);
        if(usuarioOptinal.isPresent()){
            return ResponseEntity.ok(usuarioOptinal.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping()
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result){

        if(result.hasErrors()){
            return validar(result);
        };
        if(!usuario.getEmail().isEmpty() && service.porEmail(usuario.getEmail()).isPresent()){
            return  ResponseEntity.badRequest().body(Collections.singletonMap("mensaje","ya existe el correo !!"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario,BindingResult result, @PathVariable Long id){
       if(result.hasErrors()){
            return validar(result);
        }
        Optional<Usuario> usuarioDb= service.porId(id);
        if(usuarioDb.isPresent()){
            Usuario usu= usuarioDb.get();
            if(!usuario.getEmail().isEmpty() &&
                    !usu.getEmail().equalsIgnoreCase(usuario.getEmail()) &&
                    service.porEmail(usuario.getEmail()).isPresent()){
                return  ResponseEntity.badRequest().body(Collections.singletonMap("mensaje","ya existe el correo !!"));
            }
            usu.setNombre(usuario.getNombre());
            usu.setNombre(usuario.getEmail());
            usu.setNombre(usuario.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usu));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id){
        Optional<Usuario> o=service.porId(id);
        if(o.isPresent()){
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return  ResponseEntity.notFound().build();
    }

    @GetMapping("/usuarios-por-curso")
    public ResponseEntity<?> obtenerAlumnosPorCurso(@RequestParam List<Long> ids){
        return   ResponseEntity.ok(service.listarPorIds(ids));
       }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> e= new HashMap<>();
        result.getFieldErrors().forEach((err)->{
            e.put(err.getField(),"el campo "+ err.getField()+": "+err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(e);
    }
}
