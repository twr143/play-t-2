package controllers
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.db.Database
import services.Service1

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, s1: Service1,
                               @Named("showAG") showAG: Boolean,
                               @Named("aG") aG: String) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(s1.getMyName(showAG, aG)))
  }
}
