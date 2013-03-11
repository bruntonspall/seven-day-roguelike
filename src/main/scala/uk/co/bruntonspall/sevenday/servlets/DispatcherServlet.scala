package uk.co.bruntonspall.sevenday.servlets

import org.scalatra.ScalatraServlet
import uk.co.bruntonspall.sevenday.scalatra.TwirlSupport
import uk.co.bruntonspall.sevenday.model.User

class DispatcherServlet extends ScalatraServlet with TwirlSupport {

  get("/") {
    val userToSave = User("test@test.com", "password", "Testy Testerson")
    User.save(userToSave)
    val testUser = User.fetchByEmail("test@test.com")
    html.welcome.render(testUser)
  }

}
