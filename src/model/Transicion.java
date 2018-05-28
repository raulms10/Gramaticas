/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * Para crear cada una de las Transicones de cada Estado
 * @author alejandro.gallego
 */
public class Transicion {
    private String posicionTabla;
    private String codigo;
    private String simboloEntrada;
    private String simboloPila;
    private String operacionPila;
    private String operacionEntrada;
    private String mensajeMostrar;

    public Transicion() {
    }

    public Transicion(String codigo, String simboloEntrada, String simboloPila, String operacionPila, String operacionEntrada) {
        this.codigo = codigo;
        this.simboloEntrada = simboloEntrada;
        this.simboloPila = simboloPila;
        this.operacionPila = operacionPila;
        this.operacionEntrada = operacionEntrada;
    }

    public Transicion(String simboloEntrada, String simboloPila, String operacionPila, String operacionEntrada) {
        this.simboloEntrada = simboloEntrada;
        this.simboloPila = simboloPila;
        this.operacionPila = operacionPila;
        this.operacionEntrada = operacionEntrada;
    }

    public Transicion(String operacionPila, String operacionEntrada) {
        this.operacionPila = operacionPila;
        this.operacionEntrada = operacionEntrada;
    }
    
    public String textoTrans(){
        if (!this.codigo.contains("R") && !this.codigo.contains("A")){
            return this.getCodigo() + ": " + this.getOperacionPila().replace("<", "&#60").replace(">", "&#62") + ", " + this.getOperacionEntrada();
        }
        return this.getCodigo() + ": " + this.getOperacionEntrada();
    }

    public String getOperacionPila() {
        return operacionPila;
    }

    public void setOperacionPila(String operacionPila) {
        this.operacionPila = operacionPila;
    }

    public String getOperacionEntrada() {
        return operacionEntrada;
    }

    public void setOperacionEntrada(String operacionEntrada) {
        this.operacionEntrada = operacionEntrada;
    }

    public String getMensajeMostrar() {
        return this.operacionPila + ", " + this.operacionEntrada;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSimboloEntrada() {
        return simboloEntrada;
    }

    public void setSimboloEntrada(String simboloEntrada) {
        this.simboloEntrada = simboloEntrada;
    }

    public String getSimboloPila() {
        return simboloPila;
    }

    public void setSimboloPila(String simboloPila) {
        this.simboloPila = simboloPila;
    }

    public String getPosicionTabla() {
        return posicionTabla;
    }

    public void setPosicionTabla(String posicionTabla) {
        this.posicionTabla = posicionTabla;
    }
    
}
