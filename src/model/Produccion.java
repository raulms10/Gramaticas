/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alejandro.gallegoc
 */
public class Produccion {
    private String ladoIzq;
    private String ladoDer;
    private boolean anulable;
    private List<String> terminales;
    private List<Boolean> noTerminalesAnulables;
    private List<String> noTerminales;
    private List<String> todosSimbolos;
    private boolean iniciaConTerminal;
    private List<String> primeros;
    private List<String> siguientes;
    private List<String> conjuntoSeleccion;
    
    public Produccion() {
    }

    public Produccion(String ladoIzq, String ladoDer) {
        this.ladoIzq = ladoIzq;
        this.ladoDer = ladoDer;
    }
    
    private void iniciarListas(){
        this.terminales = new ArrayList<>();
        this.noTerminales = new ArrayList<>();
        this.todosSimbolos = new ArrayList<>();
    }
    
    
    private void cargarConjuntosNecesarios(){
        
    }
    /**
     * Obtiene todos los nodos Terminales y no Terminales del Lado Derecho
     * de la Producción y los modifica es las respectivas listas
     */
    public void crearSimbolos(){
        iniciarListas();
        this.iniciaConTerminal = false;
        int longitud = this.ladoDer.length(), i;
        boolean siguiente = false;
        String ele;// = this.ladoDer.substring(0, 1);
        String nodo = "";
        for(i=0; i<longitud; i++){//recorremos el lado derecho
            ele = this.ladoDer.substring(i, i+1);//elemento por elemento
            nodo = nodo + ele;
            switch(ele){
                case ">"://controlamos de que ">"
                    this.noTerminales.add(nodo.toUpperCase());
                    this.todosSimbolos.add(nodo.toUpperCase());
                    nodo = "";
                break;
                case "<"://controlamos de que "<"
                    siguiente = true;//controlamos de que el siguiente inmediato o sea ">"
                    if (i == 0){
                        this.iniciaConTerminal = false;
                    }
                break;
                default:
                    ele = ele.toUpperCase();
                    if (!siguiente){
                        this.terminales.add(nodo.toLowerCase());
                        this.todosSimbolos.add(nodo.toLowerCase());
                        nodo = "";
                        ele = ele.toLowerCase();
                    }
                    siguiente = false;//como ya ha llegado un terminal desactivamos el siguiente inmediato
                    if (i == 0){
                        this.ladoDer = ele + this.ladoDer.substring(1);
                        this.iniciaConTerminal = true;
                    }else if (i == longitud - 1){
                        this.ladoDer = this.ladoDer.substring(0, i) + ele;
                    }else{
                        this.ladoDer = this.ladoDer.substring(0, i) + ele + this.ladoDer.substring(i+1);
                    }                    
            }
        }
    }

    public boolean isAnulable() {
        return anulable;
    }

    public void setAnulable(boolean anulable) {
        this.anulable = anulable;
    }

    public List<Boolean> getNoTerminalesAnulables() {
        return noTerminalesAnulables;
    }

    public void setNoTerminalesAnulables(List<Boolean> noTerminalesAnulables) {
        this.noTerminalesAnulables = noTerminalesAnulables;
    }

    public List<String> getPrimeros() {
        return primeros;
    }

    public void setPrimeros(List<String> primeros) {
        this.primeros = primeros;
    }

    public List<String> getSiguientes() {
        return siguientes;
    }

    public void setSiguientes(List<String> siguientes) {
        this.siguientes = siguientes;
    }

    public List<String> getConjuntoSeleccion() {
        return conjuntoSeleccion;
    }

    public void setConjuntoSeleccion(List<String> conjuntoSeleccion) {
        this.conjuntoSeleccion = conjuntoSeleccion;
    }
    
    /**
     * Elimina los símbolos de '<' y '>' del nodo no Terminal
     * @param noTerminal
     * @return el nombre del nodo no Terminal
     */
    /*public static String nombreNoTerminal(String noTerminal){
        String res = noTerminal.replace("<", "");
        res = res.replace(">", "");
        return res;
    }*/

    public boolean isIniciaConTerminal() {
        return iniciaConTerminal;
    }

    public void setIniciaConTerminal(boolean iniciaConTerminal) {
        this.iniciaConTerminal = iniciaConTerminal;
    }
    
    public String getLadoIzq() {
        return ladoIzq.toUpperCase();
    }

    public void setLadoIzq(String ladoIzq) {
        this.ladoIzq = ladoIzq;
    }

    public String getLadoDer() {
        return ladoDer;
    }

    public void setLadoDer(String ladoDer) {
        this.ladoDer = ladoDer;
    }

    public List<String> getTerminales() {
        return terminales;
    }

    public void setTerminales(List<String> terminales) {
        this.terminales = terminales;
    }

    public List<String> getNoTerminales() {
        return noTerminales;
    }

    public void setNoTerminales(List<String> noTerminales) {
        this.noTerminales = noTerminales;
    }

    public List<String> getTodosSimbolos() {
        return todosSimbolos;
    }

    public void setTodosSimbols(List<String> todosSimbolos) {
        this.todosSimbolos = todosSimbolos;
    }
    
    
    
}
