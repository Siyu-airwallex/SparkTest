package algorithm_practice

import scala.collection.mutable.ArrayBuffer


case class TreeNode(value : Int) {
  var left  : TreeNode = _
  var right : TreeNode = _
}

object TreeOperation {

  def insert(root : TreeNode, newValue : Int) : TreeNode = {
    if(root == null || root.value == newValue) return TreeNode(newValue)
    if (newValue < root.value) {
        root.left = insert(root.left, newValue)
    }
    if(newValue > root.value) {
        root.right = insert(root.right, newValue)
    }
    root
  }

  def search(root : TreeNode, newValue : Int) : TreeNode = {
    if(root == null) null
    else if(root.value == newValue) root
    else if(root.value < newValue) search(root.right, newValue)
    else search(root.left, newValue)
  }

  def preorderTraversal(root : TreeNode) : List[Int] = root match {
    case null => Nil
    case TreeNode(value) => value :: preorderTraversal(root.left) ::: preorderTraversal(root.right)
  }

  def inorderTraversal(root : TreeNode) : List[Int] = root match {
    case null => Nil
    case TreeNode(value) => {
      val leftNodes = inorderTraversal(root.left)
      val rightNodes = inorderTraversal(root.right)
      leftNodes ::: (value :: rightNodes)
    }
  }

  def postorderTraversal(root : TreeNode) : List[Int] = root match {
    case null => Nil
    case TreeNode(value) => (postorderTraversal(root.left) ::: postorderTraversal(root.right)) :+ value
  }

  def levelorderTraversal(root : TreeNode) : List[Int] = {
    var queue = ArrayBuffer(root)
    var result : List[Int] = Nil
    while(queue.nonEmpty){
      val new_queue = ArrayBuffer.empty[TreeNode]
      for(node <- queue){
        result = result :+ node.value
        if(node.left != null){
          new_queue.append(node.left)
        }
        if(node.right != null){
          new_queue.append(node.right)
        }
      }
      queue = new_queue
    }
    result
  }

}



object TreeOperationTest{
  def main(args: Array[String]): Unit = {
    val root = TreeOperation.insert(null, 12)
    TreeOperation.insert(root, 5)
    TreeOperation.insert(root, 18)
    TreeOperation.insert(root, 8)
    TreeOperation.insert(root, 10)
    TreeOperation.insert(root, 12)
    TreeOperation.insert(root, 20)
    TreeOperation.insert(root, 4)
    TreeOperation.insert(root, 2)
    TreeOperation.insert(root, 16)
    TreeOperation.insert(root, 12)
    TreeOperation.insert(root, 7)

    println(TreeOperation.search(root, 5))
    println(TreeOperation.preorderTraversal(root))
    println(TreeOperation.inorderTraversal(root))
    println(TreeOperation.postorderTraversal(root))
    println(TreeOperation.levelorderTraversal(root))

  }
  
}
