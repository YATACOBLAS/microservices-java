package org.blas.springcloud.msvc.cursos.services;

import org.blas.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.blas.springcloud.msvc.cursos.models.Usuario;
import org.blas.springcloud.msvc.cursos.models.entity.Curso;
import org.blas.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.blas.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CursoServiceImpl implements CursoService{

    @Autowired
    private CursoRepository repository;

    @Autowired
    private UsuarioClientRest client;

    @Override
    @Transactional(readOnly = true)
    public List<Curso> listar() { return (List<Curso>) repository.findAll();}

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porId(Long id) {
           return  repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Curso> porIdConUsuarios(Long id) {
        Optional<Curso> o = repository.findById(id);
        if(o.isPresent()){
            Curso curso =o.get();
            if(!curso.getCursoUsuarios().isEmpty()){
            List<Long> ids= curso.getCursoUsuarios().stream().map(e->e.getUsuarioId()).collect(Collectors.toList());
              curso.setUsuarios(client.obtenerAlumnosPorCurso(ids));
            }
            return Optional.of(curso);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Curso guardar(Curso curso) {
        return repository.save(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
         repository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {

            Optional<Curso> c= repository.findById(cursoId);
            if(c.isPresent()){
                Curso c2=c.get();
                Usuario u = client.detalle(usuario.getId());
                CursoUsuario cu = new CursoUsuario();
                cu.setUsuarioId(u.getId());
                c2.addCursoUsuario(cu);
                repository.save(c2);
             return Optional.of(u);
            }
            return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
            Optional<Curso> c= repository.findById(cursoId);
            if(c.isPresent()){
                Usuario u= client.crear(usuario);
                Curso c2= c.get();
                CursoUsuario cu= new CursoUsuario();

                cu.setUsuarioId(u.getId());
                c2.addCursoUsuario(cu);
                repository.save(c2);
           return Optional.of(u);
            }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {

     Optional<Curso> c= repository.findById(cursoId);
        if(c.isPresent()){
            Curso c2= c.get();
            Usuario u= client.detalle(usuario.getId());
            CursoUsuario cursoUsuario = new CursoUsuario();
            cursoUsuario.setUsuarioId(u.getId());
            c2.removeCursoUsuario(cursoUsuario);

            repository.save(c2);
            return Optional.of(u);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void eliminarCursoUsuarioPorId(Long id) {
        repository.eliminarCursoUsuarioPorId(id);
    }
}
