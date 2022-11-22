package org.blas.springcloud.msvc.usuarios.msvcusuarios.services;

import org.blas.springcloud.msvc.usuarios.msvcusuarios.clients.CursoClientRest;
import org.blas.springcloud.msvc.usuarios.msvcusuarios.models.entity.Usuario;
import org.blas.springcloud.msvc.usuarios.msvcusuarios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService{


    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private CursoClientRest cliente;


    //envuelve para commit para ejecutar de la operacio y rollback
    //alguos son de lectura y otros de escritura
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return (List<Usuario>)repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> porId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        return repository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
    try {
        repository.deleteById(id);
        cliente.eliminarCursoUsuarioPorId(id);
    }catch (Exception e){
        System.out.println(e.getMessage());
    }
    }

    @Override
    public Optional<Usuario> porEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarPorIds(Iterable<Long> ids) {
        return (List<Usuario>) repository.findAllById(ids);
    }


}
