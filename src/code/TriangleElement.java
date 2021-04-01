package code;

public class TriangleElement {
    Point2D p1, p2,p3;


    public TriangleElement(Point2D p1, Point2D p2,Point2D p3){

        this.p1 = p1;
        this.p2 = p2;
        this.p3=p3;

    }

    public Point2D getP1() {
        return p1;
    }

    public void setP1(Point2D p1) {
        this.p1 = p1;
    }

    public Point2D getP2() {
        return p2;
    }

    public void setP2(Point2D p2) {
        this.p2 = p2;
    }

    public Point2D getP3() {
        return p3;
    }

    public void setP3(Point2D p3) {
        this.p3 = p3;
    }

}
