import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.libs
import play.Environment
import net.codingwell.scalaguice._
import play.libs.akka.AkkaGuiceSupport
import services.Service1

/**
  * Created by Ilya Volynin on 19.10.2018 at 14:29.
  */
object MainModule {

  val defaultOrigins = Seq(
    "https://www.dev.raiffeisen.ru", "https://www.test.rbo.raiffeisen.ru",
    "https://www.rbo.raiffeisen.ru", "https://www.load.rbo.raiffeisen.ru",
    "https://www.preview.rbo.raiffeisen.ru", "https://www.test.elbrus-ng.raiffeisen.ru",
    "http://www.dev.raiffeisen.ru", "http://www.test.rbo.raiffeisen.ru",
    "http://www.load.rbo.raiffeisen.ru", "http://www.test.elbrus-ng.raiffeisen.ru", "https://cpwww.dev.rbp.raiffeisen.ru"
  )
}

class MainModule(environment: Environment, configuration: Config) extends AbstractModule with AkkaGuiceSupport with ScalaModule {
  import MainModule._

  override def configure(): Unit = {
    val addGreet = configuration.getString("user.addtitionalgreet")
    val showAddGreet = configuration.getBoolean("user.showAdditionalGreet")
    bind[Boolean].annotatedWithName("showAG").toInstance(showAddGreet)
    bind[String].annotatedWithName("aG").toInstance(addGreet)
    bind[Service1].asEagerSingleton()
  }
}
