package algorithm_practice

object Practice {

  def isPrime1(number : Int) : Boolean = {

    for(temp <- 2 to number/2 if number % temp == 0 ){
      return false
    }
    true
  }


  // skip all even numbers and only try up to the square root of the number
  def isPrime2(num : Int) : Boolean = {
    if(num % 2 == 0) return false

    for(tmp <- 3 to Math.sqrt(num).toInt + 1 by 2){
      if(num % tmp == 0) return false
    }
    true
  }

  def factorial1(num : Int) : Int = {
    if(num == 1) 1
    else num * factorial1(num-1)
  }

  def factorial2(num : Int) : Int = {
    var tmp = 1
    for(i <- 1 to num){
      tmp = tmp*i
    }
    tmp
  }

  def mergeSort(list : List[Int]) : List[Int] = {
    def merge(left : List[Int], right : List[Int]) : List[Int] = (left, right) match {
      case (Nil, res@(x :: _) ) => res
      case (res@(x :: _), Nil) => res
      case (lh :: lt, rh :: rt) => {
        if(lh <= rh) lh :: merge(lt, right)
        else rh :: merge(left, rt)
      }
    }
    list match {
      case null => null
      case Nil => Nil
      case a :: Nil => List(a)
      case _ => {
        val (left, right) = list.splitAt(list.length / 2)
        val sortedLeft = mergeSort(left)
        val sortedRight = mergeSort(right)
        merge(sortedLeft, sortedRight)
      }
    }
  }

}

object Test{

  def main(args: Array[String]): Unit = {
    println(Practice.isPrime1(107))
    println(Practice.isPrime2(107))

    println(Practice.factorial1(5))
    println(Practice.factorial2(5))


    println(Practice.mergeSort(List(14,2)))
  }
}
