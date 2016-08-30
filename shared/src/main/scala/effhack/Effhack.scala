package effhack

//import cats._, data._
import cats.data.{Xor, Reader, State}
import org.atnos.eff._
import org.atnos.eff.all._
import org.atnos.eff.syntax.all._

import scala.util.Random

object Effhack extends App {

  case class Name(s: String)

  case class Person(id: Int, name: Name)

  case class Config(maxLength: Int)

  type _Rng[R] = State[Int, ?] |= R

  type _ReadConfig[R] = Reader[Config, ?] |= R

  type _Err2[R] = (String Xor ?) |= R

  val Ben = Person(1, Name("Ben"))

  val config = Config(8)

  def randomName[R: _Rng]: Eff[R, Name] = for {
    seed <- get
    random = new Random(seed)
    _ <- put(seed + 1)
  } yield Name(random.nextString(6))

  def updateName[R: _ReadConfig: _Err2](p: Person, n: Name): Eff[R, Person] = for {
    config <- ask
    v <- if (config.maxLength < n.s.length) left(s"name too long") else right(n)
  } yield p.copy(name = v)

  def randomizeName[R: _ReadConfig: _Err2: _Rng](p: Person): Eff[R, Person] = for {
    r <- randomName
    p2 <- updateName(p, r)
  } yield p2

  type R = Fx.fx3[(String Xor ?), State[Int, ?], Reader[Config, ?]]

//  println(run(runState(1)(runReader(config)(runXor(randomizeName[R](Ben))))))

//  error: value runXor is not a member of org.atnos.eff.Eff[effhack.Effhack.R,effhack.Effhack.Person]
  randomizeName[R](Ben).runXor.runReader(config).runState(1).run

//  error: No instance found for MemberIn[[β$2$]cats.data.Xor[String,β$2$], R].
  run(runState(1)(runReader(config)(runXor(randomizeName[R](Ben)))))

}