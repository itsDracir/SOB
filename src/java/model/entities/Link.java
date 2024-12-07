/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;


/**
 *
 * @author nicol
 */
@Entity
public class Link {

    @Id
    @SequenceGenerator(name = "Link_gen", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Link_gen")
    //private long id;

    private String lastArticle;

    public Link() {
    }
    
    public String getLastArticle() {
        return lastArticle;
    }

    public void setLastArticle(long id) {
        lastArticle = "/api/v1/article/" + id;
    }

}