/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import model.Reconocedor;
import model.Estado;
import model.Transicion;

/**
 *
 * @author alejandro.gallegoc
 */
public class TransicionesFrame extends javax.swing.JFrame implements MouseListener {

    private Container pane;
    private Reconocedor reconocedor;
    private Dimension dimension;
    private final JPanel jpanel_tablaEstados;
    private JTextField jtf_hilera;
    private JTextField jtf_estado;
    private JTextField jtf_caracter;
    private JTextField jtf_operacion;
    private JLabel jl_pila;
    private JLabel jl_hilera;
    private JLabel jl_estado;
    private JLabel jl_caracter;
    private JLabel jl_operacion;
    private JButton jb_finSecuencia;
    private JButton jb_siguiente;
    private JTable tablePila;
    private JButton btnReconocedor;
    private boolean reconocedorActivo;

    private String hilera;
    private String caracter;
    private int posActual;
    private Transicion transActual;
    private Estado estadoActual;

    public TransicionesFrame() {
        initComponents();
        this.setPreferredSize(new Dimension(600, 800));
        this.setResizable(false);
        this.setTitle("Tabla de Transiciones del Autómata de Pila");
        jpanel_tablaEstados = new JPanel();
        jpanel_tablaEstados.setLayout(new BoxLayout(jpanel_tablaEstados, BoxLayout.Y_AXIS));
        //jpanel_tablaEstados.setAutoscrolls(true);
    }

    /**
     * Preparamos la interfaz
     */
    public void initUI() {
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());

        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

