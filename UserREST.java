package service;

import authn.Secured;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.entities.Article;
import model.entities.User;
/**
 *
 * @author nicol
 */
@Stateless
@Path("/api/v1/customer")
@Consumes("application/json")
@Produces("application/json")
public class UserREST {

    // Inyección del EntityManager para manejar operaciones con la base de datos
    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    /**
     * GET: Lista todos los usuarios.
     * Si el usuario es autor, incluye un enlace al último artículo publicado.
     * No retorna información confidencial como contraseñas.
     *
     * @return Respuesta HTTP con la lista de usuarios.
     */
    @GET
    public Response getAllCustomers() {
        // Consulta para obtener todos los usuarios de la base de datos
        List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();

        // Transformar los usuarios en un formato que oculte datos confidenciales
        List<Map<String, Object>> response = users.stream().map(user -> {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            if (!user.getArticles().isEmpty()) {
                userInfo.put("isAuthor", true);
                Article lastArticle = user.getArticles().get(user.getArticles().size() - 1);
                userInfo.put("links", Map.of("article", "/rest/api/v1/article/" + lastArticle.getId()));
            } else {
                userInfo.put("isAuthor", false);
            }
            return userInfo;
        }).collect(Collectors.toList());

        // Retorna la lista de usuarios transformada
        return Response.ok(response).build();
    }

    /**
     * GET: Obtiene un usuario por ID.
     * No incluye información confidencial como contraseñas.
     *
     * @param id Identificador del usuario a obtener.
     * @return Respuesta HTTP con los detalles del usuario o un error si no se encuentra.
     */
    @GET
    @Path("/{id}")
    public Response getCustomerById(@PathParam("id") Long id) {
        // Busca el usuario por ID en la base de datos
        User user = em.find(User.class, id);

        // Si el usuario no existe, retorna un error 404
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        // Construir la respuesta con datos no confidenciales
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        if (!user.getArticles().isEmpty()) {
            userInfo.put("isAuthor", true);
            userInfo.put("articlesCount", user.getArticles().size());
        } else {
            userInfo.put("isAuthor", false);
        }

        // Retorna los datos del usuario
        return Response.ok(userInfo).build();
    }

    /**
     * PUT: Actualiza los datos de un usuario existente.
     *
     * @param id          Identificador del usuario a actualizar.
     * @param updatedUser Datos actualizados del usuario.
     * @return Respuesta HTTP indicando el éxito o error de la operación.
     */
    @Secured
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") Long id, User updatedUser) {
        // Busca el usuario por ID en la base de datos
        User existingUser = em.find(User.class, id);

        // Si el usuario no existe, retorna un error 404
        if (existingUser == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }

        // Actualiza los datos del usuario
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setPassword(updatedUser.getPassword());
        em.merge(existingUser);

        // Retorna una respuesta de éxito
        return Response.ok("User updated successfully").build();
    }
}
