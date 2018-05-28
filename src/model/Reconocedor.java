/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author alejandro.gallego
 */
public class Reconocedor {

    private static Reconocedor instance; //singleton
    public static final String FIN_SECUENCIA = "┤";
    public static final String PILA_VACIA = "▼";
    public static final String ICON_UP = "▲";
    public static final String SECUENCIA_NULA = "λ";

    private Estado estado;
    private List<Transicion> transiciones; //Transaciones que el usuario ha usado
    private List<Produccion> producciones;

    public List<Produccion> getProducciones() {
        return producciones;
    }

    public void setProducciones(List<Produccion> producciones) {
        this.producciones = producciones;
    }

    public Reconocedor() {
    }

    //Singleton
    public static Reconocedor getInstance() {
        if (instance == null) {
            instance = new Reconocedor();
        }
        return instance;
    }

    public Reconocedor(Estado estado) {
        this.estado = estado;
    }

    /**
     * Agrega la transicion a todas las que el usuario ha usado
     * @param transicion a agregar
     */
    public void agregarTransicion(Transicion transicion) {
        this.transiciones.add(transicion);
    }

    /**
     * Verificamos la unicidad de la transicion, a pesar de que el usario
     * la puede utiizar varias veces, la idea es que en la definicion se 
     * le mustre solo una vez
     * @param t
     * @return true si la transicion existe, false de lo contrario
     */
    public Transicion existeTransicion(Transicion t) {
        for (Transicion trans : transiciones) {
            if (trans.getOperacionEntrada().equals(t.getOperacionEntrada()) &&
                    trans.getOperacionPila().equals(t.getOperacionPila())){
                return trans;
            }
        }
        return null;
        //return transiciones.stream().anyMatch((trans) -> ((t.getOperacionPila().equalsIgnoreCase(trans.get()))));
    }
    
    /**
     * Buscamos una Transicion que tenga el código enviado como parametro
     * @param codigo
     * @return Transicion
     */
    public Transicion obtenerTransicion(String codigo){
        for (Transicion transicion : transiciones) {
            //System.out.println("Codigo original:" + transicion.getCodigo() + "Barato:" + codigo);
            if(codigo.equals(transicion.getCodigo()) || transicion.getCodigo().contains(codigo) || codigo.contains(transicion.getCodigo())){
                return transicion;
            }
        }
        return null;
    }
    
    /**
     * Para buscar un estado por el nommbre
     * @param opEstado nombre del Estado
     * @return Estado
     */
    /*public Estado obtenerEstado(String opEstado) {
        for (Estado estado : new ArrayList<Estado>()) {
            if(opEstado.equalsIgnoreCase(estado.getNombre())){
                return estado;
            }
        }
        return null;
    }*/

    /**
     * Actualizamos la transicion
     * @param transicion 
     */
    public void actualizarTransicion(Transicion transicion) {
        for (Transicion t : transiciones) {
            if (t.getCodigo().equalsIgnoreCase(transicion.getCodigo())) {
                this.transiciones.set(transiciones.indexOf(t), transicion);
            }
        }
    }
    
    /**
     * Devuelve todas las posibles Transiciones posibles que se puedan ingresar
     * @return
     */
    public List<String> calcularTransicionesPosibles() {
        int i, j, k=0;
        Transicion trans, aux;
        String texto="", value="", sPila, sEntrada;
        List<String> transicionesBasicas = new ArrayList<>();
        //transicionesBasicas.add(" Acepte");
        //transicionesBasicas.add(" Rechace");
        
        for (i = 1; i <= this.estado.getSimbolosPila().size()+1; i++) {
            for (j = 1; j <= this.estado.getSimbolosEntrada().size()+1; j++) {
                texto = "";
                sPila = (String) this.estado.getModelTable().getValueAt(i, 0);//this.estado.getSimbolosPila().get(i);
                sEntrada = (String) this.estado.getModelTable().getValueAt(0, j);
                trans = new Transicion();
                trans.setPosicionTabla(""+i+j);
                trans.setSimboloEntrada(sEntrada);
                trans.setSimboloPila(sPila);
                for (Produccion p : this.getProducciones()) {
                    if(p.getLadoDer().startsWith(sEntrada) && p.getLadoIzq().equals(sPila)){
                        trans.setCodigo("#"+k);
                        trans.setOperacionEntrada("Avance");
                        if(p.getLadoDer().length() > 1){
                            value = p.getLadoDer().substring(1);
                            value = value.replace("<", "a");
                            value = value.replace(">", "b");
                            value = value.replace("a", ">");
                            value = value.replace("b", "<");
                            texto = "Replace('" + Reconocedor.reverse(value) + "'), Avance";
                            trans.setOperacionPila("Replace('" + Reconocedor.reverse(value) + "')");
                            break;
                        }else{
                            texto = "Desapile, Avance";
                            trans.setOperacionPila("Desapile");
                        }
                    }
                }
                if(!texto.equals("")){
                    //trans.setCodigo("#"+k);
                    //trans.setOperacionEntrada("Replce");
                    //trans.setOperacionPila("Desapile");
                }else if(sPila.equals(sEntrada)){
                    texto  = "Desapile, Avance";
                    trans.setCodigo("#"+k);
                    trans.setOperacionEntrada("Avance");
                    trans.setOperacionPila("Desapile");
                }else if (sPila.equals(PILA_VACIA) && sEntrada.equals(FIN_SECUENCIA)){
                    texto = " Acepte";
                    trans.setCodigo(" A");
                    trans.setOperacionEntrada("Acepte");
                    trans.setOperacionPila("Acepte");
                } else{
                    texto = " Rechace";
                    trans.setCodigo(" R");
                    trans.setOperacionEntrada("Rechace");
                    trans.setOperacionPila("Rechace");
                }
                if (!transicionesBasicas.contains(texto)){
                    transicionesBasicas.add(texto);
                    //agregarTransicion(trans);
                    /*if(!trans.getCodigo().equals(" R") && !trans.getCodigo().equals(" A")){
                        k++;
                    }*/
                }
                aux = existeTransicion(trans);
                if(aux != null){ 
                    //actualizarTransicion(trans);
                    trans.setCodigo(aux.getCodigo());
                }else{
                    //trans.setCodigo("#"+k);
                    agregarTransicion(trans);
                    if(!trans.getCodigo().equals(" R") && !trans.getCodigo().equals(" A")){
                        k++;
                    }
                }
                this.estado.getModelTable().setValueAt(trans.getCodigo(), i, j);
                //this.transiciones.add(trans);
                //value = (String) this.estado.getModelTable().getValueAt(i, j);
            }
        }
        //Ordenamos las posibles Transiciones
        Collections.sort(this.transiciones, new Comparator<Transicion>() {
            @Override
            public int compare(Transicion t, Transicion t1) {
                return t.getCodigo().compareTo(t1.getCodigo());
            }
        });
        Collections.sort(transicionesBasicas);
        return transicionesBasicas;
    }
    
