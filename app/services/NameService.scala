package services
/**
  * Created by Ilya Volynin on 19.10.2018 at 14:57.
  */
class NameService {

  def getMyName(showAG: Boolean, aG: String): String = {
    Thread.sleep(600)
    "Ilya! " + (if (showAG) aG else "")
  }
}
