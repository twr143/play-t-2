package services
/**
  * Created by Ilya Volynin on 19.10.2018 at 14:57.
  */
class Service1 {

  def getMyName(showAG: Boolean, aG: String): String = "Ilya! " + (if (showAG) aG else "")
}
