package org.blas.springcloud.msvc.cursos.controllers;

import feign.FeignException;
import org.blas.springcloud.msvc.cursos.models.Usuario;
import org.blas.springcloud.msvc.cursos.models.entity.Curso;
import org.blas.springcloud.msvc.cursos.services.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public List<Curso> listar(){
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle( @PathVariable Long id){

            if(service.porId(id).isPresent()){
        //   return ResponseEntity.ok(service.porId(id).get());
             return ResponseEntity.ok(service.porIdConUsuarios(id).get());
            }
                        return ResponseEntity.notFound().build();
    }

    @PostMapping()
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso,BindingResult result){
        if(result.hasErrors()){
            validar(result);
        }
         return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(curso));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso,BindingResult result, @PathVariable Long id){
        if(result.hasErrors()){
            validar(result);
        }
        Optional<Curso> c= service.porId(id);
        if(c.isPresent()){
           Curso c2= c.get();
           c2.setNombre(curso.getNombre());
           return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(c2));
         }
           return ResponseEntity.notFound().build();
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> Eliminar(@PathVariable Long id){
        Optional<Curso> d= service.porId(id);
        if(d.isPresent()){
            service.eliminar(id);
         return  ResponseEntity.noContent().build();
        }
        return  ResponseEntity.notFound().build();
    }
    @PutMapping("/asignar-usuario/{id}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long id){
        Optional<Usuario> o;
        try {
          o =  service.asignarUsuario(usuario,id);
        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                   body(Collections.singletonMap("mensaje",
                            "No existe el id de usuario o error en la comunicacion: "+e.getMessage() ));
        }
        if(o.isPresent()){
          return  ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return  ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-usuario/{id}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long id){

        Optional<Usuario> o;
        try {
            o=  service.crearUsuario(usuario,id);
        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body(Collections.singletonMap("mensaje",
                            "Error en la comunicacion: "+e.getMessage() ));
        }
        if(o.isPresent()){
            return  ResponseEntity.status(HttpStatus.CREATED).body(o.get());
        }
        return  ResponseEntity.notFound().build();
    }
    @DeleteMapping("/eliminar-usuario/{id}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long id){

        Optional<Usuario> o;
        try {
            o=  service.eliminarUsuario(usuario,id);
        }catch (FeignException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).
                    body(Collections.singletonMap("mensaje",
                            "no existe el id de usuario Error en la comunicacion: "+e.getMessage() ));
        }
        if(o.isPresent()){
            return  ResponseEntity.status(HttpStatus.OK).body(o.get());
        }
        return  ResponseEntity.notFound().build();
    }
    @DeleteMapping("/eliminar-curso-usuario/{id}")
    public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id){
                service.eliminarCursoUsuarioPorId(id);
    return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String,String> e= new HashMap<>();
        result.getFieldErrors().forEach((err)->{
            e.put(err.getField(),"el campo"+ err.getField()+": "+err.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(e);
    }

}
