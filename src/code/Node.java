package code;
public class Node {

    public Rectangle rect;

    public Node n1, n2, n3, n4;

    public Node(Rectangle rect, Node n1, Node n2, Node n3, Node n4){

        this.rect = rect;

        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;
        this.n4 = n4;

    }

    //Element-1ID node1-id node2-id node3-id
    //Element-2ID node4-id node5-id node6-id
    //...

    //Node-1ID point-x point-y

}

