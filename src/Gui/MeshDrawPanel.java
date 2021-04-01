package Gui;

import code.*;
import code.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;


public class MeshDrawPanel extends JPanel {
    private Mesh mesh;
    private int licznik=0;
    public MeshDrawPanel(Mesh dm){

        this.mesh = dm;

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //------------------------------------------RYSOWANIE OBRAZU------------------------------------------
        if(mesh.bgImg!=null)
        g2.drawImage(mesh.bgImg, 0, 0, this);



        //-----------------------------------RYSOWANIE SIATKI TROJKATNEJ-------------------------------------------
        if(mesh.triangleElements!=null && mesh.bgImg!=null) {
                TriangleElement triangle;
                Graphics2D gPicture = (Graphics2D) mesh.bgImg.getGraphics();
                gPicture.setColor(Color.yellow);

                for (int i = 0; i < mesh.triangleElements.size(); i++) {
                    triangle = mesh.triangleElements.get(i);
                    Shape line1;
                    Shape line2;
                    Shape line3;
                    line1 = new Line2D.Double(triangle.getP1().getX(), triangle.getP1().getY(), triangle.getP2().getX(), triangle.getP2().getY());
                    line2 = new Line2D.Double(triangle.getP2().getX(), triangle.getP2().getY(), triangle.getP3().getX(), triangle.getP3().getY());
                    line3 = new Line2D.Double(triangle.getP1().getX(), triangle.getP1().getY(), triangle.getP3().getX(), triangle.getP3().getY());
                    gPicture.draw(line1);
                    gPicture.draw(line2);
                    gPicture.draw(line3);

                    //--- W CO DRUGIM TROJKACIE MAM TA SAMA PRZEKATNA WIEC JEJ NIE RYSUJE
                    i++;
                    triangle = mesh.triangleElements.get(i);
                    line1 = new Line2D.Double(triangle.getP1().getX(), triangle.getP1().getY(), triangle.getP2().getX(), triangle.getP2().getY());
                    line2 = new Line2D.Double(triangle.getP2().getX(), triangle.getP2().getY(), triangle.getP3().getX(), triangle.getP3().getY());
                    gPicture.draw(line1);
                    gPicture.draw(line2);
                }
                //po narysowaniu na obrazie trzeba odswiezyc obraz
            g2.drawImage(mesh.bgImg, 0, 0, this);
        }
}

    @Override
    public void repaint() {
            super.repaint();
    }



}
