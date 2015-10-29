package uk.co.tkeetch

import play.api.mvc._
import play.api.http._

object Cors {

  def allowAll(resp:Result):Result = {
    resp.withHeaders(
      "Access-Control-Allow-Origin"  -> "*",
      "Access-Control-Allow-Methods" -> "POST, OPTIONS",
      "Access-Control-Allow-Headers" -> "Content-Type")
  }
}

class CorsController extends Controller {

  def okAllowAll() = Action { implicit request =>
    Cors.allowAll(Ok)
  }
}

