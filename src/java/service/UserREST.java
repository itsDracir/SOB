package service;

import authn.Secured;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import model.entities.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import model.entities.Article;

@Stateless
@Path("/api/v1/customer")
@Consumes("application/json")
@Produces("application/json")
public class UserREST {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    /**
 * GET: Lista todos los usuarios.
 * 
 * Ajuste al enunciado:
 * - Proporciona un listado de usuarios en formato JSON.
 * - Indica si un usuario es autor de al menos un artículo.
 * - Si el usuario es autor, incluye un enlace HATEOAS al último artículo publicado.
 * - No incluye información confidencial como contraseñas.
 * 
 * @return Respuesta HTTP con la lista de usuarios.
 */
@GET
public Response getAllCustomers() {
    // Consulta para obtener todos los usuarios de la base de datos
    List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();

    // Transformar los usuarios en un formato que cumpla con el enunciado
    List<Map<String, Object>> response = users.stream().map(user -> {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("telephone", user.getTelephone());
        userInfo.put("dni", user.getDni());
        userInfo.put("isAuthor", !user.getArticles().isEmpty());

        // Si el usuario es autor, agregar enlace HATEOAS al último artículo
        if (!user.getArticles().isEmpty()) {
            Article lastArticle = user.getArticles().get(user.getArticles().size() - 1);
            userInfo.put("links", Map.of("lastArticle", "/api/v1/article/" + lastArticle.getId()));
        }

        return userInfo;
    }).collect(Collectors.toList());

    // Retorna la lista de usuarios transformada
    return Response.ok(response).build();
}

    /**
 * GET: Obtiene un usuario por ID.
 * 
 * Ajuste al enunciado:
 * - Devuelve la información de un usuario específico.
 * - No incluye información confidencial como contraseñas.
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
    userInfo.put("name", user.getName());
    userInfo.put("telephone", user.getTelephone());
    userInfo.put("dni", user.getDni());
    userInfo.put("isAuthor", !user.getArticles().isEmpty());

    // Si el usuario es autor, incluir un contador de artículos
    if (!user.getArticles().isEmpty()) {
        userInfo.put("articlesCount", user.getArticles().size());
    }

    // Retorna los datos del usuario
    return Response.ok(userInfo).build();
}


    /**
 * PUT: Actualiza los datos de un usuario existente.
 * 
 * Ajuste al enunciado:
 * - Permite actualizar los datos de un usuario por ID.
 * - Requiere autenticación para realizar esta operación.
 * 
 * @param id          Identificador del usuario a actualizar.
 * @param updatedUser Datos actualizados del usuario.
 * @return Respuesta HTTP indicando el éxito o error de la operación.
 */
@Secured // Requiere autenticación
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
    existingUser.setName(updatedUser.getName());
    existingUser.setTelephone(updatedUser.getTelephone());
    existingUser.setDni(updatedUser.getDni());
    em.merge(existingUser);

    // Retorna una respuesta de éxito
    return Response.ok("User updated successfully").build();
}

 
    /**
     * POST: Crea un nuevo usuario.
     * @param user
     * @return 
     
    @POST
    public Response createCustomer(User user) {
        if (user.getName() == null || user.getTelephone() == null || user.getDni() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing required fields").build();
        }
        em.persist(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    /**
     * DELETE: Elimina un usuario por ID.
     * @param id
     * @return 
     
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") Long id) {
        User user = em.find(User.class, id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        em.remove(user);
        return Response.noContent().build();
    }
    */
}
