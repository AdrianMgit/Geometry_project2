package Gui;

import code.Grain;
import code.Mesh;
import code.Point2D;
import code.TriangleElement;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;


public class GuiClass extends JFrame {
    private JPanel rootPanel;
    private JPanel drawPanel;
    private JPanel controllerPanel;
    private JButton ReadPicture;
    private JButton savePictureButton;
    private JComboBox comboBox1;
    private JButton generateMeshButton;
    private JButton saveMeshTxtButton;
    private JButton readMeshTxtButton;

    Mesh mesh;

    public GuiClass() {

        mesh = new Mesh();

        setMinimumSize(new Dimension( 300, 250));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("MESH");
        setLocationRelativeTo(null);
        drawPanel = new MeshDrawPanel(mesh);
        rootPanel.add(BorderLayout.CENTER, drawPanel);
        rootPanel.add(BorderLayout.EAST, controllerPanel);
        add(rootPanel);





        //-----------------------------------------BUTTON WCZYTANIA OBRAZU--------------------------------
        ReadPicture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //----------------WCZYTANIE OBRAZU---------
                try {
                    BufferedImage bg = ImageIO.read(new File("1_Ebsd.bmp"));
                    mesh.bgImg = bg;


                    //USTAWIAM WYSOKOSC I SZEROKOSC NAJWIEKSZEGO ELEMENTU W DRZEWIE-UZYWANE W TWORZENIU DRZEWA
                    mesh.height=bg.getHeight();
                    mesh.width=bg.getWidth();
                    //USTAWIAM WIELKOSC PANELU JAKO O 200 WIEKSZA NIZ WCZYTANY OBRAZEK
                    setSize(bg.getWidth()+200,bg.getHeight()+100);
                    setLocationRelativeTo(null);
                    //--------PO WCZYTANIE OBRAZU USUWAM STARA SIATKE
                    mesh.rootNode=null;
                    mesh.triangleElements.clear();
                    mesh.meshNodes.clear();
                    drawPanel.repaint();

                    JOptionPane.showMessageDialog(null,"Aby narysowac siatke: \n-Kliknij na dane ziarno\n-Wybierz ziarno po ID" +
                            "\nAby wyczyscic siatke wczytaj obraz od nowa");

                    //-------------ODCZYT ZIAREN I UZUPELNIENIE COMBOBOXA----------------------
                    mesh.findGrain();
                    comboBox1.removeAllItems();
                    comboBox1.addItem("All Graines");
                    for(Grain grain:mesh.graines){
                        comboBox1.addItem( grain.getId());
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,"Blad.Nie znaleziono pliku z obrazem");
                    ex.printStackTrace();
                }
            }
        });

        //------------------------------TWORZENIE DRZEWA DLA KLIKNIETEGO ZIARNA------------------------------------
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if(mesh.bgImg!=null) {

                    if(e.getX()<mesh.width&&e.getY()<mesh.height)           //bo panel jest troche wiekszy od obrazka
                    mesh.constructQT(new Color(mesh.bgImg.getRGB(e.getX(), e.getY())));

                    drawPanel.repaint();
                }
                else
                    JOptionPane.showMessageDialog(null,"Blad.Nie wczytano obrazu!");

            }
        });

        //------------------------------------BUTTON ZAPISU OBRAZU-----------------------------------------
        savePictureButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(mesh.bgImg!=null) {
                    try {
                        ImageIO.write(mesh.bgImg, "jpg", new File("Mesh.jpg"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else
                    JOptionPane.showMessageDialog(null,"Blad.Nie wczytano obrazu!");
            }
        });


        //----------------------------BUTTON GENEROWANIA SIATKI PO ID--------------------------------
        generateMeshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(mesh.bgImg!=null) {

                    if (comboBox1.getSelectedItem().toString().equals("All Graines")) {
                        for (Grain grain : mesh.graines){
                            mesh.constructQT(grain.getColor());
                    }
                    } else {
                        int grainID = Integer.parseInt(comboBox1.getSelectedItem().toString());
                        for (Grain grain : mesh.graines)
                            if (grain.getId() == grainID) {
                                mesh.constructQT(grain.getColor());
                                break;
                            }
                    }

                    drawPanel.repaint();

                }
                else
                    JOptionPane.showMessageDialog(null,"Blad.Nie wczytano obrazu!");
            }
        });


        //-----------------------------------BUTTON ZAPISU SIATKI DO PLIKU TXT--------------------------------
        saveMeshTxtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                File nodesFile;

                if (mesh.triangleElements.isEmpty())
                    JOptionPane.showMessageDialog(null, "Nie wygenerowano siatki do zapisu!");
                else {
                        nodesFile = new File("triangleNodes.txt");


                    //------------ZAPIS PLIKU Z WEZLAMI-------------

                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(nodesFile))) {

                        for (int i = 0; i < mesh.meshNodes.size(); i++) {
                            bw.write("" + (i + 1) + " " + (mesh.meshNodes.get(i).getX()) + ";" + (mesh.meshNodes.get(i).getY()));
                            bw.newLine();
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    //-----------------------ZAPIS PLIKOW Z ELEMENTAMI-------------------------------

                        File elementsFile = new File("triangleElements.txt");

                        try (BufferedWriter bw = new BufferedWriter(new FileWriter(elementsFile))) {


                            for (int i = 0; i < mesh.triangleElements.size(); i++) {
                                int nr1wierzch = 0;
                                int nr2wierzch = 0;
                                int nr3wierzch = 0;
                                for (int j = 0; j < mesh.meshNodes.size(); j++) {
                                    if (mesh.triangleElements.get(i).getP1().getX() == mesh.meshNodes.get(j).getX() && mesh.triangleElements.get(i).getP1().getY() == mesh.meshNodes.get(j).getY())
                                        nr1wierzch = j + 1;
                                    if (mesh.triangleElements.get(i).getP2().getX() == mesh.meshNodes.get(j).getX() && mesh.triangleElements.get(i).getP2().getY() == mesh.meshNodes.get(j).getY())
                                        nr2wierzch = j + 1;
                                    if (mesh.triangleElements.get(i).getP3().getX() == mesh.meshNodes.get(j).getX() && mesh.triangleElements.get(i).getP3().getY() == mesh.meshNodes.get(j).getY())
                                        nr3wierzch = j + 1;
                                }
                                bw.write("" + (i + 1) + " " + nr1wierzch + ";" + nr2wierzch + ";" + nr3wierzch);
                                bw.newLine();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                }
            }
        });

        //------------------------------------BUTTON WCZYTANIA SIATKI Z PLIKU TXT-------------------------
        readMeshTxtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(mesh.bgImg!=null) {

                    if (readNodesFromFile() == true) {

                        //WCZYTANIE ELEMENTOW TROJKATNYCH
                        File triangleFile = new File("triangleElements.txt");
                        if (triangleFile.exists()) {
                            try {
                                BufferedReader br = new BufferedReader(new FileReader(triangleFile));

                                String buffer = br.readLine();
                                ArrayList<TriangleElement> triangleElements = new ArrayList<>();

                                while (buffer != null) {

                                    String bufferSplit[] = buffer.split(" ");   // wsp_x;wsp_y

                                    String podzielone = bufferSplit[1];
                                    String[] wsp = podzielone.split(";");
                                    Point2D pkt1 = mesh.meshNodes.get(Integer.parseInt(wsp[0]) - 1);
                                    Point2D pkt2 = mesh.meshNodes.get(Integer.parseInt(wsp[1]) - 1);
                                    Point2D pkt3 = mesh.meshNodes.get(Integer.parseInt(wsp[2]) - 1);
                                    triangleElements.add(new TriangleElement(pkt1, pkt2, pkt3));
                                    buffer = br.readLine();
                                }
                                mesh.readTriangleElements(triangleElements);
                                drawPanel.repaint();
                                br.close();

                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Nie znaleziono pliku z siatka z trojkatow!");

                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        } else
                            JOptionPane.showMessageDialog(null, "Nie znaleziono pliku z siatka z trojkatow!");
                    }
                }
                else
                    JOptionPane.showMessageDialog(null, "Nie wczytano obrazu!");
            }
        });

    }


//------------------------FUNKCJA POZA KONSTRUKTOREM,ZAPISUJE WEZLY DO PLIKU-------------------------------------

    private boolean readNodesFromFile(){
        File nodeFile = new File("triangleNodes.txt");
        if(nodeFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(nodeFile));

                String buffer = br.readLine();
                ArrayList<Point2D> nodes = new ArrayList<>();

                while (buffer != null) {

                    String bufferSplit[] = buffer.split(" ");   // wsp_x;wsp_y

                    String podzielone = bufferSplit[1];
                    String[] wsp = podzielone.split(";");
                    nodes.add(new Point2D(Double.parseDouble(wsp[0]), Double.parseDouble(wsp[1])));

                    buffer = br.readLine();
                }
                mesh.readNodes(nodes);
                br.close();

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Nie znaleziono pliku z wezlami!");
                return false;
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
            return true;
        }
        else{
            JOptionPane.showMessageDialog(null, "Nie znaleziono pliku z wezlami!");
            return false;
        }

    }
}
