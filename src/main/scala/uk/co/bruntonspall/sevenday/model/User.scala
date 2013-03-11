package uk.co.bruntonspall.sevenday.model
import com.google.appengine.api.datastore.{ DatastoreServiceFactory, KeyFactory, Entity, Query }
import scala.util.control.Exception.allCatch

case class User(
  email: String,
  password: String,
  name: String)

object User {
  def key(email: String) = KeyFactory.createKey("User", email)

  lazy val datastore = DatastoreServiceFactory.getDatastoreService()

  def fetchByEmail(email: String): Option[User] = {
    val entity = allCatch opt datastore.get(key(email))
    entity.map { e =>
      User(
        e.getProperty("email").asInstanceOf[String],
        e.getProperty("password").asInstanceOf[String],
        e.getProperty("name").asInstanceOf[String])
    }
  }
  def save(user: User) {
    val userEntity = new Entity(key(user.email))
    userEntity.setProperty("email", user.email)
    userEntity.setUnindexedProperty("password", user.password)
    userEntity.setProperty("name", user.name)
    datastore.put(userEntity)
  }
}