    public static String reverse(String palabra) {
        if (palabra.length() == 1)
            return palabra;
        else 
            return reverse(palabra.substring(1)) + palabra.charAt(0);
    }
    
    /**
     * Cambia los codigos de las transiciones a el patrón de tres digitos, donde
     * el primer digito indica la operacion en la pila, el segundo la operación de
     * estado y el tercero la operación de entrada
     */
    /*public void actualizarTransiciones(){
        String value;
        Transicion transicion;
        if(this.estado == null){
            return;
        }
        List<String> transPosibles = estado.calcularTransicionesPosibles();
        //for (Estado estado : estados) {
            for (int i = 1; i <= this.estado.getSimbolosPila().size()+1; i++) {
                for (int j = 1; j <= this.estado.getSimbolosEntrada().size(); j++) {
                    value = (String) this.estado.getModelTable().getValueAt(i, j);
                    //System.out.println("Para: "+  i + " " + j + " valor: " + value + " est: " + estado.getNombre());
                    if(value != null && !"A".equalsIgnoreCase(value) && !"R".equalsIgnoreCase(value)){
                        transicion = obtenerTransicion(value);
                        if(transicion != null){
                            Transicion t = new Transicion();
                            value = transicion.getOperacionPila();
                            //System.out.println("Pila: " + value);
                            if(value.contains("apile(")){
                                value = value.replace("apile(", "Apile(\"");
                                value = value.replace(")", "\")");
                            }else if(value.contains("des")){
                                value = value.replace("d", "D");
                            }else if(value.equalsIgnoreCase("")){
                                value = "Ninguna";
                            }else{
                                //System.out.println("DEfault");
                            }
                            //System.out.println("Res Pila: " + value);
                            t.setOperacionPila(value);
                            value = transicion.getOperacionEstado();
                            if("permanezca".equalsIgnoreCase(value)){
                                value = "Cambie al estado " + this.estado.getNombre();
                            }else{
                                value = value.replace("cambia", "Cambie al estado");
                            }
                            t.setOperacionEstado(value);
                            value = transicion.getOperacionEntrada();
                            if(value.contains("av")){
                                value = value.replace("av", "Av");
                            }else if(value.contains("re")){
                                value = value.replace("re", "Re");
                            }
                            t.setOperacionEntrada(value);
                            value = t.getOperacionPila() + ", " + t.getOperacionEstado() + ", " + t.getOperacionEntrada();
                            //System.out.println("Original: " + value);
                            for (String trans : transPosibles) {
                                if(trans.contains(value)){
                                    value = trans;
                                    break;
                                }
                            }
                            //System.out.println("Trans: " + value);
                            if(value.contains(":")){
                                //System.out.println("Exito");
                                value = value.substring(0, value.indexOf(":"));
                            }else{
                                //System.out.println("Fracaso: " + value);
                                break;
                            }                            
                            System.out.println("Cod: " + value);
                            this.estado.getModelTable().setValueAt(value, i, j);
                            t.setCodigo(value.replace(" ", ""));
                            t.setPosicionTabla(""+i+j);
                            t.setSimboloEntrada((String) this.estado.getModelTable().getValueAt(0, j));
                            t.setSimboloPila((String) this.estado.getModelTable().getValueAt(i, 0));
                            if(existeTransicion(t.getCodigo())){ 
                                actualizarTransicion(t);
                            }else{
                                agregarTransicion(t);
                            }
                            //actualizarTransicion(transicion);
                        }
                    }
                    
                }
                
            }
        //}
        for (int i = 0; i < transiciones.size(); i++) {
            if(transiciones.get(i).getOperacionEstado() != null && (transiciones.get(i).getOperacionEstado().contains("permanezca") || transiciones.get(i).getOperacionEstado().contains("cambia"))){
                transiciones.remove(i);
                i--;
            }
        }
               
        Collections.sort(this.transiciones, new Comparator<Transicion>() {
            @Override
            public int compare(Transicion t, Transicion t1) {
                return t.getCodigo().compareTo(t1.getCodigo());
            }
        });
    }*/

    /**
     * Devuelve el estado que tiene el Reconocedor
     * @return Lista de Estados
     */
    public Estado getEstado() {
        return this.estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public List<Transicion> getTransiciones() {
        return transiciones;
    }

    public void setTransiciones(List<Transicion> transiciones) {
        this.transiciones = transiciones;
    }
}
