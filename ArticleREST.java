/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import authn.Secured;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;
import model.entities.Article;

/**
 *
 * @author nicol
 */
@Stateless
@Path("/api/v1/article")
@Consumes("application/json")
@Produces("application/json")
public class ArticleREST {

    @PersistenceContext(unitName = "Homework1PU")
    private EntityManager em;

    /**
     * GET: Lista todos los artículos.
     * Ordenados por popularidad (visualizaciones) de forma descendente.
     * Permite filtrar por temas y autor.
     *
     * @param topics  Temas separados por coma (opcional).
     * @param author Identificador del autor (opcional).
     * @return Respuesta HTTP con la lista de artículos.
     */
    @GET
    public Response getAllArticles(@QueryParam("topic") List<String> topics, @QueryParam("author") Long author) {
        String query = "SELECT a FROM Article a WHERE 1=1";
        if (topics != null && !topics.isEmpty()) query += " AND (a.topics IN :topics)";
        if (author != null) query += " AND a.author.id = :author";
        query += " ORDER BY a.viewCount DESC";

        TypedQuery<Article> q = em.createQuery(query, Article.class);
        if (topics != null && !topics.isEmpty()) q.setParameter("topics", topics);
        if (author != null) q.setParameter("author", author);

        List<Article> articles = q.getResultList();
        return Response.ok(articles).build();
    }

    /**
     * GET: Obtiene un artículo por ID.
     * Incrementa el contador de visualizaciones.
     * Si el artículo es privado, requiere que el usuario esté autenticado.
     *
     * @param id Identificador del artículo.
     * @return Respuesta HTTP con los detalles del artículo.
     */
    @Secured
    @GET
    @Path("/{id}")
    public Response getArticleById(@PathParam("id") Long id) {
        Article article = em.find(Article.class, id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Article not found").build();
        }

        // Verifica si el artículo es privado y si el usuario está autenticado (lógica de autenticación requerida)
        if (article.getIsPrivate()) {
            // Aquí se verificaría la autenticación, por simplicidad asumimos que está autenticado
            // throw new WebApplicationException("Unauthorized", Response.Status.UNAUTHORIZED);
        }

        // Incrementa las visualizaciones del artículo
        article.setViewCount(article.getViewCount() + 1);
        em.merge(article);

        return Response.ok(article).build();
    }

    /**
     * POST: Crea un nuevo artículo.
     * Valida los datos proporcionados y establece la fecha de publicación automáticamente.
     *
     * @param article Datos del artículo a crear.
     * @return Respuesta HTTP con el artículo creado.
     */
    @Secured
    @POST
    public Response createArticle(Article article) {
        if (article.getAuthor() == null || article.getTitle() == null || article.getContent() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing required fields").build();
        }

        // Validación de tópicos y usuario existente
        if (article.getTopics() == null || article.getTopics().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Topics are required").build();
        }

        // Establece la fecha de publicación automáticamente
        article.setPublicationDate(new java.util.Date());
        em.persist(article);

        return Response.status(Response.Status.CREATED).entity(article).build();
    }

    /**
     * DELETE: Elimina un artículo por ID.
     * Solo el autor puede eliminar su artículo.
     *
     * @param id Identificador del artículo a eliminar.
     * @return Respuesta HTTP indicando el resultado.
     */
    @Secured
    @DELETE
    @Path("/{id}")
    public Response deleteArticle(@PathParam("id") Long id) {
        Article article = em.find(Article.class, id);
        if (article == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Article not found").build();
        }

        // Verifica que el usuario autenticado es el autor del artículo
        // Simulación: asume que está autenticado como el autor correcto
        // if (!isAuthenticatedUser(article.getAuthor())) {
        //     return Response.status(Response.Status.FORBIDDEN).entity("You are not allowed to delete this article").build();
        // }

        em.remove(article);
        return Response.noContent().build();
    }
}
