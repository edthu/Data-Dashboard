package dasboard
import scala.collection.mutable.Buffer

case class Student(name: String)

// Iterable-piirteen vaatimus on että luokasta täytyy löytyä
//metodi iterator, jonka avulla juuri tällaista rakennetta voidaan käydään läpi

class StudentUnion(leader: Student) extends Iterable[Student]:

  def leaderIterator = new Iterator[Student]():
    var notRead = true     // onko johtajaa luettu


    def hasNext = notRead

    def next() =
      notRead = false
      leader

  def iterator = leaderIterator ++ students.iterator  // Liitetään iteraattorit "peräkkäin"

  private val students:Buffer[Student] = Buffer[Student]()

  def addMember(person: Student) =
    students += person

end StudentUnion

@main def example() =
  val union = StudentUnion(Student("Uolevi"))
  union.addMember(Student("Pekka"))
  union.addMember(Student("Jaana"))
  union.addMember(Student("Mika"))

  // Nyt kun StudentUnion on Iterable, sitä voidaan käyttää for-silmukassa
  for s <- union do println(s)
  requestData("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&ids=ethereum")
end example
