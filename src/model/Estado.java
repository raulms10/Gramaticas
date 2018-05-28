/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Para crear todos los Estados del Reconocedor
 *
 * @author alejandro.gallegoc
 */
public class Estado implements MouseListener {

    private List<String> simbolosEntrada;
    private List<String> simbolosPila;
    private String nombre;
    private boolean inicial;
    //private List<Transicion> transiciones;
    private JTable tableEstados;
    private DefaultTableModel modelTable;
    private JScrollPane scrollPane;
    private String configuracionInical;
    private Dimension dimension;
    public static Transicion transicion;

    public Estado() {
    }

    public Estado(List<String> simbEntrada, List<String> simbPila) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Crea la el modelo y la tabla de cada estado
     */
    public void setTable() {
        int filas = this.simbolosPila.size() + 2;
        int col = this.simbolosEntrada.size() + 2;
        modelTable = new DefaultTableModel(filas, col) {

            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
        for (int i = 1; i <= simbolosEntrada.size(); i++) {
            modelTable.setValueAt(simbolosEntrada.get(i - 1), 0, i);
        }
        for (int i = 1; i <= simbolosPila.size(); i++) {
            modelTable.setValueAt(simbolosPila.get(i - 1), i, 0);
        }
        modelTable.setValueAt(Reconocedor.FIN_SECUENCIA, 0, simbolosEntrada.size() + 1);
        modelTable.setValueAt(Reconocedor.PILA_VACIA, simbolosPila.size() + 1, 0);

        tableEstados = new JTable(modelTable);
        tableEstados.getTableHeader().setUI(null);
        tableEstados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableEstados.addMouseListener(this);
        tableEstados.setPreferredSize(new Dimension((int) (dimension.width * 0.6), tableEstados.getPreferredSize().height));
        tableEstados.setPreferredScrollableViewportSize(tableEstados.getPreferredSize());
        tableEstados.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(tableEstados);
    }

    /**
     * Devuelve el componente (tabla) que se agregará el layout(vista)
     *
     * @return tabla
     */
    public JScrollPane getTable() {
        return scrollPane;
    }

    /**
     * Constructor
     *
     * @param simbolosEntrada
     * @param simbolosPila
     * @param nombre
     */
    public Estado(List<String> simbolosEntrada, List<String> simbolosPila, String nombre) {
        this.simbolosEntrada = simbolosEntrada;
        this.simbolosPila = simbolosPila;
        this.nombre = nombre;
    }

    public Estado(String nombre, boolean inicial) {
        this.nombre = nombre;
        this.inicial = inicial;
    }

    public Estado(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isInicial() {
        return inicial;
    }

    public void setInicial(boolean inicial) {
        this.inicial = inicial;
    }

    public List<String> getSimbolosEntrada() {
        return simbolosEntrada;
    }

    public void setSimbolosEntrada(List<String> simbolosEntrada) {
        this.simbolosEntrada = simbolosEntrada;
    }

    public List<String> getSimbolosPila() {
        return simbolosPila;
    }

    public void setSimbolosPila(List<String> simbolosPila) {
        this.simbolosPila = simbolosPila;
    }

    public JTable getTableEstados() {
        return tableEstados;
    }

    public void setTableEstados(JTable tableEstados) {
        this.tableEstados = tableEstados;
    }

    public DefaultTableModel getModelTable() {
        return modelTable;
    }

    public void setModelTable(DefaultTableModel modelTable) {
        this.modelTable = modelTable;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public String getConfiguracionInical() {
        return configuracionInical;
    }

    public void setConfiguracionInical(String configuracionInical) {
        this.configuracionInical = configuracionInical;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

}
