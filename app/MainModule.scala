import actions.ValidateParamsOddEvenAction.SecuredRequest
import actions.{ValidateParamsAction, ValidateParamsOddEvenAction, ValidateParamsOddEvenActionSecured}
import com.google.inject.{AbstractModule, TypeLiteral}
import javax.inject.Inject
import play.api.Configuration
import play.api.Environment
import net.codingwell.scalaguice._
import play.api.libs.concurrent.AkkaGuiceSupport
import services.{ActService1, Service1}

/**
  * Created by Ilya Volynin on 19.10.2018 at 14:29.
  */
object MainModule {
}

class MainModule(environment: Environment, configuration: Configuration) extends AbstractModule with AkkaGuiceSupport with ScalaModule {
  import MainModule._

  override def configure(): Unit = {
    val addGreet = configuration.get[String]("user.addtitionalgreet")
    val showAddGreet = configuration.get[Boolean]("user.showAdditionalGreet")
    bind[Boolean].annotatedWithName("showAG").toInstance(showAddGreet)
    bind[String].annotatedWithName("aG").toInstance(addGreet)
    bind[Service1].asEagerSingleton()
    bind(new TypeLiteral[ValidateParamsOddEvenAction[SecuredRequest]] {}).to(classOf[ValidateParamsOddEvenActionSecured])
    bind[ValidateParamsAction].asEagerSingleton()
    bindActor[ActService1]("act1")
  }
}
