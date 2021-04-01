package code;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Mesh {

    //lista wezlow drzewa
    public ArrayList<Point2D> nodes;
    //lista elementow trojkatnych
    public ArrayList<TriangleElement> triangleElements;
    //lista ziaren
    public ArrayList<Grain> graines;
    //lista wezlow siatki
    public ArrayList<Point2D> meshNodes;


    public BufferedImage bgImg;
    public Node rootNode;
    //wielkosc najmniejszego elementu
    int tolerance = 3;


    //wielkosc obrazka-ustwaiana w funkcji buttona wczytywania obrazu
    public double height;
    public double width;


    public Mesh() {
        nodes = new ArrayList<>();
        triangleElements = new ArrayList<>();
        graines = new ArrayList<>();
        meshNodes = new ArrayList<>();

    }


    //---------------------------------DO SPRAWDZENIA JAKIEGO KOLORU JEST DANY CZWOROKAT------------------------
    int checkFill(Rectangle rect, Color color) {

        Boolean mixed = false;
        Boolean isSearchingColor = false;
        Boolean isAnotherColor = false;

        //-----PRZECHODZI PO KAZDYM PIKSELU W POZIOMIE I PIONIE I SPRAWDZA JEGO KOLOR
        for (double wi = rect.origin.getX(); wi < (rect.origin.getX() + rect.width); wi++) {

            for (double hi = rect.origin.getY(); hi < (rect.origin.getY() + rect.height); hi++) {

                //----POBRANIE KOLORU PIKSELA
                Color readColor = new Color(bgImg.getRGB((int) wi, (int) hi));

                //-------GDY POBRANY KOLOR JEST ROWNY KOLOROWI WYBRANEGO ZIARNA------
                if (readColor.getRed() == color.getRed() && readColor.getGreen() == color.getGreen() && readColor.getBlue() == color.getBlue())
                    isSearchingColor = true;
                else
                    isAnotherColor = true;
                //-----JESLI W DANYM CZWOROKACIE WYSTPEUJE KOLOR WYBRANEGO ZIARNA I ROWNIEZ INNY KOLOR--
                if (isSearchingColor && isAnotherColor)
                    mixed = true;
            }
        }
        if (mixed == true)
            return 0;
        else if (isAnotherColor == true)
            return -1;
        else return 1;
    }


    //----------------------------------REKURENCJA TWORZENIA SIATKI----------------------------------
    void subdivide(Node root, Color color) {

        //--------DZIELE ELEMENTY GDY ZAWIERAJA KOLOR WYBRANEGO ZIARNA I CHOCIAZ JEDEN INNY KOLOR-----
        if (checkFill(root.rect, color) == 0) {

            double width = root.rect.width / 2;
            double height = root.rect.height / 2;

            if (width > tolerance && height > tolerance) {

                Node n1 = new Node(new Rectangle(new Point2D(root.rect.origin.getX(), root.rect.origin.getY()), width, height), null, null, null, null);
                Node n2 = new Node(new Rectangle(new Point2D(root.rect.origin.getX() + width, root.rect.origin.getY()), width, height), null, null, null, null);
                Node n3 = new Node(new Rectangle(new Point2D(root.rect.origin.getX(), root.rect.origin.getY() + height), width, height), null, null, null, null);
                Node n4 = new Node(new Rectangle(new Point2D(root.rect.origin.getX() + width, root.rect.origin.getY() + height), width, height), null, null, null, null);

                root.n1 = n1;
                root.n2 = n2;
                root.n3 = n3;
                root.n4 = n4;

                subdivide(n1, color);
                subdivide(n2, color);
                subdivide(n3, color);
                subdivide(n4, color);

            }
            //---------------LISCIE DRZEWA KTORE SA MAJA KOLOR WYBRANEGO ZIARNA ORAZ JAKIS INNY KOLOR-ELEMENTY NA KRAWEDZI ZIARNA-----
            else {
                //--------------DODANIE ELEMENTOW TROJKATNYCH---JEDEN ELEMENT TO DWA TROJKATY--------------
                triangleElements.add(new TriangleElement(new Point2D(root.rect.origin.getX(), root.rect.origin.getY() + root.rect.height), new Point2D(root.rect.origin.getX(), root.rect.origin.getY()), new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY())));
                triangleElements.add(new TriangleElement(new Point2D(root.rect.origin.getX(), root.rect.origin.getY() + root.rect.height), new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY() + root.rect.height), new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY())));
                //-------------------------DODANIE WEZLOW SIATKI-------------------
                meshNodes.add(new Point2D(root.rect.origin.getX(), root.rect.origin.getY() + root.rect.height));
                meshNodes.add(new Point2D(root.rect.origin.getX(), root.rect.origin.getY()));
                meshNodes.add(new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY()));
                meshNodes.add(new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY() + root.rect.height));

            }
        }
        //----------WSZYSTKIE CZWOROKATY KTORE SA TYLKO KOLORU WYBRANEGO ZIARNA---------------
        else if (checkFill(root.rect, color) == 1) {
            //--------------DODANIE ELEMENTOW TROJKATNYCH---JEDEN ELEMENT TO DWA TROJKATY--------------
            triangleElements.add(new TriangleElement(new Point2D(root.rect.origin.getX(), root.rect.origin.getY() + root.rect.height), new Point2D(root.rect.origin.getX(), root.rect.origin.getY()), new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY())));
            triangleElements.add(new TriangleElement(new Point2D(root.rect.origin.getX(), root.rect.origin.getY() + root.rect.height), new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY() + root.rect.height), new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY())));
            //-------------------------DODANIE WEZLOW SIATKI-------------------
            meshNodes.add(new Point2D(root.rect.origin.getX(), root.rect.origin.getY() + root.rect.height));
            meshNodes.add(new Point2D(root.rect.origin.getX(), root.rect.origin.getY()));
            meshNodes.add(new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY()));
            meshNodes.add(new Point2D(root.rect.origin.getX() + root.rect.width, root.rect.origin.getY() + root.rect.height));
        }
    }


    //--------------------------------------------TWORZENIE DRZEWA DO SIATKI-------------------------------------
    public void constructQT(Color color) {

        Rectangle initialRectangle = new Rectangle(new Point2D(0, 0), (int) width, (int) height);

        rootNode = new Node(initialRectangle, null, null, null, null);

        subdivide(rootNode, color);

    }

    //-----------------------FUNKCJA DODAJACA WSZYSTKIE ZIARNA Z OBRAZKA DO LISTY ZIARN-----------------
    public void findGrain() {
        int id = 1;
        boolean found;
        //---PRZECHODZENIE PO KAZDYM PIKSELU
        for (double wi = 0; wi < width; wi++) {
            for (double hi = 0; hi < height; hi++) {
                found = false;
                //----POBRANIE KOLORU PIKSELA
                Color readColor = new Color(bgImg.getRGB((int) wi, (int) hi));
                for (Grain grain : graines) {
                    if (grain.color.getRed() == readColor.getRed() && grain.color.getGreen() == readColor.getGreen() && grain.color.getBlue() == readColor.getBlue())
                        found = true;
                }
                //-----GDY NA LISCIE NIE MA ZIARNA O KOLORZE ANALIZOWANEGO PIKSELA
                if (found == false) {
                    graines.add(new Grain(id, readColor));
                    id++;
                }
            }
        }
    }

    //------------------------DO USTAWIENIA LISTY WEZLOW PO WCZYTANIU Z PLIKU------------------------
    public void readNodes(ArrayList<Point2D> meshNodes) {
        triangleElements.clear();
        this.meshNodes = meshNodes;
    }


    //------------------------DO USTAWIENIA LISTY ELEMENTOW TROJKATNYCH PO WCZYTANIU Z PLIKU------------------------

    public void readTriangleElements(ArrayList<TriangleElement> triangleElements) {
        this.triangleElements = triangleElements;
    }
}