        JPanel panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));

        JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout());

        pane = this.getContentPane();
        pane.setLayout(new BorderLayout());
        this.setSize(new Dimension(dimension.width, dimension.height + 150));
        JLabel jlNombre;
        //int index = -1;

        jlNombre = new JLabel("          Tabla de transiciones de cada estado", SwingConstants.CENTER);
        jlNombre.setSize((int) (dimension.width * 0.6), jlNombre.getHeight());
        panel1.add(jlNombre, BorderLayout.WEST);
        jlNombre = new JLabel("Posibles transiciones          ", SwingConstants.CENTER);
        jlNombre.setSize((int) (dimension.width * 0.4), jlNombre.getHeight());
        panel1.add(jlNombre, BorderLayout.EAST);
        pane.add(panel1, BorderLayout.PAGE_START);
        JScrollPane jsp;
        //Por cada estado voy a tener una tabla y su encabezado (Estado: X)
        Estado estado = reconocedor.getEstado();
        jlNombre = new JLabel("Estado: " + estado.getNombre(), SwingConstants.CENTER);
        jpanel_tablaEstados.add(jlNombre);
        jsp = estado.getTable();
        jpanel_tablaEstados.add(jsp);

        JScrollPane jScrollPane = new JScrollPane(jpanel_tablaEstados);

        panel2.add(jScrollPane);
        JPanel panelText = new JPanel();
        panelText.setLayout(new GridLayout(0, 1));
        //Para hacer saltos de línea entre cada posible Transicion
        String t = "<html><p>";
        for (Transicion trans : reconocedor.getTransiciones()) {
            t = t + trans.textoTrans() + "<br>";
        }
        t = t + "</p></html>";
        jlNombre = new JLabel(t);
        panelText.add(jlNombre);
        jScrollPane = new JScrollPane(panelText);
        panel2.add(jScrollPane);
        pane.add(panel2, BorderLayout.CENTER);

        //Botón para Regresar a la vista anterior
        JButton btnRegresar;
        btnRegresar = new JButton("Regresar");
        btnRegresar.addMouseListener(this);
        btnRegresar.setHorizontalAlignment(SwingConstants.CENTER);
        panel3.add(btnRegresar);

        //Botón para Exportar el Automata como un JSON en un archivo TXT
        JButton btnExportar;
        btnExportar = new JButton("Exportar");
        btnExportar.addMouseListener(this);
        btnExportar.setHorizontalAlignment(SwingConstants.CENTER);
        //panel3.add(btnExportar);

        //Botón para Guardar la Matriz de Transiciones como imagen PNG
        JButton btnGuardarPNG;
        btnGuardarPNG = new JButton("Guardar PNG");
        btnGuardarPNG.addMouseListener(this);
        btnGuardarPNG.setHorizontalAlignment(SwingConstants.CENTER);
        panel3.add(btnGuardarPNG);

        //Botón para mostrar el Reconocedor
        btnReconocedor = new JButton("Reconocedor " + Reconocedor.PILA_VACIA);
        btnReconocedor.addMouseListener(this);
        btnReconocedor.setHorizontalAlignment(SwingConstants.CENTER);
        panel3.add(btnReconocedor);
        //pane.add(panel3);

        JPanel panelA = new JPanel();
        JPanel panelB = new JPanel();
        JPanel panelC = new JPanel();

        panelA.setLayout(new BoxLayout(panelA, BoxLayout.X_AXIS));
        panelB.setLayout(new BorderLayout());
        panelC.setLayout(new BorderLayout());

        jl_hilera = new JLabel("Hilera a Reconocer: ");
        jtf_hilera = new JTextField();
        jtf_estado = new JTextField();
        jtf_caracter = new JTextField();
        jtf_operacion = new JTextField();
        jtf_estado.setEditable(false);
        jtf_caracter.setEditable(false);
        jtf_operacion.setEditable(false);

        jb_finSecuencia = new JButton(Reconocedor.FIN_SECUENCIA);
        jb_siguiente = new JButton("Siguiente");
        jb_siguiente.setEnabled(false);
        jb_finSecuencia.addMouseListener(this);
        jb_siguiente.addMouseListener(this);

        DefaultTableModel model = new DefaultTableModel(1, 1) {
            @Override
            public boolean isCellEditable(int i, int i1) {
                return false;
            }
        };
        model.setValueAt(Reconocedor.PILA_VACIA, 0, 0);
        tablePila = new JTable(model);
        tablePila.getTableHeader().setUI(null);
        tablePila.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePila.setPreferredSize(new Dimension(tablePila.getPreferredSize().width, 150));
        tablePila.setPreferredScrollableViewportSize(tablePila.getPreferredSize());
        tablePila.setFillsViewportHeight(true);

        JPanel panelA1 = new JPanel();
        panelA1.setLayout(new GridLayout(4, 1));//(new BoxLayout(panelA1, BoxLayout.Y_AXIS));

        JPanel panelA2 = new JPanel();
        panelA2.setLayout(new BoxLayout(panelA2, BoxLayout.Y_AXIS));

        panelA1.add(jl_hilera);
        panelA2.add(jtf_hilera);

        jl_estado = new JLabel("Estado actual: ");
        panelA1.add(jl_estado);
        panelA2.add(jtf_estado);

        jl_caracter = new JLabel("Caracter actual: ");
        panelA1.add(jl_caracter);
        panelA2.add(jtf_caracter);

        jl_operacion = new JLabel("Operación: ");
        panelA1.add(jl_operacion);
        panelA2.add(jtf_operacion);

        panelA.add(panelA1);
        panelA.add(panelA2);//, BorderLayout.PAGE_START);

        jlNombre = new JLabel();
        panelB.add(jlNombre);
        panelB.add(jb_finSecuencia, BorderLayout.PAGE_START);
        panelB.add(jb_siguiente, BorderLayout.PAGE_END);

        jl_pila = new JLabel("Pila", SwingConstants.CENTER);
        panelC.add(jl_pila, BorderLayout.PAGE_START);
        panelC.add(tablePila, BorderLayout.CENTER);

        panel4.add(panelA, BorderLayout.CENTER);
        panel4.add(panelB, BorderLayout.EAST);
        panel4.add(panelC, BorderLayout.WEST);

        JPanel panel3A = new JPanel();
        panel3A.setLayout(new BorderLayout());
        panel3A.add(panel3, BorderLayout.PAGE_START);
        panel3A.add(panel4, BorderLayout.CENTER);

        pane.add(panel3A, BorderLayout.PAGE_END);

        reconocedorActivo = false;
        mostrarReconocedor();
        //pane.getComponent(2).getC.setVisible(false);
    }

    public Reconocedor getReconocedor() {
        return reconocedor;
    }

    public void setReconocedor(Reconocedor reconocedor) {
        this.reconocedor = reconocedor;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    /**
     * This method i s called from within the constructor to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TransicionesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TransicionesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TransicionesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TransicionesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TransicionesFrame().setVisible(true);
            }
        });
    }

    /**
     * Guarda la matriz de trnasiciones en un archivo de texto
     *
     * @param contenido JSON del archivo de texto
     * @param nombre del arhivo de text0 (nombre.txt)
     * @throws IOException
     */
    private void guardarMatrizEnTXT(String contenido, String nombre) throws IOException {
        File fileTXT = new File(nombre + ".txt"); //Se crea el archivo de texto
        String ruta = fileTXT.getAbsolutePath(); //Se obtiene la ruta del archivo
        FileWriter fw = new FileWriter(fileTXT, false);

        fw.write(contenido); //Se escribe el JSON en el archivo

        fw.close(); //Se cierra el archivo

        JOptionPane.showMessageDialog(this, "Ruta: " + ruta, "Archivo TXT guardado", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Guarda la matriz de trnasiciones como una iamgen en PNG
     *
     * @param comp panel que contiene la matriz
     * @param nombre con el que será guardada la imagen (nombre.png)
     */
    private void guardarMatrizEnPNG(Component comp, String nombre) {
        int w = comp.getWidth();
        int h = comp.getHeight();

        //Se crea el buffered
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        comp.paint(g2); //Se dibuja la del tablero en el gráfico creado con el buffered
        g2.dispose();
        try {
            File f = new File(nombre + ".png"); //Se crea el archivo de la imagen
            String ruta = f.getAbsolutePath(); //Se obtiene la ruta de la imagen
            ImageIO.write(bi, "png", f); //Se escribe en el archivo la imagen
            JOptionPane.showMessageDialog(this, "Ruta: " + ruta, "Imagen guardada", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, "No se ha podido guardar la imagen", "Error de almacenamiento", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Para exportar el Autómata a un archivo de Texto
     */
    private void exportarTXT() {
        String value;
        String resultado = "{\n\t\"Simbolos de entrada\":[";
        for (String s : reconocedor.getEstado().getSimbolosEntrada()) {
            resultado = resultado + "\"" + s + "\",";
        }
        resultado = resultado + "\"" + Reconocedor.FIN_SECUENCIA + "\"],\n\t\"Simbolos en la pila\":[";
        for (String s : reconocedor.getEstado().getSimbolosPila()) {
            resultado = resultado + "\"" + s + "\",";
        }
        resultado = resultado + "\"" + Reconocedor.PILA_VACIA + "\"],\n\t\"Estados\":[\n";
        Estado e = reconocedor.getEstado();
            resultado = resultado + "\t\t{\n"
                    + "\t\t\t\"nombre\":\"" + e.getNombre() + "\",\n"
                    + "\t\t\t\"inicial\":" + e.isInicial() + ",\n"
                    + "\t\t\t\"transiciones\":[";
            for (int i = 1; i < reconocedor.getEstado().getSimbolosPila().size() + 2; i++) {
                for (int j = 1; j < reconocedor.getEstado().getSimbolosEntrada().size() + 2; j++) {
                    value = (String) e.getModelTable().getValueAt(i, j);
                    if (value == null || value.equalsIgnoreCase("null") || value.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Debe completar todas las Transiciones", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    resultado = resultado + value + ",";
                }
                resultado = resultado.substring(0, resultado.length() - 1) + ";";
            }
            resultado = resultado.substring(0, resultado.length() - 1) + "]\n\t\t},\n";
        //}
        resultado = resultado.substring(0, resultado.length() - 2) + "\n\t],\n"//+ "]\n\t}\n],\n"
                + "\t\"Configuracion inicial\":[\"" + reconocedor.getEstado().getConfiguracionInical() + "\"],\n"
                + "\t\"Transiciones\":{\n";
        for (Transicion t : reconocedor.getTransiciones()) {
            resultado = resultado + "\t\t\"" + t.getCodigo() + "\":[\"";
            if (transActual.getCodigo().contains("A") || transActual.getCodigo().contains("R")) {
                resultado = resultado + t.getOperacionPila() + "\"],\n";
            } else {
                if (t.getOperacionEntrada().contains(":")) {
                    resultado = resultado + t.getOperacionPila().substring(3, t.getOperacionPila().length()) + "\",\"" + "***" + "\",\"" + t.getOperacionEntrada().substring(3, t.getOperacionEntrada().length()) + "\"],\n";
                } else {
                    resultado = resultado + t.getOperacionPila() + "\",\"" + "***" +  "\",\"" + t.getOperacionEntrada() + "\"],\n";
                }

            }
        }
        resultado = resultado.substring(0, resultado.length() - 2) + "\n\t}\n}";
        //Capturamos el nombre del archivo de texto
        try {
            String nombre = JOptionPane.showInputDialog(rootPane, "Digite el nombre del archivo de texto sin espacios", "Nombre del TXT", JOptionPane.QUESTION_MESSAGE);
            if (nombre == null) {
                return;
            }
            //Validamos el nombre
            if ("".equals(nombre.replace(" ", ""))) {
                JOptionPane.showMessageDialog(this, "El nombre del archivo de text no puede estar vacío", "Error falta nombre", JOptionPane.WARNING_MESSAGE);
                return;
            }
            //Guardamos la matriz en el archivo TXT
            guardarMatrizEnTXT(resultado, nombre.replace(" ", ""));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Ha ocurrido un error al tratar de crear el archivo TXT", "Error de escritura", JOptionPane.ERROR_MESSAGE);
        }
        //System.out.println(resultado);
    }

    /**
     * Para activador los componentes del reconocerdor
     */
    private void activarReconocedor() {
        jb_finSecuencia.setEnabled(reconocedorActivo);
        //jb_siguiente.setEnabled(reconocedorActivo);
        jtf_caracter.setEnabled(reconocedorActivo);
        jtf_estado.setEnabled(reconocedorActivo);
        jtf_hilera.setEnabled(reconocedorActivo);
        jtf_operacion.setEnabled(reconocedorActivo);
        tablePila.setEnabled(reconocedorActivo);
        jl_caracter.setEnabled(reconocedorActivo);
        jl_estado.setEnabled(reconocedorActivo);
        jl_hilera.setEnabled(reconocedorActivo);
        jl_operacion.setEnabled(reconocedorActivo);
        jl_pila.setEnabled(reconocedorActivo);
        Estado estado = reconocedor.getEstado();
        estado.getTableEstados().setEnabled(!reconocedorActivo);
        estado.getTableEstados().getSelectionModel().clearSelection();
        
    }

    /**
     * Para abrir el panel que hace el reconocimiento con el Autómata de Pila
     */
    private void mostrarReconocedor() {
        if (reconocedorActivo) {
            reconocedorActivo = false;
            activarReconocedor();
            btnReconocedor.setText("Reconocedor " + Reconocedor.PILA_VACIA);
            jb_siguiente.setEnabled(false);
        } else {
            String value;
            Estado e = reconocedor.getEstado();
            for (int i = 1; i < e.getSimbolosPila().size() + 2; i++) {
                for (int j = 1; j < e.getSimbolosEntrada().size() + 2; j++) {
                    value = (String) e.getModelTable().getValueAt(i, j);
                    if (value == null || value.equalsIgnoreCase("null") || value.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Las Transiciones no han podico completar", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            //pane.getComponent(3).setVisible(true);
            //this.setSize(dimension.width, dimension.height+150);
            setConfiInicial();
            jtf_estado.setText(e.getNombre());
            reconocedorActivo = true;
            btnReconocedor.setText("Reconocedor " + Reconocedor.ICON_UP);
            activarReconocedor();
        }
    }

    /**
     * Para actualizar la pila a la configuración inicial
     */
    private void setConfiInicial() {
        String conf = reconocedor.getEstado().getConfiguracionInical();
        DefaultTableModel model = (DefaultTableModel) tablePila.getModel();
        model.setNumRows(0);
        model.addRow(new String[]{conf});
        model.addRow(new String[]{Reconocedor.PILA_VACIA});
        /*for (int i = 0; i < conf.length(); i++) {
            model.addRow(new String[]{conf.substring(i, i + 1)});
        }*/
    }

    /**
     * Para buscar la siguiente Transicion en la Tabla de Transiciones del respectivo estado
     * @param simbEntrada
     * @param simbPila
     * @param estado
     * @return Transicion
     */
    private Transicion obtenerTransicion(String simbEntrada, String simbPila, Estado estado) {
        System.out.println("Buscando para: " + simbEntrada + " " + simbPila + " " + estado.getNombre());
        int fila = -1;
        int col = -1;
        String cod;
        if (Reconocedor.PILA_VACIA.equalsIgnoreCase(simbPila)) {
            fila = estado.getSimbolosPila().size() + 1;
        }
        if (Reconocedor.FIN_SECUENCIA.equalsIgnoreCase(simbEntrada)) {
            col = estado.getSimbolosEntrada().size() + 1;
        }
        if (fila < 0) {
            fila = estado.getSimbolosPila().indexOf(simbPila) + 1;
        }
        if (col < 0) {
            col = estado.getSimbolosEntrada().indexOf(simbEntrada) + 1;
        }
        if (fila < 0 || col < 0) {
            //System.out.println("No hay posicion");
            mostrarMensaje("Error", "Ha ocurrido un error al reconocer la hilera", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        //System.out.println("F: " + fila + " C: " + col);
        cod = (String) estado.getModelTable().getValueAt(fila, col);
        //System.out.println("Cod: " + cod);
        Transicion t = reconocedor.obtenerTransicion(cod);
        //System.out.println("Cod " + t.getCodigo());
        return t;
    }

    /**
     * Para aplicar la Transicion que diga la tabla de Transiciones
     */
    private void aplicarTransicion() {
        if (transActual == null) {
            //System.out.println("T null");
            mostrarMensaje("Error", "Ha ocurrido un error al reconocer la hilera", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (transActual.getCodigo().contains("A") || transActual.getCodigo().contains("R")) {
            //System.out.println("Acepte - Rechace");
            if (!leerSiguienteSimbolo()) {
                //jl_operacion.setText(transActual.getSimboloEntrada());
                return;
            }
            return;
        }
        String opPila = transActual.getOperacionPila();
        if (opPila.contains("Apile(")) {
            if (!apile(opPila)) {
                return;
            }
        } else if (opPila.contains("Desapile")) {
            if (!desapile()) {
                return;
            }
        } else if (opPila.contains("Replace(")) {
            if (!reemplace(opPila)){
                return;
            }
        }
        
        String opEntrada = transActual.getOperacionEntrada();
        if (opEntrada.contains("Avance")) {
            leerSiguienteSimbolo();
        }
    }

    /**
     * Para leer el siguiente simbolo en la hilera
     * @return true si se puede leer, false de lo contrario
     */
    private boolean leerSiguienteSimbolo() {
        posActual++;
        if (posActual >= hilera.length()) {
            mostrarMensaje("Hilera Leída", "Ya no hay más caracteres por leer, verifique el resultado de la hilera en el campo 'Operación'", JOptionPane.WARNING_MESSAGE);
            jb_siguiente.setEnabled(true);
            return false;
        }
        caracter = hilera.substring(posActual, posActual + 1);
        jtf_caracter.setText("'" + caracter + "' en la posición " + posActual);
        return true;
    }

    /**
     * Para apilar el simbolo enviado como parémetro
     * @param op
     * @return true si se pudo apilar, false de lo contrario
     */
    private boolean apile(String op) {
        op = op.substring(op.indexOf("(") + 1, op.indexOf(")"));
        //op = op.replace("\"", "");
        op = op.replace("\'", "");
        //System.out.println("Apilando " + op);
        DefaultTableModel m = (DefaultTableModel) tablePila.getModel();
        m.addRow(new String[]{op});
        for (int i = m.getRowCount() - 2; i >= 0; i--) {
            m.setValueAt(m.getValueAt(i, 0), i + 1, 0);
        }
        m.setValueAt(op, 0, 0);
        return true;
    }
    
    /**
     * Para apilar el simbolo enviado como parémetro
     * @param simb
     * @return true si se pudo apilar, false de lo contrario
     */
    private boolean apileSimbolo(String simb) {
        //System.out.println("Apilando " + op);
        DefaultTableModel m = (DefaultTableModel) tablePila.getModel();
        m.addRow(new String[]{simb});
        for (int i = m.getRowCount() - 2; i >= 0; i--) {
            m.setValueAt(m.getValueAt(i, 0), i + 1, 0);
        }
        m.setValueAt(simb, 0, 0);
        return true;
    }

    private boolean desapile() {
        DefaultTableModel m = (DefaultTableModel) tablePila.getModel();
        if (m.getRowCount() > 1) {
            m.removeRow(0);
        } else {
            //System.out.println("Pila vacía");
        }
        return true;
    }
    
    private boolean reemplace(String op){
        if (!desapile()){
            return false;
        }
        op = op.substring(op.indexOf("(") + 1, op.indexOf(")"));
        //op = op.replace("\"", "");
        op = op.replace("\'", "");
        String ele;// = this.ladoDer.substring(0, 1);
        String nodo = "";
        boolean siguiente = false;
        for(int i=0; i<op.length(); i++){//recorremos el lado derecho
            ele = op.substring(i, i+1);//elemento por elemento
            nodo = nodo + ele;
            switch(ele){
                case ">"://controlamos de que ">"
                    apileSimbolo(nodo);
                    nodo = "";
                break;
                case "<"://controlamos de que "<"
                    siguiente = true;//controlamos de que el siguiente inmediato o sea ">"
                break;
                default:
                    ele = ele.toUpperCase();
                    if (!siguiente){
                        apileSimbolo(nodo);
                        nodo = "";
                        ele = ele.toLowerCase();
                    }
                    siguiente = false;//como ya ha llegado un terminal desactivamos el siguiente inmediato                 
            }
        }     
        return true;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getSource() == null) {
            return;
        }
        if (me.getSource().toString().contains("text=Regresar")) { //Click en Regresar
            this.setVisible(false);
        } else if (me.getSource().toString().contains("text=Exportar")) { //Click en Exportar
            exportarTXT();
        } else if (me.getSource().toString().contains("text=Guardar PNG")) { //Click en Guardar PNG
            //Capturamos el nombre de la imagen
            String nombre = JOptionPane.showInputDialog(rootPane, "Digite el nombre de la imagen sin espacios", "Nombre de la imagen", JOptionPane.QUESTION_MESSAGE);
            //Validamos el nombre
            if (nombre == null) {
                return;
            }
            if ("".equals(nombre.replace(" ", ""))) {
                JOptionPane.showMessageDialog(this, "El nombre de la imagen no puede estar vacío", "Error, falta nombre", JOptionPane.WARNING_MESSAGE);
                return;
            }
            //Guardamos la imagen en PNG
            guardarMatrizEnPNG(pane, nombre.replace(" ", ""));
        } else if (me.getSource().toString().contains("text=Reconocedor")) {
            mostrarReconocedor();
        } else if (me.getSource().toString().contains("text=" + Reconocedor.FIN_SECUENCIA)) {
            //System.out.println("Reconociendo...");
            jb_finSecuencia.setText("Editar");
            jb_siguiente.setEnabled(true);
            jtf_hilera.setText(jtf_hilera.getText() + Reconocedor.FIN_SECUENCIA);
            jtf_hilera.setEditable(false);
            hilera = jtf_hilera.getText();
            posActual = 0;
            caracter = hilera.substring(posActual, posActual + 1);
            jtf_caracter.setText("'" + caracter + "'" + " en la posición " + posActual);
            estadoActual = reconocedor.getEstado();
            jtf_estado.setText(estadoActual.getNombre());
            transActual = obtenerTransicion(caracter, (String) tablePila.getModel().getValueAt(0, 0), estadoActual);
            //aqui
            if (transActual == null) {
                //System.out.println("Transa null");
                mostrarMensaje("Error", "Ha ocurrido un error al reconocer la hilera", JOptionPane.ERROR_MESSAGE);
            } else {
                if (transActual.getCodigo().contains("A") || transActual.getCodigo().contains("R")) {
                    jtf_operacion.setText(transActual.getOperacionPila());
                    if(transActual.getCodigo().contains("R")){
                        jb_siguiente.setEnabled(false);
                        jtf_operacion.setText(transActual.getOperacionPila());// + " por " + transActual.getMensajeMostrar());
                    }
                } else {
                    jtf_operacion.setText(""
                            + transActual.getOperacionPila().substring(transActual.getOperacionPila().indexOf(":")+1, transActual.getOperacionPila().length()) + ", " 
                            + transActual.getOperacionEntrada().substring(transActual.getOperacionEntrada().indexOf(":")+1, transActual.getOperacionEntrada().length()));
                }
            }
        } else if (me.getSource().toString().contains("text=Editar")) {
            //System.out.println("Editando...");
            setConfiInicial();
            jb_finSecuencia.setText(Reconocedor.FIN_SECUENCIA);
            jb_siguiente.setEnabled(false);
            jtf_estado.setText(reconocedor.getEstado().getNombre());
            jtf_caracter.setText("");
            jtf_operacion.setText("");
            jtf_hilera.setText(jtf_hilera.getText().replace(Reconocedor.FIN_SECUENCIA, ""));
            jtf_hilera.setEditable(true);
        } else if (me.getSource().toString().contains("text=Siguiente")) {
            //System.out.println("Siguiente...");
            aplicarTransicion();
            transActual = obtenerTransicion(caracter, (String) tablePila.getModel().getValueAt(0, 0), estadoActual);
            if (transActual == null) {
                System.out.println("Transicion null");
                //mostrarMensaje("Error", "Ha ocurrido un error al reconocer la hilera", JOptionPane.ERROR_MESSAGE);
            } else {
                if (transActual.getCodigo().contains("A") || transActual.getCodigo().contains("R")) {
                    jtf_operacion.setText(transActual.getOperacionPila());
                    if(transActual.getCodigo().contains("R")){
                        jb_siguiente.setEnabled(false);
                        jtf_operacion.setText(transActual.getOperacionPila());// + " por " + transActual.getMensajeMostrar());
                    }
                } else {
                    jtf_operacion.setText(""
                            + transActual.getOperacionPila().substring(transActual.getOperacionPila().indexOf(":")+1, transActual.getOperacionPila().length()) + ", " 
                            + transActual.getOperacionEntrada().substring(transActual.getOperacionEntrada().indexOf(":")+1, transActual.getOperacionEntrada().length()));
                }
            }
        } else {
            System.out.println("Default: " + me.getSource().toString());
        }
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

    private void mostrarMensaje(String title, String msg, int cod) {
        JOptionPane.showMessageDialog(this, msg, title, cod);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
