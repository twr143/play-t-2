import actions.{SecuredRequest, ValidateParamsAction, ValidateParamsOddEvenAction, ValidateParamsOddEvenActionSecured}
import com.google.inject.TypeLiteral
import net.codingwell.scalaguice._
import play.api.libs.concurrent.AkkaGuiceSupport
import play.api.{Configuration, Environment}
import services.ActorNames.greeter
import services.{Greeter, NameService}

/**
  * Created by Ilya Volynin on 19.10.2018 at 14:29.
  */
case class MainModule(environment: Environment, configuration: Configuration)
  extends ScalaModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bind[Boolean].annotatedWithName("showAG")
      .toInstance(configuration.get[Boolean]("user.showAdditionalGreet"))
    bind[String].annotatedWithName("aG")
      .toInstance(configuration.get[String]("user.addtitionalgreet"))

    bind[NameService].asEagerSingleton()

    bind(new TypeLiteral[ValidateParamsOddEvenAction[SecuredRequest]] {})
      .to(classOf[ValidateParamsOddEvenActionSecured])

    bind[ValidateParamsAction].asEagerSingleton()
    bindActor[Greeter](greeter)
  }
}




