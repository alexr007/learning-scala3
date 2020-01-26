object Main {

  def main(args: Array[String]): Unit = {
    println("Hello world!")
    println(msg)
  }

  List(1,2,3).map(_+1)

  def msg = "I was compiled by dotty :)"

}
