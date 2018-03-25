package algorithm_practice;

import org.spark_project.jetty.util.ArrayQueue;
import java.util.ArrayList;
import java.util.Queue;

public class BinaryTreeNode {
    
    public BinaryTreeNode left;
    public BinaryTreeNode right;
    public int val;
    
    public BinaryTreeNode(int val){
        this.val = val;
        this.left = null;
        this.right = null;
    }
}

class BinaryTreeOperation {

    private static BinaryTreeNode insert(BinaryTreeNode root, int another){
        if(root == null) return new BinaryTreeNode(another);
        else if(root.val == another) return root;
        else if(root.val < another) root.right = insert(root.right, another);  // update root right and left
        else root.left = insert(root.left,another);
        return root;
    }

    private static ArrayList<Integer> inorderTraversal(BinaryTreeNode root){

        ArrayList<Integer> result = new ArrayList<>();
        if(root == null) return result;
        if(root.left != null){
            ArrayList<Integer> leftRes = inorderTraversal(root.left);
            result.addAll(leftRes);
        }
        result.add(root.val);
        if(root.right != null){
            ArrayList<Integer> rightRes = inorderTraversal(root.right);
            result.addAll(rightRes);
        }
        return result;
    }

    private static ArrayList<Integer> levelorderTraversal(BinaryTreeNode root){

        ArrayList<Integer> result = new ArrayList<>();
        if(root == null) return  result;
        Queue<BinaryTreeNode> queue = new ArrayQueue<>();
        queue.add(root);
        while (!queue.isEmpty()){
            BinaryTreeNode head = queue.poll();
            result.add(head.val);
            if(head.left != null) queue.add(head.left);
            if(head.right != null) queue.add(head.right);
        }
        return result;

    }


    public static void main(String[] args) {
        BinaryTreeNode root = BinaryTreeOperation.insert(null, 12);
        BinaryTreeOperation.insert(root, 5);
        BinaryTreeOperation.insert(root, 18);
        BinaryTreeOperation.insert(root, 8);
        BinaryTreeOperation.insert(root, 10);
        BinaryTreeOperation.insert(root, 12);
        BinaryTreeOperation.insert(root, 20);
        BinaryTreeOperation.insert(root, 4);
        BinaryTreeOperation.insert(root, 2);
        BinaryTreeOperation.insert(root, 16);
        BinaryTreeOperation.insert(root, 12);
        BinaryTreeOperation.insert(root, 7);

        System.out.println(BinaryTreeOperation.inorderTraversal(root));
        System.out.println(BinaryTreeOperation.levelorderTraversal(root));
    }
}