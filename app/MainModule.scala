import actions.ValidateParamsOddEvenAction.SecuredRequest
import actions.{ValidateParamsAction, ValidateParamsOddEvenAction, ValidateParamsOddEvenActionSecured}
import com.google.inject.{AbstractModule, TypeLiteral}
import com.typesafe.config.Config
import play.api.libs
import play.Environment
import net.codingwell.scalaguice._
import play.api.libs.concurrent.AkkaGuiceSupport
import services.{ActService1, Service1}

/**
  * Created by Ilya Volynin on 19.10.2018 at 14:29.
  */
object MainModule {
}

class MainModule(environment: Environment, configuration: Config) extends AbstractModule with AkkaGuiceSupport with ScalaModule {
  import MainModule._

  override def configure(): Unit = {
    val addGreet = configuration.getString("user.addtitionalgreet")
    val showAddGreet = configuration.getBoolean("user.showAdditionalGreet")
    bind[Boolean].annotatedWithName("showAG").toInstance(showAddGreet)
    bind[String].annotatedWithName("aG").toInstance(addGreet)
    bind[Service1].asEagerSingleton()
    bind(new TypeLiteral[ValidateParamsOddEvenAction[SecuredRequest]]{}).to(classOf[ValidateParamsOddEvenActionSecured])
    bind[ValidateParamsAction].asEagerSingleton()
    bindActor[ActService1]("act1")
  }
}
